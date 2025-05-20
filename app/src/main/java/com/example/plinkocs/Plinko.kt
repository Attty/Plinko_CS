package com.example.plinkocs.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plinkocs.R
import kotlinx.coroutines.isActive
import kotlin.math.sqrt
import kotlin.random.Random


data class Vec2(val x: Float, val y: Float)

data class Peg(
    val id: Int,
    val position: Vec2,
    val radius: Float,
    val lastHitTime: Long = 0L
)

data class Ball(
    val position: Vec2,
    val velocity: Vec2,
    val radius: Float,
    val restitution: Float = 0.25f
)

data class Bin(
    val id: Int,
    val xStart: Float,
    val xEnd: Float,
    val yStart: Float,
    val height: Float,
    val label: String,
    val multiplier: Float,
    val imageResId: Int
)

class PlinkoGameState {
    var ball by mutableStateOf<Ball?>(null)
    val pegs = mutableStateListOf<Peg>()
    val bins = mutableStateListOf<Bin>()

    var totalScore by mutableStateOf(1000)
    var lastWin by mutableStateOf(0)
    var currentBet by mutableStateOf(50)

    var boardWidth by mutableStateOf(0f)
    var boardHeight by mutableStateOf(0f)

    private val gravity = 980f
    private val NUM_BINS = 8
    private val BINS_AREA_HEIGHT_RATIO = 0.05f

    val pegRadiusRatio = 1f / 35f
    val ballRadiusRatio = 1f / 30f

    private var yOffsetForBinCollisionLogic: Float = 0f

    fun setCollisionOffsets(pegVisualOffsetY: Float, binVisualOffsetY: Float) {
        this.yOffsetForBinCollisionLogic = binVisualOffsetY - pegVisualOffsetY
    }

    fun setupBoardAndBins(width: Float, height: Float) {
        if (width == 0f || height == 0f) return
        boardWidth = width
        boardHeight = height

        pegs.clear()
        val currentPegRadius = boardWidth * pegRadiusRatio

        val topPegs = 3
        val bottomPegs = 9
        val numRows = bottomPegs - topPegs + 1

        val totalPegsHeightRatio = 0.60f
        val verticalSpacingPegs = (height * totalPegsHeightRatio) / (numRows - 1).coerceAtLeast(1)
        val topMargin = height * 0.10f

        val numGapsInBottomRow = (bottomPegs - 1).coerceAtLeast(1)
        val effectiveBoardWidthForPegs = width - (currentPegRadius * 4)
        val actualHorizontalSpacing = effectiveBoardWidthForPegs / numGapsInBottomRow

        for (row in 0 until numRows) {
            val numPegsInRow = topPegs + row
            val rowWidth = (numPegsInRow - 1) * actualHorizontalSpacing
            val startX = (width - rowWidth) / 2f
            val y = topMargin + row * verticalSpacingPegs

            for (col in 0 until numPegsInRow) {
                val x = startX + col * actualHorizontalSpacing
                if (x >= currentPegRadius && x <= width - currentPegRadius) {
                    pegs.add(Peg(id = pegs.size, position = Vec2(x, y), radius = currentPegRadius))
                } else if (numPegsInRow == 1) {
                    pegs.add(
                        Peg(
                            id = pegs.size,
                            position = Vec2(width / 2f, y),
                            radius = currentPegRadius
                        )
                    )
                }
            }
        }
        setupBins(width, height)
    }

    private fun setupBins(width: Float, height: Float) {
        bins.clear()
        if (width == 0f || height == 0f || NUM_BINS == 0) return

        val binAreaTopYLogical = height * (1f - BINS_AREA_HEIGHT_RATIO)
        val binHeight = height * BINS_AREA_HEIGHT_RATIO
        val binWidth = width / NUM_BINS.toFloat()

        val multiplierData = listOf(
            Pair(12f, R.drawable.red_multiplier),
            Pair(5f, R.drawable.orange_multiplier),
            Pair(2f, R.drawable.green_multiplier),
            Pair(0.9f, R.drawable.blue_multiplier),
            Pair(0.9f, R.drawable.blue_multiplier),
            Pair(2f, R.drawable.green_multiplier),
            Pair(5f, R.drawable.orange_multiplier),
            Pair(12f, R.drawable.red_multiplier)
        )

        for (i in 0 until NUM_BINS) {
            val xStart = i * binWidth
            val (multiplierValue, imageRes) = multiplierData.getOrElse(i) {
                multiplierData.lastOrNull() ?: Pair(0.9f, R.drawable.blue_multiplier)
            }
            bins.add(
                Bin(
                    id = i,
                    xStart = xStart,
                    xEnd = xStart + binWidth,
                    yStart = binAreaTopYLogical,
                    height = binHeight,
                    label = "x${multiplierValue}",
                    multiplier = multiplierValue,
                    imageResId = imageRes
                )
            )
        }
    }

