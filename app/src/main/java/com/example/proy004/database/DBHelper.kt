package com.example.proy004.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.content.ContentValues
import android.database.Cursor

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val context: Context = context
    private var database: SQLiteDatabase? = null

    companion object {
        private const val DATABASE_NAME = "BBDD_PeinadosJyF.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // No necesitamos crear las tablas ya que la base de datos viene predefinida en assets
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // No necesitamos actualizar la base de datos ya que no se modificarán las tablas
    }

    init {
        // Copiar la base de datos desde assets si no existe
        val dbPath = context.getDatabasePath(DATABASE_NAME)
        if (!dbPath.exists()) {
            try {
                val inputStream = context.assets.open("BBDD_PeinadosJyF.db")
                val outputStream = dbPath.outputStream()
                val buffer = ByteArray(1024)
                var length: Int
                while (inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }
                outputStream.flush()
                outputStream.close()
                inputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }







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

    // Métodos para gestionar citas
    fun createCita(cita: ContentValues): Long {
        val db = getWritableDatabase()
        return db.insert("Citas", null, cita)
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

    fun updateCitaEstado(idCita: Int, estado: String): Int {
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
