package com.vag.lmsapp.model

import android.util.Patterns
import java.time.Instant

sealed class Rule(var message: String) : RuleInterface {

    object IsEmail : Rule("No a valid email format") {
        override fun isValid(input: Any?): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(input.toString()).matches()
        }
    }

    object Required : Rule("Field is required") {
        override fun isValid(input: Any?): Boolean {
            return input != null && input != "" && input != "null"
        }
    }

    class Matched(private val matchedWith: String?) : Rule("Fields do not matched") {
        constructor(matchedWith: String?, defaultMessage: String) : this(matchedWith) {
            message = defaultMessage
        }

        override fun isValid(input: Any?): Boolean {
            return matchedWith == input
        }
    }

    class Min(private val minimumValue: Float?) : Rule("Field must be greater than or equal $minimumValue") {
        constructor(value: Float?, defaultMessage: String) : this(value) {
            message = defaultMessage
        }

        override fun isValid(input: Any?): Boolean {
            val _input = input.toString().toFloatOrNull()?:0f
            val _min = this.minimumValue?:0f
            return _input >= _min
        }
    }

    class Max(private val maxValue: Float?) : Rule("Field must be less than or equal $maxValue") {
        constructor(value: Float?, defaultMessage: String) : this(value) {
            message = defaultMessage
        }

        override fun isValid(input: Any?): Boolean {
            val _input = input.toString().toFloatOrNull()?:0f
            val _max = this.maxValue?:0f
            return _input <= _max
        }
    }

    class ExactAmount(private val amount: Float?) : Rule("Field must be exact amount only") {
        constructor(value: Float?, defaultMessage: String) : this(value) {
            message = defaultMessage
        }

        override fun isValid(input: Any?): Boolean {
            val _input = input.toString().toFloatOrNull()?:0f
            return _input == amount
        }
    }

//    class IsNumeric(private val value: Any?) : Rule("Not a valid number") {
//        override fun isValid(input: Any?): Boolean {
//            return try {
//                val keme = value?.toString()?.toDoubleOrNull()
//                println("keme")
//                println(keme)
//                value?.toString()?.toDoubleOrNull() != null
//            } catch (e: Exception) {
//                false
//            }
//        }
//    }

    object IsNumeric : Rule("Not a valid number") {
        override fun isValid(input: Any?): Boolean {
            return try {
                val keme = input?.toString()?.toDoubleOrNull()
                println("keme")
                println(keme)
                input?.toString()?.toDoubleOrNull() != null
            } catch (e: Exception) {
                false
            }
        }
    }

    object DivisibleBy10 : Rule("Field must be divisible by 10") {
        override fun isValid(input: Any?): Boolean {
            if (input is Int) {
                return input % 10 == 0
            }
            return false
        }
    }

    class NotBefore(val instant: Instant) : Rule("Must not be before $instant") {
        override fun isValid(input: Any?): Boolean {
            return if(input is Instant) {
                !input.isBefore(instant)
            } else {
                message = "Invalid date time"
                false
            }
        }
    }

    class NotAfter(val instant: Instant) : Rule("Must not be after $instant") {
        override fun isValid(input: Any?): Boolean {
            return if(input is Instant) {
                !input.isAfter(instant)
            } else {
                message = "Invalid date time"
                false
            }
        }
    }
}
