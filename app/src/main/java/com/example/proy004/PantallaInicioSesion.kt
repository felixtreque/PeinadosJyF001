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
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_sesion)

        btnGestion = findViewById(R.id.btnGestion)
        btnRecursos = findViewById(R.id.btnRecursos)
        btnCerrar = findViewById(R.id.btnCerrar)
        tvNombre = findViewById(R.id.tvNombre)
        dbHelper = DBHelper(this)

        // Obtener el ID del usuario de la sesión (debe ser guardado en MainActivity)
        val userId = intent.getIntExtra("USER_ID", -1)
        if (userId == -1) {
            finish()
            return
        }

        // Obtener el rol del usuario
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT u.ID_Rol, c.Nombre FROM Usuarios u JOIN Clientes c ON u.ID_Usuario = c.ID_Usuario WHERE u.ID_Usuario = ?", arrayOf(userId.toString()))
        
        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(cursor.getColumnIndex("Nombre"))
            val rol = cursor.getInt(cursor.getColumnIndex("ID_Rol"))
            
            tvNombre.text = "¡Hola, $nombre!"
            
            // Cambiar el texto del botón de gestión
            btnGestion.text = "Gestión de Citas"
        }
        cursor.close()

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
