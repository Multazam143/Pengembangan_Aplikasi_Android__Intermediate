package com.dicoding.picodiploma.loginwithanimation.view.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.StoryAdapter
import com.dicoding.picodiploma.loginwithanimation.addstory.AddStoryActivity
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.pref.dataStore
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.login.LoginActivity
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val userPreference by lazy { UserPreference.getInstance(dataStore) }
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupToolbar()
        checkLoginSession()
        setupFloatingActionButton()
        observeLogout()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    private fun setupFloatingActionButton() {
        val fab: FloatingActionButton = findViewById(R.id.add)
        fab.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
    }

    private fun observeLogout() {
        mainViewModel.isLoggedOut.observe(this) { isLoggedOut ->
            if (isLoggedOut) {
                Toast.makeText(this, "Logout Successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, WelcomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.story_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                lifecycleScope.launch {
                    mainViewModel.logout()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkLoginSession() {
        lifecycleScope.launch {
            val user = userPreference.getSession().first()
            if (user.isLogin) {
                setupUI()
            } else {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun setupUI() {
        val recyclerView = findViewById<RecyclerView>(R.id.rvStories)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mainViewModel.story.observe(this) { stories ->
            recyclerView.adapter = StoryAdapter(stories)
        }
    }
}