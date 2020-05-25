package com.bdn.collinsceleb.capturefarm.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast

import com.bdn.collinsceleb.capturefarm.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private val DOMAIN_NAME: String = "theagromall.com"
    private val TAG: String = "RegisterActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val username = findViewById<EditText>(R.id.username)

        val password = findViewById<EditText>(R.id.password)

        val confirmPassword = findViewById<EditText>(R.id.confirm_password)

        val registerButton = findViewById<Button>(R.id.register)


        registerButton.setOnClickListener((View.OnClickListener {
            if (!this.isEmpty(username.text.toString()) && !this.isEmpty(password.text.toString()) && !this.isEmpty(confirmPassword.text.toString())) {
                    if (isValidDomain(username.text.toString())) {

                    if (doStringsMatch((password.text.toString()), (confirmPassword.text.toString()))) {
                        registerNewUsername((username.text.toString()), (password.text.toString()))
                    }else {
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
            }
            else {
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

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(username, password).addOnCompleteListener(this) {task ->
            Log.d(TAG, "onComplete: onComplete: " + task.isSuccessful)
            if (task.isSuccessful) {
                Log.d(TAG, "createUserWithEmail:success")
                FirebaseAuth.getInstance().currentUser
                FirebaseAuth.getInstance().signOut()
            }
            else {
                Toast.makeText(
                    this,
                    "Unable to Register",
                    Toast.LENGTH_LONG
                ).show()
            }
            hideDialog()
        }
    }


    private fun isEmpty(string: String) : Boolean {
        return string == ""
    }

    private fun isValidDomain(username: String) : Boolean{
        val domain: String = username.substring(username.indexOf("@") + 1).toLowerCase(Locale.getDefault())
        return domain == DOMAIN_NAME
    }

    private fun doStringsMatch(password: String, confirmPassword: String) : Boolean {
        return password == confirmPassword
    }
    private fun showDialog() {
        val progressBar = findViewById<ProgressBar>(R.id.loading)
        progressBar.visibility = View.VISIBLE
    }

    private fun hideDialog() {
        val progressBar = findViewById<ProgressBar>(R.id.loading)
        if (progressBar.visibility == View.VISIBLE) {
            progressBar.visibility = View.INVISIBLE
        }
    }

    private fun hideSoftKeyboard() {
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }
}
