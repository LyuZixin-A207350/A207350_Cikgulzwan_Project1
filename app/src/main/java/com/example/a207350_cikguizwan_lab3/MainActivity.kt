package com.example.a207350_cikguizwan_lab3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val MyColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2),
    secondary = Color(0xFF7B1FA2),
    background = Color.White,
    surface = Color(0xFFF3E5F5)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = MyColorScheme) {
                HealthDashboardUI()
            }
        }
    }
}

@Composable
fun HealthDashboardUI() {

    var nameInput by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("Xander") }

    var stepsInput by remember { mutableStateOf("") }
    var steps by remember { mutableStateOf(8540) }

    val progress = (steps / 10000f).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // ⭐背景
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        // ===== Top =====
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
                    modifier = Modifier.size(50.dp).clip(CircleShape)
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

        // ===== Input Card =====
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface // ⭐统一
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
                        if (nameInput.isNotBlank()) displayName = nameInput
                        stepsInput.toIntOrNull()?.let { steps = it }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Update")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ===== Daily Activity =====
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface // ⭐改
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
            color = MaterialTheme.colorScheme.primary // ⭐改
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            MetricCard("Heart Rate", "72 bpm", "Normal", Modifier.weight(1f), Color(0xFFFFEBEE), Color(0xFFD32F2F))
            Spacer(modifier = Modifier.width(16.dp))
            MetricCard("Sleep", "7h 20m", "Good", Modifier.weight(1f), Color(0xFFF3E5F5), Color(0xFF7B1FA2))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            MetricCard("Blood Oxygen", "98%", "Normal", Modifier.weight(1f), Color(0xFFE3F2FD), Color(0xFF1976D2))
            Spacer(modifier = Modifier.width(16.dp))
            MetricCard("Stress", "Low", "Stable", Modifier.weight(1f), Color(0xFFE8F5E9), Color(0xFF388E3C))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            MetricCard("Mood", "Happy", "Positive", Modifier.weight(1f), Color(0xFFFFF3E0), Color(0xFFF57C00))
            Spacer(modifier = Modifier.width(16.dp))
            MetricCard("Body Status", "Good", "Stable", Modifier.weight(1f), Color(0xFFFCE4EC), Color(0xFFC2185B))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant // ⭐改
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
                    "Lab 3 - Material Design App",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun StatItem(value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            value,
            fontSize = 18.sp,            // ⭐ 数字大一点
            fontWeight = FontWeight.Bold
        )

        Text(
            unit,
            fontSize = 12.sp,            // ⭐ 单位小一点
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
    textColor: Color
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
                    onClick = { },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View Details")
                }
            }
        }
    }
}