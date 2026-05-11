package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.mh.restaurantchainpos.pos.data.Floor
import com.mh.restaurantchainpos.pos.data.FloorMetrics
import com.mh.restaurantchainpos.pos.data.FloorTable
import com.mh.restaurantchainpos.pos.data.PosMockData
import com.mh.restaurantchainpos.pos.data.Reservation
import com.mh.restaurantchainpos.pos.data.TableShape
import com.mh.restaurantchainpos.pos.data.TableStatus

/**
 * Captures every observable knob for the floor-plan screen. Everything that
 * mutates table layout flows through `commit`, which pushes a snapshot onto
 * the undo stack so the toolbar's undo/redo actions stay in sync.
 */
class FloorPlanState(
    initialFloors: List<Floor>,
    initialReservations: List<Reservation>,
) {
    private val historyStack: SnapshotStateList<List<Floor>> = mutableStateListOf<List<Floor>>().apply { add(initialFloors) }
    var historyIdx by mutableStateOf(0)
        private set

    val floors: List<Floor> get() = historyStack[historyIdx]
    var activeFloorId by mutableStateOf(initialFloors.first().id)
    var selectedTableId by mutableStateOf<String?>(null)
    var editMode by mutableStateOf(false)
    var showSeats by mutableStateOf(true)
    var zoom by mutableStateOf(1f)
    var view by mutableStateOf(FloorViewMode.Floor)
    var reservations: SnapshotStateList<Reservation> = initialReservations.toMutableStateList()

    val activeFloor: Floor get() = floors.firstOrNull { it.id == activeFloorId } ?: floors.first()
    val tables: List<FloorTable> get() = activeFloor.tables
    val selectedTable: FloorTable? get() = selectedTableId?.let { id -> tables.firstOrNull { it.id == id } }

    val canUndo: Boolean get() = historyIdx > 0
    val canRedo: Boolean get() = historyIdx < historyStack.size - 1

    fun undo() {
        if (canUndo) historyIdx -= 1
    }

    fun redo() {
        if (canRedo) historyIdx += 1
    }

    fun commitFloors(next: List<Floor>) {
        while (historyStack.size > historyIdx + 1) historyStack.removeAt(historyStack.size - 1)
        historyStack.add(next)
        historyIdx = historyStack.size - 1
    }

    fun replaceLive(next: List<Floor>) {
        historyStack[historyIdx] = next
    }

    fun updateTables(commit: Boolean = true, transform: (List<FloorTable>) -> List<FloorTable>) {
        val next = floors.map { f ->
            if (f.id == activeFloorId) f.copy(tables = transform(f.tables)) else f
        }
        if (commit) commitFloors(next) else replaceLive(next)
    }

    fun renameFloor(id: String, name: String) {
        val next = floors.map { if (it.id == id) it.copy(name = name) else it }
        commitFloors(next)
    }

    fun addFloor(name: String) {
        val id = "f${System.currentTimeMillis()}"
        val next = floors + Floor(id, name, emptyList())
        commitFloors(next)
        activeFloorId = id
    }

    fun removeFloor(id: String) {
        if (floors.size <= 1) return
        val idx = floors.indexOfFirst { it.id == id }
        val next = floors.filterNot { it.id == id }
        commitFloors(next)
        if (activeFloorId == id) activeFloorId = next.getOrNull(maxOf(0, idx - 1))?.id ?: next.first().id
    }

    fun addTable() {
        val baseSeed = (System.currentTimeMillis() % 200L).toInt()
        val sx = ((80 + baseSeed) / FloorMetrics.SnapGrid) * FloorMetrics.SnapGrid
        val sy = ((80 + baseSeed * 2 % 200) / FloorMetrics.SnapGrid) * FloorMetrics.SnapGrid
        val num = tables.size + 1
        val table = FloorTable(
            id = "T${System.currentTimeMillis()}",
            label = "Table $num",
            seats = 4,
            shape = TableShape.Rect,
            x = sx,
            y = sy,
            width = FloorMetrics.BaseUnit,
            height = FloorMetrics.BaseUnit,
            status = TableStatus.Available,
        )
        updateTables(transform = { it + table })
        selectedTableId = table.id
    }

    fun deleteTable(id: String) {
        updateTables(transform = { list -> list.filterNot { it.id == id } })
        if (selectedTableId == id) selectedTableId = null
    }

    fun duplicateTable(id: String) {
        val src = tables.firstOrNull { it.id == id } ?: return
        val dup = src.copy(
            id = "T${System.currentTimeMillis()}",
            label = "${src.label} copy",
            x = src.x + 30,
            y = src.y + 30,
        )
        updateTables(transform = { it + dup })
        selectedTableId = dup.id
    }

    fun updateSelected(transform: (FloorTable) -> FloorTable) {
        val id = selectedTableId ?: return
        updateTables(transform = { list -> list.map { if (it.id == id) transform(it) else it } })
    }

    fun moveSelected(x: Int, y: Int, commit: Boolean) {
        val id = selectedTableId ?: return
        updateTables(commit = commit) { list -> list.map { if (it.id == id) it.copy(x = x, y = y) else it } }
    }
}

@Composable
fun rememberFloorPlanState(): FloorPlanState =
    remember { FloorPlanState(PosMockData.floors, PosMockData.reservations) }

enum class FloorViewMode(val label: String, val icon: String) {
    Floor("Floor", "▦"),
    Table("Table", "▤"),
    Calendar("Calendar", "▧"),
}
