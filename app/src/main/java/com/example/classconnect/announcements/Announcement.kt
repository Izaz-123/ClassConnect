package com.example.classconnect.announcements

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.classconnect.R

class Announcement : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: AnnouncementAdapter
    val announcementList = ArrayList<AnnouncementModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_announcement)


        val toolbar: Toolbar = findViewById(R.id.announcementToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.announcementRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)


        announcementList.add(
            AnnouncementModel("Exam Tomorrow", "Prepare Unit 3 fully.", "10:30 AM")
        )

        announcementList.add(
            AnnouncementModel("Assignment Submitted", "Great work everyone!", "Yesterday")
        )

        adapter = AnnouncementAdapter(announcementList)
        recyclerView.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}