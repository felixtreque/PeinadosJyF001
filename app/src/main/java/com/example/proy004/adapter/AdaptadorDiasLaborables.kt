package com.example.proy004.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.AdapterView
import java.util.Calendar
import java.util.ArrayList
import com.example.proy004.database.DBHelper

class AdaptadorDiasLaborables(context: Context, private val diasLaborables: ArrayList<String>) :
    ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, diasLaborables) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = diasLaborables[position]
        return view
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = diasLaborables[position]
        return view
    }

    companion object {
        fun obtenerDiasLaborables(context: Context, dbHelper: DBHelper, empleadoId: Long): ArrayList<String> {
            val diasLaborables = ArrayList<String>()
            val calendar = Calendar.getInstance()
            
            // Obtener los días laborables de los próximos 20 días
            for (i in 0..20) {
                val dia = calendar.clone() as Calendar
                dia.add(Calendar.DAY_OF_MONTH, i)
                
                // Verificar si el día es laboral para el empleado
                val db = dbHelper.getReadableDatabase()
                val cursor = db.rawQuery(
                    "SELECT COUNT(*) FROM Horarios_Disponibles_Empleado " +
                    "WHERE ID_Empleado = ? AND Dia_Semana = ?",
                    arrayOf(empleadoId.toString(), getDiaSemana(dia))
                )
                
                cursor.moveToFirst()
                if (cursor.getInt(0) > 0) {
                    diasLaborables.add(formatoFecha(dia))
                }
                cursor.close()
            }
            
            return diasLaborables
        }

        private fun getDiaSemana(calendar: Calendar): String {
            return when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> "Lunes"
                Calendar.TUESDAY -> "Martes"
                Calendar.WEDNESDAY -> "Miércoles"
                Calendar.THURSDAY -> "Jueves"
                Calendar.FRIDAY -> "Viernes"
                Calendar.SATURDAY -> "Sábado"
                Calendar.SUNDAY -> "Domingo"
                else -> ""
            }
        }

        private fun formatoFecha(calendar: Calendar): String {
            return String.format(
                "%02d/%02d/%d",
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR)
            )
        }
    }
}
