package com.tfg.gasstations.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tfg.gasstations.R
import com.tfg.gasstations.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_sign_up)
        setContentView(binding.root)

        firebaseAuth = Firebase.auth
        //Verificación de campos y crear cuenta
        binding.bSignUp.setOnClickListener(){
            if(binding.eTSignUpEmail.text.toString().isEmpty()){
                Toast.makeText(baseContext,"Introduzca su correo electrónico", Toast.LENGTH_SHORT).show()
                binding.eTSignUpEmail.requestFocus()
            }
            else if(binding.eTSignUpPass1.text.toString().isEmpty()){
                Toast.makeText(baseContext,"Introduzca su contraseña", Toast.LENGTH_SHORT).show()
                binding.eTSignUpPass1.requestFocus()
            }
            else if(binding.eTSignUpPass2.text.toString().isEmpty()) {
                Toast.makeText(baseContext, "Confirme su contraseña", Toast.LENGTH_SHORT).show()
                binding.eTSignUpPass2.requestFocus()
            }
            else if(binding.eTSignUpPass1.text.toString() != binding.eTSignUpPass2.text.toString() ){
                Toast.makeText(baseContext,"Ambas contraseñas debe de ser iguales.", Toast.LENGTH_SHORT).show()
                binding.eTSignUpPass2.requestFocus()
            }
            else if(binding.eTSignUpPass1.text.toString().length<6){
                Toast.makeText(baseContext,"La contraseña debe de ser de 6 o más caracteres.", Toast.LENGTH_SHORT).show()
                binding.eTSignUpPass1.requestFocus()
            }
            else{
                signUp(binding.eTSignUpEmail.text.toString(), binding.eTSignUpPass1.text.toString())
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