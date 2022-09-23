package eu.sesma.devcalc

class Solver {
    fun solve(noCursorOperation: String): String {
        val operandStrings = noCursorOperation.split("+", "-", "x", "/")

        val operators = (0 until operandStrings.size - 1).map { index ->
            noCursorOperation.indexOfAny(
                chars = "+-/x".toCharArray(),
                startIndex = (0 until index).fold(
                    initial = 0,
                    operation = { acc, i -> acc + operandStrings[i].length }) + index
            )
        }.map { noCursorOperation[it] }
        val operands = operandStrings.map { it.toFloat() }

        return noCursorOperation
    }
}