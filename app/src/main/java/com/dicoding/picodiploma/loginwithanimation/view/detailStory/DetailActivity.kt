package com.dicoding.picodiploma.loginwithanimation.view.detailStory

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailBinding
import com.dicoding.picodiploma.loginwithanimation.response.Story
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel : DetailViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra("STORY_ID")
        if (storyId != null) {
            viewModel.getDetailStory(storyId)
            observeDetailStory()
        } else {
            Log.e("DetailActivity", "Story ID is null")
        }

        }

    private fun observeDetailStory(){
        viewModel.detailStory.observe(this) { detailStory ->
            detailStory?.let { populateDetail(it) }

        }
    }

    private fun populateDetail(story: Story) {
        binding.apply {
            tvDetailName.text = story.name
            tvDetailDescription.text = story.description
            Glide.with(this@DetailActivity)
                .load(story.photoUrl)
                .into(ivItemPhoto)
        }
    }
    }
