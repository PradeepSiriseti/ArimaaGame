package com.example.arimaa

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.arimaa.ui.theme.ArimaaTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArimaaTheme {
                Scaffold(modifier = Modifier.fillMaxSize().background(Color.Black)) { innerPadding ->
                    ArimaaBoard(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ArimaaBoard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        CurrentPlayerIndicator(isGoldTurn) // Add current player indicator
        TurnMessageDisplay(turnMessage)

        Box(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
        ) {
            ArimaaCanvas()
        }

        // Buttons in a 2x2 matrix within a box shape
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .background(Color.DarkGray)
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ResetGameButton(
                        modifier = Modifier.weight(1f),
                        onClick = { resetGame() }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    UndoMoveButton(
                        modifier = Modifier.weight(1f),
                        onClick = { undoMove() }
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FinishTurnButton(
                        modifier = Modifier.weight(1f),
                        onClick = { finishTurn() }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ResetTurnButton(
                        modifier = Modifier.weight(1f),
                        onClick = { resetTurn() }
                    )
                }
            }
        }
    }
}

// Draw the 8x8 Arimaa board
private fun DrawScope.drawArimaaBoard() {
    val boardSize = size.width
    val cellSize = boardSize / 8

    // Colors
    val lightSquareColor = Color(0xff0000ff) // Khaki
    val darkSquareColor = Color(0xff00ffff) // SaddleBrown
    val trapColor = Color(0xffff00ff) // DarkRed

    // Draw squares
    for (row in 0..7) {
        for (col in 0..7) {
            val squareColor = if ((row + col) % 2 == 0) lightSquareColor else darkSquareColor
            drawRect(
                color = squareColor,
                topLeft = androidx.compose.ui.geometry.Offset(col * cellSize, row * cellSize),
                size = Size(cellSize, cellSize)
            )
        }
    }

    // Draw trap squares
    val trapCoordinates = listOf(
        Pair(2, 2), Pair(2, 5),
        Pair(5, 2), Pair(5, 5)
    )
    trapCoordinates.forEach { (row, col) ->
        drawCircle(
            color = trapColor,
            radius = cellSize / 4,
            center = androidx.compose.ui.geometry.Offset(
                (col + 0.5f) * cellSize,
                (row + 0.5f) * cellSize
            )
        )
    }
}

// Draw initial pieces
private fun DrawScope.drawInitialPieces() {
    val boardSize = size.width
    val cellSize = boardSize / 8

    // Colors for pieces
    val goldColor = Color(0xFFFFD700) // Gold pieces
    val silverColor = Color(0xFFC0C0C0) // Silver pieces

    // Initial positions
    val goldBackRow = listOf("R", "R", "R", "R", "R", "R", "R", "R")
    val goldFrontRow = listOf("C", "D", "H", "M", "E", "H", "D", "C")

    val silverBackRow = listOf("R", "R", "R", "R", "R", "R", "R", "R")
    val silverFrontRow = listOf("C", "D", "H", "M", "E", "H", "D", "C")

    // Draw gold pieces
    goldBackRow.forEachIndexed { col, piece ->
        drawPiece(piece, 7, col, cellSize, goldColor)
    }
    goldFrontRow.forEachIndexed { col, piece ->
        drawPiece(piece, 6, col, cellSize, goldColor)
    }

    // Draw silver pieces
    silverBackRow.forEachIndexed { col, piece ->
        drawPiece(piece, 0, col, cellSize, silverColor)
    }
    silverFrontRow.forEachIndexed { col, piece ->
        drawPiece(piece, 1, col, cellSize, silverColor)
    }
}

private fun DrawScope.drawPiece(
    piece: String,
    row: Int,
    col: Int,
    cellSize: Float,
    color: Color
) {
    val centerX = (col + 0.5f) * cellSize
    val centerY = (row + 0.5f) * cellSize

    // Draw piece background as a square
    drawRect(
        color = color,
        topLeft = androidx.compose.ui.geometry.Offset(centerX - cellSize / 3, centerY - cellSize / 3),
        size = Size(cellSize / 1.5f, cellSize / 1.5f)
    )

    // Draw text for the piece type
    drawIntoCanvas { canvas ->
        val textPaint = android.graphics.Paint().apply {
            this.color = android.graphics.Color.BLACK // Use black for text
            textAlign = android.graphics.Paint.Align.CENTER
            textSize = cellSize / 3
            isAntiAlias = true
        }
        canvas.nativeCanvas.drawText(
            piece,
            centerX,
            centerY + (textPaint.textSize / 3), // Center text vertically
            textPaint
        )
    }
}

data class BoardState(val positions: Array<Array<String>>)

private val boardHistory = mutableListOf<BoardState>()
private var currentBoard by mutableStateOf(
    arrayOf(
        arrayOf("r", "r", "r", "r", "r", "r", "r", "r"),
        arrayOf("c", "d", "h", "m", "e", "h", "d", "c"),
        Array(8) { "" }, Array(8) { "" },
        Array(8) { "" }, Array(8) { "" },
        arrayOf("C", "D", "H", "M", "E", "H", "D", "C"),
        arrayOf("R", "R", "R", "R", "R", "R", "R", "R")
    )
)

private fun saveBoardState() {
    val boardState = BoardState(currentBoard.map { it.copyOf() }.toTypedArray())
    if (boardHistory.none { it.positions.contentDeepEquals(boardState.positions) }) {
        boardHistory.add(boardState)
    }
}

private var movesThisTurn = 0
private val maxMovesPerTurn = 4

private fun updateBoardState(row: Int, col: Int, newValue: String) {
    val newBoard = currentBoard.map { it.copyOf() }.toTypedArray()
    newBoard[row][col] = newValue
    currentBoard = newBoard
}

@Composable
fun ResetTurnButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A5ACD)) // SlateBlue
    ) {
        Text(
            text = "Reset Turn",
            fontWeight = FontWeight.Bold // Bold text
        )
    }
}

