package com.example.fintrack.models

data class BudgetGoal(
    val id: Int = 0,
    val userId: Int,
    val category: String,
    val minBudget: Double,
    val maxBudget: Double
)
