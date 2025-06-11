package com.example.proy004.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.proy004.R
import android.database.Cursor

class AdaptadorClientes(private val context: Context, private val cursor: Cursor) : BaseAdapter() {
    override fun getCount(): Int {
        return cursor.count
    }

    override fun getItem(position: Int): Any? {
        cursor.moveToPosition(position)
        return cursor
    }

    override fun getItemId(position: Int): Long {
        cursor.moveToPosition(position)
        return cursor.getLong(cursor.getColumnIndex("ID_Cliente"))
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val holder: ViewHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_cliente, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        cursor.moveToPosition(position)
        holder.tvNombre.text = cursor.getString(cursor.getColumnIndex("Nombre"))
        holder.tvApellidos.text = "${cursor.getString(cursor.getColumnIndex("Apellido1"))} ${cursor.getString(cursor.getColumnIndex("Apellido2"))}"
        holder.tvEmail.text = cursor.getString(cursor.getColumnIndex("Email"))
        holder.tvTelefono.text = cursor.getString(cursor.getColumnIndex("Telefono"))
        holder.tvPreferencias.text = cursor.getString(cursor.getColumnIndex("Preferencias_Servicio"))
        holder.tvAlergias.text = cursor.getString(cursor.getColumnIndex("Alergias"))

        return view!!
    }

    private class ViewHolder(view: View) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvApellidos: TextView = view.findViewById(R.id.tvApellidos)
        val tvEmail: TextView = view.findViewById(R.id.tvEmail)
        val tvTelefono: TextView = view.findViewById(R.id.tvTelefono)
        val tvPreferencias: TextView = view.findViewById(R.id.tvPreferencias)
        val tvAlergias: TextView = view.findViewById(R.id.tvAlergias)
    }
}
