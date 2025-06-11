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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.proy004.database.DBHelper
import android.database.Cursor
import java.util.Calendar
import android.widget.TextView

class PantallaReservarCita : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private var clienteId: Long = -1
    private lateinit var spServicios: Spinner
    private lateinit var spEmpleados: Spinner
    private lateinit var spDiasLaborables: Spinner
    private lateinit var spHoras: Spinner
    private lateinit var etNotasCita: EditText
    private lateinit var btnCrearCita: Button
    private lateinit var btnIrInicio: Button
    private lateinit var tvIdUsuario: TextView
    private lateinit var tvFechaSeleccionada: TextView
    private var userId: Long = -1
    private var empleadoId: Long = -1
    private var userRole: String = ""
    private var serviciosCursor: Cursor? = null
    private var empleadosCursor: Cursor? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservar_cita)
        
        // Inicializar vistas
        spServicios = findViewById(R.id.spServicios)
        spEmpleados = findViewById(R.id.spEmpleados)
        spDiasLaborables = findViewById(R.id.spDiasLaborables)
        spHoras = findViewById(R.id.spHoras)
        etNotasCita = findViewById(R.id.etNotasCita)
        btnCrearCita = findViewById(R.id.btnCrearCita)
        btnIrInicio = findViewById(R.id.btnIrInicio)
        tvIdUsuario = findViewById(R.id.tvIdUsuario)
        tvFechaSeleccionada = findViewById(R.id.tvFechaSeleccionada)

        // Obtener datos del intent
        userId = intent.getLongExtra("USER_ID", -1)
        userRole = intent.getStringExtra("USER_ROLE") ?: ""
        tvIdUsuario.text = "ID Usuario: $userId\nRol: $userRole"

        // Validar usuario
        if (userId == -1L && userRole.isEmpty()) {
            Toast.makeText(this, "Error: Usuario no válido", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Inicializar base de datos
        dbHelper = DBHelper(this)

        // Mostrar mensaje adicional para administradores
        if (userRole == "Administrador") {
            tvIdUsuario.append(" (Administrador)")
        }

        // Configurar el ID del cliente según el rol
        when (userRole) {
            "Cliente" -> {
                // Para clientes, buscar el ID_Cliente asociado al usuario
                val cursorCliente = dbHelper.getReadableDatabase().rawQuery(
                    "SELECT ID_Cliente FROM Clientes WHERE ID_Usuario = ?",
                    arrayOf(userId.toString())
                )
                if (cursorCliente.moveToFirst()) {
                    clienteId = cursorCliente.getLong(0)
                }
                cursorCliente.close()
            }
            "Administrador" -> {
                // Para administradores, usar el ID del empleado seleccionado
                clienteId = -1L // Indica que es administrador
            }
            "Empleado" -> {
                // Para empleados, buscar el ID_Empleado asociado al usuario
                val cursorEmpleado = dbHelper.getReadableDatabase().rawQuery(
                    "SELECT ID_Empleado FROM Empleados WHERE ID_Usuario = ?",
                    arrayOf(userId.toString())
                )
                if (cursorEmpleado.moveToFirst()) {
                    empleadoId = cursorEmpleado.getLong(0)
                }
                cursorEmpleado.close()
                clienteId = -1L // Indica que es empleado
            }
        }

        // Configurar los spinners
        spServicios.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                cargarEmpleados()
                cargarHoras()
                cargarDiasLaborables()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spEmpleados.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                empleadoId = id
                actualizarDiasLaborables()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                empleadoId = -1
            }
        }

        spDiasLaborables.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val fechaSeleccionada = parent?.selectedItem.toString()
                tvFechaSeleccionada.text = "Fecha seleccionada: $fechaSeleccionada"
                cargarHoras()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                tvFechaSeleccionada.text = ""
            }
        }

        // Configurar botones
        btnCrearCita.setOnClickListener { crearCita() }
        btnIrInicio.setOnClickListener { finish() }

        // Inicializar datos
        cargarServicios()
        spEmpleados.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, obtenerEmpleados())
    }

    private fun obtenerEmpleados(): List<String> {
        val db = dbHelper.getReadableDatabase()
        val cursor = db.rawQuery("SELECT Nombre, Apellido1, Apellido2 FROM Empleados ORDER BY Nombre", null)
        val empleados = mutableListOf<String>()
        while (cursor.moveToNext()) {
            val nombre = cursor.getString(0)
            val apellido1 = cursor.getString(1)
            val apellido2 = cursor.getString(2)
            empleados.add("$nombre $apellido1 ${if (!apellido2.isNullOrBlank()) apellido2 else ""}")
        }
        cursor.close()
        return empleados
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
        
        if (empleadoId == -1L || spDiasLaborables.selectedItem == null) {
            // Si no hay empleado seleccionado o no hay día, mostrar mensaje
            Toast.makeText(this, "Por favor, seleccione un empleado y un día laboral", Toast.LENGTH_SHORT).show()
            return
        }

        // Extraer el día de la semana en español directamente del texto del spinner
        val fechaSeleccionada = spDiasLaborables.selectedItem.toString()
        val partes = fechaSeleccionada.split("(")
        if (partes.size != 2) {
            Toast.makeText(this, "Formato de fecha incorrecto", Toast.LENGTH_SHORT).show()
            return
        }
        
        val diaSemana = partes[1].replace(")", "").trim()
        
        // Log de depuración
        println("DEBUG: Día de la semana extraído: $diaSemana")
        
        try {
            val db = dbHelper.getReadableDatabase()
            val cursorHorario = db.rawQuery(
                "SELECT Hora_Inicio_Bloque, Hora_Fin_Bloque FROM Horarios_Disponibles_Empleado " +
                "WHERE ID_Empleado = ? AND Dia_Semana = ? ORDER BY Hora_Inicio_Bloque",
                arrayOf(empleadoId.toString(), diaSemana)
            )
            
            if (cursorHorario.count == 0) {
                // Si no hay horarios disponibles para ese día
                Toast.makeText(this, "No hay horarios disponibles para este día y empleado", Toast.LENGTH_SHORT).show()
                spHoras.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, horas)
                cursorHorario.close()
                return
            }
            
            try {
                while (cursorHorario.moveToNext()) {
                    val horaInicio = cursorHorario.getString(0)
                    val horaFin = cursorHorario.getString(1)
                    
                    // Log de depuración
                    println("DEBUG: Horario encontrado - Inicio: $horaInicio, Fin: $horaFin")
                    
                    val horaInicioMin = convertirHoraAMinutos(horaInicio)
                    val horaFinMin = convertirHoraAMinutos(horaFin)
                    
                    var horaActualMin = horaInicioMin
                    while (horaActualMin < horaFinMin) {
                        val horaActual = String.format("%02d:%02d", horaActualMin / 60, horaActualMin % 60)
                        horas.add(horaActual)
                        horaActualMin += 30 // Incrementar en 30 minutos
                    }
                }
                
                // Log de depuración
                println("DEBUG: Horas generadas: ${horas.size}")
                
                cursorHorario.close()
                spHoras.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, horas)
                
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error al procesar los horarios: ${e.message}", Toast.LENGTH_LONG).show()
                cursorHorario.close()
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al cargar los horarios: ${e.message}", Toast.LENGTH_LONG).show()
        }
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
        if (empleadoId != -1L) {
            cargarDiasLaborables()
        }
    }

    private fun convertirHoraAMinutos(hora: String): Int {
        try {
            val partes = hora.split(":")
            if (partes.size != 2) {
                throw IllegalArgumentException("Formato de hora inválido")
            }
            return partes[0].toInt() * 60 + partes[1].toInt()
        } catch (e: Exception) {
            throw IllegalArgumentException("Formato de hora inválido: $hora", e)
        }
    }

    private fun crearCita() {
        // Validar que todos los campos requeridos estén seleccionados
        if (spServicios.selectedItemId == -1L ||
            spEmpleados.selectedItemId == -1L ||
            spDiasLaborables.selectedItemId == -1L ||
            spHoras.selectedItemId == -1L) {
            Toast.makeText(this, "Por favor, seleccione todos los campos requeridos", Toast.LENGTH_LONG).show()
            return
        }

        try {
            // Obtener valores seleccionados
            val servicioId = spServicios.selectedItemId
            val empleadoId = spEmpleados.selectedItemId
            val diaSeleccionado = spDiasLaborables.selectedItem.toString()
            val horaInicio = spHoras.selectedItem.toString()
            val notas = etNotasCita.text.toString()

            // Obtener el ID del cliente (para administradores, usar el ID del empleado)
            val idParaCita = if (userRole == "Administrador") empleadoId else clienteId

            // Verificar si la cita es para el mismo usuario (solo para clientes)
            if (userRole == "Cliente" && idParaCita != clienteId) {
                Toast.makeText(this, "Solo puedes crear citas para ti mismo", Toast.LENGTH_LONG).show()
                return
            }

            // Para administradores, asegurarse de que el empleado existe
            if (userRole == "Administrador") {
                val cursorEmpleado = dbHelper.getReadableDatabase().rawQuery(
                    "SELECT COUNT(*) FROM Empleados WHERE ID_Empleado = ?",
                    arrayOf(empleadoId.toString())
                )
                cursorEmpleado.moveToFirst()
                if (cursorEmpleado.getInt(0) == 0) {
                    Toast.makeText(this, "Error: Empleado no encontrado", Toast.LENGTH_LONG).show()
                    return
                }
                cursorEmpleado.close()
            }

            // Extraer el día de la semana del texto seleccionado
            val partes = diaSeleccionado.split("(")
            if (partes.size != 2) {
                Toast.makeText(this, "Formato de fecha incorrecto", Toast.LENGTH_LONG).show()
                return
            }
            val diaSemana = partes[1].replace(")", "").trim()

            // Verificar día laboral y horario
            val db = dbHelper.getReadableDatabase()
            val cursorHorario = db.rawQuery(
                "SELECT Dia_Semana, Hora_Inicio_Bloque, Hora_Fin_Bloque FROM Horarios_Disponibles_Empleado " +
                "WHERE ID_Empleado = ? AND Dia_Semana = ?",
                arrayOf(empleadoId.toString(), diaSemana)
            )
            
            var diaLaboral = false
            var horaValida = false
            
            if (cursorHorario.count == 0) {
                Toast.makeText(this, "El día seleccionado no es laboral para este empleado", Toast.LENGTH_LONG).show()
                cursorHorario.close()
                return
            }
            
            while (cursorHorario.moveToNext()) {
                diaLaboral = true
                val horaInicioBloque = cursorHorario.getString(1)
                val horaFinBloque = cursorHorario.getString(2)
                
                val horaInicioMin = convertirHoraAMinutos(horaInicio)
                val horaInicioBloqueMin = convertirHoraAMinutos(horaInicioBloque)
                val horaFinBloqueMin = convertirHoraAMinutos(horaFinBloque)
                
                // Verificar si la hora está dentro del bloque horario
                if (horaInicioMin >= horaInicioBloqueMin && horaInicioMin < horaFinBloqueMin) {
                    horaValida = true
                    break
                }
            }
            cursorHorario.close()

            if (!diaLaboral) {
                Toast.makeText(this, "El día seleccionado no es laboral para este empleado", Toast.LENGTH_LONG).show()
                return
            }
            
            if (!horaValida) {
                Toast.makeText(this, "La hora seleccionada no está dentro del horario laboral", Toast.LENGTH_LONG).show()
                return
            }

            // Verificar si ya existe una cita en ese horario
            val cursorCita = dbHelper.getReadableDatabase().rawQuery(
                """
                SELECT COUNT(*) FROM Citas 
                WHERE ID_Empleado = ? 
                AND Fecha_Cita = ? 
                AND Hora_Inicio = ?
                """.trimIndent(),
                arrayOf(empleadoId.toString(), diaSeleccionado, horaInicio)
            )
            cursorCita.moveToFirst()
            val citaExistente = cursorCita.getInt(0) > 0
            cursorCita.close()

            if (citaExistente) {
                Toast.makeText(this, "Ya existe una cita para este empleado en el horario seleccionado", Toast.LENGTH_LONG).show()
                return
            }

            // Obtener la duración del servicio
            val cursorServicio = db.rawQuery(
                "SELECT Duracion_Estimada_Minutos FROM Servicios WHERE ID_Servicio = ?",
                arrayOf(servicioId.toString())
            )
            
            var duracionMinutos = 0
            if (cursorServicio.moveToFirst()) {
                duracionMinutos = cursorServicio.getInt(0)
            }
            cursorServicio.close()

            // Calcular la hora fin estimada
            val calendarHoraFin = Calendar.getInstance()
            val partesHoraInicio = horaInicio.split(":")
            calendarHoraFin.set(Calendar.HOUR_OF_DAY, partesHoraInicio[0].toInt())
            calendarHoraFin.set(Calendar.MINUTE, partesHoraInicio[1].toInt())
            calendarHoraFin.add(Calendar.MINUTE, duracionMinutos)
            val horaFinEstimada = String.format("%02d:%02d", calendarHoraFin.get(Calendar.HOUR_OF_DAY), calendarHoraFin.get(Calendar.MINUTE))

            // Insertar la cita
            val values = ContentValues().apply {
                put("ID_Cliente", idParaCita)
                put("ID_Servicio", servicioId)
                put("ID_Empleado", empleadoId)
                put("Fecha_Cita", diaSeleccionado)
                put("Hora_Inicio", horaInicio)
                put("Hora_Fin_Estimada", horaFinEstimada)
                put("Notas_Cita", notas)
                put("Estado_Cita", "Pendiente")
            }

            try {
                val idCita = dbHelper.createCita(values, userId)
                if (idCita != -1L) {
                    Toast.makeText(this, "Cita creada exitosamente", Toast.LENGTH_LONG).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this, "Error al crear la cita", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error al insertar la cita: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al procesar la cita: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviciosCursor?.let { if (!it.isClosed) it.close() }
        empleadosCursor?.let { if (!it.isClosed) it.close() }
    }
}
