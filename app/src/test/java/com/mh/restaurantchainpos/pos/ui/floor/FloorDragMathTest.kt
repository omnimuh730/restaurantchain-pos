package com.mh.restaurantchainpos.pos.ui.floor

import com.mh.restaurantchainpos.pos.data.FloorMetrics
import com.mh.restaurantchainpos.pos.data.FloorTable
import com.mh.restaurantchainpos.pos.data.TableShape
import com.mh.restaurantchainpos.pos.data.TableStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class FloorDragMathTest {
    private val table = FloorTable(
        id = "T2",
        label = "Table 2",
        seats = 4,
        shape = TableShape.Circle,
        x = 216,
        y = 48,
        width = 144,
        height = 216,
        status = TableStatus.Available,
    )

    @Test
    fun dragPositionUsesTotalGestureDistanceBeforeSnapping() {
        assertEquals(216 to 48, calculateDraggedTablePosition(table, totalDxDp = 11f, totalDyDp = 0f))
        assertEquals(240 to 48, calculateDraggedTablePosition(table, totalDxDp = 12f, totalDyDp = 0f))
        assertEquals(240 to 72, calculateDraggedTablePosition(table, totalDxDp = 30f, totalDyDp = 30f))
    }

    @Test
    fun dragPositionClampsToCanvasBounds() {
        assertEquals(0 to 0, calculateDraggedTablePosition(table, totalDxDp = -500f, totalDyDp = -500f))

        val maxX = FloorMetrics.CanvasW - table.width
        val maxY = FloorMetrics.CanvasH - table.height
        assertEquals(maxX to maxY, calculateDraggedTablePosition(table, totalDxDp = 5000f, totalDyDp = 5000f))
    }
}
