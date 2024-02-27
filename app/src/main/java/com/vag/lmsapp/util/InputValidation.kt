package com.vag.lmsapp.util

import com.vag.lmsapp.model.Rule

class InputValidation {
    class ValidationError(val key: String, val message: String)
    private var errorList: MutableList<ValidationError> = mutableListOf()
    constructor(errors: List<ValidationError>) {
        errorList = errors.toMutableList()
    }
    constructor()

    fun isInvalid() : Boolean {
        return errorList.isNotEmpty()
    }

    fun getErrorList(): MutableList<ValidationError> {
        return errorList
    }

    private fun addError(validationError: ValidationError) {
        errorList.add(validationError)
        println("Add error")
        println("key ${validationError.key}")
        println("message ${validationError.message}")
    }

    fun removeError(key: String) : InputValidation {
        if(key.isEmpty()) {
            errorList.clear()
        } else {
            errorList.removeIf {
                key == it.key
            }
        }
        return this
    }

    fun has(key: String) : Boolean {
        return errorList.any {
            it.key == key
        }
    }

    fun get(key: String) : String {
        val error = errorList.find {
            key == it.key
        }
        return error?.message ?: ""
    }

    fun addRule(key: String, input: Any?, rules: Array<Rule>) {
        var error = errorList.find {
            it.key == key
        }
        rules.forEach {
            if(!it.isValid(input)) {
                error = ValidationError(key, it.message)
            }
        }
        error?.let { addError(it) }
    }

    fun addError(key: String, message: String) {
        errorList.add(ValidationError(key, message))
    }
}