package edu.nd.pmcburne.hwapp.one

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.nd.pmcburne.hwapp.one.data.GameEntity
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun GameScreen(viewModel: GameViewModel) {

    val games by viewModel.games.collectAsState()
    val gender by viewModel.gender.collectAsState()
    val date by viewModel.selectedDate.collectAsState()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = viewModel.isLoading,
        onRefresh = { viewModel.refresh() }
    )

    var showDatePicker by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.refresh()
        viewModel.uiEvents.collect { event ->
            when (event) {
                is GameViewModel.UiEvent.Error -> {
                    snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scoreboard") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary, // Use a custom color
                    titleContentColor = MaterialTheme.colorScheme.onSecondary, // Adjust text/icon color for contrast
                    // You can also change navigationIconContentColor and actionIconContentColor here
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pullRefresh(pullRefreshState)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {

                // ─── Header Controls ───────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Button(onClick = viewModel::toggleGender) {
                        Text(
                            if (gender == "men")
                                "Men's Basketball"
                            else
                                "Women's Basketball"
                        )
                    }

                    Button(onClick = { showDatePicker = true }) {
                        Text(date.toString())
                    }
                }

                Spacer(Modifier.height(4.dp))

                // ─── Games List ────────────────────────────────────
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(games) { game ->
                        GameRow(game)
                    }
                }
            }

            // Pull-to-refresh indicator
            PullRefreshIndicator(
                refreshing = viewModel.isLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        // ─── Date Picker Dialog ────────────────────────────────
        if (showDatePicker) {

            val datePickerState = rememberDatePickerState()

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let {
                                viewModel.setDate(
                                    Instant.ofEpochMilli(it)
                                        .atZone(ZoneId.of("UTC"))
                                        .toLocalDate()
                                )
                            }
                            showDatePicker = false
                        }
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }

}

@Composable
fun GameRow(game: GameEntity) {

    val isFinal = game.status == "final"
    val isLive = game.status == "live"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {

            // ─── Status Row ───────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = when (game.status) {
                        "pre" -> "Starts at ${game.startTime}"
                        "live" -> "${game.period} • ${game.clock}"
                        "final" -> "Final"
                        else -> ""
                    },
                    fontWeight = FontWeight.SemiBold
                )

                if (isFinal) {
                    Text("FINAL", fontWeight = FontWeight.Bold)
                }

                if (isLive) {
                    Text("● LIVE", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(8.dp))

            // ─── Away Team ────────────────────────────────
            TeamScoreRow(
                teamName = game.awayTeam,
                score = game.awayScore,
                isWinner = isFinal && game.awayScore!! > game.homeScore!!,
            )

            Spacer(Modifier.height(4.dp))

            // ─── Home Team ────────────────────────────────
            TeamScoreRow(
                teamName = game.homeTeam,
                score = game.homeScore,
                isWinner = isFinal && game.homeScore!! > game.awayScore!!
            )
        }
    }
}

@Composable
private fun TeamScoreRow(
    teamName: String,
    score: Int?,
    isWinner: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row {
            if (isWinner) {
                Text("🏆 ", fontWeight = FontWeight.Bold)
            }

            Text(
                text = teamName,
                fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Normal
            )
        }

        Text(
            text = score?.toString() ?: "-",
            fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Normal
        )
    }
}