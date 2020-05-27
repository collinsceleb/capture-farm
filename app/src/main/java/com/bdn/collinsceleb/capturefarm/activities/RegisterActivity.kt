package com.bdn.collinsceleb.capturefarm.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bdn.collinsceleb.capturefarm.R
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private val DOMAIN_NAME: String = "theagromall.com"
    private val TAG: String = "RegisterActivity"
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var registerButton: Button
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        username = findViewById(R.id.username)

        password = findViewById(R.id.password)

        confirmPassword = findViewById(R.id.confirm_password)

        registerButton = findViewById(R.id.register)
        progressBar = findViewById(R.id.loading)

        registerButton.setOnClickListener((View.OnClickListener {
            if (!this.isEmpty(username.text.toString()) && !this.isEmpty(password.text.toString()) && !this.isEmpty(
                    confirmPassword.text.toString()
                )
            ) {
                if (isValidDomain(username.text.toString())) {

                    if (doStringsMatch(
                            (password.text.toString()),
                            (confirmPassword.text.toString())
                        )
                    ) {
                        registerNewUsername((username.text.toString()), (password.text.toString()))
                    } else {
                        Toast.makeText(
                            this,
                            "Passwords do not match",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Please Register with Company Email",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(
                    this,
                    "You must fill out all the fields",
                    Toast.LENGTH_LONG
                ).show()
            }
        }))
        hideSoftKeyboard()
    }

    private fun registerNewUsername(username: String, password: String) {
        showDialog()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener(this) { task ->
                Log.d(TAG, "onComplete: onComplete: " + task.isSuccessful)
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    FirebaseAuth.getInstance().currentUser
                    FirebaseAuth.getInstance().signOut()
                } else {
                    Toast.makeText(
                        this,
                        "Unable to Register",
                        Toast.LENGTH_LONG
                    ).show()
                }
                hideDialog()
            }
    }


    private fun isEmpty(string: String): Boolean {
        return string == ""
    }

    private fun isValidDomain(username: String): Boolean {
        val domain: String =
            username.substring(username.indexOf("@") + 1).toLowerCase(Locale.getDefault())
        return domain == DOMAIN_NAME
    }

    private fun doStringsMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    private fun showDialog() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideDialog() {
        if (progressBar.visibility == View.VISIBLE) {
            progressBar.visibility = View.INVISIBLE
        }
    }

    private fun hideSoftKeyboard() {
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }
}
