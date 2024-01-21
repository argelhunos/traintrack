package com.example.traintrack

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.traintrack.SharedPreferencesManager.loadLine
import com.example.traintrack.SharedPreferencesManager.loadStop
import com.example.traintrack.data.nameToAbbreviation
import com.example.traintrack.data.nameToColor
import com.example.traintrack.ui.theme.TrainTrackTheme
import com.example.traintrack.ui.theme.md_theme_light_errorContainer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState


class MainActivity : ComponentActivity() {
    private val viewModel: TripViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrainTrackTheme {
                NavController(viewModel = viewModel, modifier = Modifier.fillMaxSize(), context = this)
            }
            loadLine(this)
            loadStop(this)
        }
    }
}

@Composable
fun NavController(viewModel: TripViewModel, modifier: Modifier, context: Context) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.MainScreen.route
    ) {
        composable(route = Screen.MainScreen.route) {
            TripScreen(viewModel = viewModel, navController = navController, modifier = modifier, context)
        }

        composable(route = Screen.SettingsScreen.route) {
            TripSettings(viewModel = viewModel, modifier = modifier, context = context, navController = navController)
        }
    }

    if (loadLine(context) == null || loadStop(context) == null) {
        navController.navigate(Screen.SettingsScreen.route)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun TripScreen(viewModel: TripViewModel, navController: NavController, modifier: Modifier, context: Context) {
    val trips by viewModel.lines.observeAsState(emptyList())
    val metadata by viewModel.metadata.observeAsState()

    val nextService by viewModel.nextService.observeAsState()
    val refreshState = rememberPullRefreshState(refreshing = viewModel.isLoading, onRefresh = { viewModel.fetchTrips() })

    // remember state for lazy list of trips
    val state = rememberLazyListState()

    val userLine = loadLine(context)
    val userStop = loadStop(context)

    LaunchedEffect(metadata) {
        viewModel.fetchTrips()
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.SettingsScreen.route) },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    painterResource(id = R.drawable.tune),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(8.dp),
                    contentDescription = null
                )
            }
        }
    ) { padding ->
        Column (
            modifier = modifier
                .padding(padding)
                .padding(start = 16.dp, end = 16.dp)
        ) {
            if (viewModel.isLoading) {
                Box (
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                Text(
                    text = "Last updated: " + (metadata?.TimeStamp ?: LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))),
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Light
                )
                if (nextService == null) {
                    LineName(
                        lineName = userLine ?: trips.first().LineName,
                        stopName = userStop ?: "",
                        color = nameToColor.getValue(userLine ?: trips.first().LineName),
                        abbreviation = nameToAbbreviation.getValue(userLine ?: trips.first().LineName)
                    )
                    Error()
                } else {
                    Spacer(modifier = Modifier.size(16.dp))

                    Box(Modifier.pullRefresh(refreshState)) {
                        LazyColumn (
                            state = state,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ){
                            item() {
                                LineName(
                                    lineName = userLine ?: trips.first().LineName,
                                    stopName = userStop ?: "",
                                    color = nameToColor.getValue(userLine ?: trips.first().LineName),
                                    abbreviation = nameToAbbreviation.getValue(userLine ?: trips.first().LineName)
                                )
                            }

                            item() {
                                Text("Next Departures: ")
                            }

                            items(trips.sortedWith(compareBy { it.ScheduledDepartureTime }).filter { it.LineName == userLine }) {
                                Trip(
                                    time = if (it.ComputedDepartureTime > it.ScheduledDepartureTime) {
                                        it.ComputedDepartureTime
                                    } else {
                                        it.ScheduledDepartureTime
                                    },
                                    platformNumber = it.ScheduledPlatform,
                                    destination = it.DirectionName.drop(5),
                                    isDelayed = it.ComputedDepartureTime > it.ScheduledDepartureTime
                                )
                            }

                            item {
                                Spacer(modifier = modifier.height(40.dp))
                            }
                        }

                        PullRefreshIndicator(refreshing = viewModel.isLoading, state = refreshState, Modifier.align(Alignment.TopCenter))
                    }
                }
            }
        }
    }
}

@Composable
fun Trip(time: String, platformNumber: String, destination: String, isDelayed: Boolean) {
    Card() {
        Row(modifier = Modifier.padding(16.dp)) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = time.split(" ").last().dropLast(3),
                    style = MaterialTheme.typography.displayMedium,
                    color = if (isDelayed) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    },
                    modifier = Modifier.widthIn(min = 500.dp)
                )
                Text(text = stringResource(id = R.string.platform) + " " + platformNumber)
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.tertiary)
                        .padding(start = 8.dp, end = 8.dp)
                ) {
                    Text(
                        text = "to $destination",
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }
            }
        }

    }
}

@Composable
fun LineName(lineName: String, stopName: String, color: Color, abbreviation: String) {
    Column() {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.train_icon),
                contentDescription = null
            )
            Spacer(modifier = Modifier.size(4.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(14.dp))
                    .background(color)
                    .height(32.dp)
                    .weight(1f)

            ) {
                Text(
                    text = abbreviation,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = lineName,
                modifier = Modifier.weight(4f),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Text(text = "@ $stopName", modifier = Modifier.padding(4.dp))
    }
}

@Composable
fun Error() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(400.dp)
            .clip(shape = RoundedCornerShape(14.dp))
            .background(color = md_theme_light_errorContainer),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No trips found.",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(Modifier.size(16.dp))
            Image(
                painterResource(id = R.drawable.train),
                contentDescription = null
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun LineNamePreview() {
    TrainTrackTheme() {
        LineName(lineName = "Kitchener", stopName = "Mount Pleasant GO", color = nameToColor["Kitchener"]!!, abbreviation = nameToAbbreviation["Kitchener"]!!)
    }
}

@Preview
@Composable
fun TripPreview() {
    TrainTrackTheme() {
        Column (verticalArrangement = Arrangement.spacedBy(16.dp)){
            Trip(time = "2023-05-30 14:40:00", platformNumber = "3", destination = "Niagara Falls GO", isDelayed = true)
            Trip(time = "2023-05-30 15:41:00", platformNumber = "3", destination = "Union Station", isDelayed = false)
            Trip(time = "2023-05-30 23:23:00", platformNumber = "3", destination = "Union Station", isDelayed = false)
        }
    }
}


@Preview
@Composable
fun ErrorPreview() {
    TrainTrackTheme() {
        Error()
    }
}