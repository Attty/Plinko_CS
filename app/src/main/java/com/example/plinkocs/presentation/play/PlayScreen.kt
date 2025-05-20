
package com.example.plinkocs.presentation.play

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.plinkocs.R
import com.example.plinkocs.game.PlinkoGameScreen
import com.example.plinkocs.game.PlinkoGameState
import com.example.plinkocs.presentation.menu.screen.AnimatedButton
import com.example.plinkocs.ui.theme.kickflipFont


@Composable
fun PlayScreen(modifier: Modifier = Modifier, navController: NavController) {
    val gameState = remember { PlinkoGameState() }

    var showWinDialog by remember { mutableStateOf(false) }
    var currentWinningsForDialog by remember { mutableStateOf(0) }
    var processedWinAmount by remember { mutableStateOf(-1) }

    LaunchedEffect(gameState.ball, gameState.lastWin) {
        if (gameState.ball == null && gameState.lastWin > 0 && gameState.lastWin != processedWinAmount) {
            if (!showWinDialog) {
                currentWinningsForDialog = gameState.lastWin
                showWinDialog = true
                processedWinAmount = gameState.lastWin
            }
        } else if (gameState.ball != null) {
            processedWinAmount = -1
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Background()

        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopSection(
                totalCoins = gameState.totalScore,
                lastWin = gameState.lastWin
            )
            GameZone(gameState = gameState, modifier = Modifier.weight(0.70f))
            BetZone(
                gameState = gameState,
                onPlayClicked = {
                    if (gameState.ball == null) {
                        if (gameState.totalScore >= gameState.currentBet) {
                            gameState.dropBall()
                        }
                    }
                },
                onBetChanged = { newBet ->
                    gameState.changeBet(newBet)
                },
                modifier = Modifier.weight(0.30f)
            )
        }


        if (showWinDialog) {
            WinDialogCard(
                winnings = currentWinningsForDialog,
                onContinue = {
                    showWinDialog = false
                },
                onGoToMenu = {
                    showWinDialog = false
                    navController.popBackStack()
                }, onDismissRequest = {
                    showWinDialog = false

                }
            )
        }
    }
}


@Composable
fun WinDialogCard(
    winnings: Int,
    onContinue: () -> Unit,
    onGoToMenu: () -> Unit,
    onDismissRequest: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onDismissRequest()
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 32.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(R.drawable.play_game_over),
                contentDescription = "",
                modifier = Modifier
                    .zIndex(1f)
                    .scale(0.9f)
                    .offset(y = (-10).dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 30.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(R.drawable.play_game_over_window),
                        contentDescription = "",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 52.dp)
                            .align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Earn:",
                                fontFamily = kickflipFont,
                                fontSize = 54.sp,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(start = 24.dp, bottom = 16.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            InfoWindow(winnings.toString())
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 55.dp),
                        ) {
                            AnimatedButton(
                                text = "Menu",
                                buttonImage = R.drawable.play_top_window,
                                onClick = onGoToMenu,
                                isGameOver = true,
                                scale = 2f
                            )
                            Spacer(modifier = Modifier.width(48.dp))
                            AnimatedButton(
                                text = "Replay",
                                buttonImage = R.drawable.play_top_window,
                                onClick = onContinue,
                                isGameOver = true,
                                scale = 2f
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun Background() {
    Image(
        painter = painterResource(R.drawable.plinko_background),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds
    )
    Image(
        painter = painterResource(R.drawable.plinko_bright_ball),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillHeight
    )
}

@Composable
private fun TopSection(totalCoins: Int, lastWin: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp)
            .padding(horizontal = 8.dp)
    ) {
        InfoWindow(
            text = "Coins: $totalCoins",
            modifier = Modifier.weight(1f)
        )
        InfoWindow(
            text = "Last Win: $lastWin",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun InfoWindow(text: String, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.height(90.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.play_top_window),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillWidth
        )
        Text(
            text = text,
            fontFamily = kickflipFont,
            fontSize = 20.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp, end = 24.dp)
        )
    }
}

@Composable
private fun GameZone(gameState: PlinkoGameState, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .padding(top = 16.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.play_game_window),
            contentDescription = "Game Window Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        PlinkoGameScreen(
            gameState = gameState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 4.dp)
        )
    }
}

@Composable
private fun BetZone(
    gameState: PlinkoGameState,
    onPlayClicked: () -> Unit,
    onBetChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val betAmounts = listOf(50, 100, 200, 500)
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .padding(top = 16.dp, bottom = 12.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.play_bet_window),
            contentDescription = "Bet Window Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(0.68f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "Your Bet: ${gameState.currentBet}",
                    fontFamily = kickflipFont,
                    fontSize = 18.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 6.dp, top = 32.dp)
                )

                Column(
                    modifier = Modifier.padding(bottom = 42.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        betAmounts.take(2).forEach { amount ->

                            AnimatedButton(
                                text = "$amount",
                                buttonImage = R.drawable.play_bet_button,
                                onClick = { onBetChanged(amount) },
                                scale = 2f,
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(2.2f)
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        betAmounts.drop(2).forEach { amount ->
                            AnimatedButton(
                                text = "$amount",
                                buttonImage = R.drawable.play_bet_button,
                                onClick = { onBetChanged(amount) },
                                scale = 2f,
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(2.2f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .weight(0.32f)
                    .fillMaxHeight(0.75f),
                contentAlignment = Alignment.Center
            ) {

                AnimatedButton(
                    text = "Play",
                    buttonImage = R.drawable.play_play_button,
                    onClick = onPlayClicked,
                    scale = 2.5f,
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(
                            0.9f,
                            matchHeightConstraintsFirst = false
                        )
                )
            }
        }
    }
}