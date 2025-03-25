package com.to_do_listapp

data class Task(
    val description: String,
    var isCompleted: Boolean = false,
    val date: String  // ✅ Store the date (Format: YYYY-MM-DD)
)
