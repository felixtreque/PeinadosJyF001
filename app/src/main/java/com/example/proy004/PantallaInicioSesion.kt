package com.example.proy004

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proy004.database.DBHelper

class PantallaInicioSesion : AppCompatActivity() {
    private lateinit var btnGestion: Button
    private lateinit var btnRecursos: Button
    private lateinit var btnCerrar: Button
    private lateinit var tvNombre: TextView
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_sesion)

        btnGestion = findViewById(R.id.btnGestion)
        btnRecursos = findViewById(R.id.btnRecursos)
        btnCerrar = findViewById(R.id.btnCerrar)
        tvNombre = findViewById(R.id.tvNombre)
        dbHelper = DBHelper(this)

        // TODO: Obtener nombre del usuario desde la sesión
        tvNombre.text = "¡Hola, Usuario!"

        btnGestion.setOnClickListener {
            val intent = Intent(this, PantallaGestionCitas::class.java)
            startActivity(intent)
        }

        btnRecursos.setOnClickListener {
            val intent = Intent(this, PantallaRecursos::class.java)
            startActivity(intent)
        }

        btnCerrar.setOnClickListener {
            finish()
        }
    }
}
