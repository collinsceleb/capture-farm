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

    private lateinit var displayPicture: ImageView
    private lateinit var takePicture: TextView
    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var emailAddress: EditText
    private lateinit var farmLocation: TextView
    private lateinit var farmName: EditText
    private lateinit var farmCoordinates: TextView
    private lateinit var submitButton: Button
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_farmers_profile)
        displayPicture = findViewById(R.id.imagedisplay)
        firstName = findViewById(R.id.firstname)
        lastName = findViewById(R.id.lastname)
        phoneNumber = findViewById(R.id.phonenumber)
        emailAddress = findViewById(R.id.email)
        takePicture = findViewById(R.id.takephotograph)
        farmLocation = findViewById(R.id.farmlocation)
        farmName = findViewById(R.id.firstname)
        farmCoordinates = findViewById(R.id.farmcoordinates)
        submitButton = findViewById(R.id.submit)
        progressBar = findViewById(R.id.loading)
        takePicture.setOnClickListener((View.OnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePictureIntent, imageCaptureCode)

            hideSoftKeyboard()
        }))
        farmCoordinates.setOnClickListener((View.OnClickListener {
            val farmCoordinatesIntent = Intent(this, MapActivity::class.java)
            startActivity(farmCoordinatesIntent)

            hideSoftKeyboard()
        }))

        submitButton.setOnClickListener((View.OnClickListener {
            val submitIntent = Intent(this, MainActivity::class.java)
            startActivity(submitIntent)
        }))
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imageCaptureCode) {

            if (resultCode == Activity.RESULT_OK) {
                val bp = data?.extras!!["data"] as Bitmap?
                takePicture.visibility = View.VISIBLE
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
