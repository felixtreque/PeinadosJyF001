package com.example.proy004

import android.content.Intent
import android.os.Bundle
import android.provider.BaseColumns
import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proy004.adapter.AdaptadorSpinnerEmpleados
import com.example.proy004.adapter.AdaptadorSpinnerServicios
import com.example.proy004.database.DBHelper
import android.widget.DatePicker
import android.widget.TimePicker
import android.database.Cursor
import java.util.Calendar

class PantallaReservarCita : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var spServicios: Spinner
    private lateinit var spEmpleados: Spinner
    private lateinit var dpFecha: DatePicker
    private lateinit var tpHora: TimePicker
    private lateinit var etNotasCita: EditText
    private lateinit var btnCrearCita: Button
    private lateinit var btnIrInicio: Button

    private var serviciosCursor: Cursor? = null
    private var empleadosCursor: Cursor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservar_cita)

        dbHelper = DBHelper(this)
        spServicios = findViewById(R.id.spServicios)
        spEmpleados = findViewById(R.id.spEmpleados)
        dpFecha = findViewById(R.id.dpFecha)
        tpHora = findViewById(R.id.tpHora)
        etNotasCita = findViewById(R.id.etNotasCita)
        btnCrearCita = findViewById(R.id.btnCrearCita)
        btnIrInicio = findViewById(R.id.btnIrInicio)

        cargarServicios()
        cargarEmpleados()

        btnCrearCita.setOnClickListener {
            crearCita()
        }

        btnIrInicio.setOnClickListener {
            finish()
        }
    }

    private fun cargarServicios() {
        val db = dbHelper.getReadableDatabase()
        serviciosCursor = db.rawQuery(
            "SELECT * FROM Servicios ORDER BY Nombre_Servicio ASC",
            null
        )
        val adaptador = AdaptadorSpinnerServicios(this, serviciosCursor!!)
        spServicios.adapter = adaptador
    }

    private fun cargarEmpleados() {
        val db = dbHelper.getReadableDatabase()
        empleadosCursor = db.rawQuery(
            "SELECT * FROM Empleados ORDER BY Nombre ASC",
            null
        )
        val adaptador = AdaptadorSpinnerEmpleados(this, empleadosCursor!!)
        spEmpleados.adapter = adaptador
    }

    private fun crearCita() {
        val servicioId = spServicios.selectedItemId
        val empleadoId = spEmpleados.selectedItemId
        val notas = etNotasCita.text.toString()

        // Obtener fecha y hora seleccionadas
        val calendar = Calendar.getInstance()
        calendar.set(
            dpFecha.year,
            dpFecha.month,
            dpFecha.dayOfMonth,
            tpHora.hour,
            tpHora.minute
        )

        val fechaCita = calendar.time
        val horaInicio = String.format("%02d:%02d", tpHora.hour, tpHora.minute)

        // Calcular hora fin estimada
        val db = dbHelper.getReadableDatabase()
        val cursor = db.rawQuery(
            "SELECT Duracion_Estimada_Minutos FROM Servicios WHERE ID_Servicio = ?",
            arrayOf(servicioId.toString())
        )
        cursor.moveToFirst()
        val duracion = cursor.getInt(0)
        cursor.close()

        calendar.add(Calendar.MINUTE, duracion)
        val horaFinEstimada = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))

        // Insertar cita
        val valores = ContentValues().apply {
            put("ID_Cliente", obtenerIdCliente()) // Asumiendo que el ID del cliente está en la sesión
            put("ID_Empleado", empleadoId)
            put("ID_Servicio", servicioId)
            put("Fecha_Cita", fechaCita.toString())
            put("Hora_Inicio", horaInicio)
            put("Hora_Fin_Estimada", horaFinEstimada)
            put("Notas_Cita", notas)
        }

        val dbw = dbHelper.getWritableDatabase()
        val resultado = dbw.insert("Citas", null, valores)

        if (resultado == -1L) {
            Toast.makeText(this, "Error al crear la cita", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Cita creada exitosamente", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun obtenerIdCliente(): Long {
        // TODO: Implementar obtención del ID del cliente desde la sesión
        return 1 // Temporalmente usando ID 1
    }

    override fun onDestroy() {
        super.onDestroy()
        serviciosCursor?.close()
        empleadosCursor?.close()
    }
}
