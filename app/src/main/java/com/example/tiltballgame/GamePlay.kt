package com.example.tiltballgame

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.Surface
import androidx.compose.ui.tooling.preview.Preview
import com.example.tiltballgame.ui.theme.TiltBallGameTheme

data class WorldObject(
    val x: Float,
    val y: Float,
    val width: Float = 80f,
    val height: Float = 80f,
    val colorCode: String = "#FFFFFF"
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

    private var objects: List<WorldObject> = listOf()
    private var lvlNum: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lvlNum = intent.getIntExtra("Level Number", 0)

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
                        WorldObject(ballX - 100f, ballY - 100f),
                        WorldObject(ballX + 120f, ballY - 80f)
                    )
                    2 -> listOf(
                        WorldObject(ballX - 150f, ballY - 120f),
                        WorldObject(ballX + 160f, ballY - 50f),
                        WorldObject(ballX, ballY + 200f)
                    )
                    else -> listOf(
                        WorldObject(ballX - 100f, ballY - 100f),
                        WorldObject(ballX + 120f, ballY - 80f),
                        WorldObject(ballX - 90f, ballY + 130f),
                        WorldObject(ballX + 110f, ballY + 100f)
                    )
                }
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

                        val objPaint = Paint().apply {
                            color = Color.parseColor(obj.colorCode)
                            isAntiAlias = true
                        }
                        canvas.drawRect(obj.x, obj.y, obj.x + obj.width, obj.y + obj.height, objPaint)
                    }
                }

                val textPaint = Paint().apply { // Draw lvl number that follow camera
                    color = Color.WHITE
                    textSize = 60f
                    isAntiAlias = true
                }
                canvas.drawText("Level $lvlNum", cameraX + 50f, cameraY + 200f, textPaint)
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

            // Swap axes for landscape
            ballX += y * 5    // tilting forward/back tilts horizontally
            ballY += x * 5   // tilting left/right tilts vertically

            // Keep ball inside world
            ballX = ballX.coerceIn(radius, worldWidth - radius)
            ballY = ballY.coerceIn(radius, worldHeight - radius)

            ballView.invalidate()
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

}
