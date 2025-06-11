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
                // Primero obtenemos el ID del usuario
                val db = dbHelper.readableDatabase
                val cursor = db.rawQuery("SELECT ID_Usuario, Contrasena_Hash FROM Usuarios WHERE Email = ?", arrayOf(correo))
                
                if (cursor.moveToFirst()) {
                    val userId = cursor.getLong(cursor.getColumnIndex("ID_Usuario"))
                    val contrasenaHash = cursor.getString(cursor.getColumnIndex("Contrasena_Hash"))
                    cursor.close()
                    
                    // Verificamos si la contraseña coincide con el hash
                    if (contrasena == contrasenaHash) {
                // Obtener el rol del usuario
                val cursorRol = db.rawQuery("SELECT ID_Rol FROM Usuarios WHERE ID_Usuario = ?", arrayOf(userId.toString()))
                if (cursorRol.moveToFirst()) {
                    val rol = cursorRol.getInt(cursorRol.getColumnIndex("ID_Rol"))
                    cursorRol.close()

                    // Redirigir según el rol
                    val intent = when (rol) {
                        1 -> Intent(this, PantallaInicioSesion::class.java) // Administrador
                        2 -> Intent(this, PantallaPrincipalCliente::class.java) // Cliente
                        else -> {
                            Toast.makeText(this, "Rol no válido", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                    }
                    intent.putExtra("USER_ID", userId)
                    intent.putExtra("USER_ROLE", when (rol) {
                        1 -> "Administrador"
                        2 -> "Cliente"
                        else -> ""
                    })
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Error al obtener el rol del usuario", Toast.LENGTH_SHORT).show()
                }
                    } else {
                        Toast.makeText(this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show()
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
