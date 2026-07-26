package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.ui.AiGenerationState
import com.example.ui.AleetrixViewModel
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.*

@Composable
fun AiAutomationScreen(viewModel: AleetrixViewModel) {
    val aiState by viewModel.aiState.collectAsState()
    val recentLogs by viewModel.recentAiLogs.collectAsState()

    var selectedMode by remember { mutableStateOf("Blog") } // "Blog", "Social Media", "Client Reply"
    var queryInput by remember { mutableStateOf("") }
    var blogCategory by remember { mutableStateOf("AI Tools") }

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 100.dp)
    ) {
        // 1. Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "AI Automation Copilot",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = "Powered by Google Gemini 3.5 Flash",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeonYellow
                    )
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(NeonYellow.copy(alpha = 0.15f))
                        .border(1.dp, NeonYellow, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NeonYellow)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 2. Mode Selector Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Blog", "Social Media", "Client Reply").forEach { mode ->
                    val isSelected = selectedMode == mode
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedMode = mode },
                        label = {
                            Text(
                                text = mode,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) ObsidianBlack else TextPrimary
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonYellow,
                            containerColor = DarkSurfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Prompt Form
        item {
            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = NeonYellow.copy(alpha = 0.3f)
            ) {
                Text(
                    text = when (selectedMode) {
                        "Blog" -> "Generate AEO/SEO Blog Post"
                        "Social Media" -> "Generate Viral Social Captions"
                        else -> "Draft AI Client Success Reply"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (selectedMode == "Blog") {
                    Text("Select Blog Category:", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("AI Tools", "AI News", "Business Growth").forEach { cat ->
                            AssistChip(
                                onClick = { blogCategory = cat },
                                label = { Text(cat, fontSize = 11.sp) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (blogCategory == cat) NeonYellow else DarkSurfaceVariant,
                                    labelColor = if (blogCategory == cat) ObsidianBlack else TextPrimary
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                OutlinedTextField(
                    value = queryInput,
                    onValueChange = { queryInput = it },
                    placeholder = {
                        Text(
                            text = when (selectedMode) {
                                "Blog" -> "Enter blog topic or title (e.g. 'How AI Workflows Save 20 Hours Weekly')..."
                                "Social Media" -> "Enter product/service to promote (e.g. 'Custom Gemini AI Chatbot for E-Commerce')..."
                                else -> "Paste client message or scenario (e.g. 'Client asking about Meezan Bank transfer confirmation time')..."
                            },
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 90.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurface,
                        unfocusedContainerColor = DarkSurface,
                        focusedBorderColor = NeonYellow,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (queryInput.isNotBlank()) {
                            if (selectedMode == "Blog") {
                                viewModel.generateAndPublishBlog(queryInput, blogCategory)
                            } else {
                                viewModel.generateAiContent(selectedMode, queryInput)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonYellow, contentColor = ObsidianBlack),
                    shape = RoundedCornerShape(12.dp),
                    enabled = aiState !is AiGenerationState.Loading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (aiState is AiGenerationState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = ObsidianBlack, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gemini AI Processing...", fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (selectedMode == "Blog") "Generate & Publish Article" else "Generate Output", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // 4. Output Display Container
        item {
            AnimatedVisibility(visible = aiState !is AiGenerationState.Idle) {
                GlassmorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    borderColor = when (aiState) {
                        is AiGenerationState.Success -> CyberGreen
                        is AiGenerationState.Error -> CyberRed
                        else -> NeonYellow
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Generated AI Result",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NeonYellow
                        )

                        if (aiState is AiGenerationState.Success) {
                            IconButton(onClick = {
                                val text = (aiState as AiGenerationState.Success).output
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("AI Result", text))
                                Toast.makeText(context, "Copied result to clipboard!", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = NeonYellow)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    when (val state = aiState) {
                        is AiGenerationState.Loading -> {
                            Text("Querying Gemini 3.5 Flash Model... Please wait...", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                        }
                        is AiGenerationState.Success -> {
                            Text(text = state.output, color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
                        }
                        is AiGenerationState.Error -> {
                            Text(text = "Error: ${state.message}", color = CyberRed, style = MaterialTheme.typography.bodyMedium)
                        }
                        else -> {}
                    }
                }
            }
        }

        // 5. Recent Generation Logs
        item {
            Text("Recent AI Logs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(recentLogs) { log ->
            GlassmorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = log.promptType, fontWeight = FontWeight.Bold, color = NeonYellow, fontSize = 12.sp)
                    Text(text = log.userQuery, color = TextSecondary, fontSize = 11.sp, maxLines = 1)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = log.generatedOutput, color = TextPrimary, fontSize = 12.sp, maxLines = 3)
            }
        }
    }
}
