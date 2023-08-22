package com.example.traintrack

import android.content.Context
import android.widget.Toast

object SharedPreferencesManager {
    fun saveData(context: Context, line: String?, stop: String?) {
        if (line.isNullOrBlank() || stop.isNullOrBlank()) {
            Toast.makeText(context, "Please specify a line and GO Station.", Toast.LENGTH_LONG).show()
        } else {
            val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.apply {
                putString("USER_LINE", line)
                putString("USER_STOP", stop)
            }.apply()

            Toast.makeText(context, "Trip set to: $line, $stop.", Toast.LENGTH_SHORT).show()
        }
    }

    fun loadLine(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return sharedPreferences.getString("USER_LINE", null)
    }

    fun loadStop(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return sharedPreferences.getString("USER_STOP", null)
    }

    fun clear(context: Context) {
        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }
}