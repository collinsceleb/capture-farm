package com.bdn.collinsceleb.capturefarm.activities

import android.content.Intent
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
import com.google.firebase.auth.FirebaseUser

class SignInActivity : AppCompatActivity() {

    private val TAG: String = "SignInActivity"
    private val  authStateListener:FirebaseAuth.AuthStateListener? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val username = findViewById<EditText>(R.id.username)

        val password = findViewById<EditText>(R.id.password)

        val loginButton = findViewById<Button>(R.id.login)

        val registerButton = findViewById<Button>(R.id.register)

        setUpFirebaseAuth()

        loginButton.setOnClickListener((View.OnClickListener {

            if (!this.isEmpty(username.text.toString()) && !this.isEmpty(password.text.toString())) {
                Log.d(TAG, "Attempting to authenticate")
                showDialog()
                FirebaseAuth.getInstance().signInWithEmailAndPassword((username.text.toString()), (password.text.toString()))
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {

                            val loginIntent = Intent(this, FarmersProfileActivity::class.java)
                            startActivity(loginIntent)
                            hideDialog()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        }

                    }

            }
            else {
                Toast.makeText(
                    this,
                    "You didn't fill in all the fields",
                    Toast.LENGTH_LONG
                ).show()
            }
        }))

        registerButton.setOnClickListener((View.OnClickListener {
            val registerIntent = Intent(this, RegisterActivity::class.java)
            startActivity(registerIntent)
        }))


    }
    private fun isEmpty(string: String): Boolean {
        return string == ""
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

    private fun setUpFirebaseAuth() {
        FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if(user != null){
                // User is signed in
                Log.d(TAG, "Signed in")
//                Toast.makeText(this, "User", Toast.LENGTH_LONG).show();
            }else{
                // User is signed out
                Log.d(TAG, "Signed out")
                Toast.makeText(this, "Null", Toast.LENGTH_LONG).show();
            }
        }
    }
    public override fun onStart() {
        super.onStart()
        if (authStateListener != null) {
            FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
        }
    }

    public override fun onStop() {
        super.onStop()
        if (authStateListener != null)
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
    }

}
