package com.example.a207350_cikguizwan_lab3

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class HealthData(
    val name: String = "Xander",
    val steps: Int = 0
)

data class QuoteResponse(
    val quote: String,
    val author: String
)

interface HealthApi {
    @GET("quotes/random")
    suspend fun getDailyTip(): QuoteResponse
}

class HealthViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val db = HealthDatabase.getDatabase(application)
    private val dao = db.activityDao()

    private val firestore = FirebaseFirestore.getInstance()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://dummyjson.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(HealthApi::class.java)

    private val _healthData = MutableStateFlow(HealthData())
    val healthData: StateFlow<HealthData> = _healthData

    private val _activityList = MutableStateFlow<List<ActivityEntity>>(emptyList())
    val activityList: StateFlow<List<ActivityEntity>> = _activityList

    private val _apiTip = MutableStateFlow("Loading tip...")
    val apiTip: StateFlow<String> = _apiTip

    private val _communityData = MutableStateFlow<List<String>>(listOf("Loading community..."))
    val communityData: StateFlow<List<String>> = _communityData

    init {
        loadActivitiesFromRoom()
        fetchDailyTip()
        loadCommunityLeaderboard()
    }

    fun updateStepsFromSensor(newSteps: Int) {
        _healthData.value = _healthData.value.copy(steps = newSteps)
    }

    fun updateData(name: String, steps: Int) {
        _healthData.value = HealthData(name = name, steps = steps)
    }

    private fun loadActivitiesFromRoom() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _activityList.value = dao.getAllActivities()
            } catch (e: Exception) {
                _activityList.value = emptyList()
            }
        }
    }

    fun addActivity(title: String, steps: Int) {
        if (title.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                dao.insertActivity(
                    ActivityEntity(
                        title = title,
                        steps = steps
                    )
                )
                _activityList.value = dao.getAllActivities()
            } catch (e: Exception) {
                // 你也可以加 Log
            }
        }
    }

    private fun fetchDailyTip() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = api.getDailyTip()
                _apiTip.value = "\"${response.quote}\" - ${response.author}"
            } catch (e: Exception) {
                _apiTip.value = "Drink more water and walk 10,000 steps daily."
            }
        }
    }

    private fun loadCommunityLeaderboard() {
        firestore.collection("leaderboard")
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapIndexed { index, doc ->
                    val name = doc.getString("name") ?: "Unknown"
                    val steps = doc.getLong("steps") ?: 0L
                    "${index + 1}. $name: $steps steps"
                }

                _communityData.value = if (list.isEmpty()) {
                    listOf("No community data yet.")
                } else {
                    list
                }
            }
            .addOnFailureListener {
                _communityData.value = listOf("Failed to load community leaderboard.")
            }
    }

    fun syncToCommunity() {
        val userName = _healthData.value.name
        val userSteps = _healthData.value.steps

        val data = hashMapOf(
            "name" to userName,
            "steps" to userSteps
        )

        firestore.collection("leaderboard")
            .document(userName)
            .set(data)
            .addOnSuccessListener {
                loadCommunityLeaderboard()
            }
            .addOnFailureListener {
                _communityData.value = listOf("Sync failed.")
            }
    }
}