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

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSignIn : Button = findViewById(R.id.buttonSignIn)
        val editTextEmail : TextView = findViewById(R.id.editTextSignInEmail)
        val editTextPassword : TextView = findViewById(R.id.editTextSignInPassword)
        val textViewSignUp : TextView = findViewById(R.id.textViewSignUp)
        val textViewForgotPassword : TextView = findViewById(R.id.textViewForgotPassword)
        firebaseAuth = Firebase.auth

        //Accede a la aplicación
        buttonSignIn.setOnClickListener(){
            if(editTextEmail.text.toString().isEmpty() || editTextPassword.text.toString().isEmpty()){
                Toast.makeText(baseContext,"Debe rellenar ambos campos.", Toast.LENGTH_SHORT).show()
            }
            else{
                signIn(editTextEmail.text.toString(), editTextPassword.text.toString())
            }
        }
        //Accede a creación de cuentas
        textViewSignUp.setOnClickListener(){
            val i = Intent(this, SignUpActivity::class.java)
            startActivity(i)
        }
        //Accede a recuperación de cuentas
        textViewForgotPassword.setOnClickListener(){
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
                    val i = Intent(this, GasStationsActivity::class.java)
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