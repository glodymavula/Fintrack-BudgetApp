package com.example.fintrack

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.content.res.ColorStateList
import com.github.mikephil.charting.components.LimitLine
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fintrack.adapters.TransactionAdapter
import com.example.fintrack.database.DatabaseHelper
import com.example.fintrack.models.CategorySpending
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.example.fintrack.models.BudgetGoal
import com.example.fintrack.util.GamificationManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.*

class StatisticsActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var lineChart: LineChart
    private lateinit var pieChart: PieChart
    private lateinit var spinnerYear: Spinner
    private lateinit var rvHistory: RecyclerView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var tabLayout: TabLayout
    private lateinit var tvStartDate: TextView
    private lateinit var tvEndDate: TextView
    private lateinit var btnSelectStartDate: Button
    private lateinit var btnSelectEndDate: Button
    private lateinit var btnApplyDateFilter: Button
    private lateinit var tvNoData: TextView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var progressBudget: ProgressBar
    private lateinit var btnSetBudgetGoals: Button
    private lateinit var tvBudgetStatus: TextView
    private lateinit var tvXP: TextView
    private lateinit var tvLevel: TextView
    private lateinit var tvBadges: TextView
    private var userId: Int = 0
    private val startCalendar = Calendar.getInstance()
    private val endCalendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        // Get user ID from shared preferences
        val sharedPref = getSharedPreferences("FinTrackPrefs", MODE_PRIVATE)
        userId = sharedPref.getInt("userId", 0)

        if (userId == 0) {
            // User not logged in, redirect to login
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Initialize views
        lineChart = findViewById(R.id.lineChart)
        pieChart = findViewById(R.id.pieChart)
        btnSetBudgetGoals = findViewById(R.id.btnSetBudgetGoals)

        // Set up budget goals button
        btnSetBudgetGoals.setOnClickListener {
            showSetBudgetGoalsDialog()
        }
        spinnerYear = findViewById(R.id.spinnerYear)
        rvHistory = findViewById(R.id.rvHistory)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        fabAdd = findViewById(R.id.fabAdd)
        tabLayout = findViewById(R.id.tabLayout)
        tvStartDate = findViewById(R.id.tvStartDate)
        tvEndDate = findViewById(R.id.tvEndDate)
        btnSelectStartDate = findViewById(R.id.btnSelectStartDate)
        btnSelectEndDate = findViewById(R.id.btnSelectEndDate)
        btnApplyDateFilter = findViewById(R.id.btnApplyDateFilter)
        tvNoData = findViewById(R.id.tvNoData)

        // Initialize views
        progressBudget = findViewById(R.id.progressBudget)
        tvBudgetStatus = findViewById(R.id.tvBudgetStatus)
        tvXP = findViewById(R.id.tvXP)
        tvLevel = findViewById(R.id.tvLevel)
        tvBadges = findViewById(R.id.tvBadges)

        // Initialize database helper
        dbHelper = DatabaseHelper(this)

        // Set up RecyclerView
        rvHistory.layoutManager = LinearLayoutManager(this)
        transactionAdapter = TransactionAdapter(this, ArrayList())
        rvHistory.adapter = transactionAdapter

        // Set up spinner
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.years,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerYear.adapter = adapter
        spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                if (position > 0) {
                    val year = parent.getItemAtPosition(position).toString().toInt()
                    loadChartData(year)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        // Set up bottom navigation
        bottomNavigation.setOnNavigationItemSelectedListener(this)
        bottomNavigation.menu.findItem(R.id.navigation_stats).isChecked = true

        // Set up FAB
        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddSpendingActivity::class.java))
        }

        // Set up tab layout
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                updateChartVisibility(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // Set up date pickers
        btnSelectStartDate.setOnClickListener {
            showDatePickerDialog(true)
        }

        btnSelectEndDate.setOnClickListener {
            showDatePickerDialog(false)
        }

        btnApplyDateFilter.setOnClickListener {
            loadCategorySpendingData()
        }

        // Set default dates (current month)
        startCalendar.set(Calendar.DAY_OF_MONTH, 1)
        updateDateTextViews()

        // Load data
        loadData()
        setupCharts()
        loadChartData(Calendar.getInstance().get(Calendar.YEAR))
        loadCategorySpendingData()
        loadBudgetPerformance()
        loadGamificationData()

        // Default to pie chart view
        tabLayout.getTabAt(1)?.select()
    }

    private fun updateDateTextViews() {
        tvStartDate.text = dateFormat.format(startCalendar.time)
        tvEndDate.text = dateFormat.format(endCalendar.time)
    }

    private fun showDatePickerDialog(isStartDate: Boolean) {
        val calendar = if (isStartDate) startCalendar else endCalendar
        DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                updateDateTextViews()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateChartVisibility(tabPosition: Int) {
        when (tabPosition) {
            0 -> { // Monthly spending
                lineChart.visibility = View.VISIBLE
                pieChart.visibility = View.GONE
                spinnerYear.visibility = View.VISIBLE
                findViewById<View>(R.id.dateFilterLayout).visibility = View.GONE
            }
            1 -> { // Category breakdown
                lineChart.visibility = View.GONE
                pieChart.visibility = View.VISIBLE
                spinnerYear.visibility = View.GONE
                findViewById<View>(R.id.dateFilterLayout).visibility = View.VISIBLE
            }
        }
        // Ensure bottom navigation is always visible
        bottomNavigation.visibility = View.VISIBLE
    }

    private fun showSetBudgetGoalsDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_set_budget_goals)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val spinnerCategory = dialog.findViewById<Spinner>(R.id.spinnerCategory)
        val etMinBudget = dialog.findViewById<EditText>(R.id.etMinBudget)
        val etMaxBudget = dialog.findViewById<EditText>(R.id.etMaxBudget)
        val btnSave = dialog.findViewById<Button>(R.id.btnSave)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)

        // Set up category spinner
        val categories = resources.getStringArray(R.array.expense_categories)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        // Load existing budget goal if any
        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    val category = parent.getItemAtPosition(position).toString()
                    val budgetGoals = dbHelper.getBudgetGoalsByUserId(userId)
                    val existingGoal = budgetGoals.find { it.category == category }
                    
                    if (existingGoal != null) {
                        etMinBudget.setText(existingGoal.minBudget.toString())
                        etMaxBudget.setText(existingGoal.maxBudget.toString())
                    } else {
                        etMinBudget.text.clear()
                        etMaxBudget.text.clear()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        btnSave.setOnClickListener {
            val category = spinnerCategory.selectedItem.toString()
            val minBudgetStr = etMinBudget.text.toString()
            val maxBudgetStr = etMaxBudget.text.toString()

            if (category.isEmpty()) {
                Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (minBudgetStr.isEmpty() || maxBudgetStr.isEmpty()) {
                Toast.makeText(this, "Please enter both minimum and maximum budget", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val minBudget = minBudgetStr.toDoubleOrNull()
            val maxBudget = maxBudgetStr.toDoubleOrNull()

            if (minBudget == null || maxBudget == null) {
                Toast.makeText(this, "Please enter valid budget amounts", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (minBudget >= maxBudget) {
                Toast.makeText(this, "Maximum budget must be greater than minimum budget", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save or update budget goal
            val budgetGoal = BudgetGoal(
                userId = userId,
                category = category,
                minBudget = minBudget,
                maxBudget = maxBudget
            )

            val success = dbHelper.addBudgetGoal(budgetGoal)
            if (success > 0) {
                Toast.makeText(this, "Budget goals saved successfully", Toast.LENGTH_SHORT).show()
                loadCategorySpendingData() // Refresh the chart
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Failed to save budget goals", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        loadData()
        loadChartData(Calendar.getInstance().get(Calendar.YEAR))
        loadCategorySpendingData()
        loadBudgetPerformance()
        loadGamificationData()
    }

    private fun loadData() {
        // Load today's transactions
        val todayTransactions = dbHelper.getTodayTransactionsByUserId(userId)
        transactionAdapter.updateTransactions(todayTransactions)
    }

    private fun setupCharts() {
        // Setup Line Chart
        lineChart.description.isEnabled = false
        lineChart.legend.isEnabled = true
        lineChart.legend.textSize = 12f
        lineChart.setDrawGridBackground(false)
        lineChart.setDrawBorders(false)
        lineChart.setTouchEnabled(true)
        lineChart.isDragEnabled = true
        lineChart.setScaleEnabled  (true)
        lineChart.setPinchZoom(true)
        lineChart.animateX(1500)

        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.textSize = 12f
        xAxis.textColor = Color.BLACK

        val leftAxis = lineChart.axisLeft
        leftAxis.setDrawGridLines(true)
        leftAxis.axisMinimum = 0f
        leftAxis.textSize = 12f
        leftAxis.textColor = Color.BLACK

        val rightAxis = lineChart.axisRight
        rightAxis.isEnabled = false

        // Setup Pie Chart
        pieChart.description.isEnabled = false
        pieChart.setUsePercentValues(true)
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        pieChart.dragDecelerationFrictionCoef = 0.95f
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)
        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)
        pieChart.holeRadius = 58f
        pieChart.transparentCircleRadius = 61f
        pieChart.setDrawCenterText(true)
        pieChart.rotationAngle = 0f
        pieChart.isRotationEnabled = true
        pieChart.isHighlightPerTapEnabled = true
        pieChart.animateY(1400)
        pieChart.legend.isEnabled = true
        pieChart.legend.verticalAlignment = com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.BOTTOM
        pieChart.legend.horizontalAlignment = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER
        pieChart.legend.orientation = com.github.mikephil.charting.components.Legend.LegendOrientation.HORIZONTAL
        pieChart.legend.setDrawInside(false)
        pieChart.legend.textSize = 12f
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.setEntryLabelTextSize(12f)
    }

    private fun loadChartData(year: Int) {
        val monthlyData = dbHelper.getMonthlySpendingByYear(userId, year)
        val entries = ArrayList<Entry>()
        val months = ArrayList<String>()

        for (i in 0 until 12) {
            val month = i + 1
            val spending = monthlyData[month] ?: 0.0
            entries.add(Entry(i.toFloat(), Math.abs(spending).toFloat()))
            months.add(getMonthName(month))
        }

        val dataSet = LineDataSet(entries, "Monthly Spending (R)")
        dataSet.color = ContextCompat.getColor(this, R.color.colorPrimary)
        dataSet.setCircleColor(ContextCompat.getColor(this, R.color.colorPrimary))
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 4f
        dataSet.setDrawCircleHole(false)
        dataSet.valueTextSize = 12f
        dataSet.setDrawFilled(true)
        dataSet.fillColor = ContextCompat.getColor(this, R.color.colorPrimary)
        dataSet.fillAlpha = 30
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.cubicIntensity = 0.2f

        val lineData = LineData(dataSet)
        lineChart.data = lineData

        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(months)

        lineChart.invalidate()
    }

    private fun loadCategorySpendingData() {
        val startDate = startCalendar.time
        val endDate = endCalendar.time

        if (startDate.after(endDate)) {
            Toast.makeText(this, "Start date cannot be after end date", Toast.LENGTH_SHORT).show()
            return
        }

        val categorySpending = dbHelper.getCategorySpendingByPeriod(userId, startDate, endDate)
        val budgetGoals = dbHelper.getBudgetGoalsByUserId(userId)

        if (categorySpending.isEmpty()) {
            pieChart.visibility = View.GONE
            tvNoData.visibility = View.VISIBLE
            return
        }

        pieChart.visibility = View.VISIBLE
        tvNoData.visibility = View.GONE

        val entries = ArrayList<PieEntry>()
        val colors = ArrayList<Int>()
        val budgetLimitLines = ArrayList<LimitLine>()

        for (category in categorySpending) {
            val amount = Math.abs(category.amount)
            entries.add(PieEntry(amount.toFloat(), category.category))

            // Find budget goal for this category
            val budgetGoal = budgetGoals.find { it.category == category.category }
            
            // Add limit lines for min and max budget if available
            if (budgetGoal != null) {
                val minLine = LimitLine(budgetGoal.minBudget.toFloat(), "Min Budget").apply {
                    lineWidth = 2f
                    lineColor = Color.parseColor("#4CAF50") // Green
                    textColor = Color.BLACK
                    textSize = 12f
                }
                
                val maxLine = LimitLine(budgetGoal.maxBudget.toFloat(), "Max Budget").apply {
                    lineWidth = 2f
                    lineColor = Color.parseColor("#F44336") // Red
                    textColor = Color.BLACK
                    textSize = 12f
                }
                
                budgetLimitLines.add(minLine)
                budgetLimitLines.add(maxLine)
            }

            // Assign colors based on category and budget status
            val color = when {
                budgetGoal != null && amount > budgetGoal.maxBudget -> 
                    Color.parseColor("#F44336") // Red for over budget
                budgetGoal != null && amount < budgetGoal.minBudget -> 
                    Color.parseColor("#FFC107") // Yellow for under minimum
                budgetGoal != null -> 
                    Color.parseColor("#4CAF50") // Green for within budget
                else -> when (category.category.lowercase(Locale.ROOT)) {
                    "food" -> Color.parseColor("#FF9800")
                    "entertainment" -> Color.parseColor("#4CAF50")
                    "groceries" -> Color.parseColor("#9C27B0")
                    "movies" -> Color.parseColor("#2196F3")
                    "transport" -> Color.parseColor("#3D82F7")
                    "savings" -> Color.parseColor("#E91E63")
                    "utilities" -> Color.parseColor("#607D8B")
                    else -> ColorTemplate.MATERIAL_COLORS[categorySpending.indexOf(category) % ColorTemplate.MATERIAL_COLORS.size]
                }
            }
            colors.add(color)
        }

        val dataSet = PieDataSet(entries, "Categories")
        dataSet.colors = colors
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f
        dataSet.valueLinePart1OffsetPercentage = 80f
        dataSet.valueLinePart1Length = 0.2f
        dataSet.valueLinePart2Length = 0.4f
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(pieChart))
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.BLACK)

        pieChart.data = data
        pieChart.description.text = "Category Spending with Budget Goals"
        pieChart.centerText = "Category\nBreakdown"
        
        // Limit lines are not supported for PieChart; skipping addition of axis limit lines
        
        pieChart.invalidate()

        // Update transactions list with filtered data
        val transactions = dbHelper.getTransactionsByPeriod(userId, startDate, endDate)
        transactionAdapter.updateTransactions(transactions)
    }

    private fun getMonthName(month: Int): String {
        return when (month) {
            1 -> "Jan"
            2 -> "Feb"
            3 -> "Mar"
            4 -> "Apr"
            5 -> "May"
            6 -> "Jun"
            7 -> "Jul"
            8 -> "Aug"
            9 -> "Sep"
            10 -> "Oct"
            11 -> "Nov"
            12 -> "Dec"
            else -> ""
        }
    }

    private fun loadBudgetPerformance() {
        val sharedPref = getSharedPreferences("FinTrackPrefs", MODE_PRIVATE)
        val budget = sharedPref.getFloat("userBudget", 0.0f).toDouble()

        if (budget <= 0) {
            progressBudget.progress = 0
            tvBudgetStatus.text = "No budget set"
            return
        }

        val totalSpent = Math.abs(dbHelper.getTotalSpentByUserId(userId))
        val percentSpent = ((totalSpent / budget) * 100).toInt()
        
        progressBudget.max = 100
        progressBudget.progress = percentSpent

        val status = when {
            percentSpent <= 50 -> "Under Budget"
            percentSpent <= 75 -> "Within Budget"
            percentSpent <= 90 -> "Approaching Limit"
            else -> "Over Budget"
        }

        val color = when {
            percentSpent <= 50 -> Color.parseColor("#4CAF50") // Green
            percentSpent <= 75 -> Color.parseColor("#FFC107") // Yellow
            percentSpent <= 90 -> Color.parseColor("#FF9800") // Orange
            else -> Color.parseColor("#F44336") // Red
        }

        progressBudget.progressTintList = ColorStateList.valueOf(color)
        tvBudgetStatus.text = "$status (${percentSpent}%)"
        tvBudgetStatus.setTextColor(color)
    }

    private fun loadGamificationData() {
        val xp = GamificationManager.getXP(this)
        val level = GamificationManager.getLevel(this)
        val badges = GamificationManager.getBadges(this)

        tvXP.text = "XP: $xp"
        tvLevel.text = "Level: $level"
        tvBadges.text = "Badges Earned: ${badges.size}"
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_home -> {
                startActivity(Intent(this, MainActivity::class.java))
                return true
            }
            R.id.navigation_stats -> {
                return true
            }
            R.id.navigation_add -> {
                startActivity(Intent(this, AddSpendingActivity::class.java))
                return true
            }
            R.id.navigation_wallet -> {
                val wallet = dbHelper.getWalletByUserId(userId)
                if (wallet != null) {
                    startActivity(Intent(this, EditWalletActivity::class.java))
                } else {
                    startActivity(Intent(this, AddWalletActivity::class.java))
                }
                return true
            }
            R.id.navigation_notifications -> {
                startActivity(Intent(this, NotificationActivity::class.java))
                return true
            }
        }
        return false
    }
}
