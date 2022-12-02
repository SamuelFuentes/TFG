package com.tfg.gasstations.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tfg.gasstations.MapsActivity
import com.tfg.gasstations.R
import com.tfg.gasstations.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)
        setContentView(binding.root)
        firebaseAuth = Firebase.auth

        //Accede a la aplicación
        binding.bSignIn.setOnClickListener{
            if(binding.eTSignInEmail.text.toString().isEmpty() || binding.eTSignInPassword.text.toString().isEmpty()){
                Toast.makeText(baseContext,"Debe rellenar ambos campos.", Toast.LENGTH_SHORT).show()
            }
            else{
                signIn(binding.eTSignInEmail.text.toString(), binding.eTSignInPassword.text.toString())
            }
        }

        binding.byPass.setOnClickListener(){
            val i = Intent(this, MapsActivity::class.java)
            startActivity(i)
        }

        //Accede a creación de cuentas
        binding.tVSignUp.setOnClickListener(){
            val i = Intent(this, SignUpActivity::class.java)
            startActivity(i)
        }
        //Accede a recuperación de cuentas
        binding.tVForgotPassword.setOnClickListener(){
            val i = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(i)
        }
    }

    //Función para acceder a la aplicación principal mediante FireBase y comprobación de correo verificado
    private fun signIn(email : String, password : String){
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this){
                task ->
            if(task.isSuccessful){
                val user = firebaseAuth.currentUser
                val verify = user?.isEmailVerified
                if(verify==true){
                    val i = Intent(this, MapsActivity::class.java)
                    startActivity(i)
                }
                else{
                    Toast.makeText(baseContext,"Por favor, verifique su correo electrónico.", Toast.LENGTH_SHORT).show()
                }

            }
            else{
                Toast.makeText(baseContext,"Correo electrónico y/o contraseña incorrectos.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}