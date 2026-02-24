package com.example.classconnect

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.WindowCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var auth: FirebaseAuth

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvInitial: TextView
    private lateinit var bannerVerify: LinearLayout
    private lateinit var tvResendVerification: TextView

    private lateinit var cardAI: CardView
    private lateinit var cardAnnouncement: CardView
    private lateinit var cardDiscussion: CardView
    private lateinit var cardNotes: CardView

    // Toolbar menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) return true

        when (item.itemId) {
            R.id.action_logout -> {
                signOut()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Prevent edge-to-edge so status bar doesn't overlap toolbar/drawer
        WindowCompat.setDecorFitsSystemWindows(window, true)

        auth = FirebaseAuth.getInstance()

        // ── STAY LOGGED IN: if already signed in & verified, skip login ──
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // Not logged in at all → go to Login
            startActivity(Intent(this, Login::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        actionBarDrawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.nav_open, R.string.nav_close
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        // ── Bind views ──
        tvName               = findViewById(R.id.tvName)
        tvEmail              = findViewById(R.id.tvEmail)
        tvInitial            = findViewById(R.id.tvInitial)
        bannerVerify         = findViewById(R.id.bannerVerify)
        tvResendVerification = findViewById(R.id.tvResendVerification)
        cardAI               = findViewById(R.id.cardAI)
        cardAnnouncement     = findViewById(R.id.cardAnnouncement)
        cardDiscussion       = findViewById(R.id.cardDiscussion)
        cardNotes            = findViewById(R.id.cardNotes)

        // ── Drawer header views ──
        val headerView          = navView.getHeaderView(0)
        val tvDrawerName: TextView  = headerView.findViewById(R.id.tvDrawerName)
        val tvDrawerEmail: TextView = headerView.findViewById(R.id.tvDrawerEmail)
        val tvDrawerInitial: TextView = headerView.findViewById(R.id.tvDrawerInitial)

        // ── Populate user info ──
        val name  = currentUser.displayName?.takeIf { it.isNotBlank() } ?: "Student"
        val email = currentUser.email ?: ""
        val initial = name.first().uppercaseChar().toString()

        tvName.text    = name
        tvEmail.text   = email
        tvInitial.text = initial

        tvDrawerName.text    = name
        tvDrawerEmail.text   = email
        tvDrawerInitial.text = initial

        // ── Verification Banner ──
        if (!currentUser.isEmailVerified) {
            bannerVerify.visibility = View.VISIBLE
        }

        tvResendVerification.setOnClickListener {
            resendVerificationEmail()
        }

        // ── Card navigation ──
        cardAI.setOnClickListener           { startActivity(Intent(this, AIChatBuddy::class.java)) }
        cardAnnouncement.setOnClickListener { startActivity(Intent(this, Announcement::class.java)) }
        cardDiscussion.setOnClickListener   { startActivity(Intent(this, ClassDiscussion::class.java)) }
        cardNotes.setOnClickListener        { startActivity(Intent(this, ClassNotes::class.java)) }

        // ── Drawer menu ──
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> signOut()
                R.id.nav_profile -> startActivity(Intent(this, Profile::class.java))
                else -> Toast.makeText(this, menuItem.title, Toast.LENGTH_SHORT).show()
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    // ── Called on resume to refresh verification state ──
    override fun onResume() {
        super.onResume()
        auth.currentUser?.reload()?.addOnCompleteListener {
            val verified = auth.currentUser?.isEmailVerified ?: false
            bannerVerify.visibility = if (verified) View.GONE else View.VISIBLE
        }
    }

    private fun resendVerificationEmail() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "No user signed in.", Toast.LENGTH_SHORT).show()
            return
        }
        if (user.isEmailVerified) {
            bannerVerify.visibility = View.GONE
            Toast.makeText(this, "Your email is already verified ✅", Toast.LENGTH_SHORT).show()
            return
        }
        user.sendEmailVerification()
            .addOnSuccessListener {
                Toast.makeText(this, "Verification email sent! Check your inbox.", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to send: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun signOut() {
        auth.signOut()
        startActivity(Intent(this, Login::class.java))
        finish()
    }
}