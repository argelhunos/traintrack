package com.example.traintrack

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.traintrack.SharedPreferencesManager.saveData
import com.example.traintrack.data.lineToStations
import com.example.traintrack.data.listOfLines
import com.example.traintrack.ui.theme.TrainTrackTheme
import kotlinx.coroutines.launch

@Composable
fun TripSettings(viewModel: TripViewModel, modifier: Modifier, context: Context, navController: NavController) {
    val lineOptions = listOfLines

    var linesExpanded by remember { mutableStateOf(false) }
    val selectedLineText by viewModel.selectedLineText.observeAsState()

    val stopOptions = lineToStations[selectedLineText]
    var stopsExpanded by remember { mutableStateOf(false) }
    val selectedStopText by viewModel.selectedStopText.observeAsState()

    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxHeight()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.train_track),
                    tint = MaterialTheme.colorScheme.onBackground,
                    contentDescription = null,
                )
                Text(
                    text = "Trip Settings",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.size(8.dp))

            TripSelector(
                expanded = linesExpanded,
                selectedOptionText = selectedLineText,
                label = "Line",
                onExpandedChange = { linesExpanded = !linesExpanded },
                onDismissRequest = { linesExpanded = false },
                onOptionClicked = {
                    viewModel.onLineSelected(line = it)
                    linesExpanded = false
                },
                options = lineOptions
            )

            Spacer(modifier = Modifier.size(8.dp))

            TripSelector(
                expanded = stopsExpanded,
                selectedOptionText = selectedStopText,
                label = "Stop",
                onExpandedChange = { stopsExpanded = !stopsExpanded },
                onDismissRequest = { stopsExpanded = false },
                onOptionClicked = {
                    viewModel.onStopSelected(stop = it)
                    stopsExpanded = false
                },
                options = stopOptions!!
            )

            Spacer(modifier = Modifier.size(8.dp))

            Button(
                onClick = {
                    saveData(context = context, line = selectedLineText, stop = selectedStopText)
                    navController.navigate(Screen.MainScreen.route)
                }
            ) {
                Text("Save")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripSelector(expanded: Boolean, selectedOptionText: String?, label: String, onExpandedChange: () -> Unit, onDismissRequest: () -> Unit, onOptionClicked: (String) -> Unit, options: List<String>) {
    Column() {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { onExpandedChange() }
        ) {
            TextField(
                readOnly = true,
                value = selectedOptionText!!,
                onValueChange = { },
                label = { Text(label) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    onDismissRequest()
                }
            ) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text (text = selectionOption) },
                        onClick = {
                            onOptionClicked(selectionOption)
                        }
                    )
                }
            }
        }
    }
}
