package com.example.proy004

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.proy004.adapter.AdaptadorCitas
import com.example.proy004.database.DBHelper
import android.database.Cursor

class PantallaGestionCitas : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var lvCitas: ListView
    private lateinit var btnNuevaCita: Button
    private lateinit var btnIrInicio: Button
    private var citasCursor: Cursor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_citas)

        dbHelper = DBHelper(this)
        lvCitas = findViewById(R.id.lvCitas)
        btnNuevaCita = findViewById(R.id.btnNuevaCita)
        btnIrInicio = findViewById(R.id.btnIrInicio)

        cargarCitas()

        btnNuevaCita.setOnClickListener {
            val intent = Intent(this, PantallaReservarCita::class.java)
            startActivity(intent)
        }

        btnIrInicio.setOnClickListener {
            finish()
        }
    }

    private fun cargarCitas() {
        val db = dbHelper.getReadableDatabase()
        citasCursor = db.rawQuery(
            """
            SELECT 
                c.ID_Cita,
                c.Fecha_Cita,
                c.Hora_Inicio,
                c.Estado_Cita,
                cli.Nombre AS Nombre_Cliente,
                serv.Nombre_Servicio,
                emp.Nombre AS Nombre_Empleado
            FROM Citas c
            JOIN Clientes cli ON c.ID_Cliente = cli.ID_Cliente
            JOIN Servicios serv ON c.ID_Servicio = serv.ID_Servicio
            JOIN Empleados emp ON c.ID_Empleado = emp.ID_Empleado
            ORDER BY c.Fecha_Cita, c.Hora_Inicio
            """.trimIndent(),
            null
        )

        val adaptador = AdaptadorCitas(this, citasCursor!!)
        lvCitas.adapter = adaptador
    }

    override fun onDestroy() {
        super.onDestroy()
        citasCursor?.close()
    }
}
