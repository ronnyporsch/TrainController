package de.ronnyporsch.train_controller.util.presentation

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints

/**
 * - Based on [this Stackoverflow answer](https://stackoverflow.com/a/71129399)
 */
fun Modifier.rotateWhileKeepingConstrains(degrees: Float = -90f) = this.then(
    graphicsLayer {
        rotationZ = degrees
        transformOrigin = TransformOrigin(0f, 0f)
    }.then(
        layout { measurable, constraints ->
            val placeable = measurable.measure(
                Constraints(
                    minWidth = constraints.minHeight,
                    maxWidth = constraints.maxHeight,
                    minHeight = constraints.minWidth,
                    maxHeight = constraints.maxHeight,
                )
            )
            layout(placeable.height, placeable.width) {
                placeable.place(-placeable.width, 0)
            }
        }
    )
)