package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.ui.AleetrixViewModel
import com.example.ui.UserRole
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    viewModel: AleetrixViewModel,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val whatsAppNum by viewModel.whatsAppNumber.collectAsState()
    val easyPaisaNum by viewModel.easyPaisaNumber.collectAsState()
    val bankDetails by viewModel.bankAccountDetails.collectAsState()
    val userRole by viewModel.userRole.collectAsState()

    var editableWhatsApp by remember(whatsAppNum) { mutableStateOf(whatsAppNum) }
    var editableEasyPaisa by remember(easyPaisaNum) { mutableStateOf(easyPaisaNum) }
    var editableBank by remember(bankDetails) { mutableStateOf(bankDetails) }

    val context = LocalContext.current

    val isGeminiKeySet = try {
        BuildConfig.GEMINI_API_KEY.isNotBlank() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY"
    } catch (e: Exception) {
        false
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 100.dp)
    ) {
        // 1. Header
        item {
            Text(
                text = "System Settings",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Text(
                text = "Agency profile, payment credentials & AI configuration",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 2. Active Role Switcher
        item {
            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = NeonYellow.copy(alpha = 0.3f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Active Dashboard Mode", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Switch between Admin, Team & Client Portal views", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NeonYellow.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = NeonYellow)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    UserRole.values().forEach { role ->
                        val isSelected = userRole == role
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setUserRole(role) },
                            label = { Text(role.name.replace("_", " "), fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonYellow,
                                selectedLabelColor = ObsidianBlack
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Theme Toggle & UI Customization
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Interface Theme Mode", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(if (isDarkTheme) "OLED Cyber Obsidian Dark" else "Clean Light Mode", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }

                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { onToggleTheme() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ObsidianBlack,
                            checkedTrackColor = NeonYellow
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 4. Agency WhatsApp & Payment Credentials Editor
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Text("Agency WhatsApp & Payment Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = editableWhatsApp,
                    onValueChange = { editableWhatsApp = it },
                    label = { Text("Agency WhatsApp Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = editableEasyPaisa,
                    onValueChange = { editableEasyPaisa = it },
                    label = { Text("EasyPaisa Account Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = editableBank,
                    onValueChange = { editableBank = it },
                    label = { Text("Bank Transfer Details (Meezan/HBL)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        viewModel.whatsAppNumber.value = editableWhatsApp
                        viewModel.easyPaisaNumber.value = editableEasyPaisa
                        viewModel.bankAccountDetails.value = editableBank
                        Toast.makeText(context, "Agency settings saved successfully!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonYellow, contentColor = ObsidianBlack),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Credentials", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 5. Gemini API Status
        item {
            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = if (isGeminiKeySet) CyberGreen.copy(alpha = 0.5f) else CyberOrange.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Google Gemini 3.5 Flash API", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(
                            text = if (isGeminiKeySet) "Active • Key injected via AI Studio Secrets" else "Placeholder Key Active • Managed via Secrets panel",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isGeminiKeySet) CyberGreen else CyberOrange
                        )
                    }

                    Icon(
                        imageVector = if (isGeminiKeySet) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (isGeminiKeySet) CyberGreen else CyberOrange
                    )
                }
            }
        }
    }
}
