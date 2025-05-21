package com.example.plinkocs.presentation.play

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.unit.Dp
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

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenHeight = this.maxHeight
        val screenWidth = this.maxWidth

        val wd_paddingHorizontal = screenWidth * 0.082f
        val wd_imageOffsetY = screenHeight * -0.005f
        val wd_cardPaddingTop = screenHeight * 0.0375f
        val wd_columnPaddingVertical = screenHeight * 0.065f
        val wd_earnTextPaddingStart = screenWidth * 0.0615f
        val wd_earnTextPaddingBottom = screenHeight * 0.02f
        val wd_spacer1Width = screenWidth * 0.0307f
        val wd_spacer2Height = screenHeight * 0.01f
        val wd_buttonsRowPaddingStart = screenWidth * 0.135f
        val wd_buttonsSpacerWidth = screenWidth * 0.123f

        val ts_paddingTop = screenHeight * 0.0375f
        val iw_height = screenHeight * 0.1125f
        val iw_textPaddingBottom = screenHeight * 0.015f
        val iw_textPaddingEnd = screenWidth * 0.0615f

        val gz_paddingHorizontal = screenWidth * 0.0205f
        val gameZoneEffectiveWidth = screenWidth - (gz_paddingHorizontal * 2)

        val gz_paddingTop = screenHeight * 0.02f
        val gz_plinkoScreenPaddingH = screenWidth * 0.051f
        val gz_plinkoScreenPaddingTop = screenHeight * 0.02f
        val gz_plinkoScreenPaddingBottom = screenHeight * 0.005f

        val bz_paddingHorizontal = screenWidth * 0.0205f
        val bz_paddingTop = screenHeight * 0.02f
        val bz_paddingBottom = screenHeight * 0.015f
        val bz_rowPaddingHorizontal = screenWidth * 0.0307f
        val bz_rowPaddingVertical = screenHeight * 0.01f
        val bz_betTextPaddingBottom = screenHeight * 0.00f
        val bz_betTextPaddingTop = screenHeight * 0.04f
        val bz_betButtonsColumnPaddingBottom = screenHeight * 0.0525f
        val bz_buttonsVerticalSpacing = screenHeight * 0.02f
        val bz_buttonsHorizontalSpacing = screenWidth * 0.015f
        val bz_columnSpacerWidth = screenWidth * 0.0205f

        Background()

        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopSection(
                totalCoins = gameState.totalScore,
                lastWin = gameState.lastWin,
                paddingTop = ts_paddingTop,
                rowWidth = gameZoneEffectiveWidth,
                infoWindowHeight = iw_height,
                infoWindowTextPaddingBottom = iw_textPaddingBottom,
                infoWindowTextPaddingEnd = iw_textPaddingEnd
            )
            GameZone(
                gameState = gameState,
                modifier = Modifier.weight(0.70f),
                paddingHorizontal = gz_paddingHorizontal,
                paddingTop = gz_paddingTop,
                plinkoScreenPaddingH = gz_plinkoScreenPaddingH,
                plinkoScreenPaddingTop = gz_plinkoScreenPaddingTop,
                plinkoScreenPaddingBottom = gz_plinkoScreenPaddingBottom
            )
            BetZone(
                gameState = gameState,
                onPlayClicked = {
                    if (gameState.ball == null) {
                        if (gameState.totalScore >= gameState.currentBet) {
                            gameState.dropBall()
                        }
                    }
                },
                onBetChanged = { newBet -> gameState.changeBet(newBet) },
                modifier = Modifier.weight(0.30f),
                paddingHorizontal = bz_paddingHorizontal,
                paddingTop = bz_paddingTop,
                paddingBottom = bz_paddingBottom,
                rowPaddingHorizontal = bz_rowPaddingHorizontal,
                rowPaddingVertical = bz_rowPaddingVertical,
                betTextPaddingBottom = bz_betTextPaddingBottom,
                betTextPaddingTop = bz_betTextPaddingTop,
                betButtonsColumnPaddingBottom = bz_betButtonsColumnPaddingBottom,
                buttonsVerticalSpacing = bz_buttonsVerticalSpacing,
                buttonsHorizontalSpacing = bz_buttonsHorizontalSpacing,
                columnSpacerWidth = bz_columnSpacerWidth
            )
        }

        if (showWinDialog) {
            WinDialogCard(
                winnings = currentWinningsForDialog,
                onContinue = { showWinDialog = false },
                onGoToMenu = {
                    showWinDialog = false
                    navController.popBackStack()
                },
                onDismissRequest = { showWinDialog = false },
                paddingHorizontal = wd_paddingHorizontal,
                imageOffsetY = wd_imageOffsetY,
                cardPaddingTop = wd_cardPaddingTop,
                columnPaddingVertical = wd_columnPaddingVertical,
                earnTextPaddingStart = wd_earnTextPaddingStart,
                earnTextPaddingBottom = wd_earnTextPaddingBottom,
                spacer1Width = wd_spacer1Width,
                spacer2Height = wd_spacer2Height,
                buttonsRowPaddingStart = wd_buttonsRowPaddingStart,
                buttonsSpacerWidth = wd_buttonsSpacerWidth,
                infoWindowHeight = iw_height,
                infoWindowTextPaddingBottom = iw_textPaddingBottom,
                infoWindowTextPaddingEnd = iw_textPaddingEnd
            )
        }
    }
}

