package com.example.traintrack

import android.content.Context
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.example.traintrack.data.nameToAbbreviation
import com.example.traintrack.data.nameToColor
import kotlinx.coroutines.withContext

class TripWidget: GlanceAppWidget(), ViewModelStoreOwner {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val line = SharedPreferencesManager.loadLine(context)
        val stop = SharedPreferencesManager.loadStop(context)
        var viewModel: TripViewModel = ViewModelProvider(this)[TripViewModel::class.java]

        provideContent {
            LineName(
                lineName = line ?: "Please set up your default trip in the app.",
                stopName = stop ?: "",
                color = nameToColor[line] ?: Color.Red,
                abbreviation = nameToAbbreviation[line] ?: "N/A"
            )
        }

    }

    override val viewModelStore: ViewModelStore
        get() = ViewModelStore()
}

@Composable
fun WidgetLineName(lineName: String, stopName: String, color: Color, abbreviation: String) {
    Column() {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = GlanceModifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.train_icon),
                contentDescription = null
            )
            Spacer(modifier = GlanceModifier.size(4.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = GlanceModifier
                    .cornerRadius(14.dp)
                    .background(color)
                    .height(32.dp)
            ) {
                Text(
                    text = abbreviation,
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontWeight = FontWeight.Bold,
                        //fontStyle = MaterialTheme.typography.headlineSmall ??
                    )
                )
            }
            Spacer(modifier = GlanceModifier.size(8.dp))
            Text(
                text = lineName,
                modifier = GlanceModifier.defaultWeight(),
                style = TextStyle(
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Text(text = "@ $stopName", modifier = GlanceModifier.padding(4.dp))
    }
}
