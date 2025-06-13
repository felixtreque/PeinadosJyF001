package com.example.proy004.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.File
import android.content.ContentValues
import android.database.Cursor

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val context: Context = context
    private var database: SQLiteDatabase? = null

    companion object {
        private const val DATABASE_NAME = "BBDD_PeinadosJyFProyecto.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // No necesitamos crear las tablas ya que la base de datos viene predefinida en assets
        // Solo copiamos la base de datos existente
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // No necesitamos actualizar la base de datos ya que no se modificarán las tablas
        // Solo copiamos la base de datos existente
    }

    init {
        // Crear la base de datos usando el script SQL
        Log.d("DBHelper", "Inicializando base de datos...")
        try {
            val db = getWritableDatabase()
            
            // Leer el script SQL
            val inputStream = context.assets.open("BBDD_PeinadosJyF.sql")
            val script = inputStream.bufferedReader().use { it.readText() }
            inputStream.close()
            
            // Dividir el script en sentencias individuales
            val statements = script.split(';').filter { it.isNotBlank() }
            
            // Ejecutar cada sentencia
            for (statement in statements) {
                try {
                    // Limpiar la sentencia
                    val cleanStatement = statement.trim()
                    
                    // Solo ejecutar si la sentencia no está vacía
                    if (cleanStatement.isNotEmpty()) {
                        db.execSQL(cleanStatement)
                        Log.d("DBHelper", "Ejecutada sentencia: $cleanStatement")
                    }
                } catch (e: Exception) {
                    Log.e("DBHelper", "Error ejecutando sentencia: $statement")
                    e.printStackTrace()
                }
            }
            
            Log.d("DBHelper", "Base de datos inicializada exitosamente")
        } catch (e: Exception) {
            Log.e("DBHelper", "Error inicializando base de datos: ${e.message}")
            e.printStackTrace()
        }
    }

    // Métodos para obtener instancias de la base de datos
    override fun getReadableDatabase(): SQLiteDatabase {
        return super.getReadableDatabase()
    }

    override fun getWritableDatabase(): SQLiteDatabase {
        return super.getWritableDatabase()
    }

    // Métodos para gestionar usuarios
    fun login(email: String, contrasena: String): Boolean {
        try {
            val db = getReadableDatabase()
            val query = "SELECT * FROM Usuarios WHERE Email = ? AND Contrasena_Hash = ?"
            val cursor = db.rawQuery(query, arrayOf(email, contrasena))
            
            val result = cursor.count > 0
            cursor.close()
            return result
        } catch (e: Exception) {
            Log.e("DBHelper", "Error en login: ${e.message}")
            return false
        }
    }

    // Método para obtener el rol del usuario
    fun getRolUsuario(idUsuario: Long): String? {
        val db = getReadableDatabase()
        val cursor = db.rawQuery(
            "SELECT r.Nombre_Rol FROM Usuarios u JOIN Roles r ON u.ID_Rol = r.ID_Rol " +
            "WHERE u.ID_Usuario = ?",
            arrayOf(idUsuario.toString())
        )
        
        var rol: String? = null
        if (cursor.moveToFirst()) {
            rol = cursor.getString(0)
        }
        cursor.close()
        return rol
    }

    // Método para verificar si un usuario puede gestionar una cita
    fun puedeGestionarCita(idUsuario: Long, idCita: Long): Boolean {
        val db = getReadableDatabase()
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM Citas c " +
            "WHERE c.ID_Cita = ? AND (" +
            "    (c.ID_Cliente = (SELECT ID_Cliente FROM Clientes WHERE ID_Usuario = ?)) OR " +
            "    (c.ID_Empleado = (SELECT ID_Empleado FROM Empleados WHERE ID_Usuario = ?)) OR " +
            "    (c.ID_Cliente = ?) " +
            ")",
            arrayOf(idCita.toString(), idUsuario.toString(), idUsuario.toString(), idUsuario.toString())
        )
        
        val puedeGestionar = cursor.count > 0
        cursor.close()
        return puedeGestionar
    }

    // Métodos para gestionar citas
    fun createCita(cita: ContentValues, idUsuario: Long): Long {
        val db = writableDatabase
        val idCita = db.insert("Citas", null, cita)
        
        if (idCita != -1L) {
            val cursor = db.query(
                "Citas",
                arrayOf("COUNT(*)"),
                "ID_Cita = ? AND (" +
                "ID_Cliente = (SELECT ID_Cliente FROM Clientes WHERE ID_Usuario = ?) OR " +
                "ID_Empleado = (SELECT ID_Empleado FROM Empleados WHERE ID_Usuario = ?) OR " +
                "ID_Cliente = ?)",
                arrayOf(idCita.toString(), idUsuario.toString(), idUsuario.toString(), idUsuario.toString()),
                null, null, null
            )
            
            if (cursor.moveToFirst() && cursor.getInt(0) == 0) {
                db.delete("Citas", "ID_Cita = ?", arrayOf(idCita.toString()))
                return -1L
            }
            cursor.close()
        }
        
        return idCita
    }

    fun getCitasCliente(idCliente: Int): Cursor {
        val db = getReadableDatabase()
        return db.query(
            "Citas",
            null,
            "ID_Cliente = ?",
            arrayOf(idCliente.toString()),
            null,
            null,
            "Fecha_Cita ASC"
        )
    }

    fun updateCitaEstado(idCita: Int, estado: String, idUsuario: Long): Int {
        val db = getWritableDatabase()
        val values = ContentValues().apply {
            put("Estado_Cita", estado)
            put("Fecha_Actualizacion", System.currentTimeMillis())
        }
        
        // Solo administradores pueden cambiar el estado de las citas
        val rol = getRolUsuario(idUsuario)
        if (rol != "Administrador") {
            return 0
        }
        
        // Verificar si la cita existe
        val cursor = db.rawQuery("SELECT COUNT(*) FROM Citas WHERE ID_Cita = ?", arrayOf(idCita.toString()))
        if (cursor.count == 0) {
            cursor.close()
            return 0
        }
        cursor.close()
        
        return db.update("Citas", values, "ID_Cita = ?", arrayOf(idCita.toString()))
    }

    // Versión simplificada para uso en adaptadores
    fun updateCitaEstado(idCita: Int, estado: String): Int {
        // Para el adaptador, asumimos que el usuario actual es el administrador
        val db = getWritableDatabase()
        val values = ContentValues().apply {
            put("Estado_Cita", estado)
            put("Fecha_Actualizacion", System.currentTimeMillis())
        }
        
        return db.update("Citas", values, "ID_Cita = ?", arrayOf(idCita.toString()))
    }

    fun deleteCita(idCita: Int): Int {
        val db = getWritableDatabase()
        return db.delete("Citas", "ID_Cita = ?", arrayOf(idCita.toString()))
    }

    // Métodos para gestionar recursos
    fun getEmpleados(): Cursor {
        val db = getReadableDatabase()
        return db.query("Empleados", null, null, null, null, null, "Nombre ASC")
    }

    fun getServicios(): Cursor {
        val db = getReadableDatabase()
        return db.query("Servicios", null, null, null, null, null, "Nombre ASC")
    }

    fun getClientes(): Cursor {
        val db = getReadableDatabase()
        return db.query("Clientes", null, null, null, null, null, "Nombre ASC")
    }

    // Métodos adicionales para gestión de usuarios
    fun getClientePorUsuario(idUsuario: Long): Long {
        val db = getReadableDatabase()
        val cursor = db.rawQuery(
            "SELECT ID_Cliente FROM Clientes WHERE ID_Usuario = ?",
            arrayOf(idUsuario.toString())
        )
        
        var idCliente = -1L
        if (cursor.moveToFirst()) {
            idCliente = cursor.getLong(0)
        }
        cursor.close()
        return idCliente
    }

    // Métodos adicionales para gestión de citas
    fun getCitasEmpleado(idEmpleado: Int): Cursor {
        val db = getReadableDatabase()
        return db.query(
            "Citas",
            null,
            "ID_Empleado = ?",
            arrayOf(idEmpleado.toString()),
            null,
            null,
            "Fecha_Cita ASC"
        )
    }

    fun getCitasPorFecha(fecha: String): Cursor {
        val db = getReadableDatabase()
        return db.query(
            "Citas",
            null,
            "Fecha_Cita = ?",
            arrayOf(fecha),
            null,
            null,
            "Hora_Inicio ASC"
        )
    }

    // Métodos para gestionar horarios
    fun getHorariosEmpleado(idEmpleado: Int): Cursor {
        val db = getReadableDatabase()
        return db.query(
            "Horarios_Disponibles_Empleado",
            null,
            "ID_Empleado = ?",
            arrayOf(idEmpleado.toString()),
            null,
            null,
            "Dia_Semana ASC, Hora_Inicio_Bloque ASC"
        )
    }
}
