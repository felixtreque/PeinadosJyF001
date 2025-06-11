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
import android.widget.AdapterView
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.proy004.adapter.AdaptadorSpinnerEmpleados
import com.example.proy004.adapter.AdaptadorSpinnerServicios
import com.example.proy004.adapter.AdaptadorSpinnerHoras
import com.example.proy004.adapter.AdaptadorDiasLaborables
import com.example.proy004.database.DBHelper
import android.database.Cursor
import java.util.Calendar

class PantallaReservarCita : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var spServicios: Spinner
    private lateinit var spEmpleados: Spinner
    private lateinit var spDiasLaborables: Spinner
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
        spDiasLaborables = findViewById(R.id.spDiasLaborables)
        spHoras = findViewById(R.id.spHoras)
        etNotasCita = findViewById(R.id.etNotasCita)
        btnCrearCita = findViewById(R.id.btnCrearCita)
        btnIrInicio = findViewById(R.id.btnIrInicio)

        cargarServicios()
        cargarEmpleados()
        cargarHoras()
        actualizarDiasLaborables()

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
        // Añadir horas desde las 9:00 hasta las 18:30 en intervalos de 30 minutos
        val horas = ArrayList<String>()
        for (hora in 9..18) {
            horas.add(String.format("%02d:00", hora))
            if (hora < 18) {
                horas.add(String.format("%02d:30", hora))
            }
        }
        
        val adaptador = AdaptadorSpinnerHoras(this, horas)
        spHoras.adapter = adaptador
    }

    private fun cargarDiasLaborables() {
        if (spEmpleados.selectedItemId >= 0) {
            val empleadoId = spEmpleados.selectedItemId
            val diasLaborables = AdaptadorDiasLaborables.obtenerDiasLaborables(this, dbHelper, empleadoId)
            val adaptador = AdaptadorDiasLaborables(this, diasLaborables)
            spDiasLaborables.adapter = adaptador
        }
    }

    private fun actualizarDiasLaborables() {
        spEmpleados.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                cargarDiasLaborables()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hacer nada
            }
        }
    }



    private fun convertirHoraAMinutos(hora: String): Int {
        val partes = hora.split(":")
        return partes[0].toInt() * 60 + partes[1].toInt()
    }

    private fun crearCita() {
        val servicioId = spServicios.selectedItemId
        val empleadoId = spEmpleados.selectedItemId
        val notas = etNotasCita.text.toString()

        // Obtener fecha y hora seleccionadas
        val fechaSeleccionada = spDiasLaborables.selectedItem.toString() // La fecha ya viene en formato dd/MM/yyyy
        val horaInicio = spHoras.selectedItem.toString()
        
        // Obtener el día de la semana de la fecha seleccionada
        val partesFechaNumerica = fechaSeleccionada.split("/")
        val calendar = Calendar.getInstance()
        calendar.set(
            partesFechaNumerica[2].toInt(), // año
            partesFechaNumerica[1].toInt() - 1, // mes (0-11)
            partesFechaNumerica[0].toInt() // día
        )
        val diaSeleccionado = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Lunes"
            Calendar.TUESDAY -> "Martes"
            Calendar.WEDNESDAY -> "Miércoles"
            Calendar.THURSDAY -> "Jueves"
            Calendar.FRIDAY -> "Viernes"
            Calendar.SATURDAY -> "Sábado"
            Calendar.SUNDAY -> "Domingo"
            else -> ""
        }

        // Validar que el día sea laboral para el empleado
        val dbHorario = dbHelper.getReadableDatabase()
        val cursorHorario = dbHorario.rawQuery(
            "SELECT COUNT(*) FROM Horarios_Disponibles_Empleado " +
            "WHERE ID_Empleado = ? AND Dia_Semana = ?",
            arrayOf(empleadoId.toString(), diaSeleccionado)
        )
        
        cursorHorario.moveToFirst()
        val diaLaboral = cursorHorario.getInt(0) > 0
        cursorHorario.close()
        
        if (!diaLaboral) {
            Toast.makeText(this, "El día seleccionado no es laboral para este empleado", Toast.LENGTH_LONG).show()
            return
        }

        // Validar que la hora esté dentro del horario del empleado
        val cursorHorario2 = dbHorario.rawQuery(
            "SELECT Hora_Inicio_Bloque, Hora_Fin_Bloque FROM Horarios_Disponibles_Empleado " +
            "WHERE ID_Empleado = ? AND Dia_Semana = ?",
            arrayOf(empleadoId.toString(), diaSeleccionado)
        )
        
        var horaValida = false
        while (cursorHorario2.moveToNext()) {
            val horaInicioBloque = cursorHorario2.getString(0)
            val horaFinBloque = cursorHorario2.getString(1)
            
            // Convertir las horas a minutos para comparar
            val horaInicioMin = convertirHoraAMinutos(horaInicio)
            val horaInicioBloqueMin = convertirHoraAMinutos(horaInicioBloque)
            val horaFinBloqueMin = convertirHoraAMinutos(horaFinBloque)
            
            if (horaInicioMin >= horaInicioBloqueMin && horaInicioMin < horaFinBloqueMin) {
                horaValida = true
                break
            }
        }
        cursorHorario2.close()

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

        // Crear el objeto Date a partir de la fecha seleccionada
        val partesFechaCita = fechaSeleccionada.split("/")
        val anioCita = partesFechaCita[2].toInt()
        val mesCita = partesFechaCita[1].toInt() - 1 // En Calendar, enero es 0
        val calendarCita = Calendar.getInstance()
        calendarCita.clear()
        calendarCita.set(Calendar.YEAR, anioCita)
        calendarCita.set(Calendar.MONTH, mesCita)
        calendarCita.set(Calendar.DAY_OF_MONTH, partesFechaCita[0].toInt())
        val fechaCita = calendarCita.time
        val duracion = duracionMinutos
        
        calendarCita.add(Calendar.MINUTE, duracion)
        val horaFinEstimada = String.format("%02d:%02d", calendarCita.get(Calendar.HOUR_OF_DAY), calendarCita.get(Calendar.MINUTE))
        
        val valores = ContentValues()
        valores.put("ID_Cliente", obtenerIdCliente())
        valores.put("ID_Empleado", empleadoId)
        valores.put("ID_Servicio", servicioId)
        valores.put("Fecha_Cita", fechaCita.toString())
        valores.put("Hora_Inicio", horaInicio)
        valores.put("Hora_Fin_Estimada", horaFinEstimada)
        valores.put("Notas_Cita", notas)

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