private fun resetTurn() {
    movesThisTurn = 0
    currentBoard = boardHistory.lastOrNull()?.positions?.map { it.copyOf() }?.toTypedArray() ?: currentBoard
}

@Composable
fun FinishTurnButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF228B22)) // ForestGreen
    ) {
        Text(
            text = "Finish Turn",
            fontWeight = FontWeight.Bold // Bold text
        )
    }
}

private fun finishTurn() {
    if (movesThisTurn == 0) {
        turnMessage = "You must make at least one move before finishing the turn."
        return
    }
    if (boardHistory.any { it.positions.contentDeepEquals(currentBoard) }) {
        turnMessage = "This board position has already occurred. You cannot repeat it."
        return
    }
    // Check for traps and remove affected pieces
    handleTraps()

    // Check for win condition
    if (checkForVictory()) {
        turnMessage = if (isGoldTurn) "Gold wins!" else "Silver wins!"
        return
    }

    turnMessage = "" // Clear the message if valid
    saveBoardState()
    updateImmobilizedPieces() // Update immobilized pieces at the start of the next turn
    movesThisTurn = 0
    isGoldTurn = !isGoldTurn // Switch players
}

private fun handleTraps() {
    val trapCoordinates = listOf(Pair(2, 2), Pair(2, 5), Pair(5, 2), Pair(5, 5))

    trapCoordinates.forEach { (trapRow, trapCol) ->
        val piece = currentBoard[trapRow][trapCol]
        if (piece.isNotEmpty()) {
            val isFriendlyAdjacent = calculateValidMoves(trapRow, trapCol).any { (adjRow, adjCol) ->
                isCorrectPlayerPiece(currentBoard[adjRow][adjCol]) // Check for adjacent friendly pieces
            }
            if (!isFriendlyAdjacent) {
                currentBoard[trapRow][trapCol] = "" // Remove piece from trap
            }
        }
    }
}

private fun checkForVictory(): Boolean {
    // Check if a Gold Rabbit reaches row 0
    if (currentBoard[0].contains("R")) return true

    // Check if a Silver Rabbit reaches row 7
    if (currentBoard[7].contains("r")) return true

    return false
}

