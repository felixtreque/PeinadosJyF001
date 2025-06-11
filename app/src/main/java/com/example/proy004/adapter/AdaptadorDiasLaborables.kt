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
import java.text.SimpleDateFormat
import java.util.Locale

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
                    val fecha = formatoFecha(dia)
                    val diaSemana = getDiaSemana(dia)
                    
                    diasLaborables.add("$fecha ($diaSemana)")
                }
                cursor.close()
            }
            
            return diasLaborables
        }

        private fun getDiaSemana(calendar: Calendar): String {
            val diasSemana = arrayOf("Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")
            val dia = calendar.get(Calendar.DAY_OF_WEEK)
            return diasSemana[dia - 1]
        }

        private fun formatoFecha(calendar: Calendar): String {
            val formato = SimpleDateFormat("dd/MM/yyyy")
            return formato.format(calendar.time)
        }
    }
}
