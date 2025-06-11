package com.example.proy004

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proy004.database.DBHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PantallaRegistro : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var etNombre: EditText
    private lateinit var etApellido1: EditText
    private lateinit var etApellido2: EditText
    private lateinit var etEmail: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etFechaNacimiento: EditText
    private lateinit var etDni: EditText
    private lateinit var etPreferencias: EditText
    private lateinit var etAlergias: EditText
    private lateinit var etNotas: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var btnIrInicio: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_cliente)

        dbHelper = DBHelper(this)
        etNombre = findViewById(R.id.etNombre)
        etApellido1 = findViewById(R.id.etApellido1)
        etApellido2 = findViewById(R.id.etApellido2)
        etEmail = findViewById(R.id.etEmail)
        etTelefono = findViewById(R.id.etTelefono)
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento)
        etDni = findViewById(R.id.etDni)
        etPreferencias = findViewById(R.id.etPreferencias)
        etAlergias = findViewById(R.id.etAlergias)
        etNotas = findViewById(R.id.etNotas)
        etPassword = findViewById(R.id.etPassword)
        btnRegistrar = findViewById(R.id.btnRegistrar)
        btnIrInicio = findViewById(R.id.btnIrInicio)

        btnRegistrar.setOnClickListener {
            registrarCliente()
        }

        btnIrInicio.setOnClickListener {
            finish()
        }
    }

    private fun registrarCliente() {
        val nombre = etNombre.text.toString()
        val apellido1 = etApellido1.text.toString()
        val apellido2 = etApellido2.text.toString()
        val email = etEmail.text.toString()
        val telefono = etTelefono.text.toString()
        val fechaNacimiento = etFechaNacimiento.text.toString()
        val dni = etDni.text.toString()
        val preferencias = etPreferencias.text.toString()
        val alergias = etAlergias.text.toString()
        val notas = etNotas.text.toString()
        val password = etPassword.text.toString()

        if (nombre.isEmpty() || email.isEmpty() || dni.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, complete los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val db = dbHelper.getWritableDatabase()
        
        // Insertar usuario
        val usuarioValues = ContentValues().apply {
            put("Email", email)
            put("Contrasena_Hash", password) // En una aplicación real, usaríamos un hash de la contraseña
            put("DNI_NIE_Pasaporte", dni)
            put("ID_Rol", 2) // 2 es el ID del rol Cliente
        }
        val usuarioId = db.insert("Usuarios", null, usuarioValues)

        if (usuarioId == -1L) {
            Toast.makeText(this, "Error al crear el usuario", Toast.LENGTH_SHORT).show()
            return
        }

        // Insertar cliente
        val clienteValues = ContentValues().apply {
            put("Nombre", nombre)
            put("Apellido1", apellido1)
            put("Apellido2", apellido2)
            put("Email", email)
            put("Telefono", telefono)
            put("Fecha_Nacimiento", fechaNacimiento)
            put("ID_Usuario", usuarioId)
            put("Preferencias_Servicio", preferencias)
            put("Alergias", alergias)
            put("Notas", notas)
        }
        val clienteId = db.insert("Clientes", null, clienteValues)

        if (clienteId == -1L) {
            Toast.makeText(this, "Error al crear el cliente", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "Cliente registrado exitosamente", Toast.LENGTH_SHORT).show()
        finish()
    }
}
