package com.example.proy004.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.database.Cursor

class AdaptadorSpinnerEmpleados(context: Context, cursor: Cursor) : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item) {
    private val cursor: Cursor

    init {
        this.cursor = cursor
        this.cursor.moveToFirst()
    }

    override fun getCount(): Int {
        return cursor.count
    }

    override fun getItem(position: Int): String? {
        cursor.moveToPosition(position)
        return "${cursor.getString(cursor.getColumnIndex("Nombre"))} ${cursor.getString(cursor.getColumnIndex("Apellido1"))}"
    }

    override fun getItemId(position: Int): Long {
        cursor.moveToPosition(position)
        return cursor.getLong(cursor.getColumnIndex("ID_Empleado"))
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val text = view.findViewById<TextView>(android.R.id.text1)
        text.text = getItem(position)
        return view
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val text = view.findViewById<TextView>(android.R.id.text1)
        text.text = getItem(position)
        return view
    }
}
