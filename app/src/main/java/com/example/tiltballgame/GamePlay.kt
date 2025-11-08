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
import android.view.MotionEvent
import com.example.tiltballgame.ui.theme.TiltBallGameTheme

data class WorldObject(
    val x: Float,
    val y: Float,
    val width: Float = 80f,
    val height: Float = 80f,
    var colorCode: String = "#FFFFFF",
    val isColorChanger: Boolean = false,
    var isGoal: Boolean = false
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
                        WorldObject(ballX - 200f , ballY - 500f, 2000f, 100f),
                        WorldObject(ballX - 200f , ballY - 400f, 100f, 1000f),
                        WorldObject(ballX - 200f , ballY + 500f, 2000f, 100f),
                        WorldObject(ballX - 400f, ballY, 150f, 150f, "#EF476F", isColorChanger = true),
                        WorldObject(ballX + 200f , ballY - 400f, 100f, 900f, "#EF476F", isColorChanger = true),
                        WorldObject(ballX + 350f , ballY - 400f, 100f, 900f, "#EF476F", isColorChanger = true),
                        WorldObject(ballX + 550f , ballY - 400f, 150f,900f, "#08F26E", isGoal = true),
                    )
                    else -> listOf(
                        WorldObject(ballX - 100f, ballY - 100f),
                        WorldObject(ballX + 120f, ballY - 80f),
                        WorldObject(ballX - 90f, ballY + 130f),
                        WorldObject(ballX + 110f, ballY + 100f)
                    )
                }
            }

            override fun onTouchEvent(event: MotionEvent): Boolean { // Touch event
                if (event.action == MotionEvent.ACTION_DOWN) { // If user tap on screen

                    // Check the relative coordinate cuz my camera will mov
                    val touchX = event.x + cameraX
                    val touchY = event.y + cameraY

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

                timeSpend = System.currentTimeMillis() - startTime // Show the time u spend on this lvl
                val totalSec = (timeSpend / 1000).toInt()
                val min = totalSec / 60
                val sec = totalSec % 60

                val timeText = if (min > 0) {
                    String.format("%dm %02ds", min, sec)
                } else {
                    "$sec s" // still under a minute
                }

                canvas.drawText("Time: $timeText", cameraX + 50f, cameraY + 300f, textPaint)
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

            // Collision detection and response
            for (obj in objects) {
                if (isColliding(ballX, ballY, radius, obj)) {

                    if (obj.isGoal) { // Navigate to VictoryPage
                        val intent = Intent(this@GamePlay, VictoryPage::class.java)
                        intent.putExtra("Time Spend", timeSpend)
                        startActivity(intent)
                        return
                    }

                    val corrected = resolveCollision(ballX, ballY, radius, obj)
                    ballX = corrected.first
                    ballY = corrected.second
                }
            }

            // Redraw after movement
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

    fun isColliding(ballX: Float, ballY: Float, radius: Float, obj: WorldObject): Boolean { // Check the collision between block and world objects

        val objColor = Color.parseColor(obj.colorCode)

        if (objColor == paint.color) { // If color is same then no collision
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
}
