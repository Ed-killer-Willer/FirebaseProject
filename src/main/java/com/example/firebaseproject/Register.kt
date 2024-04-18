package com.example.firebaseproject

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.FirebaseApp

class Register: AppCompatActivity() {

    //name variables

    private lateinit var txtName: EditText
    private lateinit var txtLastName: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var progressBar: ProgressBar

    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        FirebaseApp.initializeApp(this)


        //relacion de variables
        //creadas        vs         locales
        txtName=findViewById(R.id.txtName)
        txtLastName=findViewById(R.id.txtLastName)
        txtEmail=findViewById(R.id.txtEmail)
        txtPassword=findViewById(R.id.txtPassword)
        progressBar=findViewById(R.id.progressbar)


        //Firebase Data
        database=FirebaseDatabase.getInstance()
        auth=FirebaseAuth.getInstance()

        //referencia para guardar los usuarios

        dbReference = database.reference.child("User")

    }
    //metodo que corresponde al boton de registrar
    fun register (view:View){
        createNewAccount()

    }

    private fun createNewAccount(){
        //validacion de datos
        val name:String = txtName.text.toString()
        val lastName:String = txtLastName.text.toString()
        val email:String = txtEmail.text.toString()
        val password:String = txtPassword.text.toString()

        //No debe ser diferente a vacío

        if (!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(lastName)&&!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password)) {
            //Muestra la barra
            progressBar.visibility = View.VISIBLE

            //Dar de alta al usuario y la contraseña

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Registro exitoso
                        val user: FirebaseUser? = auth.currentUser
                        verifyEmail(user)

                        val userBD = dbReference.child(user?.uid!!)

                        userBD.child("Name").setValue(name)
                        userBD.child("LastName").setValue(lastName)
                        action()
                    }
                }

        }
    }

    private fun action(){
        startActivity(Intent(this,MainActivity::class.java))
    }


    // el ? hace referencia a una llamada segura
    //Enviar correo de registro correctamente
    private fun verifyEmail(user: FirebaseUser?){
        user?.sendEmailVerification()
            ?.addOnCompleteListener(this){
                    task ->
                if (task.isSuccessful){
                    Toast.makeText(this,"Email Enviado",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this,"Error al enviar el correo",Toast.LENGTH_LONG).show()
                }

            }

    }
}