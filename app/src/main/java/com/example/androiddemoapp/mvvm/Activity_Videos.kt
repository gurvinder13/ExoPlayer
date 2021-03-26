package com.example.androiddemoapp.mvvm

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androiddemoapp.R
import java.util.*

class Activity_Videos : AppCompatActivity() {
    private val videosList = ArrayList<ModelVideo>()
    private var adapterVideoList: AdapterVideoList? = null
    private var searchView: SearchView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videos)
        initializeViews()
        checkPermissions()
    }

    private fun initializeViews() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_videos)
        searchView = findViewById(R.id.searchView)
        recyclerView.layoutManager = GridLayoutManager(this, 2) //3 = column count
        adapterVideoList = AdapterVideoList(this, videosList)
        recyclerView.itemAnimator = null
        recyclerView.adapter = adapterVideoList
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filter(newText)
                return true
            }
        })
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 123)
            } else {
                loadVideos()
            }
        } else {
            loadVideos()
        }
    }

    private fun filter(text: String) {
        val filteredList = ArrayList<ModelVideo>()
        for (item in videosList) {
            if (item.title.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item)
            }
        }
        adapterVideoList!!.filterList(filteredList)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadVideos()
            } else {
                Toast.makeText(this, "Permission was not granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadVideos() {
        object : Thread() {
            override fun run() {
                super.run()
                val projection = arrayOf(MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DURATION)
                val sortOrder = MediaStore.Video.Media.DATE_ADDED + " DESC"
                val cursor = application.contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder)
                if (cursor != null) {
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                    val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                    val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val title = cursor.getString(titleColumn)
                        val duration = cursor.getInt(durationColumn)
                        val data = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                        var duration_formatted: String
                        val sec = duration / 1000 % 60
                        val min = duration / (1000 * 60) % 60
                        val hrs = duration / (1000 * 60 * 60)
                        duration_formatted = if (hrs == 0) {
                            min.toString() + ":" + String.format(Locale.UK, "%02d", sec)
                        } else {
                            hrs.toString() + ":" + String.format(Locale.UK, "%02d", min)
                             ":" + String.format(Locale.UK, "%02d", sec)
                        }
                        videosList.add(ModelVideo(id, data, title, duration_formatted))
                        runOnUiThread { adapterVideoList!!.notifyItemInserted(videosList.size - 1) }
                    }
                }
            }
        }.start()
    }
}