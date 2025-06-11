package com.example.proy004.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.proy004.R
import android.database.Cursor

class AdaptadorServicios(private val context: Context, private val cursor: Cursor) : BaseAdapter() {
    override fun getCount(): Int {
        return cursor.count
    }

    override fun getItem(position: Int): Any? {
        cursor.moveToPosition(position)
        return cursor
    }

    override fun getItemId(position: Int): Long {
        cursor.moveToPosition(position)
        return cursor.getLong(cursor.getColumnIndex("ID_Servicio"))
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val holder: ViewHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_servicio, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        cursor.moveToPosition(position)
        holder.tvNombre.text = cursor.getString(cursor.getColumnIndex("Nombre_Servicio"))
        holder.tvDescripcion.text = cursor.getString(cursor.getColumnIndex("Descripcion"))
        holder.tvDuracion.text = "Duración: ${cursor.getInt(cursor.getColumnIndex("Duracion_Estimada_Minutos"))} min"
        holder.tvPrecio.text = "Precio: ${cursor.getDouble(cursor.getColumnIndex("Precio"))} €"

        return view!!
    }

    private class ViewHolder(view: View) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val tvDuracion: TextView = view.findViewById(R.id.tvDuracion)
        val tvPrecio: TextView = view.findViewById(R.id.tvPrecio)
    }
}
