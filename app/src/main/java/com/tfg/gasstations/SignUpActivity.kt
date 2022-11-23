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

class SignUpActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val editTextSignUpEmail : TextView = findViewById(R.id.editTextSignUpEmail)
        val editTextSignUpPassword1 : TextView = findViewById(R.id.editTextSignUpPassword1)
        val editTextSignUpPassword2 : TextView = findViewById(R.id.editTextSignUpPassword2)
        val buttonSignUp : Button = findViewById(R.id.buttonSignUp)
        firebaseAuth = Firebase.auth
        //Verificación de campos y crear cuenta
        buttonSignUp.setOnClickListener(){
            if(editTextSignUpEmail.text.toString().isEmpty()){
                Toast.makeText(baseContext,"Introduzca su correo electrónico", Toast.LENGTH_SHORT).show()
                editTextSignUpEmail.requestFocus()
            }
            else if(editTextSignUpPassword1.text.toString().isEmpty()){
                Toast.makeText(baseContext,"Introduzca su contraseña", Toast.LENGTH_SHORT).show()
                editTextSignUpPassword1.requestFocus()
            }
            else if(editTextSignUpPassword2.text.toString().isEmpty()) {
                Toast.makeText(baseContext, "Confirme su contraseña", Toast.LENGTH_SHORT).show()
                editTextSignUpPassword2.requestFocus()
            }
            else if(editTextSignUpPassword1.text.toString() != editTextSignUpPassword2.text.toString() ){
                Toast.makeText(baseContext,"Ambas contraseñas debe de ser iguales.", Toast.LENGTH_SHORT).show()
                editTextSignUpPassword2.requestFocus()
            }
            else if(editTextSignUpPassword1.text.toString().length<6){
                Toast.makeText(baseContext,"La contraseña debe de ser de 6 o más caracteres.", Toast.LENGTH_SHORT).show()
                editTextSignUpPassword1.requestFocus()
            }
            else{
                signUp(editTextSignUpEmail.text.toString(), editTextSignUpPassword1.text.toString())
            }
        }
    }
    //Función para crear cuentas
    private fun signUp(email: String, password : String){
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this){
                task ->
            if(task.isSuccessful){
                verifyEmail()
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
            }
            else{
                Toast.makeText(baseContext,"Se ha producido un error al crear la cuenta.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    //Función para mandar email de verificación
    private fun verifyEmail(){
        val user = firebaseAuth.currentUser!!
        user.sendEmailVerification().addOnCompleteListener(this){
                task ->
            if(task.isSuccessful){
                Toast.makeText(baseContext,"Se ha enviado un correo de verificación a su email.", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(baseContext,"Error: compruebe su conexión a internet.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}