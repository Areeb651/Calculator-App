package com.example.calculatorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.calculatorapp.ui.theme.CalculatorAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorAppTheme {
                CalculatorScreen()
            }
        }
    }
}

@Composable
fun CalculatorScreen() {
    var expression by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = result.ifEmpty { expression.ifEmpty { "0" } },
            fontSize = 48.sp,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
        )

        val buttons = listOf(
            listOf("7", "8", "9", "/"),
            listOf("4", "5", "6", "*"),
            listOf("1", "2", "3", "-"),
            listOf("0", "C", "=", "+")
        )

        buttons.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { label ->
                    Button(
                        onClick = {
                            when (label) {
                                "C" -> {
                                    expression = ""
                                    result = ""
                                }
                                "=" -> {
                                    try {
                                        result = simpleEvaluate(expression)
                                    } catch (e: Exception) {
                                        result = "Error"
                                    }
                                }
                                else -> {
                                    if (result.isNotEmpty()) {
                                        expression = result
                                        result = ""
                                    }
                                    expression += label
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(label, fontSize = 24.sp)
                    }
                }
            }
        }
    }
}

fun simpleEvaluate(expression: String): String {
    return try {
        val tokens = expression.split(" ").filter { it.isNotBlank() }
        val numbers = mutableListOf<Double>()
        val operators = mutableListOf<Char>()

        var current = ""
        for (char in expression) {
            when (char) {
                in '0'..'9', '.' -> current += char
                '+', '-', '*', '/' -> {
                    numbers.add(current.toDouble())
                    current = ""
                    operators.add(char)
                }
            }
        }
        if (current.isNotEmpty()) numbers.add(current.toDouble())

        var result = numbers[0]
        for (i in operators.indices) {
            val next = numbers[i + 1]
            when (operators[i]) {
                '+' -> result += next
                '-' -> result -= next
                '*' -> result *= next
                '/' -> result = if (next != 0.0) result / next else return "Error"
            }
        }
        result.toString()
    } catch (e: Exception) {
        "Error"
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    MaterialTheme {
        CalculatorScreen()
    }
}