package com.example.proy004

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proy004.adapter.AdaptadorClientes
import com.example.proy004.adapter.AdaptadorEmpleados
import com.example.proy004.adapter.AdaptadorServicios
import com.example.proy004.database.DBHelper
import android.database.Cursor

class PantallaRecursos : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var lvEmpleados: ListView
    private lateinit var lvServicios: ListView
    private lateinit var lvClientes: ListView
    private lateinit var btnIrInicio: Button

    private var empleadosCursor: Cursor? = null
    private var serviciosCursor: Cursor? = null
    private var clientesCursor: Cursor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recursos)

        dbHelper = DBHelper(this)
        lvEmpleados = findViewById(R.id.lvEmpleados)
        lvServicios = findViewById(R.id.lvServicios)
        lvClientes = findViewById(R.id.lvClientes)
        btnIrInicio = findViewById(R.id.btnIrInicio)

        cargarEmpleados()
        cargarServicios()
        cargarClientes()

        btnIrInicio.setOnClickListener {
            finish()
        }
    }

    private fun cargarEmpleados() {
        val db = dbHelper.getReadableDatabase()
        empleadosCursor = db.rawQuery(
            "SELECT * FROM Empleados ORDER BY Nombre ASC",
            null
        )
        val adaptador = AdaptadorEmpleados(this, empleadosCursor!!)
        lvEmpleados.adapter = adaptador
    }

    private fun cargarServicios() {
        val db = dbHelper.getReadableDatabase()
        serviciosCursor = db.rawQuery(
            "SELECT * FROM Servicios ORDER BY Nombre_Servicio ASC",
            null
        )
        val adaptador = AdaptadorServicios(this, serviciosCursor!!)
        lvServicios.adapter = adaptador
    }

    private fun cargarClientes() {
        val db = dbHelper.getReadableDatabase()
        clientesCursor = db.rawQuery(
            "SELECT * FROM Clientes ORDER BY Nombre ASC",
            null
        )
        val adaptador = AdaptadorClientes(this, clientesCursor!!)
        lvClientes.adapter = adaptador
        
        // Mostrar número total de clientes
        val totalClientes = clientesCursor?.count ?: 0
        Toast.makeText(this, "Total clientes: $totalClientes", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        empleadosCursor?.close()
        serviciosCursor?.close()
        clientesCursor?.close()
    }
}
