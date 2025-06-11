package com.example.proy004

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proy004.database.DBHelper

class MainActivity : AppCompatActivity() {
    private lateinit var etCorreo: EditText
    private lateinit var etContrasena: EditText
    private lateinit var btnAcceso: Button
    private lateinit var btnNuevoCliente: Button
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etCorreo = findViewById(R.id.etCorreo)
        etContrasena = findViewById(R.id.etContrasena)
        btnAcceso = findViewById(R.id.btnAcceso)
        btnNuevoCliente = findViewById(R.id.btnNuevoCliente)
        dbHelper = DBHelper(this)

        btnAcceso.setOnClickListener {
            val correo = etCorreo.text.toString()
            val contrasena = etContrasena.text.toString()

            if (correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return@setOnClickListener
            }

            try {
                if (dbHelper.login(correo, contrasena)) {
                    // Obtener el ID del usuario
                    val db = dbHelper.readableDatabase
                    val cursor = db.rawQuery("SELECT ID_Usuario FROM Usuarios WHERE Email = ?", arrayOf(correo))
                    
                    if (cursor.moveToFirst()) {
                        val userId = cursor.getInt(cursor.getColumnIndex("ID_Usuario"))
                        cursor.close()
                        
                        // Pasar el ID del usuario a la siguiente actividad
                        val intent = Intent(this, PantallaInicioSesion::class.java)
                        intent.putExtra("USER_ID", userId)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Error al obtener el ID del usuario", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error en login: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        btnNuevoCliente.setOnClickListener {
            val intent = Intent(this, PantallaRegistro::class.java)
            startActivity(intent)
        }
    }
}