private var selectedPiece: Pair<Int, Int>? by mutableStateOf(null)
private var validMoves: List<Pair<Int, Int>> by mutableStateOf(emptyList())

// Adjust cell click logic to restrict movement by turn
private fun onCellClick(row: Int, col: Int) {
    if (selectedPiece == null) {
        if (currentBoard[row][col].isNotEmpty() && isCorrectPlayerPiece(currentBoard[row][col])) {
            selectedPiece = row to col
            validMoves = calculateValidMoves(row, col)
        }
    } else {
        if (validMoves.contains(row to col)) {
            val (selectedRow, selectedCol) = selectedPiece!!

            // Perform the move
            val piece = currentBoard[selectedRow][selectedCol]
            updateBoardState(row, col, piece)
            updateBoardState(selectedRow, selectedCol, "")

            movesThisTurn++
            onMoveMade() // Save the move in history
        }

        selectedPiece = null
        validMoves = emptyList()
    }
}

private fun showPullDialog(targetRow: Int, targetCol: Int) {
    // Show a dialog to the user
    val (selectedRow, selectedCol) = selectedPiece!!

    val pullRow = selectedRow + (selectedRow - targetRow)
    val pullCol = selectedCol + (selectedCol - targetCol)

    if (isValidMove(targetRow, targetCol, pullRow, pullCol)) {
        // Move selected piece to target location
        currentBoard[targetRow][targetCol] = currentBoard[selectedRow][selectedCol]
        currentBoard[selectedRow][selectedCol] = ""

        // Pull the opposing piece
        currentBoard[pullRow][pullCol] = currentBoard[targetRow][targetCol]
        currentBoard[targetRow][targetCol] = ""

        movesThisTurn += 2
    }
}

private fun calculateValidMoves(row: Int, col: Int): List<Pair<Int, Int>> {
    val piece = currentBoard[row][col]
    val directions = listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1) // Up, Down, Left, Right
    return directions.mapNotNull { (dr, dc) ->
        val newRow = row + dr
        val newCol = col + dc
        if (isValidMove(row, col, newRow, newCol)) newRow to newCol else null
    }
}

private fun isCorrectPlayerPiece(piece: String): Boolean {
    return (isGoldTurn && piece.uppercase() == piece) || (!isGoldTurn && piece.lowercase() == piece)
}

private fun isValidMove(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Boolean {
    if (immobilizedPieces.contains(fromRow to fromCol)) return false
    // Ensure the target cell is within bounds
    val isWithinBounds = toRow in 0..7 && toCol in 0..7
    if (!isWithinBounds) return false

    // Check adjacency
    val isAdjacent = Math.abs(fromRow - toRow) + Math.abs(fromCol - toCol) == 1
    if (!isAdjacent) return false

    // Check if target is empty
    val isEmptyTarget = currentBoard[toRow][toCol].isEmpty()
    if (!isEmptyTarget) return false

    // Rabbit-specific rules
    val piece = currentBoard[fromRow][fromCol]
    val isRabbit = piece.uppercase() == "R"
    if (isRabbit) {
        val isForward = if (piece.uppercase() == piece) toRow < fromRow else toRow > fromRow
        return isForward // Rabbits cannot move sideways or backward
    }

    // General move rules for non-rabbit pieces
    return true
}

@Composable
fun ArimaaCanvas(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val boardSize = size.width
                    val cellSize = boardSize / 8
                    val row = (offset.y / cellSize).toInt()
                    val col = (offset.x / cellSize).toInt()
                    onCellClick(row, col)
                }
            }
    ) {
        drawArimaaBoard()
        drawHighlights()
        drawPieces()
    }
}

// Modify `drawPieces` to differentiate Gold and Silver
private fun DrawScope.drawPieces() {
    val boardSize = size.width
    val cellSize = boardSize / 8

    currentBoard.forEachIndexed { row, rowArray ->
        rowArray.forEachIndexed { col, piece ->
            if (piece.isNotEmpty()) {
                val color = if (piece.uppercase() == piece) Color(0xFFFFD700) else Color(0xFFC0C0C0)
                drawPiece(piece.uppercase(), row, col, cellSize, color)
            }
        }
    }
}

