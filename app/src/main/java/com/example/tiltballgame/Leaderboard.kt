package com.example.tiltballgame

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tiltballgame.ui.theme.TiltBallGameTheme

private var lvlnum = 0

data class LeaderboardData( // data class we read from db
    val username: String = "",
    val time: Long = 0L
)

fun formatTime(ms: Long): String { // func to convert time format
    val totalSec = (ms / 1000).toInt()
    val min = totalSec / 60
    val sec = totalSec % 60

    return if (min > 0) {
        String.format("%dm %02ds", min, sec)
    } else {
        "${sec}s"
    }
}

@Composable
fun LeaderboardRow(rank: Int, entry: LeaderboardData) { // row design

    val rankColor = when (rank) {
        1 -> Color(0xFFe8c670) // Gold
        2 -> Color(0xFFe0e0e0) // Silver
        3 -> Color(0xFFd29f69) // Bronze
        else -> Color.White
    }

    Row(
        modifier = Modifier.fillMaxWidth().height(30.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically

    ) {
        Text("$rank.", color = rankColor, style = MaterialTheme.typography.titleMedium)
        Text(entry.username, color = rankColor, style = MaterialTheme.typography.titleMedium)
        Text(formatTime(entry.time), color = rankColor, style = MaterialTheme.typography.titleMedium)
    }
}

suspend fun loadLeaderboard( db: FirebaseFirestore, level: Int ): List<LeaderboardData> = withContext(Dispatchers.IO) { // func that read data from db

    try {
        val result = db.collection("Level$level").orderBy("time").limit(10).get().await() // read the data in time ascending

        return@withContext result.documents.mapNotNull { doc ->
            LeaderboardData(
                username = doc.getString("username") ?: "",
                time = doc.getLong("time") ?: 0L
            )
        }

    } catch (e: Exception) {
        e.printStackTrace()
        return@withContext emptyList()
    }
}

@Composable
fun LeaderboardScreen() {
    val db = FirebaseFirestore.getInstance()

    var selectedLevel by remember { mutableStateOf(lvlnum) }
    var entries by remember { mutableStateOf<List<LeaderboardData>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(selectedLevel) { // Load when the level get select
        loading = true
        entries = loadLeaderboard(db, selectedLevel)
        loading = false
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(30.dp, 50.dp, 30.dp, 30.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            for (i in 1..4) {
                Button(
                    onClick = { selectedLevel = i },
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (selectedLevel == i)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("$i", style = MaterialTheme.typography.titleMedium)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.crown),
                contentDescription = "Leaderboard Icon",
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(text = "TOP 10", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSecondary)

            Spacer(modifier = Modifier.width(8.dp))

            Image(
                painter = painterResource(id = R.drawable.crown),
                contentDescription = "Leaderboard Icon",
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        if (loading) { // wait the server finish load
            Text("Loading leaderboard...", style = MaterialTheme.typography.titleMedium)
        } else {
            LazyColumn {
                itemsIndexed(entries) { index, item ->
                    LeaderboardRow(rank = index + 1, entry = item)
                    if (index < entries.size - 1) {
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
    }
}


class Leaderboard : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lvlnum = intent.getIntExtra("Level", 1)

        setContent {
            TiltBallGameTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        LeaderboardScreen()

                        Button(
                            onClick = {
                                val intent = Intent(this@Leaderboard, MainActivity::class.java)
                                startActivity(intent)
                            },
                            modifier = Modifier.size(50.dp).align(Alignment.BottomEnd).offset(x = -16.dp, y = -40.dp), // bottom-right
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("<", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}