@Composable
fun WinDialogCard(
    winnings: Int,
    onContinue: () -> Unit,
    onGoToMenu: () -> Unit,
    onDismissRequest: () -> Unit,
    paddingHorizontal: Dp,
    imageOffsetY: Dp,
    cardPaddingTop: Dp,
    columnPaddingVertical: Dp,
    earnTextPaddingStart: Dp,
    earnTextPaddingBottom: Dp,
    spacer1Width: Dp,
    spacer2Height: Dp,
    buttonsRowPaddingStart: Dp,
    buttonsSpacerWidth: Dp,
    infoWindowHeight: Dp,
    infoWindowTextPaddingBottom: Dp,
    infoWindowTextPaddingEnd: Dp
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onDismissRequest() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = paddingHorizontal)
                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {},
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(R.drawable.play_game_over),
                contentDescription = "Game Over Status",
                modifier = Modifier
                    .zIndex(1f)
                    .scale(0.9f)
                    .offset(y = imageOffsetY)
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = cardPaddingTop),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(R.drawable.play_game_over_window),
                        contentDescription = "Dialog window background",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = columnPaddingVertical)
                            .align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Earn:",
                                fontFamily = kickflipFont, fontSize = 54.sp, color = Color.White,
                                maxLines = 1, overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(start = earnTextPaddingStart, bottom = earnTextPaddingBottom)
                            )
                            Spacer(modifier = Modifier.width(spacer1Width))
                            InfoWindow(
                                text = winnings.toString(),
                                height = infoWindowHeight,
                                textPaddingBottom = infoWindowTextPaddingBottom,
                                textPaddingEnd = infoWindowTextPaddingEnd
                            )
                        }
                        Spacer(modifier = Modifier.height(spacer2Height))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = buttonsRowPaddingStart),
                        ) {
                            AnimatedButton(
                                text = "Menu", buttonImage = R.drawable.play_top_window,
                                onClick = onGoToMenu, isGameOver = true, scale = 2f
                            )
                            Spacer(modifier = Modifier.width(buttonsSpacerWidth))
                            AnimatedButton(
                                text = "Replay", buttonImage = R.drawable.play_top_window,
                                onClick = onContinue, isGameOver = true, scale = 2f
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
private fun TopSection(
    totalCoins: Int, lastWin: Int,
    paddingTop: Dp,
    rowWidth: Dp,
    infoWindowHeight: Dp, infoWindowTextPaddingBottom: Dp, infoWindowTextPaddingEnd: Dp
) {
    Row(
        modifier = Modifier
            .width(rowWidth)
            .padding(top = paddingTop)
    ) {
        InfoWindow(
            text = "Coins: $totalCoins",
            modifier = Modifier.weight(1f),
            height = infoWindowHeight,
            textPaddingBottom = infoWindowTextPaddingBottom,
            textPaddingEnd = infoWindowTextPaddingEnd
        )
        InfoWindow(
            text = "Last Win: $lastWin",
            modifier = Modifier.weight(1f),
            height = infoWindowHeight,
            textPaddingBottom = infoWindowTextPaddingBottom,
            textPaddingEnd = infoWindowTextPaddingEnd
        )
    }
}

@Composable
private fun InfoWindow(
    text: String, modifier: Modifier = Modifier,
    height: Dp, textPaddingBottom: Dp, textPaddingEnd: Dp
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.height(height)
    ) {
        Image(
            painter = painterResource(R.drawable.play_top_window),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Text(
            text = text,
            fontFamily = kickflipFont, fontSize = 20.sp, color = Color.White,
            textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = textPaddingBottom, end = textPaddingEnd)
        )
    }
}

@Composable
private fun GameZone(
    gameState: PlinkoGameState, modifier: Modifier = Modifier,
    paddingHorizontal: Dp, paddingTop: Dp,
    plinkoScreenPaddingH: Dp, plinkoScreenPaddingTop: Dp, plinkoScreenPaddingBottom: Dp
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = paddingHorizontal)
            .padding(top = paddingTop)
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
                .padding(horizontal = plinkoScreenPaddingH)
                .padding(top = plinkoScreenPaddingTop, bottom = plinkoScreenPaddingBottom)
        )
    }
}

