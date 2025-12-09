package com.example.tiltballgame

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import com.example.tiltballgame.ui.theme.TiltBallGameTheme

private var timeSpend = 0L
private var lvlnum = 0
class VictoryPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        timeSpend = intent.getLongExtra("Time Spend", 0L)
        lvlnum = intent.getIntExtra("Level Number", 0)

        val totalSec = (timeSpend / 1000).toInt()
        val min = totalSec / 60
        val sec = totalSec % 60

        val timeText = if (min > 0) {
            String.format("%dm %02ds", min, sec)
        } else {
            "$sec s" // still under a minute
        }

        setContent {

            var username by remember { mutableStateOf("") }
            var isTop10 by remember { mutableStateOf<Boolean?>(null) }
            var submitted by remember { mutableStateOf(false) }

            // Check top 10
            LaunchedEffect(lvlnum, timeSpend) {
                isTop10 = checkIfTop10(lvlnum, timeSpend) // coroutine suspends until result
            }

            TiltBallGameTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Black){
                    Column(
                        verticalArrangement = Arrangement.Center, // Center Design
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Text(text = "Victory", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSecondary)
                        Spacer(modifier = Modifier.width(10.dp))

                        Text(text = "$timeText", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimary)
                        Spacer(modifier = Modifier.width(50.dp))

                        when { // wait for top 10 checking
                            isTop10 == null -> {
                            Text(text = "Checking leaderboard", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimary)
                        }

                        isTop10 == true && !submitted -> { // if the time in top 10 and haven't press summit
                            OutlinedTextField(
                                value = username,
                                onValueChange = { username = it },
                                label = { Text("Enter username") }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    if (username.isBlank()) { // validation for empty username
                                        Toast.makeText(
                                            this@VictoryPage,
                                            "Username cannot be empty",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        lifecycleScope.launch { // enable to destroy coroutines while it fail
                                            val success = submitScore(lvlnum, username, timeSpend)
                                            if (success) submitted = true
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.onSecondary,
                                    contentColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Text("Submit to Leaderboard", style = MaterialTheme.typography.titleMedium)
                            }
                        }

                        submitted -> {
                            Text(text = "Record Submitted!", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    val intent = Intent(this@VictoryPage, Leaderboard::class.java)
                                    startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.onSecondary,
                                    contentColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Text("Leaderboard", style = MaterialTheme.typography.titleMedium)
                            }
                        }

                        isTop10 == false -> {
                            Text(text = "Not in top 10.", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }

                        Button(
                            onClick = {
                                val intent = Intent(this@VictoryPage, MainActivity::class.java)
                                startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onSecondary,
                                contentColor = MaterialTheme.colorScheme.surface
                            )
                        ){
                            Text("Home", style = MaterialTheme.typography.titleMedium)
                        }

                    }
                }
            }
        }
    }

    suspend fun checkIfTop10(level: Int, time: Long): Boolean {
        val db = FirebaseFirestore.getInstance() // get db
        val collectionName = "Level$level" // correct collection

        return try {
            val result = db.collection(collectionName)  // point to correct collection
                .orderBy("time") // sort by time
                .limit(10) // i need only 10
                .get()
                .await() // suspend until data is returned

            if (result.isEmpty) return true // if collection empty then the time is in top 10

            val times: List<Long> = result.mapNotNull { it.getLong("time") } // convert doc into list
            if (times.size < 10) return true // if the collection contain less then 10 data, the time also in top 10

            val worstTop10 = times.maxOrNull() ?: Long.MAX_VALUE // get the slowest(largest) time and compare
            time < worstTop10
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun submitScore(level: Int, username: String, time: Long): Boolean {
        return try {
            val db = FirebaseFirestore.getInstance()
            val data = hashMapOf( // prepare data in the format
                "username" to username,
                "time" to time
            )
            db.collection("Level$level").add(data).await() // append it into correct collection
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}
