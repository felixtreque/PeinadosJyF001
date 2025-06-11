package com.example.proy004

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PantallaInicioSesion : AppCompatActivity() {
    private lateinit var btnGestionCitas: Button
    private lateinit var btnRecursos: Button
    private lateinit var btnCerrarSesion: Button
    private lateinit var tvNombreUsuario: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_sesion)

        btnGestionCitas = findViewById(R.id.btnGestionCitas)
        btnRecursos = findViewById(R.id.btnRecursos)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)
        tvNombreUsuario = findViewById(R.id.tvNombreUsuario)

        // TODO: Obtener nombre del usuario desde la sesión
        tvNombreUsuario.text = "¡Hola, Usuario!"

        btnGestionCitas.setOnClickListener {
            // TODO: Navegar a la pantalla de gestión de citas
        }

        btnRecursos.setOnClickListener {
            // TODO: Navegar a la pantalla de recursos
        }

        btnCerrarSesion.setOnClickListener {
            // TODO: Implementar logout
            finish()
        }
    }
}
