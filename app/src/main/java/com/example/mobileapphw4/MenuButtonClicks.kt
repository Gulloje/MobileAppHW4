package com.example.mobileapphw4

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import com.firebase.ui.auth.AuthUI

class MenuButtonClicks(private val context: Context) {
    fun signOut() {
        AuthUI.getInstance().signOut(context)
            .addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    val intent = Intent(context, LoginPage::class.java)
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Cannot log out at this time.", Toast.LENGTH_SHORT).show()
                }
            }

    }
}