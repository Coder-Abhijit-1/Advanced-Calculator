package com.example.android.calculator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.android.calculator.databinding.ActivityMainBinding
import java.util.Stack

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Set click listeners for all buttons
        binding.buttonAC.setOnClickListener { allClearAction() }
        binding.buttonDel.setOnClickListener { backSpaceAction() }
        binding.equalTo.setOnClickListener { equalsAction() }

        binding.number0.setOnClickListener { appendText("0") }
        binding.number1.setOnClickListener { appendText("1") }
        binding.number2.setOnClickListener { appendText("2") }
        binding.number3.setOnClickListener { appendText("3") }
        binding.number4.setOnClickListener { appendText("4") }
        binding.number5.setOnClickListener { appendText("5") }
        binding.number6.setOnClickListener { appendText("6") }
        binding.number7.setOnClickListener { appendText("7") }
        binding.number8.setOnClickListener { appendText("8") }
        binding.number9.setOnClickListener { appendText("9") }
        binding.dot.setOnClickListener { appendDot() }
        binding.percentage.setOnClickListener { appendPercentage() }
        binding.division.setOnClickListener { appendText("/") }
        binding.multiply.setOnClickListener { appendText("x") }
        binding.subtract.setOnClickListener { appendText("-") }
        binding.addition.setOnClickListener { appendText("+") }
    }

    // Append text to the input TextView
    private fun appendText(text: String) {
        binding.inputsTextView.append(text)
    }

    // Append a dot for decimal numbers
    private fun appendDot() {
        val currentText = binding.inputsTextView.text.toString()
        // Ensure only one dot is allowed in a number
        if (currentText.isNotEmpty() && !currentText.endsWith(".") && !currentText.endsWith(" ")) {
            val lastNumber = currentText.split(Regex("[+\\-*/x]")).lastOrNull()
            if (lastNumber != null && !lastNumber.contains(".")) {
                appendText(".")
            }
        }
    }

    // Append percentage sign
    private fun appendPercentage() {
        val currentText = binding.inputsTextView.text.toString()
        if (currentText.isNotEmpty() && currentText.last().isDigit()) {
            appendText("%")
        }
    }

    // Clear all inputs and results
    private fun allClearAction() {
        binding.inputsTextView.text = ""
        binding.resultTextView.text = ""
    }

    // Remove the last character from the input
    private fun backSpaceAction() {
        val currentText = binding.inputsTextView.text.toString()
        if (currentText.isNotEmpty()) {
            binding.inputsTextView.text = currentText.substring(0, currentText.length - 1)
        }
    }

    // Evaluate the expression in inputsTextView and display the result
    private fun equalsAction() {
        val inputExpression = binding.inputsTextView.text.toString()

        try {
            // Replace "x" with "*" and "%" with "/100" for percentage evaluation
            val sanitizedExpression = inputExpression
                .replace("x", "*")
                .replace("%", "/100")
            val result = evaluateExpression(sanitizedExpression)
            binding.resultTextView.text = result.toString()
        } catch (e: Exception) {
            binding.resultTextView.text = "Error"
        }
    }

    // Function to evaluate the mathematical expression
    private fun evaluateExpression(expression: String): Double {
        val values = Stack<Double>()
        val operators = Stack<Char>()

        var i = 0
        while (i < expression.length) {
            val ch = expression[i]

            when {
                ch.isDigit() || ch == '.' -> {
                    // Parse the number and push it to the values stack
                    val sb = StringBuilder()
                    while (i < expression.length && (expression[i].isDigit() || expression[i] == '.')) {
                        sb.append(expression[i++])
                    }
                    values.push(sb.toString().toDouble())
                    i-- // Step back to re-evaluate the character
                }

                ch == '(' -> {
                    operators.push(ch)
                }

                ch == ')' -> {
                    // Evaluate the expression inside parentheses
                    while (operators.peek() != '(') {
                        values.push(applyOperator(operators.pop(), values.pop(), values.pop()))
                    }
                    operators.pop() // Remove '(' from the stack
                }

                isOperator(ch) -> {
                    // Evaluate based on operator precedence
                    while (operators.isNotEmpty() && hasPrecedence(ch, operators.peek())) {
                        values.push(applyOperator(operators.pop(), values.pop(), values.pop()))
                    }
                    operators.push(ch)
                }
            }
            i++
        }

        // Evaluate the remaining expression
        while (operators.isNotEmpty()) {
            values.push(applyOperator(operators.pop(), values.pop(), values.pop()))
        }

        return values.pop()
    }

    // Check if the character is an operator
    private fun isOperator(ch: Char): Boolean {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/'
    }

    // Check operator precedence
    private fun hasPrecedence(op1: Char, op2: Char): Boolean {
        if (op2 == '(' || op2 == ')') return false
        return !(op1 == '*' || op1 == '/') || (op2 != '+' && op2 != '-')
    }

    // Apply an operator to two operands
    private fun applyOperator(op: Char, b: Double, a: Double): Double {
        return when (op) {
            '+' -> a + b
            '-' -> a - b
            '*' -> a * b
            '/' -> {
                if (b == 0.0) throw ArithmeticException("Division by zero")
                a / b
            }

            else -> throw UnsupportedOperationException("Unknown operator: $op")
        }
    }
}
