package com.example.proy004

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.proy004.adapter.AdaptadorCitasCliente
import com.example.proy004.database.DBHelper
import android.database.Cursor

class PantallaPrincipalCliente : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var lvCitas: ListView
    private lateinit var btnIrInicio: Button
    private var citasCursor: Cursor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_citas) // Usamos el mismo layout que gestión de citas

        dbHelper = DBHelper(this)
        lvCitas = findViewById(R.id.lvCitas)
        btnIrInicio = findViewById(R.id.btnIrInicio)
        btnIrInicio.text = "Cerrar Sesión"

        cargarCitas()

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
            WHERE cli.ID_Cliente = ?
            ORDER BY c.Fecha_Cita, c.Hora_Inicio
            """.trimIndent(),
            arrayOf(obtenerIdCliente().toString())
        )

        val adaptador = AdaptadorCitasCliente(this, citasCursor!!)
        lvCitas.adapter = adaptador
    }

    private fun obtenerIdCliente(): Long {
        // TODO: Implementar obtención del ID del cliente desde la sesión
        return 1 // Temporalmente usando ID 1
    }

    override fun onDestroy() {
        super.onDestroy()
        citasCursor?.close()
    }
}
