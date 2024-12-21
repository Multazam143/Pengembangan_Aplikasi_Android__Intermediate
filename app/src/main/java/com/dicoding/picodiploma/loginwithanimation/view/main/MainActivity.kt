package com.dicoding.picodiploma.loginwithanimation.view.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
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
import com.dicoding.picodiploma.loginwithanimation.data.LoadingStateAdapter
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.pref.dataStore
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMainBinding
import com.dicoding.picodiploma.loginwithanimation.maps.MapsActivity
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.login.LoginActivity
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val userPreference by lazy { UserPreference.getInstance(dataStore) }
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        checkLoginSession()

        val fab: FloatingActionButton = findViewById(R.id.add)
        fab.setOnClickListener{
            val intent = Intent (this, AddStoryActivity::class.java )
            startActivity(intent)
        }

        mainViewModel.isLoggedOut.observe(this){ isLoggedOut ->
            if (isLoggedOut) {
                Toast.makeText(this, " Logout Successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent (this, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

        getStories()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.story_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.logout -> {
                lifecycleScope.launch {
                    mainViewModel.logout()
                }
                true
            }
            R.id.map -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun getStories(){
        val adapter = StoryAdapter()
        binding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        mainViewModel.stories.observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }

    private fun checkLoginSession(){
        lifecycleScope.launch {
            val user = userPreference.getSession().first()
            val isUserLoggedIn = user.isLogin

            if (isUserLoggedIn){
                setupUI()
            } else{
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
        }

    }

    private fun setupUI(){
        val recyclerView = findViewById<RecyclerView>(R.id.rvStories)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = StoryAdapter()
        recyclerView.adapter = adapter

        mainViewModel.stories.observe(this) { pagingData ->
            adapter.submitData(lifecycle, pagingData)
        }
    }
}