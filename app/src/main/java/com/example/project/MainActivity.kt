package com.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.times

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArimaaGame()
        }
    }
}

@Composable
fun ArimaaGame() {
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ArimaaGameBoard()
        }
    }
}

@Composable
fun ArimaaGameBoard() {
    val boardSize = 8
    val trapPositions = setOf(
        Pair(2, 2), Pair(2, 5), Pair(5, 2), Pair(5, 5) // Trap squares
    )
    val initialPieces = getInitialPieces()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(16.dp)
    ) {
        val squareSize = maxWidth / boardSize

        // Draw the board grid
        Canvas(modifier = Modifier.fillMaxSize()) {
            for (row in 0 until boardSize) {
                for (col in 0 until boardSize) {
                    drawSquare(row, col, squareSize.toPx(), trapPositions)
                }
            }
        }

        // Draw the pieces
        for ((position, piece) in initialPieces) {
            val (row, col) = position
            Box(
                modifier = Modifier
                    .size(squareSize)
                    .offset(x = col * squareSize, y = row * squareSize)
                    .align(Alignment.TopStart),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = piece.symbol,
                    fontSize = 16.sp,
                    color = if (piece.isGold) Color.Yellow else Color.Gray
                )
            }
        }
    }
}

fun DrawScope.drawSquare(
    row: Int,
    col: Int,
    squareSize: Float,
    trapPositions: Set<Pair<Int, Int>>
) {
    val isTrap = trapPositions.contains(Pair(row, col))
    val color = if ((row + col) % 2 == 0) Color.LightGray else Color.DarkGray
    drawRect(
        color = if (isTrap) Color.Red else color,
        topLeft = androidx.compose.ui.geometry.Offset(col * squareSize, row * squareSize),
        size = androidx.compose.ui.geometry.Size(squareSize, squareSize)
    )
}

// Piece data class
data class Piece(val symbol: String, val isGold: Boolean)

// Initial game setup
fun getInitialPieces(): Map<Pair<Int, Int>, Piece> {
    val pieces = mutableMapOf<Pair<Int, Int>, Piece>()

    // Gold player's pieces
    val goldRow1 = listOf("C", "D", "H", "M", "E", "H", "D", "C")
    val goldRow2 = List(8) { "R" }
    goldRow1.forEachIndexed { index, symbol ->
        pieces[Pair(6, index)] = Piece(symbol, true)
    }
    goldRow2.forEachIndexed { index, symbol ->
        pieces[Pair(7, index)] = Piece(symbol, true)
    }

    // Silver player's pieces
    val silverRow1 = listOf("C", "D", "H", "M", "E", "H", "D", "C")
    val silverRow2 = List(8) { "R" }
    silverRow1.forEachIndexed { index, symbol ->
        pieces[Pair(1, index)] = Piece(symbol, false)
    }
    silverRow2.forEachIndexed { index, symbol ->
        pieces[Pair(0, index)] = Piece(symbol, false)
    }

    return pieces
}

@Preview(showBackground = true)
@Composable
fun ArimaaGamePreview() {
    ArimaaGame()
}
