package de.ronnyporsch.train_controller.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.ronnyporsch.train_controller.APP_NAME
import de.ronnyporsch.train_controller.core.MyIO
import de.ronnyporsch.train_controller.domain.Train
import de.ronnyporsch.train_controller.util.presentation.rotateWhileKeepingConstrains
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun TrainControlScreen(viewModel: TrainControlViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        uiState.error?.let { error ->
            Text("$error\nRetrying...", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.titleLarge)
            return
        }
        Column(modifier = Modifier.padding(16.dp)) {
            Text(APP_NAME, style = MaterialTheme.typography.titleLarge, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)

            Spacer(Modifier.height(16.dp))
            if (uiState.trains.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No trains found", style = MaterialTheme.typography.titleLarge)
                }
                return
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                for (train in uiState.trains) {
                    val playerColor = train.currentPlayer?.color?.composeColor ?: Color.Gray
                    Column(
                        Modifier.displayHoveringPlayers(uiState, train).displayCurrentPlayer(train).padding(32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(train.name, modifier = Modifier.padding(bottom = 4.dp))
                        Slider(
                            modifier = Modifier.rotateWhileKeepingConstrains().width(120.dp).height(50.dp),
                            value = train.speed.toFloat().absoluteValue,
                            onValueChange = { newSpeed ->
                                viewModel.process(TrainControlIntent.ChangeSpeed(train.bluetoothAddress, newSpeed.toInt()))
                            },
                            valueRange = 0f..100f,
                            colors = SliderDefaults.colors().copy(
                                activeTrackColor = playerColor,
                                thumbColor = playerColor,
                            )
                        )
                        Text("Speed: ${train.speed}", modifier = Modifier.padding(top = 4.dp))
                        Button(
                            shape = RectangleShape,
                            enabled = train.speed != 0,
                            colors = ButtonDefaults.buttonColors().copy(containerColor = playerColor),
                            onClick = { CoroutineScope(Dispatchers.MyIO).launch { train.changeSpeed(0) } }) {
                            Text("Stop")
                        }
                        Row(Modifier.clickable { CoroutineScope(Dispatchers.MyIO).launch { train.toggleReverseDirection() } }) {
                            Text("Reverse\nDirection:")
                            Checkbox(
                                checked = train.reverseDirection,
                                colors = CheckboxDefaults.colors(checkedColor = playerColor),
                                onCheckedChange = { CoroutineScope(Dispatchers.MyIO).launch { train.toggleReverseDirection() } }
                            )
                        }
                    }
                }
            }
        }
    }
    Box(contentAlignment = Alignment.BottomCenter) {
        GamepadButtonMappingDisplay()
    }
}


@Composable
expect fun GamepadButtonMappingDisplay()

fun Modifier.displayCurrentPlayer(train: Train): Modifier {
    val player = train.currentPlayer ?: return this
    return this.border(20.dp, player.color.composeColor)
}

fun Modifier.displayHoveringPlayers(uiState: TrainControlUiState, train: Train): Modifier {
    val borders = uiState.players
        .withIndex()
        .filter { it.value.hoveredTrain == train }

    return this.then(
        Modifier.drawBehind {
            val strokeWidth = 2.dp.toPx()
            val gap = 4.dp.toPx()

            borders.forEach { (_, player) ->
                val inset = player.index * gap + strokeWidth / 2
                drawRoundRect(
                    color = player.color.composeColor,
                    topLeft = Offset(inset, inset),
                    size = Size(
                        size.width - inset * 2,
                        size.height - inset * 2
                    ),
                    cornerRadius = CornerRadius(4.dp.toPx()),
                    style = Stroke(width = strokeWidth)
                )
            }
        }
    ).padding(8.dp)
}
