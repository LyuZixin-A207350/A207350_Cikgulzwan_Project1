package com.example.a207350_cikguizwan_lab3

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

private val MyColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2),
    secondary = Color(0xFF7B1FA2),
    background = Color.White,
    surface = Color(0xFFF3E5F5),
    surfaceVariant = Color(0xFFEDE7F6)
)

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var healthViewModel: HealthViewModel? = null
    private var sensorRegistered = false

    private val requestActivityPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                registerStepSensor()
            } else {
                Toast.makeText(
                    this,
                    "Activity permission denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        enableEdgeToEdge()

        setContent {
            MaterialTheme(colorScheme = MyColorScheme) {
                healthViewModel = viewModel(
                    factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(application)
                )
                NavigationApp(healthViewModel!!)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (checkSelfPermission(android.Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
                registerStepSensor()
            } else {
                requestActivityPermission.launch(android.Manifest.permission.ACTIVITY_RECOGNITION)
            }
        } else {
            registerStepSensor()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
            checkSelfPermission(android.Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED
        ) {
            registerStepSensor()
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterStepSensor()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val totalSteps = event.values[0].toInt()
            healthViewModel?.updateStepsFromSensor(totalSteps)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun registerStepSensor() {
        if (sensorRegistered) return

        if (stepSensor == null) {
            Toast.makeText(this, "No Step Counter Sensor Found!", Toast.LENGTH_SHORT).show()
            return
        }

        sensorManager.registerListener(
            this,
            stepSensor,
            SensorManager.SENSOR_DELAY_UI
        )
        sensorRegistered = true
    }

    private fun unregisterStepSensor() {
        if (sensorRegistered) {
            sensorManager.unregisterListener(this)
            sensorRegistered = false
        }
    }
}

@Composable
fun NavigationApp(viewModel: HealthViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController, viewModel) }
        composable("profile") { ProfileScreen(navController, viewModel) }
        composable("details") { DetailScreen(navController, viewModel) }
        composable("add") { AddScreen(navController, viewModel) }
        composable("list") { ListScreen(navController, viewModel) }
        composable("community") { CommunityScreen(navController, viewModel) }
        composable("api_tip") { ApiTipScreen(navController, viewModel) }
    }
}

@Composable
fun HomeScreen(navController: NavHostController, viewModel: HealthViewModel) {
    val data by viewModel.healthData.collectAsState()

    HealthDashboardUI(
        displayName = data.name,
        steps = data.steps,
        onUpdateClick = { nameInput, stepsInput ->
            val newName = if (nameInput.isNotBlank()) nameInput else data.name
            val newSteps = stepsInput.toIntOrNull() ?: data.steps
            viewModel.updateData(newName, newSteps)
        },
        onOpenProfileClick = { navController.navigate("profile") },
        onDetailClick = { navController.navigate("details") },
        onAddClick = { navController.navigate("add") },
        onListClick = { navController.navigate("list") },
        onCommunityClick = { navController.navigate("community") },
        onApiTipClick = { navController.navigate("api_tip") }
    )
}

@Composable
fun HealthDashboardUI(
    displayName: String,
    steps: Int,
    onUpdateClick: (String, String) -> Unit,
    onOpenProfileClick: () -> Unit,
    onDetailClick: () -> Unit,
    onAddClick: () -> Unit,
    onListClick: () -> Unit,
    onCommunityClick: () -> Unit,
    onApiTipClick: () -> Unit
) {
    var nameInput by remember { mutableStateOf(displayName) }
    var stepsInput by remember { mutableStateOf(steps.toString()) }

    LaunchedEffect(displayName, steps) {
        nameInput = displayName
        stepsInput = steps.toString()
    }

    val progress = (steps / 10000f).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .padding(top = 40.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = android.R.drawable.ic_menu_camera),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.LightGray)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Good Morning,", color = Color.White)
                    Text(displayName, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
            androidx.compose.material3.Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("App Menu (7 Screens)", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(onClick = onOpenProfileClick, modifier = Modifier.fillMaxWidth()) { Text("1. Edit Profile") }
                OutlinedButton(onClick = onDetailClick, modifier = Modifier.fillMaxWidth()) { Text("2. Health Details") }
                OutlinedButton(onClick = onAddClick, modifier = Modifier.fillMaxWidth()) { Text("3. Add Activity (Room)") }
                OutlinedButton(onClick = onListClick, modifier = Modifier.fillMaxWidth()) { Text("4. View Activities") }
                Button(onClick = onCommunityClick, modifier = Modifier.fillMaxWidth()) { Text("5. Community Cloud (Firebase)") }
                Button(onClick = onApiTipClick, modifier = Modifier.fillMaxWidth()) { Text("6. Daily Health Tip (API)") }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    "Live Hardware Sensor Data",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(20.dp))
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(150.dp),
                        strokeWidth = 12.dp
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$steps", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                        Text("Steps", color = Color.Gray)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    StatItem("🔥 ${(steps * 0.05).toInt()}", "kcal")
                    StatItem("🕒 ${(steps / 200).toInt()}", "min")
                    StatItem("📍 ${(steps / 1500.0).toString().take(4)}", "km")
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(navController: NavHostController, viewModel: HealthViewModel) {
    val data by viewModel.healthData.collectAsState()
    var nameInput by remember { mutableStateOf(data.name) }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp).padding(top = 40.dp)) {
        Text("Profile Screen", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {
                viewModel.updateData(nameInput, data.steps)
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Save") }
        OutlinedButton(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) { Text("Back") }
    }
}

@Composable
fun DetailScreen(navController: NavHostController, viewModel: HealthViewModel) {
    val data by viewModel.healthData.collectAsState()
    Column(modifier = Modifier.fillMaxSize().padding(20.dp).padding(top = 40.dp)) {
        Text("Details Screen", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))
        Text("Name: ${data.name}\nSteps: ${data.steps}", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) { Text("Back") }
    }
}

@Composable
fun AddScreen(navController: NavHostController, viewModel: HealthViewModel) {
    var title by remember { mutableStateOf("") }
    var steps by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp).padding(top = 40.dp)) {
        Text("Add Activity (Local Room DB)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Activity Title") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = steps,
            onValueChange = { steps = it },
            label = { Text("Steps") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {
                viewModel.addActivity(title, steps.toIntOrNull() ?: 0)
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Save to Room DB") }
        OutlinedButton(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) { Text("Back") }
    }
}

@Composable
fun ListScreen(navController: NavHostController, viewModel: HealthViewModel) {
    val list by viewModel.activityList.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .padding(top = 40.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Activities (Room DB)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        list.forEach { item ->
            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Title: ${item.title}", fontWeight = FontWeight.Bold)
                    Text("Steps: ${item.steps}")
                }
            }
        }
        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) { Text("Back") }
    }
}

@Composable
fun CommunityScreen(navController: NavHostController, viewModel: HealthViewModel) {
    val communityData by viewModel.communityData.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(20.dp).padding(top = 40.dp)) {
        Text(
            "Community Cloud (Firebase)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { viewModel.syncToCommunity() }, modifier = Modifier.fillMaxWidth()) {
            Text("Sync My Steps to Firebase")
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text("Community Leaderboard:", fontWeight = FontWeight.Bold)

        communityData.forEach { dataString ->
            Text(dataString, modifier = Modifier.padding(vertical = 4.dp))
        }

        Spacer(modifier = Modifier.weight(1f))
        OutlinedButton(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) { Text("Back") }
    }
}

@Composable
fun ApiTipScreen(navController: NavHostController, viewModel: HealthViewModel) {
    val tip by viewModel.apiTip.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(20.dp).padding(top = 40.dp)) {
        Text(
            "Daily Health Tip (Web API)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    tip,
                    fontSize = 18.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        OutlinedButton(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) { Text("Back") }
    }
}

@Composable
fun StatItem(value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(unit, fontSize = 12.sp, color = Color.Gray)
    }
}