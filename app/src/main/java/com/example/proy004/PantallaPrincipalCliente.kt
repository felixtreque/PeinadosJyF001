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
        setContentView(R.layout.activity_gestion_citas)

        dbHelper = DBHelper(this)
        lvCitas = findViewById(R.id.lvCitas)
        btnIrInicio = findViewById(R.id.btnIrInicio)
        val btnNuevaCita = findViewById<Button>(R.id.btnNuevaCita)
        btnIrInicio.text = "Cerrar Sesión"

        cargarCitas()

        btnIrInicio.setOnClickListener {
            finish()
        }

        btnNuevaCita.setOnClickListener {
            val intent = Intent(this, PantallaReservarCita::class.java)
            intent.putExtra("USER_ID", obtenerIdCliente())
            intent.putExtra("USER_ROLE", "Cliente")
            startActivityForResult(intent, 1)
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
                cli.Apellido1 AS Apellido1_Cliente,
                cli.Apellido2 AS Apellido2_Cliente,
                serv.Nombre_Servicio,
                emp.Nombre AS Nombre_Empleado,
                emp.Apellido1 AS Apellido1_Empleado,
                emp.Apellido2 AS Apellido2_Empleado
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

    private fun actualizarCitas() {
        citasCursor?.close()
        cargarCitas()
    }

    private fun obtenerIdCliente(): Long {
        // Obtener el ID del usuario del intent
        return intent.getLongExtra("USER_ID", -1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            actualizarCitas()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        citasCursor?.close()
    }
}
