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
    private var userId: Long = -1
    private var userRole: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_sesion)

        btnGestion = findViewById(R.id.btnGestion)
        btnRecursos = findViewById(R.id.btnRecursos)
        btnCerrar = findViewById(R.id.btnCerrar)
        tvNombre = findViewById(R.id.tvNombre)
        dbHelper = DBHelper(this)

        // Obtener el ID del usuario y rol de la sesión
        userId = intent.getLongExtra("USER_ID", -1)
        userRole = intent.getStringExtra("USER_ROLE") ?: ""
        
        if (userId == -1L || userRole.isEmpty()) {
            finish()
            return
        }

        // Obtener el nombre del usuario
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT c.Nombre FROM Usuarios u JOIN Clientes c ON u.ID_Usuario = c.ID_Usuario WHERE u.ID_Usuario = ?", arrayOf(userId.toString()))
        
        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(cursor.getColumnIndex("Nombre"))
            tvNombre.text = "¡Hola, $nombre!"
            
            // Cambiar el texto del botón de gestión
            btnGestion.text = "Gestión de Citas"
        }
        cursor.close()

        btnGestion.setOnClickListener {
            val intent = Intent(this, PantallaGestionCitas::class.java)
            intent.putExtra("USER_ID", userId)
            intent.putExtra("USER_ROLE", userRole)
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
