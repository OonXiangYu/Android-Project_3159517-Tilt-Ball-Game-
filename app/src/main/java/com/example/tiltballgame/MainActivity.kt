package com.example.tiltballgame

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
            NavigationScreen()
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
        Surface(modifier = Modifier.fillMaxSize()){
            Column(
                verticalArrangement = Arrangement.Center, // Center Design
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text("Home")
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

        Surface(modifier = Modifier.fillMaxSize(), color = Color.Black){
            Column(
                modifier = Modifier.fillMaxSize().padding(25.dp)
            ) {
                Button({ navController.navigate("home")},
                    modifier = Modifier.height(50.dp).width(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF476F), // Button color
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
                    Button({ // Go to lvl1
                        lvlNum.value = 1
                        val intent = Intent(context, GamePlay::class.java)
                        intent.putExtra("Level Number", lvlNum.value)
                        startActivity(intent)
                    },
                        modifier = Modifier.height(75.dp).width(75.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF118AF2), // Button color
                            contentColor = Color.White           // Text color
                        )
                    ){
                        Text("1", fontSize = 24.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button({ // Go to lvl2
                        lvlNum.value = 2
                        val intent = Intent(context, GamePlay::class.java)
                        intent.putExtra("Level Number", lvlNum.value)
                        startActivity(intent)
                    },
                        modifier = Modifier.height(75.dp).width(75.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF118AF2), // Button color
                            contentColor = Color.White           // Text color
                        )
                    ){
                        Text("2", fontSize = 24.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button({ // Go to lvl3
                        lvlNum.value = 3
                        val intent = Intent(context, GamePlay::class.java)
                        intent.putExtra("Level Number", lvlNum.value)
                        startActivity(intent)
                    },
                        modifier = Modifier.height(75.dp).width(75.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF118AF2), // Button color
                            contentColor = Color.White           // Text color
                        )
                    ){
                        Text("3", fontSize = 24.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                Row {
                    Button({ // Go to lvl4
                        lvlNum.value = 4
                        val intent = Intent(context, GamePlay::class.java)
                        intent.putExtra("Level Number", lvlNum.value)
                        startActivity(intent)
                    },
                        modifier = Modifier.height(75.dp).width(75.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF118AF2), // Button color
                            contentColor = Color.White           // Text color
                        )
                    ){
                        Text("4", fontSize = 24.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button({ // Go to lvl5
                        lvlNum.value = 5
                        val intent = Intent(context, GamePlay::class.java)
                        intent.putExtra("Level Number", lvlNum.value)
                        startActivity(intent)
                    },
                        modifier = Modifier.height(75.dp).width(75.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF118AF2), // Button color
                            contentColor = Color.White           // Text color
                        )
                    ){
                        Text("5", fontSize = 24.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button({ // Go to lvl6
                        lvlNum.value = 6
                        val intent = Intent(context, GamePlay::class.java)
                        intent.putExtra("Level Number", lvlNum.value)
                        startActivity(intent)
                    },
                        modifier = Modifier.height(75.dp).width(75.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF118AF2), // Button color
                            contentColor = Color.White           // Text color
                        )
                    ){
                        Text("6", fontSize = 24.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
