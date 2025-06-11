package com.example.proy004

import android.content.Intent
import android.os.Bundle
import android.provider.BaseColumns
import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proy004.adapter.AdaptadorSpinnerEmpleados
import com.example.proy004.adapter.AdaptadorSpinnerServicios
import com.example.proy004.adapter.AdaptadorSpinnerHoras
import com.example.proy004.database.DBHelper
import android.database.Cursor
import java.util.Calendar

class PantallaReservarCita : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var spServicios: Spinner
    private lateinit var spEmpleados: Spinner
    private lateinit var dpFecha: DatePicker
    private lateinit var spHoras: Spinner
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
        spHoras = findViewById(R.id.spHoras)
        etNotasCita = findViewById(R.id.etNotasCita)
        btnCrearCita = findViewById(R.id.btnCrearCita)
        btnIrInicio = findViewById(R.id.btnIrInicio)

        cargarServicios()
        cargarEmpleados()
        cargarHoras()

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

    private fun cargarHoras() {
        val horas = ArrayList<String>()
        
        // Añadir horas desde las 9:00 hasta las 18:30 en intervalos de 30 minutos
        for (hora in 9..18) {
            horas.add(String.format("%02d:00", hora))
            if (hora < 18) { // No añadimos 18:30 ya que es el límite
                horas.add(String.format("%02d:30", hora))
            }
        }
        
        val adaptador = AdaptadorSpinnerHoras(this, horas)
        spHoras.adapter = adaptador
    }

    private fun getDiaSemana(diaMes: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, diaMes)
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Lunes"
            Calendar.TUESDAY -> "Martes"
            Calendar.WEDNESDAY -> "Miércoles"
            Calendar.THURSDAY -> "Jueves"
            Calendar.FRIDAY -> "Viernes"
            Calendar.SATURDAY -> "Sábado"
            Calendar.SUNDAY -> "Domingo"
            else -> ""
        }
    }

    private fun crearCita() {
        val servicioId = spServicios.selectedItemId
        val empleadoId = spEmpleados.selectedItemId
        val notas = etNotasCita.text.toString()

        // Obtener fecha y hora seleccionadas
        val fecha = dpFecha.year.toString() + "-" + 
                    String.format("%02d", dpFecha.month + 1) + "-" +
                    String.format("%02d", dpFecha.dayOfMonth)
        val horaInicio = spHoras.selectedItem.toString()

        // Validar que la hora esté dentro del horario del empleado
        val dbHorario = dbHelper.getReadableDatabase()
        val cursorHorario = dbHorario.rawQuery(
            "SELECT Hora_Inicio_Bloque, Hora_Fin_Bloque FROM Horarios_Disponibles_Empleado " +
            "WHERE ID_Empleado = ? AND Dia_Semana = ?",
            arrayOf(empleadoId.toString(), getDiaSemana(dpFecha.dayOfMonth))
        )
        
        var horaValida = false
        while (cursorHorario.moveToNext()) {
            val horaInicioBloque = cursorHorario.getString(0)
            val horaFinBloque = cursorHorario.getString(1)
            
            if (horaInicio >= horaInicioBloque && horaInicio < horaFinBloque) {
                horaValida = true
                break
            }
        }
        cursorHorario.close()

        if (!horaValida) {
            Toast.makeText(this, "La hora seleccionada no está dentro del horario del empleado", Toast.LENGTH_LONG).show()
            return
        }

        val dbServicio = dbHelper.getReadableDatabase()
        val cursorServicio = dbServicio.rawQuery(
            "SELECT Duracion_Estimada_Minutos FROM Servicios WHERE ID_Servicio = ?",
            arrayOf(servicioId.toString())
        )
        
        var duracionMinutos = 0
        if (cursorServicio.moveToFirst()) {
            duracionMinutos = cursorServicio.getInt(0)
        }
        cursorServicio.close()

        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.set(Calendar.YEAR, dpFecha.year)
        calendar.set(Calendar.MONTH, dpFecha.month)
        calendar.set(Calendar.DAY_OF_MONTH, dpFecha.dayOfMonth)
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(horaInicio.split(":")[0]))
        calendar.set(Calendar.MINUTE, Integer.parseInt(horaInicio.split(":")[1]))

        val fechaCita = calendar.time
        val duracion = duracionMinutos

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
