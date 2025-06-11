package com.example.proy004.adapter

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.example.proy004.R
import android.database.Cursor
import com.example.proy004.database.DBHelper
import android.widget.Toast
import android.widget.LinearLayout

class AdaptadorCitas(private val context: Context, private val cursor: android.database.Cursor) : BaseAdapter() {
    private val dbHelper = DBHelper(context)
    private var db: SQLiteDatabase? = null

    init {
        db = dbHelper.getReadableDatabase()
    }

    override fun getCount(): Int {
        return cursor.count
    }

    override fun getItem(position: Int): Any? {
        cursor.moveToPosition(position)
        return cursor
    }

    override fun getItemId(position: Int): Long {
        cursor.moveToPosition(position)
        return cursor.getLong(cursor.getColumnIndex("ID_Cita"))
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val holder: ViewHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_cita, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        cursor.moveToPosition(position)
        val idCita = cursor.getLong(cursor.getColumnIndex("ID_Cita"))
        val cliente = cursor.getString(cursor.getColumnIndex("Nombre_Cliente"))
        val servicio = cursor.getString(cursor.getColumnIndex("Nombre_Servicio"))
        val fecha = cursor.getString(cursor.getColumnIndex("Fecha_Cita"))
        val horaInicio = cursor.getString(cursor.getColumnIndex("Hora_Inicio"))
        val estado = cursor.getString(cursor.getColumnIndex("Estado_Cita"))

        holder.tvHora.text = "$horaInicio"
        holder.tvCliente.text = cliente
        holder.tvServicio.text = servicio
        holder.tvEmpleado.text = cursor.getString(cursor.getColumnIndex("Nombre_Empleado"))
        holder.tvEstado.text = estado

        holder.btnCompletar.setOnClickListener {
            val result = dbHelper.updateCitaEstado(idCita.toInt(), "Completada")
            if (result > 0) {
                Toast.makeText(context, "Cita marcada como completada", Toast.LENGTH_SHORT).show()
                // TODO: Actualizar la lista
            }
        }

        holder.btnCancelar.setOnClickListener {
            val result = dbHelper.deleteCita(idCita.toInt())
            if (result > 0) {
                Toast.makeText(context, "Cita cancelada", Toast.LENGTH_SHORT).show()
                // TODO: Actualizar la lista
            }
        }

        return view!!
    }

    private class ViewHolder(view: View) {
        val tvHora: TextView = view.findViewById(R.id.tvHora)
        val tvCliente: TextView = view.findViewById(R.id.tvCliente)
        val tvServicio: TextView = view.findViewById(R.id.tvServicio)
        val tvEmpleado: TextView = view.findViewById(R.id.tvEmpleado)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
        val btnCompletar: Button = view.findViewById(R.id.btnCompletar)
        val btnCancelar: Button = view.findViewById(R.id.btnCancelar)
    }
}
