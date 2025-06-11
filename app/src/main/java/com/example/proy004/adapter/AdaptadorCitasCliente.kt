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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class AdaptadorCitasCliente(private val context: Context, private val cursor: android.database.Cursor) : BaseAdapter() {
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
        val empleado = cursor.getString(cursor.getColumnIndex("Nombre_Empleado"))
        val apellido1Cliente = cursor.getString(cursor.getColumnIndex("Apellido1_Cliente")) ?: ""
        val apellido2Cliente = cursor.getString(cursor.getColumnIndex("Apellido2_Cliente")) ?: ""
        val apellido1Empleado = cursor.getString(cursor.getColumnIndex("Apellido1_Empleado")) ?: ""
        val apellido2Empleado = cursor.getString(cursor.getColumnIndex("Apellido2_Empleado")) ?: ""

        // Formatear fecha y hora
        if (fecha.isNotEmpty() && horaInicio.isNotEmpty()) {
            try {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH).parse(fecha)
                holder.tvHoraServicio.text = "$horaInicio - $servicio"
                holder.tvFecha.text = sdf.format(date)
            } catch (e: ParseException) {
                holder.tvHoraServicio.text = "Hora: $horaInicio - Servicio: $servicio"
                holder.tvFecha.text = "Fecha inválida"
            }
        } else {
            holder.tvHoraServicio.text = "Hora: $horaInicio - Servicio: $servicio"
            holder.tvFecha.text = "Fecha no disponible"
        }

        // Mostrar nombres completos sin mostrar "null"
        holder.tvCliente.text = "Cliente: $cliente ${if (apellido1Cliente.isNotEmpty()) apellido1Cliente else ""} ${if (apellido2Cliente.isNotEmpty()) apellido2Cliente else ""}".trim()
        holder.tvEmpleado.text = "Empleado: $empleado ${if (apellido1Empleado.isNotEmpty()) apellido1Empleado else ""} ${if (apellido2Empleado.isNotEmpty()) apellido2Empleado else ""}".trim()
        holder.tvEstado.text = estado

        // Solo mostrar el botón de cancelar
        holder.btnCancelar.visibility = View.VISIBLE
        holder.btnCompletar.visibility = View.GONE

        // Configurar el color del estado según su valor
        holder.tvEstado.setTextColor(
            when (estado) {
                "Pendiente" -> context.resources.getColor(android.R.color.holo_orange_dark)
                "Confirmada" -> context.resources.getColor(android.R.color.holo_green_dark)
                "Cancelada" -> context.resources.getColor(android.R.color.holo_red_dark)
                "Completada" -> context.resources.getColor(android.R.color.holo_blue_dark)
                else -> context.resources.getColor(android.R.color.holo_orange_dark)
            }
        )

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
        val tvHoraServicio: TextView = view.findViewById(R.id.tvHoraServicio)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvCliente: TextView = view.findViewById(R.id.tvCliente)
        val tvEmpleado: TextView = view.findViewById(R.id.tvEmpleado)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
        val btnCancelar: Button = view.findViewById(R.id.btnCancelar)
        val btnCompletar: Button = view.findViewById(R.id.btnCompletar)
    }
}
