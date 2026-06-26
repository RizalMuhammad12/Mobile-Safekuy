package com.example.safekuy

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class CalendarDay(
    val date: Date,
    val dayName: String,
    val dayNumber: String,
    val isToday: Boolean,
    val isSelected: Boolean,
    val dateString: String // yyyy-MM-dd
)

class CalendarAdapter(
    private val onDateClick: (String) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    private val days = mutableListOf<CalendarDay>()

    fun setData(selectedDate: String) {
        days.clear()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val centerDate = sdf.parse(selectedDate) ?: Date()
        val cal = Calendar.getInstance()
        val todayStr = sdf.format(Date())

        val hariIndo = mapOf(
            "Sun" to "Min", "Mon" to "Sen", "Tue" to "Sel",
            "Wed" to "Rab", "Thu" to "Kam", "Fri" to "Jum", "Sat" to "Sab"
        )
        val dayFormat = SimpleDateFormat("EEE", Locale.ENGLISH)
        val numFormat = SimpleDateFormat("dd", Locale.getDefault())

        for (i in -15..15) {
            cal.time = centerDate
            cal.add(Calendar.DAY_OF_MONTH, i)
            val d = cal.time
            val dateStr = sdf.format(d)
            val engDay = dayFormat.format(d)
            days.add(
                CalendarDay(
                    date = d,
                    dayName = hariIndo[engDay] ?: engDay,
                    dayNumber = numFormat.format(d),
                    isToday = dateStr == todayStr,
                    isSelected = dateStr == selectedDate,
                    dateString = dateStr
                )
            )
        }
        notifyDataSetChanged()
    }

    fun getSelectedPosition(): Int {
        return days.indexOfFirst { it.isSelected }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(days[position])
    }

    override fun getItemCount() = days.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDayName: TextView = itemView.findViewById(R.id.tvDayName)
        private val tvDayNumber: TextView = itemView.findViewById(R.id.tvDayNumber)
        private val dotToday: View = itemView.findViewById(R.id.dotToday)
        private val root: LinearLayout = itemView as LinearLayout

        fun bind(day: CalendarDay) {
            tvDayName.text = day.dayName
            tvDayNumber.text = day.dayNumber

            if (day.isSelected) {
                root.setBackgroundResource(R.drawable.bg_calendar_selected)
                tvDayName.setTextColor(Color.parseColor("#10B981"))
                tvDayNumber.setTextColor(Color.parseColor("#1E293B"))
                dotToday.visibility = View.GONE
            } else {
                root.setBackgroundResource(R.drawable.bg_calendar_normal)
                tvDayName.setTextColor(Color.parseColor("#E6FFFFFF"))
                tvDayNumber.setTextColor(Color.WHITE)
                dotToday.visibility = if (day.isToday) View.VISIBLE else View.GONE
            }

            itemView.setOnClickListener {
                onDateClick(day.dateString)
            }
        }
    }
}
