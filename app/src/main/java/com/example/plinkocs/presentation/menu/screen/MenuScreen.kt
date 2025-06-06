package com.example.plinkocs.presentation.menu.screen

import android.app.Activity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.plinkocs.Destinations
import com.example.plinkocs.R
import com.example.plinkocs.presentation.menu.viewmodel.MenuViewModel
import com.example.plinkocs.ui.theme.kickflipFont
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MenuScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: MenuViewModel = viewModel()
) {
    val viewState = viewModel.viewState.value
    val isLoading = viewState.isLoading
    val context = LocalContext.current
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val screenHeight = this.maxHeight



        val plinkoMasterPaddingTopFraction = 0.055f
        val loadingBallOffsetYFraction = 0.09375f
        val loadingTextPaddingBottomFraction = 0.055f


        val plinkoBallOffsetYFraction = 0.2125f
        val menuWindowBoxPaddingTopFraction = 0.15f
        val menuButtonsColumnHeightFraction = 0.40375f

        Image(
            painter = painterResource(R.drawable.plinko_background),
            contentDescription = "background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        if (isLoading) {
            Image(
                painter = painterResource(R.drawable.plinko_bright_ball),
                contentDescription = "plinko_ball_loading",
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = screenHeight * loadingBallOffsetYFraction),
                contentScale = ContentScale.FillHeight
            )
            Image(
                painter = painterResource(R.drawable.plinko_master),
                contentDescription = "plinko_master",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = screenHeight * plinkoMasterPaddingTopFraction),
                contentScale = ContentScale.FillWidth
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = screenHeight * loadingTextPaddingBottomFraction)
            ) {
                Text(
                    text = "Loading...",
                    fontFamily = kickflipFont,
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(
                        drawStyle = Stroke(
                            width = 8f,
                            join = StrokeJoin.Round
                        )
                    ),
                    color = Color.Black
                )
                Text(
                    text = "Loading...",
                    fontFamily = kickflipFont,
                    fontSize = 52.sp,
                    color = Color.White
                )
            }
        } else {
            Image(
                painter = painterResource(R.drawable.plinko_bright_ball),
                contentDescription = "background_blur_ball",
                modifier = Modifier
                    .fillMaxSize()
                    .blur(13.4.dp),
                contentScale = ContentScale.FillHeight
            )
            Image(
                painter = painterResource(R.drawable.plinko_ball),
                contentDescription = "plinko_ball",
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(1.8f)
                    .align(Alignment.BottomCenter)
                    .offset(y = screenHeight * plinkoBallOffsetYFraction),
                contentScale = ContentScale.FillWidth
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(top = screenHeight * menuWindowBoxPaddingTopFraction)
            ) {
                Image(
                    painter = painterResource(R.drawable.menu_window),
                    contentDescription = "menu_window",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .scale(2.5f)
                )

                AnimatedMenuButtons(
                    columnHeight = screenHeight * menuButtonsColumnHeightFraction,
                    onPlayClick = {
                        navController.navigate(Destinations.Play)
                    },
                    onInfoClick = {
                        navController.navigate(Destinations.Info)
                    },
                    onQuitClick = {
                        val activity = context as? Activity
                        activity?.finishAffinity()
                    }
                )
            }

            Image(
                painter = painterResource(R.drawable.plinko_master),
                contentDescription = "plinko_master",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = screenHeight * plinkoMasterPaddingTopFraction),
                contentScale = ContentScale.FillWidth
            )
        }
    }
}

@Composable
fun BoxScope.AnimatedMenuButtons(
    columnHeight: Dp,
    onPlayClick: () -> Unit,
    onInfoClick: () -> Unit,
    onQuitClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .height(columnHeight)
            .align(Alignment.Center),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        AnimatedButton(
            text = "Play",
            buttonImage = R.drawable.menu_button,
            onClick = { onPlayClick() }
        )

        AnimatedButton(
            text = "Info",
            buttonImage = R.drawable.menu_button,
            onClick = { onInfoClick() }
        )

        AnimatedButton(
            text = "Quit",
            buttonImage = R.drawable.menu_button,
            onClick = { onQuitClick() }
        )
    }
}

@Composable
fun AnimatedButton(
    text: String,
    buttonImage: Int,
    isGameOver: Boolean = false,
    onClick: () -> Unit,
    scale: Float = 2.5f,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    var isClickable by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) scale * 0.85f else scale,
        animationSpec = tween(durationMillis = 100),
        label = "scale"
    )

    val verticalPadding = if (isGameOver) 8.dp else 0.dp
    val horizontalPadding = if (isGameOver) 32.dp else 0.dp
    val textSize = if (isGameOver) 25.sp else 45.sp
    val textScale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "textScale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier

    ) {
        Image(
            painter = painterResource(buttonImage),
            contentDescription = "Button",
            modifier = Modifier
                .scale(animatedScale)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            if (isClickable) {
                                isPressed = true
                                isClickable = false

                                tryAwaitRelease()
                                isPressed = false
                                onClick()

                                coroutineScope.launch {
                                    delay(300)
                                    isClickable = true
                                }
                            }
                        }
                    )
                }
        )
        Text(
            text = text,
            fontFamily = kickflipFont,
            fontSize = textSize,
            color = Color.White,
            modifier = Modifier
                .scale(textScale * scale / 2.5f)
                .padding(bottom = verticalPadding, end = horizontalPadding)
        )
    }
}

@Preview
@Composable
fun MenuScreenPreview() {
    val navController = rememberNavController()

    MenuScreen(navController = navController)
}