    fun changeBet(newBet: Int) {
        if (ball == null) {
            currentBet = newBet
        }
    }

    fun dropBall() {
        if (ball == null && boardWidth > 0f) {
            if (totalScore >= currentBet) {
                totalScore -= currentBet
                lastWin = 0

                val currentBallRadius = (boardWidth * ballRadiusRatio) / 1.5f
                val dropPositionX = boardWidth / 2f
                val spawnY = currentBallRadius * 2.5f

                ball = Ball(
                    position = Vec2(dropPositionX, spawnY),
                    velocity = Vec2(Random.nextFloat() * 60f - 30f, 120f),
                    radius = currentBallRadius,
                    restitution = 0.25f
                )
            } else {
                println("Not enough coins to bet (internal check).")
            }
        }
    }


    fun update(deltaTime: Float) {
        ball?.let { currentBall ->
            var newVelocity = currentBall.velocity.copy(
                y = currentBall.velocity.y + gravity * deltaTime
            )
            var newPosition = currentBall.position.copy(
                x = currentBall.position.x + newVelocity.x * deltaTime,
                y = currentBall.position.y + newVelocity.y * deltaTime
            )

            pegs.forEach { peg ->
                val dx = newPosition.x - peg.position.x
                val dy = newPosition.y - peg.position.y
                val distanceSquared = dx * dx + dy * dy
                val combinedRadius = currentBall.radius + peg.radius

                if (distanceSquared < combinedRadius * combinedRadius) {
                    val distance = sqrt(distanceSquared)
                    if (distance < combinedRadius) {
                        val overlap = combinedRadius - distance
                        if (distance > 0.001f) {
                            val pushX = (dx / distance) * overlap
                            val pushY = (dy / distance) * overlap
                            newPosition = newPosition.copy(
                                x = newPosition.x + pushX,
                                y = newPosition.y + pushY
                            )
                        } else {
                            newPosition = newPosition.copy(x = newPosition.x + overlap, y = newPosition.y)
                        }

                        val normalX = dx / distance.coerceAtLeast(0.001f)
                        val normalY = dy / distance.coerceAtLeast(0.001f)
                        val relativeVelocityNormal = newVelocity.x * normalX + newVelocity.y * normalY

                        if (relativeVelocityNormal < 0) {
                            val impulseMagnitude = -(1 + currentBall.restitution) * relativeVelocityNormal
                            var reflectedVx = newVelocity.x + impulseMagnitude * normalX
                            var reflectedVy = newVelocity.y + impulseMagnitude * normalY
                            val horizontalInfluence = (newPosition.x - peg.position.x) / peg.radius
                            val nudgeFactor = 100f + Random.nextFloat() * 30f
                            reflectedVx += horizontalInfluence * nudgeFactor * currentBall.restitution

                            val maxSpeed = 1000f
                            val currentSpeedSq = reflectedVx * reflectedVx + reflectedVy * reflectedVy
                            if (currentSpeedSq > maxSpeed * maxSpeed) {
                                val currentSpeed = sqrt(currentSpeedSq)
                                reflectedVx = (reflectedVx / currentSpeed) * maxSpeed
                                reflectedVy = (reflectedVy / currentSpeed) * maxSpeed
                            }
                            newVelocity = Vec2(reflectedVx, reflectedVy)

                            val pegIndex = pegs.indexOfFirst { it.id == peg.id }
                            if (pegIndex != -1) {
                                pegs[pegIndex] = peg.copy(lastHitTime = System.currentTimeMillis())
                            }
                        }
                    }
                }
            }

            if (newPosition.x - currentBall.radius < 0) {
                newPosition = newPosition.copy(x = currentBall.radius)
                newVelocity = newVelocity.copy(x = -newVelocity.x * currentBall.restitution * 0.7f)
            }
            else if (newPosition.x + currentBall.radius > boardWidth) {
                newPosition = newPosition.copy(x = boardWidth - currentBall.radius)
                newVelocity = newVelocity.copy(x = -newVelocity.x * currentBall.restitution * 0.7f)
            }
            if (newPosition.y - currentBall.radius < 0 && newVelocity.y < 0) {
                newPosition = newPosition.copy(y = currentBall.radius)
                newVelocity = newVelocity.copy(y = -newVelocity.y * currentBall.restitution * 0.4f)
            }

            if (bins.isNotEmpty()) {
                val ballLogicalBottom = newPosition.y + currentBall.radius
                val ballLogicalTop = newPosition.y - currentBall.radius
                val effectiveBinAreaTopY = bins.first().yStart + yOffsetForBinCollisionLogic
                val effectiveBinAreaBottomY = bins.first().yStart + bins.first().height + yOffsetForBinCollisionLogic

                if (ballLogicalBottom >= effectiveBinAreaTopY && ballLogicalTop < effectiveBinAreaBottomY) {
                    val landedBin = bins.find { bin ->
                        newPosition.x >= bin.xStart && newPosition.x < bin.xEnd
                    }

                    if (landedBin != null) {
                        val winnings = (currentBet * landedBin.multiplier).toInt()
                        totalScore += winnings
                        lastWin = winnings
                        ball = null
                        return@let
                    }
                }
            }

            if (newPosition.y - currentBall.radius > boardHeight) {
                ball = null
                lastWin = 0
                return@let
            }
            ball = currentBall.copy(position = newPosition, velocity = newVelocity)
        }
    }
}