@Composable
private fun BetZone(
    gameState: PlinkoGameState, onPlayClicked: () -> Unit, onBetChanged: (Int) -> Unit, modifier: Modifier = Modifier,
    paddingHorizontal: Dp, paddingTop: Dp, paddingBottom: Dp,
    rowPaddingHorizontal: Dp, rowPaddingVertical: Dp,
    betTextPaddingBottom: Dp, betTextPaddingTop: Dp,
    betButtonsColumnPaddingBottom: Dp,
    buttonsVerticalSpacing: Dp, buttonsHorizontalSpacing: Dp,
    columnSpacerWidth: Dp
) {
    val betAmounts = listOf(50, 100, 200, 500)
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = paddingHorizontal)
            .padding(top = paddingTop, bottom = paddingBottom)
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
                .padding(horizontal = rowPaddingHorizontal, vertical = rowPaddingVertical),
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
                    fontFamily = kickflipFont, fontSize = 18.sp, color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = betTextPaddingBottom, top = betTextPaddingTop)
                )

                Column(
                    modifier = Modifier.padding(bottom = betButtonsColumnPaddingBottom),
                    verticalArrangement = Arrangement.spacedBy(buttonsVerticalSpacing),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(buttonsHorizontalSpacing),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        betAmounts.take(2).forEach { amount ->
                            AnimatedButton(
                                text = "$amount", buttonImage = R.drawable.play_bet_button,
                                onClick = { onBetChanged(amount) }, scale = 2f,
                                modifier = Modifier.weight(1f).aspectRatio(2.2f)
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(buttonsHorizontalSpacing),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        betAmounts.drop(2).forEach { amount ->
                            AnimatedButton(
                                text = "$amount", buttonImage = R.drawable.play_bet_button,
                                onClick = { onBetChanged(amount) }, scale = 2f,
                                modifier = Modifier.weight(1f).aspectRatio(2.2f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(columnSpacerWidth))

            Box(
                modifier = Modifier.weight(0.32f).fillMaxHeight(0.75f),
                contentAlignment = Alignment.Center
            ) {
                AnimatedButton(
                    text = "Play", buttonImage = R.drawable.play_play_button,
                    onClick = onPlayClicked, scale = 2.5f,
                    modifier = Modifier.fillMaxSize().aspectRatio(0.9f, matchHeightConstraintsFirst = false)
                )
            }
        }
    }
}