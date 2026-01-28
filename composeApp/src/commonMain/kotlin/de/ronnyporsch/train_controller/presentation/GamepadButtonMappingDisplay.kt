package de.ronnyporsch.train_controller.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import de.ronnyporsch.train_controller.gamepad.GamepadTrigger
import de.ronnyporsch.train_controller.util.camelCaseToCapitalizedWithSpaces
import org.jetbrains.compose.resources.painterResource

@Composable
fun GamepadButtonMappingDisplay() {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(8.dp)
    ) {
        items(gamepadButtonMapping.keys.toList()) {
            val painterResource = gamepadIconMapping[it]
            val intent = gamepadButtonMapping[it]
            if (painterResource == null || intent == null) return@items
            Box(modifier = Modifier.height(80.dp)) {
                GamepadIntentCell(intent::class.simpleName!!.camelCaseToCapitalizedWithSpaces(), painterResource(painterResource))
            }
        }
        items( GamepadTrigger.entries.toList()) {
            val painterResource = gamepadIconMapping[it]
            val intentDescription = if (it == GamepadTrigger.RightTrigger) "Move Forward" else "Move Backward"
            if (painterResource == null) return@items
            Box(modifier = Modifier.height(80.dp)) {
                GamepadIntentCell(intentDescription, painterResource(painterResource))
            }
        }
    }
}

@Composable
private fun GamepadIntentCell(intentDescription: String, painterResource: Painter) {
    Row(
        verticalAlignment = CenterVertically,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        Image(
            painter = painterResource,
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = intentDescription,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}