package com.bdn.collinsceleb.capturefarm.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bdn.collinsceleb.capturefarm.R



class FarmersProfileActivity : AppCompatActivity() {
    private val imageCaptureCode = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_farmers_profile)

        val firstName = findViewById<EditText>(R.id.firstname)
        val lastName = findViewById<EditText>(R.id.lastname)
        val phoneNumber = findViewById<EditText>(R.id.phonenumber)
        val emailAddress = findViewById<EditText>(R.id.email)
        val takePicture = findViewById<EditText>(R.id.takephotograph)

        val submitButton = findViewById<Button>(R.id.submit)
        val progressBar = findViewById<ProgressBar>(R.id.loading)

        takePicture.setOnClickListener((View.OnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePictureIntent, imageCaptureCode)

            hideSoftKeyboard()
        }))


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val displayPicture = findViewById<ImageView>(R.id.imagedisplay)
        val takePicture = findViewById<EditText>(R.id.takephotograph)
        if (requestCode == imageCaptureCode) {
            if (resultCode == Activity.RESULT_OK) {
                val bp = data?.extras!!["data"] as Bitmap?
                takePicture.visibility = View.GONE
                displayPicture.setImageBitmap(bp)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
    private fun hideSoftKeyboard() {
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }
}
