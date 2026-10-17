package com.tuonome.coach30km

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val healthConnectClient = HealthConnectClient.getOrCreate(this)
        val permissions = setOf(
            HealthPermission.getReadPermission(ExerciseSessionRecord::class),
            HealthPermission.getReadPermission(DistanceRecord::class)
        )

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    var distanceKm by remember { mutableStateOf(0.0) }
                    var hasPermissions by remember { mutableStateOf(false) }
                    val scope = rememberCoroutineScope()

                    val requestPermissionActivityContract = PermissionController.createRequestPermissionResultContract()
                    val requestPermissions = rememberLauncherForActivityResult(requestPermissionActivityContract) { granted ->
                        if (granted.containsAll(permissions)) {
                            hasPermissions = true
                            scope.launch {
                                distanceKm = readDistanceLast7Days(healthConnectClient)
                            }
                        }
                    }
