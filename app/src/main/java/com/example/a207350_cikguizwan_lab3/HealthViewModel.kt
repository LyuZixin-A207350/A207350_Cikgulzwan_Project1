package com.example.a207350_cikguizwan_lab3

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class HealthData(
    val name: String = "Xander",
    val steps: Int = 8540
)

data class Activity(
    val title: String,
    val steps: Int
)

class HealthViewModel : ViewModel() {

    private val _healthData = MutableStateFlow(HealthData())
    val healthData: StateFlow<HealthData> = _healthData

    private val _activityList = MutableStateFlow<List<Activity>>(emptyList())
    val activityList: StateFlow<List<Activity>> = _activityList

    fun updateData(name: String, steps: Int) {
        _healthData.value = HealthData(name, steps)
    }

    fun addActivity(title: String, steps: Int) {
        val newItem = Activity(title, steps)
        _activityList.value = _activityList.value + newItem
    }
}