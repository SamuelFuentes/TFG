package com.tfg.gasstations

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val editTextEmailForgotPasswordRecover : TextView = findViewById(R.id.editTextEmailForgotPasswordRecover)
        val buttonForgotPasswordRecover : Button = findViewById(R.id.buttonForgotPasswordRecover)
        firebaseAuth = Firebase.auth

        //Mandar correo de recuperación
        buttonForgotPasswordRecover.setOnClickListener(){
            recoverPassword(editTextEmailForgotPasswordRecover.text.toString())
        }
    }
    //Función para solicitar correo de recuperación
    private fun recoverPassword(email : String){
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(){
                task ->
            if(task.isSuccessful){
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
                Toast.makeText(baseContext,"Se ha enviado un correo electrónico a su cuenta de email.",
                    Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(baseContext,"Algo ha salido mal.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}