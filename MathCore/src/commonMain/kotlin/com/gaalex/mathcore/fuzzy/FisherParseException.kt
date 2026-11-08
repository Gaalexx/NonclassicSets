package com.gaalex.mathcore.fuzzy

sealed class FisherParseException(message: String, cause: Throwable? = null) :
    IllegalArgumentException(message, cause) {

    class WrongFormat(val input: String) :
        FisherParseException("Wrong string format: '$input'")

    class NotANumber(val parameter: String, val value: String) :
        FisherParseException("$parameter is not a number: '$value'")

    class InvalidParameters(val k1: Double, val k2: Double, cause: Throwable) :
        FisherParseException("Invalid parameters k1 = $k1, k2 = $k2: ${cause.message}", cause)
}
