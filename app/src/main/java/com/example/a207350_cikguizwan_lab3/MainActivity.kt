package com.example.a207350_cikguizwan_lab3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = MyColorScheme) {
                val healthViewModel: HealthViewModel = viewModel()
                NavigationApp(healthViewModel)
            }
        }
    }
}

@Composable
fun NavigationApp(viewModel: HealthViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(navController = navController, viewModel = viewModel)
        }
        composable("profile") {
            ProfileScreen(navController = navController, viewModel = viewModel)
        }
        composable("details") {
            DetailScreen(navController = navController, viewModel = viewModel)
        }
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
        onOpenProfileClick = {
            navController.navigate("profile")
        },
        onDetailClick = {
            navController.navigate("details")
        }
    )
}

@Composable
fun HealthDashboardUI(
    displayName: String,
    steps: Int,
    onUpdateClick: (String, String) -> Unit,
    onOpenProfileClick: () -> Unit,
    onDetailClick: () -> Unit
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
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.avatar),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text("Good Morning,", color = Color.White)
                    Text(
                        displayName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Icon(
                Icons.Default.Notifications,
                contentDescription = null,
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text("Update Profile", fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Enter your name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = stepsInput,
                    onValueChange = { stepsInput = it },
                    label = { Text("Enter steps") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        onUpdateClick(nameInput, stepsInput)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Update")
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onOpenProfileClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Open Profile Screen")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {

                Text(
                    "Daily Activity",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem("🔥 ${(steps * 0.05).toInt()}", "kcal")
                    StatItem("🕒 ${(steps / 200).toInt()}", "min")
                    StatItem("📍 ${(steps / 1500.0).toString().take(4)}", "km")
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            "Health Metrics",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            MetricCard(
                title = "Heart Rate",
                value = "72 bpm",
                status = "Normal",
                modifier = Modifier.weight(1f),
                bgColor = Color(0xFFFFEBEE),
                textColor = Color(0xFFD32F2F),
                onDetailClick = onDetailClick
            )
            Spacer(modifier = Modifier.width(16.dp))
            MetricCard(
                title = "Sleep",
                value = "7h 20m",
                status = "Good",
                modifier = Modifier.weight(1f),
                bgColor = Color(0xFFF3E5F5),
                textColor = Color(0xFF7B1FA2),
                onDetailClick = onDetailClick
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            MetricCard(
                title = "Blood Oxygen",
                value = "98%",
                status = "Normal",
                modifier = Modifier.weight(1f),
                bgColor = Color(0xFFE3F2FD),
                textColor = Color(0xFF1976D2),
                onDetailClick = onDetailClick
            )
            Spacer(modifier = Modifier.width(16.dp))
            MetricCard(
                title = "Stress",
                value = "Low",
                status = "Stable",
                modifier = Modifier.weight(1f),
                bgColor = Color(0xFFE8F5E9),
                textColor = Color(0xFF388E3C),
                onDetailClick = onDetailClick
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            MetricCard(
                title = "Mood",
                value = "Happy",
                status = "Positive",
                modifier = Modifier.weight(1f),
                bgColor = Color(0xFFFFF3E0),
                textColor = Color(0xFFF57C00),
                onDetailClick = onDetailClick
            )
            Spacer(modifier = Modifier.width(16.dp))
            MetricCard(
                title = "Body Status",
                value = "Good",
                status = "Stable",
                modifier = Modifier.weight(1f),
                bgColor = Color(0xFFFCE4EC),
                textColor = Color(0xFFC2185B),
                onDetailClick = onDetailClick
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Student ID: A207350",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Lab 4 - Navigation and ViewModel",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ProfileScreen(navController: NavHostController, viewModel: HealthViewModel) {
    val data by viewModel.healthData.collectAsState()

    var nameInput by remember { mutableStateOf(data.name) }
    var stepsInput by remember { mutableStateOf(data.steps.toString()) }

    LaunchedEffect(data) {
        nameInput = data.name
        stepsInput = data.steps.toString()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            "Profile Screen",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Edit User Information", fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = stepsInput,
                    onValueChange = { stepsInput = it },
                    label = { Text("Steps") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        viewModel.updateData(
                            if (nameInput.isNotBlank()) nameInput else data.name,
                            stepsInput.toIntOrNull() ?: data.steps
                        )
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save")
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Back")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Current Data", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Name: ${data.name}")
                Text("Steps: ${data.steps}")
            }
        }
    }
}

@Composable
fun DetailScreen(navController: NavHostController, viewModel: HealthViewModel) {
    val data by viewModel.healthData.collectAsState()
    val progress = (data.steps / 10000f).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Details Screen",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("User Summary", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Name: ${data.name}")
                Text("Steps: ${data.steps}")
                Text("Goal Progress: ${(progress * 100).toInt()}%")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Health Notes", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                Text("❤️ Health is stable")
                Text("📊 Last updated: Today")
                Text("⚡ Keep exercising regularly")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Home")
        }
    }
}

@Composable
fun StatItem(value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            unit,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    status: String,
    modifier: Modifier,
    bgColor: Color,
    textColor: Color,
    onDetailClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .clickable { expanded = !expanded }
            .animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Text(title, color = Color.Gray)

            Spacer(modifier = Modifier.height(10.dp))

            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColor)
            Text(status, color = textColor)

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))

                Divider()

                Spacer(modifier = Modifier.height(12.dp))

                Text("❤️ Health is stable")
                Text("📊 Last updated: Today")
                Text("⚡ Keep exercising regularly")

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDetailClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View Details")
                }
            }
        }
    }
}