package com.example.a207350_cikguizwan_lab3

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class HealthData(
    val name: String = "Xander",
    val steps: Int = 8540
)

class HealthViewModel : ViewModel() {

    private val _healthData = MutableStateFlow(HealthData())
    val healthData: StateFlow<HealthData> = _healthData

    fun updateData(name: String, steps: Int) {
        _healthData.value = HealthData(name, steps)
    }
}