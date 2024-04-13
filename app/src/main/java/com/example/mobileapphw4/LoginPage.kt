package com.example.mobileapphw4

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class LoginPage : AppCompatActivity() {
    private val TAG = "RegisterActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        //Set the title of the default navbar
        getSupportActionBar()?.setTitle("Some Cool Name");

        val curUser = FirebaseAuth.getInstance().currentUser
        //continue if a user already exists
        if (curUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // Make sure to call finish(), otherwise the user would be able to go back to the RegisterActivity
            finish()
        } else {
             val signInLauncher = registerForActivityResult(
                FirebaseAuthUIActivityResultContract(),
            ) { res ->
                this.onSignInResult(res)
            }

            findViewById<Button>(R.id.btnLogin).setOnClickListener {
                // Choose authentication providers -- make sure enable them on your firebase account first
                val providers = arrayListOf(
                    AuthUI.IdpConfig.EmailBuilder().build()
                )

                // Create  sign-in intent
                val signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setAlwaysShowSignInMethodScreen(false) // use this to true if you have only one provider and really want the see the signin page
                    .setIsSmartLockEnabled(false)
                    .build()

                // Launch sign-in Activity with the sign-in intent above
                signInLauncher.launch(signInIntent)
            }
        }

    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            Log.d(TAG, "onSignInResult: $user")
            if (user?.metadata?.creationTimestamp == user?.metadata?.lastSignInTimestamp && user != null) {
                //This is a New User
                addUserToFirestore(user)
                Toast.makeText(this, "Welcome New User!", Toast.LENGTH_SHORT).show()
            } else {
                //This is a returning user
                Toast.makeText(this, "Welcome Back!", Toast.LENGTH_SHORT).show()
            }
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            if (response == null) {
                Toast.makeText(this, "Canceled the Login", Toast.LENGTH_SHORT).show()
            } else {
                Log.d(TAG, "onSignInResult: ${response.error}")
            }
        }
    }
    private fun addUserToFirestore(user: FirebaseUser) {
        val db = FirebaseFirestore.getInstance()
        val userData = mapOf<String, Any>() //since i dont really care about also writing the uid as a field under the document with uid, just need an object to send to .set
        db.collection("users").document(user.uid).set(userData)
            .addOnFailureListener {
                Log.d(TAG, "Error adding user.")
            }
    }


}