package com.example.hpmemory.views

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.hpmemory.R
import com.example.hpmemory.extensions.Extensions.toast
import com.example.hpmemory.utils.FirebaseUtils.firebaseAuth
import com.example.hpmemory.utils.FirebaseUtils.firebaseUser
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.UserProfileChangeRequest.Builder
import kotlinx.android.synthetic.main.activity_create_user.*


class CreateUserActivity : AppCompatActivity() {
    private lateinit var userEmail: String
    private lateinit var userPassword: String
    private lateinit var userUserName: String
    private lateinit var createAccountInputsArray: Array<EditText>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        createAccountInputsArray = arrayOf(etEmail,etUserName, etPassword, etConfirmPassword)
        btnCreateAccount.setOnClickListener {
            signIn()
        }

        btnSignIn2.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))

            finish()
        }
    }



    override fun onStart() {
        super.onStart()
        val user: FirebaseUser? = firebaseAuth.currentUser
        user?.let {
            startActivity(Intent(this, HomeActivity::class.java))

        }
    }

    private fun notEmpty(): Boolean = etEmail.text.toString().trim().isNotEmpty() &&
            etPassword.text.toString().trim().isNotEmpty() &&
            etConfirmPassword.text.toString().trim().isNotEmpty()

    private fun identicalPassword(): Boolean {
        var identical = false
        if (notEmpty() &&
            etPassword.text.toString().trim() == etConfirmPassword.text.toString().trim()
        ) {
            identical = true
        } else if (!notEmpty()) {
            createAccountInputsArray.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.hint} alanı boş olamaz"
                }
            }
        } else {
            toast("Şifreler Eşleşmiyor !")
        }
        return identical
    }

    private fun signIn() {
        if (identicalPassword()) {

            userEmail = etEmail.text.toString().trim()
            userPassword = etPassword.text.toString().trim()
            userUserName = etUserName.text.toString().trim()


            firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        toast("Kullanıcı oluşturma başarılı")
                        val profileUpdates: UserProfileChangeRequest =
                            Builder().setDisplayName(userUserName).build();
                        firebaseAuth.currentUser?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { taskUser ->
                                if (taskUser.isSuccessful) {

                                    startActivity(Intent(this, HomeActivity::class.java))
                                    finish()
                                } else {
                                    toast("Kayıt Başarısız oldu !")
                                }
                            }

                    } else {
                        toast("Kayıt Başarısız oldu !")
                    }
                }
        }
    }



}