private fun DrawScope.drawHighlights() {
    val boardSize = size.width
    val cellSize = boardSize / 8

    // Highlight the selected piece
    selectedPiece?.let { (row, col) ->
        drawRect(
            color = Color.Yellow.copy(alpha = 0.3f),
            topLeft = androidx.compose.ui.geometry.Offset(col * cellSize, row * cellSize),
            size = Size(cellSize, cellSize)
        )
    }

    // Highlight valid moves
    validMoves.forEach { (row, col) ->
        drawRect(
            color = Color.Green.copy(alpha = 0.3f),
            topLeft = androidx.compose.ui.geometry.Offset(col * cellSize, row * cellSize),
            size = Size(cellSize, cellSize)
        )
    }
}

// Track the active player: true = Gold, false = Silver
private var isGoldTurn by mutableStateOf(true)

@Composable
fun CurrentPlayerIndicator(isGoldTurn: Boolean) {
    Text(
        text = if (isGoldTurn) "Current Player: Gold" else "Current Player: Silver",
        modifier = Modifier.padding(8.dp),
        fontSize = 24.sp,
        color = if (isGoldTurn) Color(0xFFFFD700) else Color(0xFFC0C0C0) // Gold or Silver color
    )
}

private var turnMessage by mutableStateOf("")

@Composable
fun TurnMessageDisplay(message: String) {
    if (message.isNotEmpty()) {
        Text(
            text = message,
            modifier = Modifier.padding(8.dp),
            color = Color.Red
        )
    }
}

@Composable
fun ResetGameButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB22222)) // FireBrick
    ) {
        Text(
            text = "Reset Game",
            fontWeight = FontWeight.Bold // Bold text
        )
    }
}

private fun resetGame() {
    currentBoard = arrayOf(
        arrayOf("r", "r", "r", "r", "r", "r", "r", "r"),
        arrayOf("c", "d", "h", "m", "e", "h", "d", "c"),
        Array(8) { "" }, Array(8) { "" },
        Array(8) { "" }, Array(8) { "" },
        arrayOf("C", "D", "H", "M", "E", "H", "D", "C"),
        arrayOf("R", "R", "R", "R", "R", "R", "R", "R")
    )
    boardHistory.clear()
    movesThisTurn = 0
    isGoldTurn = true
    selectedPiece = null
    validMoves = emptyList()
    turnMessage = ""
}

private val immobilizedPieces = mutableSetOf<Pair<Int, Int>>()

private fun updateImmobilizedPieces() {
    immobilizedPieces.clear()
    currentBoard.forEachIndexed { row, rowArray ->
        rowArray.forEachIndexed { col, piece ->
            if (piece.isNotEmpty()) {
                val adjacentLargerPieces = calculateValidMoves(row, col).filter { (adjRow, adjCol) ->
                    currentBoard[adjRow][adjCol].isNotEmpty() &&
                            isLargerPiece(currentBoard[adjRow][adjCol], piece)
                }
                if (adjacentLargerPieces.isNotEmpty()) {
                    immobilizedPieces.add(row to col)
                }
            }
        }
    }
}

private fun isLargerPiece(larger: String, smaller: String): Boolean {
    val hierarchy = "rmhecdRMHECD"
    return hierarchy.indexOf(larger) > hierarchy.indexOf(smaller)
}

private val currentTurnHistory = mutableListOf<BoardState>()

private fun onMoveMade() {
    val boardState = BoardState(currentBoard.map { it.copyOf() }.toTypedArray())
    currentTurnHistory.add(boardState)
}

@SuppressLint("NewApi")
private fun undoMove() {
    if (currentTurnHistory.isNotEmpty()) {
        val lastState = currentTurnHistory.removeLast()
        currentBoard = lastState.positions
        movesThisTurn--
    }
}

@Composable
fun UndoMoveButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF808080)) // Gray
    ) {
        Text(
            text = "Undo Move",
            fontWeight = FontWeight.Bold,
            color = Color.White // Ensure the text color contrasts well with the dark background
        )
    }
}