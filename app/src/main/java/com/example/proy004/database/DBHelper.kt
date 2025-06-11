package com.example.proy004.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.*
import android.content.ContentValues
import android.database.Cursor

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val context: Context = context
    private var database: SQLiteDatabase? = null

    companion object {
        private const val DATABASE_NAME = "BBDD_PeinadosJyF.db"
        private const val DATABASE_VERSION = 1
        private const val DATABASE_PATH = "assets/BBDD_PeinadosJyF.sql"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // No se necesita crear tablas ya que se copia desde assets
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // No se necesita actualizar ya que no se modifican las tablas
    }

    init {
        try {
            val databasePath = context.getDatabasePath(DATABASE_NAME)
            if (!databasePath.exists()) {
                copyDatabaseFromAssets()
            }
        } catch (e: IOException) {
            Log.e("DBHelper", "Error copying database: ${e.message}")
        }
    }

    private fun copyDatabaseFromAssets() {
        try {
            val inputStream = context.assets.open(DATABASE_PATH)
            val outputStream = FileOutputStream(context.getDatabasePath(DATABASE_NAME))
            
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
            
            outputStream.flush()
            outputStream.close()
            inputStream.close()
        } catch (e: IOException) {
            Log.e("DBHelper", "Error copying database: ${e.message}")
        }
    }

    fun getReadableDatabase(): SQLiteDatabase {
        if (database == null) {
            database = writableDatabase
        }
        return database!!
    }

    fun getWritableDatabase(): SQLiteDatabase {
        if (database == null) {
            database = writableDatabase
        }
        return database!!
    }

    // Métodos para gestionar usuarios
    fun login(email: String, password: String): Boolean {
        val db = getReadableDatabase()
        val query = "SELECT * FROM Usuarios WHERE Email = ? AND Contrasena_Hash = ?"
        val cursor = db.rawQuery(query, arrayOf(email, password))
        
        val result = cursor.count > 0
        cursor.close()
        return result
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

    // Métodos para gestionar servicios
    fun getServicios(): Cursor {
        val db = getReadableDatabase()
        return db.query("Servicios", null, null, null, null, null, "Nombre_Servicio ASC")
    }

    // Métodos para gestionar empleados
    fun getEmpleados(): Cursor {
        val db = getReadableDatabase()
        return db.query("Empleados", null, null, null, null, null, "Nombre ASC")
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
