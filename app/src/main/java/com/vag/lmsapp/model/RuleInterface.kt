package com.vag.lmsapp.model

interface RuleInterface {
//    var message: String
    fun isValid(input: Any?) : Boolean
}
