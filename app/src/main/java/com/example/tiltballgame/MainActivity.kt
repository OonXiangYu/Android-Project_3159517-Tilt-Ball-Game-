package com.example.tiltballgame

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tiltballgame.ui.theme.TiltBallGameTheme

// variables
var lvlNum = mutableStateOf(0)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TiltBallGameTheme {
                NavigationScreen()
            }
        }
    }

    @Composable
    fun NavigationScreen(){ // Route setting
        val navController = rememberNavController()
        Surface(modifier = Modifier.fillMaxSize()){
            NavHost(
                navController = navController,
                startDestination = "home"
            ){
                composable ("home"){ // home route
                    HomeScreen( navController )
                }
                composable ("level"){ // level route
                    LevelScreen( navController )
                }
            }
        }
    }

    @Composable
    fun HomeScreen( navController: NavController ){ // Home screen
        Surface(modifier = Modifier.fillMaxSize(), color = Color.Black){
            Column(
                verticalArrangement = Arrangement.Center, // Center Design
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Row{
                    Text(text = "Tilt", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    Text(text = "Ball", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.secondary)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button({ navController.navigate("level")}){ // Button to next page (Level page)
                    Text("Start")
                }
            }
        }
    }

    @Composable
    fun LevelScreen( navController: NavController ){ // Level screen
        val context = LocalContext.current // Current Screen

        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background){
            Column(
                modifier = Modifier.fillMaxSize().padding(25.dp)
            ) {
                Button({ navController.navigate("home")},
                    modifier = Modifier.height(50.dp).width(50.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary, // Button color
                    ),
                    contentPadding = PaddingValues(0.dp) // remove internal padding
                ){
                    Image(
                        painter = painterResource(id = R.drawable.whilte_left_arrow),
                        contentDescription = "Back Arrow",
                        modifier = Modifier.height(25.dp).wrapContentWidth(),
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
            Column(
                verticalArrangement = Arrangement.Center, // Center Design
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    LevelButton(level = 1) { selectedLevel ->
                        lvlNum.value = selectedLevel
                        val intent = Intent(context, GamePlay::class.java)
                        intent.putExtra("Level Number", selectedLevel)
                        context.startActivity(intent)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    LevelButton(level = 2) { selectedLevel ->
                        lvlNum.value = selectedLevel
                        val intent = Intent(context, GamePlay::class.java)
                        intent.putExtra("Level Number", selectedLevel)
                        context.startActivity(intent)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    LevelButton(level = 3) { selectedLevel ->
                        lvlNum.value = selectedLevel
                        val intent = Intent(context, GamePlay::class.java)
                        intent.putExtra("Level Number", selectedLevel)
                        context.startActivity(intent)
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                Row {
                    LevelButton(level = 4) { selectedLevel ->
                        lvlNum.value = selectedLevel
                        val intent = Intent(context, GamePlay::class.java)
                        intent.putExtra("Level Number", selectedLevel)
                        context.startActivity(intent)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    LevelButton(level = 5) { selectedLevel ->
                        lvlNum.value = selectedLevel
                        val intent = Intent(context, GamePlay::class.java)
                        intent.putExtra("Level Number", selectedLevel)
                        context.startActivity(intent)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    LevelButton(level = 6) { selectedLevel ->
                        lvlNum.value = selectedLevel
                        val intent = Intent(context, GamePlay::class.java)
                        intent.putExtra("Level Number", selectedLevel)
                        context.startActivity(intent)
                    }
                }
            }
        }
    }

    @Composable // Reusable button for going to game level
    fun LevelButton(
        level: Int,
        color: Color = MaterialTheme.colorScheme.primary,
        onClick: (Int) -> Unit
    ) {
        Button(
            onClick = { onClick(level) },
            modifier = Modifier
                .height(75.dp)
                .width(75.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = color,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = "$level",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }

}
