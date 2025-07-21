package com.example.fintrack.util

import android.content.Context
import android.content.SharedPreferences
import com.example.fintrack.database.DatabaseHelper

object GamificationManager {
    private const val PREFS_NAME = "FinTrackGamification"
    private const val KEY_USER_XP = "userXP"
    private const val KEY_USER_LEVEL = "userLevel"
    private const val KEY_BADGES = "userBadges"
    
    // Define level thresholds
    private val levelThresholds = listOf(0, 100, 250, 500, 1000)

    // Define badges
    enum class Badge {
        FIRST_EXPENSE,
        BUDGET_MASTER,
        CONSISTENT_LOGGER,
        SAVINGS_CHAMPION
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    fun getXP(context: Context): Int {
        return getPrefs(context).getInt(KEY_USER_XP, 0)
    }
    
    fun getLevel(context: Context): Int {
        return getPrefs(context).getInt(KEY_USER_LEVEL, 1)
    }
    
    fun addXP(context: Context, points: Int): Boolean {
        val prefs = getPrefs(context)
        val currentXP = getXP(context)
        val newXP = currentXP + points
        var currentLevel = getLevel(context)
        var leveledUp = false
        
        // Check for level up
        if (currentLevel < levelThresholds.size - 1 && newXP >= levelThresholds[currentLevel + 1]) {
            currentLevel++
            leveledUp = true
        }
        
        prefs.edit().apply {
            putInt(KEY_USER_XP, newXP)
            putInt(KEY_USER_LEVEL, currentLevel)
            apply()
        }
        
        return leveledUp
    }

    fun getBadges(context: Context): Set<String> {
        return getPrefs(context).getStringSet(KEY_BADGES, setOf()) ?: setOf()
    }

    fun awardBadge(context: Context, badge: Badge) {
        val prefs = getPrefs(context)
        val currentBadges = getBadges(context).toMutableSet()
        currentBadges.add(badge.name)
        
        prefs.edit().putStringSet(KEY_BADGES, currentBadges).apply()
    }

    fun checkAndAwardBadges(context: Context, dbHelper: DatabaseHelper) {
        val userId = context.getSharedPreferences("FinTrackPrefs", Context.MODE_PRIVATE)
            .getInt("userId", 0)
        
        if (userId == 0) return

        val badges = getBadges(context)
        
        // Check for First Expense badge
        if (!badges.contains(Badge.FIRST_EXPENSE.name)) {
            val transactions = dbHelper.getTodayTransactionsByUserId(userId)
            if (transactions.isNotEmpty()) {
                awardBadge(context, Badge.FIRST_EXPENSE)
            }
        }

        // Check for Budget Master badge
        if (!badges.contains(Badge.BUDGET_MASTER.name)) {
            val totalSpent = Math.abs(dbHelper.getTotalSpentByUserId(userId))
            val budget = context.getSharedPreferences("FinTrackPrefs", Context.MODE_PRIVATE)
                .getFloat("userBudget", 0.0f)
            
            if (budget > 0 && totalSpent <= budget) {
                awardBadge(context, Badge.BUDGET_MASTER)
            }
        }

        // Future badge checks can be added here
    }
}
