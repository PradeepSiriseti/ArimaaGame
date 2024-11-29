package com.example.arimaa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.times
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.times
import kotlin.math.abs
import kotlin.time.times

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
    var boardState by remember { mutableStateOf(getInitialPieces()) }
    var currentPlayer by remember { mutableStateOf(true) } // true = Gold, false = Silver
    var selectedPiece by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var history by remember { mutableStateOf(mutableSetOf<Map<Pair<Int, Int>, Piece>>()) }
    var errorMessage by remember { mutableStateOf("") }

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (currentPlayer) "Gold's Turn" else "Silver's Turn",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (history.contains(boardState)) {
                        errorMessage = "Repeated board state is not allowed."
                        return@Button
                    }
                    errorMessage = ""
                    currentPlayer = !currentPlayer // Switch turn
                    selectedPiece = null
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("End Turn")
            }

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp)
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
// Piece data class
data class Piece(val symbol: String, val isGold: Boolean)
// Initial game setup
fun getInitialPieces(): Map<Pair<Int, Int>, Piece> {
    val pieces = mutableMapOf<Pair<Int, Int>, Piece>()

//@Preview(showBackground = true)
//@Composable
//fun ArimaaGamePreview() {
//    ArimaaGame()
//}
