package com.example.tiltballgame

import android.content.Intent
import android.os.Bundle
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tiltballgame.ui.theme.TiltBallGameTheme

private var timeSpend = 0L

class VictoryPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        timeSpend = intent.getLongExtra("Time Spend", 0L)

        val totalSec = (timeSpend / 1000).toInt()
        val min = totalSec / 60
        val sec = totalSec % 60

        val timeText = if (min > 0) {
            String.format("%dm %02ds", min, sec)
        } else {
            "$sec s" // still under a minute
        }

        setContent {
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
}
