package com.example.plinkocs.presentation.info

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.plinkocs.R
import com.example.plinkocs.presentation.menu.screen.AnimatedButton
import com.example.plinkocs.ui.theme.kickflipFont

@Composable
fun InfoScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {

        val screenHeight = this.maxHeight
        val screenWidth = this.maxWidth


        val ballOffsetYFraction = 0.09375f
        val infoTextPaddingTopFraction = 0.23f
        val buttonPaddingBottomFraction = 0.21f
        val rulesTextPaddingHorizontalFraction = 0.138f
        val rulesTextPaddingVerticalFraction = 0.0675f

        Image(
            painter = painterResource(R.drawable.plinko_background),
            contentDescription = "background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Image(
            painter = painterResource(R.drawable.plinko_bright_ball),
            contentDescription = "plinko_ball",
            modifier = Modifier
                .fillMaxSize()
                .offset(y = screenHeight * ballOffsetYFraction),
            contentScale = ContentScale.FillHeight
        )

        Image(
            painter = painterResource(R.drawable.info_window),
            contentDescription = "info_window",
            modifier = Modifier
                .scale(2.5f)
                .align(Alignment.Center)
        )

        Image(
            painter = painterResource(R.drawable.info_text),
            contentDescription = "info_text",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = screenHeight * infoTextPaddingTopFraction)
                .scale(2f)
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = screenHeight * buttonPaddingBottomFraction),
        ) {
            AnimatedButton(
                text = "Menu",
                buttonImage = R.drawable.info_button,
                onClick = { navController.popBackStack() },
                scale = 2f
            )
        }

        Text(
            text = "Plinko-style gambling games are based on the TV show activity. Players bet on where a chip will land on a pegged board, with payouts based on outcomes. It's a blend of luck and strategy in a thrilling game of chance.",
            fontFamily = kickflipFont,
            fontSize = 24.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(
                    horizontal = screenWidth * rulesTextPaddingHorizontalFraction,
                    vertical = screenHeight * rulesTextPaddingVerticalFraction
                )
        )
    }
}

@Preview
@Composable
fun InfoScreenPreview(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    InfoScreen(navController = navController)
}