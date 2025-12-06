package com.example.tiltballgame

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import com.example.tiltballgame.ui.theme.TiltBallGameTheme
import kotlinx.coroutines.*

data class WorldObject(
    val x: Float,
    val y: Float,
    val width: Float = 80f,
    val height: Float = 80f,
    var colorCode: String = "#FFFFFF",
    val isColorChanger: Boolean = false,
    var isGoal: Boolean = false,
    val type: String = "block",
    val text: String = "",
    val teleportId: Int? = null,
    val radius: Float = 40f,
    val shape: String = "rect"
)

class GamePlay : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager // Manage the sensor
    private lateinit var ballView: View // The custom view where i draw the ball

    private var ballX = 0f // X-axis of ball
    private var ballY = 0f // Y-axis of ball
    private val radius = 60f // Radius of ball
    private val paint = Paint().apply { // Color of Ball
        color = Color.parseColor("#118AF2")
        isAntiAlias = true
    }

    private var maxX = 0f // screen width
    private var maxY = 0f //  screen height

    private var worldWidth = 0f // world width
    private var worldHeight = 0f // world height

    private var cameraX = 0f // camera x-axis
    private var cameraY = 0f // camera y-axis

    private var objects: List<WorldObject> = listOf() // objs for design lvl
    private var lvlNum: Int = 0 // level


    private var startTime = 0L // the time u start this level
    private var timeSpend = 0L // time u spend on this level

    var pauseBtnX = 0f // x axis of pause button
    var pauseBtnY = 0f // y axis of pause button
    val pauseBtnSize = 120f

    var isPaused = false // boolean for pause

    var totalTimePaused = 0L // cumulative paused time

    var pauseStartTime = 0L // when the pause start

    var homeBtnX = 0f // x axis of home button
    var homeBtnY = 0f // y axis of home button
    var homeBtnSize = 0f

    private var tpCD = 0L // cd for teleport time

    private val gameScope = CoroutineScope(Dispatchers.Main + SupervisorJob()) // handle multi thread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lvlNum = intent.getIntExtra("Level Number", 0)

        startTime = System.currentTimeMillis()

        ballView = object : View(this) { // Draw the ball
            override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
                maxX = w.toFloat()
                maxY = h.toFloat()

                // World is 3x bigger than screen
                worldWidth = maxX * 3f
                worldHeight = maxY * 3f

                // Start in the middle of the world
                ballX = worldWidth / 2f
                ballY = worldHeight / 2f

                objects = when (lvlNum) {
                    1 -> listOf(
                        WorldObject(ballX - 200f , ballY - 500f, 3000f, 100f),
                        WorldObject(ballX - 200f , ballY - 400f, 100f, 1000f),
                        WorldObject(ballX - 200f , ballY + 500f, 3000f, 100f),
                        WorldObject(ballX + 200f , ballY - 400f, 100f, 900f, "#EF476F", isColorChanger = true),
                        WorldObject(ballX + 200f , ballY,100f,50f, type = "text", text = "Click"),
                        WorldObject(ballX + 700f , ballY - 400f, 100f, 900f, "#EF476F", isColorChanger = true),
                        WorldObject(ballX + 700f , ballY,100f,50f, type = "text", text = "Click"),
                        WorldObject(ballX + 450f , ballY - 300f,100f,50f, type = "text", text = "Match Color to Pass"),
                        WorldObject(ballX + 1200f , ballY - 400f, 100f, 750f),
                        WorldObject(ballX + 1200f , ballY + 350f, 100f, 150f, "#118AF2", isColorChanger = true),
                        WorldObject(ballX + 2000f , ballY - 250f, 100f, 750f),
                        WorldObject(ballX + 2000f , ballY - 400f, 100f, 150f, "#EF476F", isColorChanger = true),
                        WorldObject(ballX + 2500f , ballY + 300f,100f,50f, type = "text", text = "Goal"),
                        WorldObject(ballX + 2500f , ballY + 350f, 100f,100f, "#08F26E", isGoal = true),
                        WorldObject(ballX + 2800f , ballY - 500f, 100f, 1100f),
                    )
                    2 -> listOf(
                        WorldObject(ballX - 3000f , ballY - 800f, 6000f, 600f),
                        WorldObject(ballX - 4000f , ballY - 800f, 1000f, 600f),
                        WorldObject(ballX - 200f , ballY - 200f, 100f, 500f, "#118AF2", isColorChanger = true),
                        WorldObject(ballX + 300f , ballY - 200f, 100f, 500f, "#EF476F", isColorChanger = true),
                        WorldObject(ballX - 2800f , ballY - 200f, 100f, 500f, "#EF476F", isColorChanger = true),
                        WorldObject(ballX - 3000f , ballY + 300f, 6000f, 600f),
                        WorldObject(ballX + 3200f , ballY - 2000f, 800f, 4000f),
                        WorldObject(ballX - 4000f , ballY - 2000f, 800f, 4000f),
                        WorldObject(ballX + 1700f , ballY - 200f, 1300f, 500f),
                        WorldObject(ballX - 4000f, ballY + 1500f, 8000f, 400f),
                        WorldObject(ballX - 2800f , ballY + 900f, 100f, 310f, "#EF476F", isColorChanger = true),
                        WorldObject(ballX - 2800f , ballY + 1200f, 100f, 300f, "#118AF2", isColorChanger = true),
                        WorldObject(ballX - 2500f , ballY + 900f, 100f, 310f, "#118AF2", isColorChanger = true),
                        WorldObject(ballX - 2500f , ballY + 1200f, 100f, 300f, "#EF476F", isColorChanger = true),
                        WorldObject(ballX - 2200f , ballY + 1050f, 100f, 450f),
                        WorldObject(ballX - 1950f , ballY + 1050f, 400f, 100f),
                        WorldObject(ballX - 1550f , ballY + 850f, 100f, 300f),
                        WorldObject(ballX - 1200f , ballY + 900f, 100f, 600f, "#118AF2", isColorChanger = true),
                        WorldObject(ballX - 400f , ballY + 1050f, 100f, 450f),
                        WorldObject(ballX , ballY + 900f, 100f, 450f),
                        WorldObject(ballX + 500f , ballY + 900f, 100f, 600f, "#EF476F", isColorChanger = true),
                        WorldObject(ballX + 800f, ballY + 900f, 100f, 450f),
                        WorldObject(ballX + 1200f , ballY + 1050f, 100f, 450f),
                        WorldObject(ballX + 1600f, ballY + 900f, 100f, 450f),
                        WorldObject(ballX + 2000f , ballY + 900f, 100f, 310f, "#EF476F", isColorChanger = true),
                        WorldObject(ballX + 2000f , ballY + 1200f, 100f, 300f, "#118AF2", isColorChanger = true),
                        WorldObject(ballX + 2500f , ballY + 900f, 100f, 310f, "#118AF2", isColorChanger = true),
                        WorldObject(ballX + 2500f , ballY + 1200f, 100f, 300f, "#EF476F", isColorChanger = true),
                        WorldObject(ballX + 3000f , ballY - 800f, 200f, 100f, "#118AF2", isColorChanger = true),
                        WorldObject(ballX - 4000f, ballY - 2000f, 8000f, 400f),
                        WorldObject(ballX + 2500f , ballY - 1100f, 100f, 300f),
                        WorldObject(ballX + 2500f , ballY - 1600f, 100f, 300f),
                        WorldObject(ballX + 2200f , ballY - 1200f, 100f, 400f),
                        WorldObject(ballX + 2200f , ballY - 1700f, 100f, 300f),
                        WorldObject(ballX + 1900f , ballY - 1000f, 100f, 300f),
                        WorldObject(ballX + 1900f , ballY - 1600f, 100f, 400f),
                        WorldObject(ballX + 1600f , ballY - 1200f, 100f, 400f, "#EF476F", isColorChanger = true),
                        WorldObject(ballX + 1600f , ballY - 1600f, 100f, 400f, "#118AF2", isColorChanger = true),
                        WorldObject(ballX + 1300f , ballY - 1000f, 100f, 300f),
                        WorldObject(ballX + 1300f , ballY - 1600f, 100f, 400f),
                        WorldObject(ballX + 1000f , ballY - 1200f, 100f, 400f),
                        WorldObject(ballX + 1000f , ballY - 1700f, 100f, 300f),
                        WorldObject(ballX + 700f , ballY - 1100f, 100f, 300f),
                        WorldObject(ballX + 700f , ballY - 1600f, 100f, 300f),
                        WorldObject(ballX + 400f , ballY - 1000f, 100f, 200f),
                        WorldObject(ballX + 400f , ballY - 1600f, 100f, 400f),
                        WorldObject(ballX + 100f , ballY - 900f, 100f, 200f),
                        WorldObject(ballX + 100f , ballY - 1600f, 100f, 500f),
                        WorldObject(ballX - 200f , ballY - 1000f, 100f, 200f),
                        WorldObject(ballX - 200f , ballY - 1600f, 100f, 400f),
                        WorldObject(ballX - 500f , ballY - 1100f, 100f, 300f),
                        WorldObject(ballX - 500f , ballY - 1600f, 100f, 300f),
                        WorldObject(ballX - 800f , ballY - 1200f, 100f, 400f, "#118AF2", isColorChanger = true),
                        WorldObject(ballX - 800f , ballY - 1600f, 100f, 400f, "#EF476F", isColorChanger = true),
                        WorldObject(ballX - 1100f , ballY - 1000f, 100f, 200f),
                        WorldObject(ballX - 1100f , ballY - 1600f, 100f, 400f),
                        WorldObject(ballX - 1400f , ballY - 1100f, 100f, 300f),
                        WorldObject(ballX - 1400f , ballY - 1600f, 100f, 300f),
                        WorldObject(ballX - 1700f , ballY - 1100f, 100f, 300f),
                        WorldObject(ballX - 1700f , ballY - 1600f, 100f, 300f),
                        WorldObject(ballX - 2000f , ballY - 1000f, 100f, 300f),
                        WorldObject(ballX - 2000f , ballY - 1600f, 100f, 400f),
                        WorldObject(ballX - 2300f , ballY - 900f, 100f, 300f),
                        WorldObject(ballX - 2300f , ballY - 1600f, 100f, 500f),
                        WorldObject(ballX - 2600f , ballY - 1200f, 100f, 400f),
                        WorldObject(ballX - 2600f , ballY - 1600f, 100f, 200f),
                        WorldObject(ballX - 3200f , ballY - 1600f, 100f,100f, "#08F26E", isGoal = true),
                    )
                    3 -> listOf(
                        WorldObject(ballX - 1100f , ballY - 350f, colorCode = "#7B2CBF", type = "tpBg", radius = 90f, shape = "circle"),
                        WorldObject(ballX - 2600f , ballY - 1200f, colorCode = "#7B2CBF", type = "tpBg", radius = 90f, shape = "circle"),
                        WorldObject(ballX - 3600f , ballY - 1500f, colorCode = "#7B2CBF", type = "tpBg", radius = 90f, shape = "circle"),
                        WorldObject(ballX + 1100f , ballY - 1500f, colorCode = "#7B2CBF", type = "tpBg", radius = 90f, shape = "circle"),
                        WorldObject(ballX - 1100f , ballY - 1200f, colorCode = "#7B2CBF", type = "tpBg", radius = 90f, shape = "circle"),
                        WorldObject(ballX - 3600f , ballY + 100f, colorCode = "#7B2CBF", type = "tpBg", radius = 90f, shape = "circle"),
                        WorldObject(ballX - 2600f , ballY + 100f, colorCode = "#7B2CBF", type = "tpBg", radius = 90f, shape = "circle"),
                        WorldObject(ballX + 1100f , ballY + 1350f, colorCode = "#7B2CBF", type = "tpBg", radius = 90f, shape = "circle"),
                        WorldObject(ballX - 1100f , ballY + 950f, colorCode = "#7B2CBF", type = "tpBg", radius = 90f, shape = "circle"),
                        WorldObject(ballX + 2600f , ballY + 950f, colorCode = "#7B2CBF", type = "tpBg", radius = 90f, shape = "circle"),
                        WorldObject(ballX + 1100f , ballY + 100f, colorCode = "#7B2CBF", type = "tpBg", radius = 90f, shape = "circle"),
                        WorldObject(ballX + 3600f , ballY - 350f, colorCode = "#7B2CBF", type = "tpBg", radius = 90f, shape = "circle"),
                        WorldObject(ballX + 2600f , ballY + 100f, colorCode = "#7B2CBF", type = "tpBg", radius = 90f, shape = "circle"),
                        WorldObject(ballX - 2600f , ballY + 950f, colorCode = "#7B2CBF", type = "tpBg", radius = 90f, shape = "circle"),
                        WorldObject(ballX - 3600f , ballY + 1350f, colorCode = "#7B2CBF", type = "tpBg", radius = 90f, shape = "circle"),
                        WorldObject(ballX - 2600f , ballY - 350f, colorCode = "#7B2CBF", type = "tpBg", radius = 90f, shape = "circle"),
                        WorldObject(ballX - 3600f , ballY - 350f, colorCode = "#7B2CBF", type = "tpBg", radius = 90f, shape = "circle"),
                        WorldObject(ballX + 3600f , ballY - 1200f, colorCode = "#7B2CBF", type = "tpBg", radius = 90f, shape = "circle"),
                        WorldObject(ballX + 2600f , ballY - 1500f, colorCode = "#7B2CBF", type = "tpBg", radius = 90f, shape = "circle"),
                        WorldObject(ballX + 3600f , ballY + 950f, colorCode = "#7B2CBF", type = "tpBg", radius = 90f, shape = "circle"),
                        WorldObject(ballX - 4000f , ballY - 1900f, 8000f, 300f),
                        WorldObject(ballX - 4000f , ballY + 1550f, 8000f, 300f),
                        WorldObject(ballX - 4000f , ballY - 1600f, 300f, 3200f),
                        WorldObject(ballX + 3700f , ballY - 1600f, 300f, 3200f),
                        WorldObject(ballX - 4000f , ballY - 1000f, 8000f, 550f),
                        WorldObject(ballX - 4000f , ballY + 300f, 8000f, 550f),
                        WorldObject(ballX - 2500f , ballY - 1600f, 1300f, 3200f),
                        WorldObject(ballX + 1200f , ballY - 1600f, 1300f, 3200f),
                        WorldObject(ballX - 1100f , ballY - 350f, colorCode = "#C77DFF", type = "tpIn", teleportId = 1, radius = 75f, shape = "circle"),
                        WorldObject(ballX - 2600f , ballY - 1200f, colorCode = "#C77DFF", type = "tpOut", teleportId = 1, radius = 75f, shape = "circle"),
                        WorldObject(ballX - 3600f , ballY - 1500f, colorCode = "#C77DFF", type = "tpIn", teleportId = 3, radius = 75f, shape = "circle"),
                        WorldObject(ballX + 1100f , ballY - 1500f, colorCode = "#C77DFF", type = "tpOut", teleportId = 3, radius = 75f, shape = "circle"),
                        WorldObject(ballX - 1100f , ballY - 1200f, colorCode = "#C77DFF", type = "tpIn", teleportId = 6, radius = 75f, shape = "circle"),
                        WorldObject(ballX - 3600f , ballY + 100f, colorCode = "#C77DFF", type = "tpOut", teleportId = 6, radius = 75f, shape = "circle"),
                        WorldObject(ballX - 2600f , ballY + 100f, colorCode = "#C77DFF", type = "tpIn", teleportId = 8, radius = 75f, shape = "circle"),
                        WorldObject(ballX + 1100f , ballY + 1350f, colorCode = "#C77DFF", type = "tpOut", teleportId = 8, radius = 75f, shape = "circle"),
                        WorldObject(ballX - 1100f , ballY + 950f, colorCode = "#C77DFF", type = "tpIn", teleportId = 9, radius = 75f, shape = "circle"),
                        WorldObject(ballX + 2600f , ballY + 950f, colorCode = "#C77DFF", type = "tpOut", teleportId = 9, radius = 75f, shape = "circle"),
                        WorldObject(ballX + 1100f , ballY + 100f, colorCode = "#C77DFF", type = "tpIn", teleportId = 2, radius = 75f, shape = "circle"),
                        WorldObject(ballX + 3600f , ballY - 350f, colorCode = "#C77DFF", type = "tpOut", teleportId = 2, radius = 75f, shape = "circle"),
                        WorldObject(ballX + 2600f , ballY + 100f, colorCode = "#C77DFF", type = "tpIn", teleportId = 4, radius = 75f, shape = "circle"),
                        WorldObject(ballX - 2600f , ballY + 950f, colorCode = "#C77DFF", type = "tpOut", teleportId = 4, radius = 75f, shape = "circle"),
                        WorldObject(ballX - 3600f , ballY + 1350f, colorCode = "#C77DFF", type = "tpIn", teleportId = 5, radius = 75f, shape = "circle"),
                        WorldObject(ballX - 2600f , ballY - 350f, colorCode = "#C77DFF", type = "tpOut", teleportId = 5, radius = 75f, shape = "circle"),
                        WorldObject(ballX - 3600f , ballY - 350f, colorCode = "#C77DFF", type = "tpIn", teleportId = 7, radius = 75f, shape = "circle"),
                        WorldObject(ballX + 3600f , ballY - 1200f, colorCode = "#C77DFF", type = "tpOut", teleportId = 7, radius = 75f, shape = "circle"),
                        WorldObject(ballX + 2600f , ballY - 1500f, colorCode = "#C77DFF", type = "tpIn", teleportId = 10, radius = 75f, shape = "circle"),
                        WorldObject(ballX + 3600f , ballY + 950f, colorCode = "#C77DFF", type = "tpOut", teleportId = 10, radius = 75f, shape = "circle"),
                        WorldObject(ballX - 950f , ballY - 350f,100f,50f, type = "text", text = "TP?"),
                        WorldObject(ballX - 500f , ballY - 450f, 100f, 750f, "#EF476F", isColorChanger = true),
                        WorldObject(ballX + 500f , ballY - 450f, 100f, 750f, "#EF476F", isColorChanger = true),
                        WorldObject(ballX - 3200f , ballY - 1600f, 100f, 600f, "#118AF2", isColorChanger = true),
                        WorldObject(ballX + 900f , ballY - 1600f, 100f, 400f),
                        WorldObject(ballX + 600f , ballY - 1400f, 100f, 400f),
                        WorldObject(ballX , ballY - 1600f, 100f, 600f, "#EF476F", isColorChanger = true),
                        WorldObject(ballX - 900f , ballY - 1400f, 100f, 400f),
                        WorldObject(ballX - 600f , ballY - 1600f, 100f, 400f),
                        WorldObject(ballX , ballY + 850f, 100f, 700f, "#118AF2", isColorChanger = true),
                        WorldObject(ballX + 250f , ballY + 850f, 100f, 300f),
                        WorldObject(ballX + 250f , ballY + 1350f, 100f, 200f),
                        WorldObject(ballX + 500f , ballY + 850f, 100f, 200f),
                        WorldObject(ballX + 500f , ballY + 1250f, 100f, 300f),
                        WorldObject(ballX + 750f , ballY + 850f, 100f, 100f),
                        WorldObject(ballX + 750f , ballY + 1150f, 100f, 400f),
                        WorldObject(ballX - 250f , ballY + 850f, 100f, 300f),
                        WorldObject(ballX - 250f , ballY + 1350f, 100f, 200f),
                        WorldObject(ballX - 500f , ballY + 850f, 100f, 200f),
                        WorldObject(ballX - 500f , ballY + 1250f, 100f, 300f),
                        WorldObject(ballX - 750f , ballY + 850f, 100f, 100f),
                        WorldObject(ballX - 750f , ballY + 1150f, 100f, 400f),
                        WorldObject(ballX + 3100f, ballY - 450f, 100f, 750f, "#118AF2", isColorChanger = true),
                        WorldObject(ballX - 3100f, ballY + 850f, 100f, 700f, "#EF476F", isColorChanger = true),
                        WorldObject(ballX + 3100f, ballY - 1600f, 100f, 600f, "#118AF2", isColorChanger = true),
                        WorldObject(ballX + 3600f , ballY + 1450f, 100f,100f, "#08F26E", isGoal = true),
                    )
                    4 -> listOf(
                        WorldObject(ballX - 1600f , ballY - 600f, colorCode = "#7B2CBF", type = "tpBg", radius = 90f, shape = "circle"),
                        WorldObject(ballX - 2100f , ballY - 200f, colorCode = "#7B2CBF", type = "tpBg", radius = 90f, shape = "circle"),
                        WorldObject(ballX - 4000f , ballY - 1900f, 8000f, 300f),
                        WorldObject(ballX - 4000f , ballY + 1550f, 8000f, 300f),
                        WorldObject(ballX - 4000f , ballY - 1600f, 300f, 3200f),
                        WorldObject(ballX + 3700f , ballY - 1600f, 300f, 3200f),
                        WorldObject(ballX - 2000f , ballY - 1000f, 4000f, 300f),
                        WorldObject(ballX - 2000f , ballY + 900f, 4000f, 300f),
                        WorldObject(ballX - 2000f, ballY - 1000f, 300f, 2000f),
                        WorldObject(ballX + 1700f, ballY - 1000f, 300f, 2000f),
                        WorldObject(ballX - 1400f , ballY + 500f, 2800f, 100f),
                        WorldObject(ballX - 800f , ballY - 700f, 100f, 1300f),
                        WorldObject(ballX - 3700f , ballY, 2000f, 300f),
                        WorldObject(ballX + 300f , ballY - 500f, 100f, 1000f),
                        WorldObject(ballX - 700f , ballY - 200f, 800f, 100f),
                        WorldObject(ballX - 400f , ballY - 500f, 800f, 100f),
                        WorldObject(ballX + 600f , ballY - 700f, 100f, 1000f),
                        WorldObject(ballX + 900f , ballY - 500f, 100f, 1000f),
                        WorldObject(ballX + 1200f , ballY - 700f, 500f, 1000f),
                        WorldObject(ballX - 1700f , ballY + 200f, 700f, 100f),
                        WorldObject(ballX - 1500f , ballY - 100f, 700f, 100f),
                        WorldObject(ballX - 1700f , ballY - 400f, 700f, 100f),
                        WorldObject(ballX - 3400f , ballY - 500f, 1500f, 100f),
                        WorldObject(ballX - 3700f , ballY - 1700f, 1500f, 800f),
                        WorldObject(ballX - 3400f , ballY - 900f, 100f, 200f, "#EF476F", isColorChanger = true),
                        WorldObject(ballX - 3400f , ballY - 700f, 100f, 200f, "#118AF2", isColorChanger = true),
                        WorldObject(ballX - 3100f , ballY - 900f, 100f, 200f, "#EF476F", isColorChanger = true),
                        WorldObject(ballX - 3100f , ballY - 700f, 100f, 200f, "#118AF2", isColorChanger = true),
                        WorldObject(ballX - 2800f , ballY - 900f, 100f, 200f, "#118AF2", isColorChanger = true),
                        WorldObject(ballX - 2800f , ballY - 700f, 100f, 200f, "#EF476F", isColorChanger = true),
                        WorldObject(ballX - 2500f , ballY - 900f, 100f, 200f, "#EF476F", isColorChanger = true),
                        WorldObject(ballX - 2500f , ballY - 700f, 100f, 200f, "#118AF2", isColorChanger = true),
                        WorldObject(ballX - 2000f , ballY - 1700f, 100f, 300f),
                        WorldObject(ballX - 2000f , ballY - 1200f, 100f, 300f),
                        WorldObject(ballX - 1700f , ballY - 1800f, 100f, 300f),
                        WorldObject(ballX - 1700f , ballY - 1300f, 100f, 400f),
                        WorldObject(ballX - 1400f , ballY - 1900f, 100f, 300f),
                        WorldObject(ballX - 1400f , ballY - 1400f, 100f, 500f),
                        WorldObject(ballX - 1100f , ballY - 1800f, 100f, 300f),
                        WorldObject(ballX - 1100f , ballY - 1300f, 100f, 400f),
                        WorldObject(ballX - 800f , ballY - 1700f, 100f, 300f),
                        WorldObject(ballX - 800f , ballY - 1200f, 100f, 300f),
                        WorldObject(ballX - 500f , ballY - 1700f, 100f, 400f),
                        WorldObject(ballX - 500f , ballY - 1100f, 100f, 300f),
                        WorldObject(ballX - 200f , ballY - 1700f, 100f, 500f),
                        WorldObject(ballX - 200f , ballY - 1000f, 100f, 300f),
                        WorldObject(ballX + 100f , ballY - 1700f, 100f, 400f),
                        WorldObject(ballX + 100f , ballY - 1100f, 100f, 300f),
                        WorldObject(ballX + 400f , ballY - 1700f, 100f, 300f),
                        WorldObject(ballX + 400f , ballY - 1200f, 100f, 300f),
                        WorldObject(ballX + 700f , ballY - 1800f, 100f, 300f),
                        WorldObject(ballX + 700f , ballY - 1300f, 100f, 400f),
                        WorldObject(ballX + 1100f , ballY - 1900f, 100f, 300f),
                        WorldObject(ballX + 1100f , ballY - 1400f, 100f, 500f),
                        WorldObject(ballX + 1400f , ballY - 1800f, 100f, 300f),
                        WorldObject(ballX + 1400f , ballY - 1300f, 100f, 400f),
                        WorldObject(ballX + 1700f , ballY - 1700f, 100f, 300f),
                        WorldObject(ballX + 1700f , ballY - 1200f, 100f, 300f),
                        WorldObject(ballX + 1700f , ballY - 1200f, 100f, 300f),
                        WorldObject(ballX + 2000f , ballY - 2000f, 1700f, 800f),
                        WorldObject(ballX + 2000f , ballY - 1000f, 1100f, 100f),
                        WorldObject(ballX + 3400f , ballY - 1000f, 800f, 100f),
                        WorldObject(ballX + 2000f , ballY - 700f, 900f, 100f),
                        WorldObject(ballX + 3200f , ballY - 700f, 800f, 100f),
                        WorldObject(ballX + 2000f , ballY - 400f, 700f, 100f),
                        WorldObject(ballX + 3000f , ballY - 400f, 800f, 100f),
                        WorldObject(ballX + 2000f , ballY - 100f, 900f, 100f, "#EF476F", isColorChanger = true),
                        WorldObject(ballX + 2800f , ballY - 100f, 900f, 100f, "#118AF2", isColorChanger = true),
                        WorldObject(ballX + 2000f , ballY + 200f, 500f, 100f),
                        WorldObject(ballX + 2800f , ballY + 200f, 900f, 100f),
                        WorldObject(ballX + 2000f , ballY + 500f, 300f, 100f),
                        WorldObject(ballX + 2600f , ballY + 500f, 1100f, 100f),
                        WorldObject(ballX + 2000f , ballY + 800f, 100f, 400f),
                        WorldObject(ballX + 2400f , ballY + 800f, 1300f, 800f),
                        WorldObject(ballX - 1600f , ballY - 600f, colorCode = "#C77DFF", type = "tpIn", teleportId = 1, radius = 75f, shape = "circle"),
                        WorldObject(ballX - 2100f , ballY - 200f, colorCode = "#C77DFF", type = "tpOut", teleportId = 1, radius = 75f, shape = "circle"),
                    )
                    else -> listOf(
                    )
                }
            }

            override fun onTouchEvent(event: MotionEvent): Boolean { // Touch event
                if (event.action == MotionEvent.ACTION_DOWN) { // If user tap on screen

                    // Check the relative coordinate cuz my camera will mov
                    val touchX = event.x + cameraX
                    val touchY = event.y + cameraY

                    // Check the coordinate just specific for UI
                    val touchCamX = event.x
                    val touchCamY = event.y

                    if (touchCamX >= pauseBtnX && touchCamX <= pauseBtnX + pauseBtnSize  &&
                        touchCamY >= pauseBtnY  && touchCamY <= pauseBtnY + pauseBtnSize ) { // When player click the pause button

                        togglePause()
                        invalidate()
                        return true
                    }

                    if (touchCamX >= homeBtnX && touchCamX <= homeBtnX + homeBtnSize  &&
                        touchCamY >= homeBtnY  && touchCamY <= homeBtnY + homeBtnSize &&
                        isPaused) { // When player click the home button and is pause

                        val intent = Intent(this@GamePlay, MainActivity::class.java)
                        startActivity(intent)

                    }

                    if(!isPaused){ // If the game not pause
                        objects.forEach { obj ->
                            if (obj.isColorChanger && // Check whether this block a interactable block
                                touchX >= obj.x && touchX <= obj.x + obj.width && // Check whether user click on the block
                                touchY >= obj.y && touchY <= obj.y + obj.height) {

                                // Swap colors
                                val tempColor = paint.color
                                paint.color = Color.parseColor(obj.colorCode)
                                obj.colorCode = String.format("#%06X", 0xFFFFFF and tempColor)
                            }
                        }
                    }
                    invalidate() // Force to redraw
                }
                return true
            }


            override fun onDraw(canvas: Canvas) { // Draw when it need to redraw
                super.onDraw(canvas)

                // Camera follows ball
                cameraX = ballX - maxX / 2f
                cameraY = ballY - maxY / 2f

                // Clamp camera so it doesn't show outside world
                cameraX = cameraX.coerceIn(0f, worldWidth - maxX)
                cameraY = cameraY.coerceIn(0f, worldHeight - maxY)

                // Draw background
                canvas.drawColor(Color.BLACK)

                // Move camera
                canvas.translate(-cameraX, -cameraY)

                // Draw ball
                canvas.drawCircle(ballX, ballY, radius, paint)

                // Draw world objects only if inside camera view
                objects.forEach { obj ->
                    if (obj.x + obj.width > cameraX && obj.x < cameraX + maxX &&
                        obj.y + obj.height > cameraY && obj.y < cameraY + maxY) {

                        if (obj.type == "text" && obj.text.isNotEmpty()) { // If the world object is text
                            val textPaint = Paint().apply {
                                color = Color.parseColor(obj.colorCode)
                                textSize = 80f
                                isAntiAlias = true
                                textAlign = Paint.Align.CENTER
                            }
                            canvas.drawText(
                                obj.text,
                                obj.x + obj.width / 2,   // Center horizontally
                                obj.y + obj.height / 2,  // Roughly center vertically
                                textPaint
                            )
                        }else if (obj.shape == "rect"){ // If the world object is rectangle block
                            val objPaint = Paint().apply {
                                color = Color.parseColor(obj.colorCode)
                                isAntiAlias = true
                            }
                            canvas.drawRect(obj.x, obj.y, obj.x + obj.width, obj.y + obj.height, objPaint)
                        }else if (obj.shape == "circle"){ // If the object is teleport
                            val objPaint = Paint().apply {
                                color = Color.parseColor(obj.colorCode)
                                isAntiAlias = true
                            }
                            canvas.drawCircle(obj.x, obj.y, obj.radius, objPaint)
                        }
                    }
                }

                //UI
                val outlinePaint = Paint().apply { // Draw a outline for UI to avoid the white text disappear on white background
                    color = Color.BLACK
                    textSize = 40f
                    style = Paint.Style.STROKE
                    strokeWidth = 6f
                    isAntiAlias = true
                }

                val textPaint = Paint().apply { // Draw lvl number that follow camera
                    color = Color.WHITE
                    textSize = 40f
                    isAntiAlias = true
                }
                canvas.drawText("Level $lvlNum", cameraX + 50f, cameraY + 200f, outlinePaint)
                canvas.drawText("Level $lvlNum", cameraX + 50f, cameraY + 200f, textPaint)

                timeSpend = if (isPaused) { // Show the time u spend on this lvl
                    pauseStartTime - startTime - totalTimePaused  // Freeze time while paused
                } else {
                    System.currentTimeMillis() - startTime - totalTimePaused
                }

                val totalSec = (timeSpend / 1000).toInt()
                val min = totalSec / 60
                val sec = totalSec % 60

                val timeText = if (min > 0) {
                    String.format("%dm %02ds", min, sec)
                } else {
                    "$sec s" // still under a minute
                }

                canvas.drawText("Time: $timeText", cameraX + 50f, cameraY + 300f, outlinePaint)
                canvas.drawText("Time: $timeText", cameraX + 50f, cameraY + 300f, textPaint)

                val pauseBtnBg = Paint().apply { // Pause button background
                    color = if (isPaused) Color.GREEN else Color.RED
                    isAntiAlias = true
                }

                val pauseSymbol = Paint().apply { // Pause symbol (two white bars)
                    color = Color.WHITE
                    strokeWidth = 12f
                    isAntiAlias = true
                }

                pauseBtnX = 2400f
                pauseBtnY = 150f
                val barWidth = 20f
                val barHeight = 70f

                canvas.drawRoundRect( // Draw button square
                    pauseBtnX + cameraX,
                    pauseBtnY + cameraY,
                    pauseBtnX + pauseBtnSize + cameraX,
                    pauseBtnY + pauseBtnSize + cameraY,
                    20f, 20f,
                    pauseBtnBg
                )

                if (!isPaused) { // Draw pause bars
                    canvas.drawRect(
                        pauseBtnX + cameraX + 30f, pauseBtnY + cameraY + 25f,
                        pauseBtnX + cameraX + 30f + barWidth, pauseBtnY + cameraY + 25f + barHeight,
                        pauseSymbol
                    )
                    canvas.drawRect(
                        pauseBtnX + cameraX + 70f, pauseBtnY + cameraY + 25f,
                        pauseBtnX + cameraX + 70f + barWidth, pauseBtnY + cameraY + 25f + barHeight,
                        pauseSymbol
                    )
                } else { // Draw triangle “play” symbol
                    val path = Path()
                    val left = pauseBtnX + cameraX + 30f
                    val top = pauseBtnY + cameraY + 25f
                    val right = pauseBtnX + cameraX + 90f
                    val bottom = pauseBtnY + cameraY + 95f

                    path.moveTo(left, top)
                    path.lineTo(right, (top + bottom) / 2)   // tip of triangle
                    path.lineTo(left, bottom)
                    path.close()

                    canvas.drawPath(path, pauseSymbol)

                    // Draw home button when pause
                    homeBtnX = 2250f
                    homeBtnY = 150f
                    homeBtnSize = 120f
                    val homeBtnPaint = Paint().apply { color = Color.RED }

                    canvas.drawRoundRect(
                        homeBtnX + cameraX,
                        homeBtnY + cameraY,
                        homeBtnX + cameraX + homeBtnSize,
                        homeBtnY + cameraY + homeBtnSize,
                        20f, 20f,
                        homeBtnPaint
                    )

                    val iconPaint = Paint().apply {
                        color = Color.WHITE
                        style = Paint.Style.FILL
                        isAntiAlias = true
                    }

                    val roofPath = Path().apply { // Roof
                        moveTo(homeBtnX + cameraX + homeBtnSize / 2, homeBtnY + cameraY + 25f)
                        lineTo(homeBtnX + cameraX + 25f, homeBtnY + cameraY + homeBtnSize / 2)
                        lineTo(homeBtnX + cameraX + homeBtnSize - 25f, homeBtnY + cameraY + homeBtnSize / 2)
                        close()
                    }
                    canvas.drawPath(roofPath, iconPaint)

                    canvas.drawRect( // Main body of home
                        homeBtnX + cameraX + 35f,
                        homeBtnY + cameraY + homeBtnSize / 2,
                        homeBtnX + cameraX + homeBtnSize - 35f,
                        homeBtnY + cameraY + homeBtnSize - 25f,
                        iconPaint
                    )
                }

                invalidate()
            }

        }

        setContentView(ballView) // Display it

        // Get access from accelerometer sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0] // tilting left/right
            val y = event.values[1] // tilting forward/backward

            // Read current ball position
            val currentBallX = ballX
            val currentBallY = ballY



            gameScope.launch(Dispatchers.Default) { // Do calculation in bg

                // Apply tilt movement and consider when paused
                var newBallX = if (!isPaused) currentBallX + y * 5 else currentBallX
                var newBallY = if (!isPaused) currentBallY + x * 5 else currentBallY

                // Keep ball inside world bounds
                newBallX = newBallX.coerceIn(radius, worldWidth - radius)
                newBallY = newBallY.coerceIn(radius, worldHeight - radius)

                val now = System.currentTimeMillis()

                // Collision detection and response
                for (obj in objects) {
                    if (isColliding(newBallX, newBallY, radius, obj)) {

                        if (obj.isGoal) { // Navigate to VictoryPage
                            withContext(Dispatchers.Main) {
                                val intent = Intent(this@GamePlay, VictoryPage::class.java)
                                intent.putExtra("Time Spend", timeSpend)
                                startActivity(intent)
                            }

                            return@launch
                        }
                        if (now - tpCD >= 1000) {

                            if (obj.teleportId != null && obj.type == "tpIn") { // If collision with teleport portal for in

                                val outPortal =
                                    objects.firstOrNull { it.teleportId == obj.teleportId && it.type == "tpOut" }

                                if (outPortal != null) { // Tp to below portal
                                    newBallX = outPortal.x + outPortal.width / 2
                                    newBallY = outPortal.y + outPortal.height + 50f

                                    tpCD = now
                                }

                                break
                            }

                            if (obj.teleportId != null && obj.type == "tpOut") { // If collision with teleport portal for out

                                val inPortal =
                                    objects.firstOrNull { it.teleportId == obj.teleportId && it.type == "tpIn" }

                                if (inPortal != null) { // Tp to below portal
                                    newBallX = inPortal.x + inPortal.width / 2
                                    newBallY = inPortal.y + inPortal.height + 50f

                                    tpCD = now
                                }

                                break
                            }
                        }

                        val corrected = resolveCollision(newBallX, newBallY, radius, obj)
                        newBallX = corrected.first
                        newBallY = corrected.second
                    }
                }

                // Update UI
                withContext(Dispatchers.Main) {
                    ballX = newBallX
                    ballY = newBallY
                    ballView.invalidate()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onPause() { // Stop when the app paused
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onResume() { // Resume when you back the app
        super.onResume()
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    fun isColliding(ballX: Float, ballY: Float, radius: Float, obj: WorldObject): Boolean { // Check the collision between block and world objects

        val objColor = Color.parseColor(obj.colorCode)

        if (objColor == paint.color || obj.type == "text" || obj.type == "tpBg") { // If color is same or the object is a text then no collision
            return false
        }

        // Find the closet point on the world object to the ball center
        val closestX = ballX.coerceIn(obj.x, obj.x + obj.width)
        val closestY = ballY.coerceIn(obj.y, obj.y + obj.height)

        // Get the distance from ball center to the closest point on the world object
        val dx = ballX - closestX
        val dy = ballY - closestY

        return dx * dx + dy * dy < radius * radius
    }

    /*
        Simple Explanation for fun below with an example, assume our ball center is (10,10), radius is 60,
        there is a rectangle with 100 * 100 and top left corner is (50, 50), bottom right corner is (150, 150)

        Step 1:
        val closestX = ballX.coerceIn(obj.x, obj.x + obj.width) = 0.coerceIn(50, 150) = 50
        val closestY = ballY.coerceIn(obj.y, obj.y + obj.height) = 0.coerceIn(50, 150) = 50

        The closet point on rectangle to ball is (50, 50)

        Step 2:
        val dx = ballX - closestX = 10 - 50 = -40
        val dy = ballY - closestY = 10 - 50 = -40
        val dist = kotlin.math.sqrt(dx * dx + dy * dy) = sqrt((-40) ^ 2 + (-40) ^ 2) = sqrt(3200) = 56.57

        Step 3:
        val overlap = radius - dist = 60 - 56.57 = 3.43 ( over 0 means overlap)

        Step 4:
        val nx = dx / dist = -40 / 56.57 = -0.707
        val ny = dy / dist = -40 / 56.57 = -0.707

        Step 5 (Since we need to push the ball out as it overlapping):
        ballX + nx * overlap = 10 + (-0.707 * 3.42) = 7.57
        ballY + ny * overlap = 10 + (-0.707 * 3.42) = 7.57

        So the ball will be reset to (7.57 ,7.57) and it is the border of the world object
    */
    fun resolveCollision(ballX: Float, ballY: Float, radius: Float, obj: WorldObject): Pair<Float, Float> { // This is for adjust the ball position if it collides with world object

        // Find the closet point on the world object to the ball center
        val closestX = ballX.coerceIn(obj.x, obj.x + obj.width)
        val closestY = ballY.coerceIn(obj.y, obj.y + obj.height)

        // Get the distance from ball center to the closest point on the world object
        val dx = ballX - closestX
        val dy = ballY - closestY
        val dist = kotlin.math.sqrt(dx * dx + dy * dy) // Distance formula

        if (dist == 0f) return Pair(ballX, ballY) // Avoid divide by zero

        val overlap = radius - dist // How much ball is overlapping the world object

        if (overlap > 0) { // Above zero means overlap(collide) happens
            val nx = dx / dist // Get the unit vector
            val ny = dy / dist
            return Pair(ballX + nx * overlap, ballY + ny * overlap) // Reset the ball position
        }

        return Pair(ballX, ballY)
    }

    fun togglePause() { // Function to calculate how much time pause
        if (isPaused) {
            isPaused = false
            totalTimePaused += System.currentTimeMillis() - pauseStartTime
        } else {
            isPaused = true
            pauseStartTime = System.currentTimeMillis()
        }
    }

    override fun onDestroy() { // Cancel when leaving
        super.onDestroy()
        gameScope.cancel()
    }

}
