package com.example.safekuy

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class SafeKuyWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Lakukan update untuk semua widget yang aktif
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    // Siapkan intent untuk membuka AddTransactionActivity
    val intent = Intent(context, AddTransactionActivity::class.java)
    // FLAG_ACTIVITY_NEW_TASK dibutuhkan saat membuka activity dari luar aplikasi
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    
    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Buat RemoteViews berdasarkan layout widget kita
    val views = RemoteViews(context.packageName, R.layout.widget_quick_add)
    
    // Pasangkan aksi klik ke seluruh area widget atau tombol khusus
    views.setOnClickPendingIntent(R.id.btnWidgetAdd, pendingIntent)
    
    // Instruksikan widget manager untuk memperbarui widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}