@Composable
fun PlinkoGameScreen(
    modifier: Modifier = Modifier,
    gameState: PlinkoGameState
) {
    var boardSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    val pegVisualOffsetYpx = remember(density) { with(density) { 40.dp.toPx() } }
    val binVisualOffsetYpx = remember(density) { with(density) { (-50).dp.toPx() } }

    LaunchedEffect(pegVisualOffsetYpx, binVisualOffsetYpx, gameState) {
        gameState.setCollisionOffsets(
            pegVisualOffsetY = pegVisualOffsetYpx,
            binVisualOffsetY = binVisualOffsetYpx
        )
    }

    val gameBallPainter: Painter = painterResource(id = R.drawable.plinko_game_ball)
    val pegImagePainter: Painter = painterResource(id = R.drawable.plinko_ball)

    val blueMultiplierPainter = painterResource(id = R.drawable.blue_multiplier)
    val greenMultiplierPainter = painterResource(id = R.drawable.green_multiplier)
    val orangeMultiplierPainter = painterResource(id = R.drawable.orange_multiplier)
    val redMultiplierPainter = painterResource(id = R.drawable.red_multiplier)

    val binPainters = remember {
        mapOf(
            R.drawable.blue_multiplier to blueMultiplierPainter,
            R.drawable.green_multiplier to greenMultiplierPainter,
            R.drawable.orange_multiplier to orangeMultiplierPainter,
            R.drawable.red_multiplier to redMultiplierPainter
        )
    }

    val isGameRunning by remember {
        derivedStateOf { gameState.ball != null }
    }

    LaunchedEffect(isGameRunning) {
        if (isGameRunning) {
            var lastFrameTimeNanos = System.nanoTime()
            while (isActive && gameState.ball != null) {
                androidx.compose.runtime.withFrameNanos { currentTimeNanos ->
                    val deltaTimeSeconds = (currentTimeNanos - lastFrameTimeNanos) / 1_000_000_000f
                    gameState.update(deltaTimeSeconds.coerceAtMost(0.033f))
                    lastFrameTimeNanos = currentTimeNanos
                }
            }
        }
    }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                    }
                )
            }
            .onSizeChanged { newSize ->
                boardSize = newSize
                if (newSize.width > 0 && newSize.height > 0) {
                    if (gameState.pegs.isEmpty() ||
                        newSize.width.toFloat() != gameState.boardWidth ||
                        newSize.height.toFloat() != gameState.boardHeight
                    ) {
                        if (gameState.ball == null) {
                            gameState.setupBoardAndBins(
                                newSize.width.toFloat(),
                                newSize.height.toFloat()
                            )
                        }
                    }
                }
            }
    ) {
        if (boardSize.width > 0 && boardSize.height > 0) {
            PlinkoCanvas(
                gameState = gameState,
                ballPainter = gameBallPainter,
                pegPainter = pegImagePainter,
                binPainters = binPainters,
                pegDrawingOffsetY = pegVisualOffsetYpx,
                binDrawingOffsetY = binVisualOffsetYpx
            )
        }
    }
}

