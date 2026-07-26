package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PackageEntity
import com.example.data.local.StringListConverter
import com.example.ui.AleetrixViewModel
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.WhatsAppButton
import com.example.ui.theme.*

@Composable
fun PackagesScreen(viewModel: AleetrixViewModel) {
    val packages by viewModel.packages.collectAsState()
    val whatsAppNumber by viewModel.whatsAppNumber.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }

    val converter = remember { StringListConverter() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 100.dp)
    ) {
        // 1. Header & Add Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Packages & Services",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = "Custom agency tiers, services & PKR pricing",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    containerColor = NeonYellow,
                    contentColor = ObsidianBlack,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Package")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 2. Package Cards List
        items(packages) { pkg ->
            val servicesList = converter.toStringList(pkg.servicesJson)

            GlassmorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                borderColor = if (pkg.isFeatured) NeonYellow else DarkBorder
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pkg.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    IconButton(onClick = { viewModel.deletePackage(pkg.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CyberRed.copy(alpha = 0.8f))
                    }
                }

                Text(
                    text = "PKR ${String.format("%,.0f", pkg.pricePkr)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeonYellow
                )

                Text(
                    text = "Billing Cycle: ${pkg.billingCycle}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Included Services:",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                servicesList.forEach { service ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = NeonYellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = service,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WhatsAppButton(
                        phoneNumber = whatsAppNumber,
                        message = "Hi ALEETRIX! 👋 I'm interested in subscribing to the *$pkg.name* (PKR ${String.format("%,.0f", pkg.pricePkr)}). Please guide me on next steps!",
                        buttonText = "Inquire on WhatsApp",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreatePackageDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name, pricePkr, services, cycle ->
                viewModel.addPackage(name, pricePkr, services, cycle)
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun CreatePackageDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, pricePkr: Double, services: List<String>, cycle: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var servicesText by remember { mutableStateOf("") }
    var cycle by remember { mutableStateOf("Monthly") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Package", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Package Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Price in PKR (e.g. 250000)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = servicesText,
                    onValueChange = { servicesText = it },
                    label = { Text("Services (separated by commas)") },
                    placeholder = { Text("AI Chatbot, SEO Audit, Custom CRM") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = cycle == "Monthly",
                        onClick = { cycle = "Monthly" },
                        label = { Text("Monthly") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NeonYellow)
                    )
                    FilterChip(
                        selected = cycle == "One-time",
                        onClick = { cycle = "One-time" },
                        label = { Text("One-time") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NeonYellow)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceText.toDoubleOrNull() ?: 0.0
                    val serviceList = servicesText.split(",").map { it.trim() }.filter { it.isNotBlank() }
                    if (name.isNotBlank() && price > 0) {
                        onConfirm(name, price, serviceList, cycle)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonYellow, contentColor = ObsidianBlack)
            ) {
                Text("Save Package", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = DarkSurface,
        shape = RoundedCornerShape(20.dp)
    )
}
