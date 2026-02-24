package com.example.classconnect

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import com.example.classconnect.announcements.Announcement
import com.example.classconnect.livechat.LiveChatRoom
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var auth: FirebaseAuth

    lateinit var tvName: TextView
    lateinit var tvEmail: TextView

    // ⭐ All Cards
    lateinit var cardAnnouncement: CardView
    lateinit var cardNotes: CardView
    lateinit var cardDiscussion: CardView
    lateinit var cardGroupProject: CardView
    lateinit var cardLiveChat: CardView
    lateinit var cardAI: CardView
    lateinit var cardAIQuiz: CardView

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }

        if (item.itemId == R.id.action_logout) {
            auth.signOut()
            startActivity(Intent(this, Login::class.java))
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.nav_open,
            R.string.nav_close
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        // ⭐ Profile Header
        tvName = findViewById(R.id.tvName)
        tvEmail = findViewById(R.id.tvEmail)

        val headerView = navView.getHeaderView(0)
        val tvDrawerName: TextView = headerView.findViewById(R.id.tvDrawerName)
        val tvDrawerEmail: TextView = headerView.findViewById(R.id.tvDrawerEmail)

        val user = auth.currentUser
        if (user != null) {
            val name = user.displayName ?: "Student"
            val email = user.email ?: ""

            tvName.text = name
            tvEmail.text = email

            tvDrawerName.text = name
            tvDrawerEmail.text = email
        }

        // ⭐ Find Cards
        cardAnnouncement = findViewById(R.id.cardAnnouncement)
        cardNotes = findViewById(R.id.cardNotes)
        cardDiscussion = findViewById(R.id.cardDiscussion)
        cardGroupProject = findViewById(R.id.cardGroupProject)
        cardLiveChat = findViewById(R.id.cardLiveChat)
        cardAI = findViewById(R.id.cardAI)
        cardAIQuiz = findViewById(R.id.cardAIQuiz)

        // ⭐ Click Listeners
        cardAnnouncement.setOnClickListener {
            startActivity(Intent(this, Announcement::class.java))
        }

        cardNotes.setOnClickListener {
            startActivity(Intent(this, ClassNotes::class.java))
        }

        cardDiscussion.setOnClickListener {
            startActivity(Intent(this, ClassDiscussion::class.java))
        }

        cardGroupProject.setOnClickListener {
            startActivity(Intent(this, GroupProjectDiscussion::class.java))
        }

        cardLiveChat.setOnClickListener {
            startActivity(Intent(this, LiveChatRoom::class.java))
        }

        cardAI.setOnClickListener {
            startActivity(Intent(this, AIDoubtSolver::class.java))
        }

        cardAIQuiz.setOnClickListener {
            startActivity(Intent(this, AIQuizGenerator::class.java))
        }

        // ⭐ Drawer Menu
        navView.setNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {

                R.id.nav_logout -> {
                    auth.signOut()
                    startActivity(Intent(this, Login::class.java))
                    finish()
                }

                R.id.nav_profile -> {
                    startActivity(Intent(this, Profile::class.java))
                }

                else -> {
                    Toast.makeText(this, "Menu Clicked", Toast.LENGTH_SHORT).show()
                }
            }

            drawerLayout.closeDrawers()
            true
        }
    }
}