@Composable
fun PlinkoCanvas(
    gameState: PlinkoGameState,
    ballPainter: Painter,
    pegPainter: Painter,
    binPainters: Map<Int, Painter>,
    pegDrawingOffsetY: Float,
    binDrawingOffsetY: Float
) {
    val blinkDurationMs = 350L
    val density = LocalDensity.current

    val textPaint = remember(density) {
        android.graphics.Paint().apply {
            textAlign = android.graphics.Paint.Align.CENTER
            textSize = with(density) { 16.sp.toPx() }
            color = android.graphics.Color.WHITE
            isAntiAlias = true
            typeface = android.graphics.Typeface.create(
                android.graphics.Typeface.DEFAULT,
                android.graphics.Typeface.BOLD
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        gameState.bins.forEach { bin ->
            val painter = binPainters[bin.imageResId]
            val yStartVisual = bin.yStart + binDrawingOffsetY
            val heightVisual = bin.height

            painter?.let {
                translate(left = bin.xStart, top = yStartVisual) {
                    with(it) {
                        draw(size = Size(bin.xEnd - bin.xStart, heightVisual))
                    }
                }
            } ?: run {
                drawRect(
                    color = Color.DarkGray,
                    topLeft = Offset(bin.xStart, yStartVisual),
                    size = Size(bin.xEnd - bin.xStart, heightVisual)
                )
            }

            if (bin.id > 0) {
                drawLine(
                    color = Color.Black.copy(alpha = 0.3f),
                    start = Offset(bin.xStart, yStartVisual),
                    end = Offset(bin.xStart, yStartVisual + heightVisual),
                    strokeWidth = density.density * 1f
                )
            }

            drawContext.canvas.nativeCanvas.drawText(
                bin.label,
                bin.xStart + (bin.xEnd - bin.xStart) / 2f,
                yStartVisual + heightVisual / 2f + textPaint.textSize / 3f,
                textPaint
            )
        }

        gameState.pegs.forEach { peg ->
            val pegDrawRadius = peg.radius
            val yVisual = peg.position.y + pegDrawingOffsetY
            val xVisual = peg.position.x

            translate(left = xVisual - pegDrawRadius, top = yVisual - pegDrawRadius) {
                with(pegPainter) {
                    draw(size = Size(pegDrawRadius * 2, pegDrawRadius * 2))
                }
            }

            val timeSinceHit = System.currentTimeMillis() - peg.lastHitTime
            val isCurrentlyBlinking = timeSinceHit < blinkDurationMs && peg.lastHitTime != 0L
            if (isCurrentlyBlinking) {
                val blinkProgress = timeSinceHit.toFloat() / blinkDurationMs
                val alpha = (1f - blinkProgress) * 0.7f
                drawCircle(
                    color = Color.White.copy(alpha = alpha.coerceIn(0f, 1f)),
                    radius = peg.radius + (3f * blinkProgress * density.density),
                    style = Stroke(
                        width = (2f * (1f - blinkProgress) * density.density).coerceAtLeast(
                            0.5f * density.density
                        )
                    ),
                    center = Offset(xVisual, yVisual)
                )
            }
        }

        gameState.ball?.let { ball ->
            val ballDrawRadius = ball.radius
            val yVisual = ball.position.y + pegDrawingOffsetY
            val xVisual = ball.position.x

            translate(left = xVisual - ballDrawRadius, top = yVisual - ballDrawRadius) {
                with(ballPainter) {
                    draw(size = Size(ballDrawRadius * 2, ballDrawRadius * 2))
                }
            }
        }
    }
}