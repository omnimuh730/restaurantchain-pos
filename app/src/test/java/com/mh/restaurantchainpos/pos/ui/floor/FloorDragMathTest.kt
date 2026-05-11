package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.ui.geometry.Offset
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
    fun pointerDeltaConversionDoesNotApplyZoomTwice() {
        assertEquals(20f, pointerDeltaPxToCanvasDp(deltaPx = 60f, pxPerDp = 3f), 0.001f)
    }

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

    @Test
    fun tableContentBoundsUsesContainingRectInsteadOfCanvasOrigin() {
        val tables = listOf(
            table.copy(x = 120, y = 80, width = 144, height = 72),
            table.copy(id = "T3", x = 520, y = 360, width = 216, height = 144),
        )

        assertEquals(FloorContentBoundsDp(left = 120, top = 80, right = 736, bottom = 504), calculateTableContentBoundsDp(tables))
    }

    @Test
    fun minimumZoomFitsTheTableBoundingRect() {
        val bounds = FloorContentBoundsPx(left = 100f, top = 50f, right = 900f, bottom = 250f)

        assertEquals(0.5f, calculateFitContentZoom(viewportW = 400f, viewportH = 300f, contentWidth = bounds.width, contentHeight = bounds.height), 0.001f)
    }

    @Test
    fun panClampKeepsAllTablesVisibleAtMinimumZoom() {
        val bounds = FloorContentBoundsPx(left = 40f, top = 48f, right = 860f, bottom = 620f)
        val minZoom = calculateFitContentZoom(viewportW = 410f, viewportH = 500f, contentWidth = bounds.width, contentHeight = bounds.height)

        val pan = clampPanToContentBounds(
            proposedX = 0f,
            proposedY = 0f,
            zoom = minZoom,
            viewportW = 410f,
            viewportH = 500f,
            canvasW = 2400f,
            canvasH = 1800f,
            contentBounds = bounds,
        )

        assertEquals(-20f, pan.x, 0.001f)
        assertEquals(0f, pan.y, 0.001f)
    }

    @Test
    fun zoomingAroundCentroidKeepsTheSameContentPointUnderTheFinger() {
        val pan = panForZoomAroundCentroid(
            panX = 0f,
            panY = 0f,
            centroid = Offset(100f, 150f),
            oldZoom = 1f,
            newZoom = 2f,
        )

        assertEquals(-100f, pan.x, 0.001f)
        assertEquals(-150f, pan.y, 0.001f)
    }
}
