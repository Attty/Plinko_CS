package com.example.plinkocs.presentation.info

import androidx.compose.animation.core.Animation
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.plinkocs.Destinations
import com.example.plinkocs.R
import com.example.plinkocs.presentation.menu.screen.AnimatedButton
import com.example.plinkocs.ui.theme.kickflipFont

@Composable
fun InfoScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Box(modifier = Modifier.fillMaxSize()) {
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
                .offset(y = 75.dp),
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
                .padding(top = 200.dp)
                .scale(2f)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 200.dp),
        ) {
            AnimatedButton(
                text = "Menu",
                buttonImage = R.drawable.info_button,
                onClick = { navController.popBackStack() }, scale = 2f
            )
        }
        Text(
            text = "Plinko-style gambling games are based on the TV show activity. Players bet on where a chip will land on a pegged board, with payouts based on outcomes. It's a blend of luck and strategy in a thrilling game of chance.",
            fontFamily = kickflipFont,
            fontSize = 28.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(54.dp)
        )

    }
}

@Preview
@Composable
fun InfoScreenPreview(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    InfoScreen(navController = navController)
}