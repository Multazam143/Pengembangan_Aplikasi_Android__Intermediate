package com.dicoding.picodiploma.loginwithanimation.addstory

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.dicoding.picodiploma.loginwithanimation.utils.Result
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityAddStoryBinding
import com.dicoding.picodiploma.loginwithanimation.utils.getImageUri
import com.dicoding.picodiploma.loginwithanimation.utils.reduceFileImage
import com.dicoding.picodiploma.loginwithanimation.utils.uriToFile
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddStoryBinding

    private var currentImageUri: Uri? = null

    private val viewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListener()

    }

    private fun setupListener (){

        binding.btnCamera.setOnClickListener {
            currentImageUri = getImageUri(this)
            cameraLaunch.launch(currentImageUri!!)
        }
        binding.btnGallery.setOnClickListener{
            galleryLaunch.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.buttonUpload.setOnClickListener{
            if (!binding.edAddDescription.text.isNullOrBlank() && currentImageUri  != null){
                currentImageUri?.let { uri: Uri ->
                    val imageFile = uriToFile(uri, this).reduceFileImage()
                    val desc = binding.edAddDescription.text.toString()

                    true.showLoading()

                    val requestBody = desc.toRequestBody("text/pain".toMediaType())
                    val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())

                    val multipartBody = MultipartBody.Part.createFormData("photo", imageFile.name, requestImageFile)

                    viewModel.addStory(multipartBody, requestBody)
                        .observe(this) {
                            response ->
                            when(response) {
                                is Result.Error -> showToast("Foto gagal di Upload Karena ${response.error}")

                                Result.Loading -> true.showLoading()

                                is Result.Success -> {
                                    showToast("Berhasil Mengupload Story")
                                    runBlocking {
                                        delay(500)
                                    }

                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                }
                            }
                        }
                }
            } else {
                showToast("Isi Foto terlebih dahulu")
            }
        }

    }



    private val galleryLaunch = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private val cameraLaunch =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                showImage()
            } else {
                currentImageUri = null
            }
        }


    private fun showImage(){
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivPreview.setImageURI(it)
        }
    }

    private fun Boolean.showLoading() {
        val animation = if (this) {
            AlphaAnimation(0f, 1f).apply {
                duration = 300
            }
        } else {
            AlphaAnimation(1f, 0f).apply {
                duration = 300
            }
        }
        binding.progressBar.startAnimation(animation)
        binding.progressBar.visibility = if (this) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}