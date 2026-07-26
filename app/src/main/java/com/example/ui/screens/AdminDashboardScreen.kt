package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ClientEntity
import com.example.data.local.PaymentEntity
import com.example.ui.AleetrixViewModel
import com.example.ui.UserRole
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.MetricCard
import com.example.ui.components.SectionTitleHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun AdminDashboardScreen(
    viewModel: AleetrixViewModel,
    onNavigateToClients: () -> Unit,
    onNavigateToPayments: () -> Unit,
    onNavigateToAi: () -> Unit,
    onNavigateToPackages: () -> Unit
) {
    val totalRevenue by viewModel.totalRevenue.collectAsState()
    val totalClients by viewModel.totalClients.collectAsState()
    val activeClients by viewModel.activeClients.collectAsState()
    val clients by viewModel.clients.collectAsState()
    val payments by viewModel.payments.collectAsState()
    val userRole by viewModel.userRole.collectAsState()

    val pendingPayments = payments.filter { it.status == "Pending" }
    val conversionRate = if (totalClients > 0) (activeClients.toDouble() / totalClients.toDouble()) * 100 else 75.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 100.dp)
    ) {
        // 1. Header & Role Switcher
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ALEETRIX Dashboard",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = "Real-time SaaS metrics & AI agency control center",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                // Role Selector Switch
                Surface(
                    color = DarkSurfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Row(modifier = Modifier.padding(4.dp)) {
                        UserRole.values().forEach { role ->
                            val isSelected = userRole == role
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) NeonYellow else Color.Transparent)
                                    .clickable { viewModel.setUserRole(role) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = role.name.take(5),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) ObsidianBlack else TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 2. Metrics Cards Grid
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "TOTAL REVENUE",
                        value = "PKR ${String.format("%,.0f", totalRevenue)}",
                        subtitle = "+24.5% vs last month",
                        icon = Icons.Default.Payments,
                        iconColor = NeonYellow,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "ACTIVE CLIENTS",
                        value = "$activeClients / $totalClients",
                        subtitle = "${String.format("%.0f", conversionRate)}% conversion rate",
                        icon = Icons.Default.People,
                        iconColor = CyberCyan,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "PENDING PAYMENTS",
                        value = "${pendingPayments.size} Invoices",
                        subtitle = "EasyPaisa / Bank proof",
                        icon = Icons.Default.AccountBalanceWallet,
                        iconColor = CyberOrange,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "WHATSAPP CLICKS",
                        value = "142 Leads",
                        subtitle = "Instant conversions",
                        icon = Icons.Default.Chat,
                        iconColor = CyberGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // 3. Quick Action Hub
        item {
            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = NeonYellow.copy(alpha = 0.3f)
            ) {
                Text(
                    text = "Quick Agency Operations",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = NeonYellow
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionButton(
                        text = "CRM Clients",
                        icon = Icons.Default.PersonAdd,
                        onClick = onNavigateToClients,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionButton(
                        text = "Packages",
                        icon = Icons.Default.CardGiftcard,
                        onClick = onNavigateToPackages,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionButton(
                        text = "AI Copilot",
                        icon = Icons.Default.AutoAwesome,
                        onClick = onNavigateToAi,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionButton(
                        text = "Payments",
                        icon = Icons.Default.ReceiptLong,
                        onClick = onNavigateToPayments,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // 4. Pending Payment Approvals Alert Section
        if (pendingPayments.isNotEmpty()) {
            item {
                SectionTitleHeader(
                    title = "Pending Payment Verification",
                    subtitle = "Action required: Review EasyPaisa/Bank proof notes",
                    action = {
                        TextButton(onClick = onNavigateToPayments) {
                            Text("Review All (${pendingPayments.size})", color = NeonYellow, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }

            items(pendingPayments.take(3)) { payment ->
                PendingPaymentApprovalCard(
                    payment = payment,
                    onApprove = { viewModel.updatePaymentStatus(payment.id, "Approved") },
                    onReject = { viewModel.updatePaymentStatus(payment.id, "Rejected") }
                )
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }

        // 5. Recent Active Clients Overview
        item {
            SectionTitleHeader(
                title = "Client Directory Overview",
                subtitle = "Latest active accounts & leads",
                action = {
                    TextButton(onClick = onNavigateToClients) {
                        Text("View Full CRM", color = NeonYellow, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        items(clients.take(4)) { client ->
            DashboardClientRow(client = client)
        }
    }
}

@Composable
private fun QuickActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant, contentColor = TextPrimary),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 10.dp),
        modifier = modifier
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = null, tint = NeonYellow, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
        }
    }
}

@Composable
private fun PendingPaymentApprovalCard(
    payment: PaymentEntity,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        borderColor = CyberOrange.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = payment.clientName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = "${payment.packageName} • PKR ${String.format("%,.0f", payment.amountPkr)}", style = MaterialTheme.typography.bodySmall, color = NeonYellow)
                Text(text = "Method: ${payment.paymentMethod} • Note: ${payment.proofNote}", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
            }
            StatusBadge(status = payment.status)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onApprove,
                colors = ButtonDefaults.buttonColors(containerColor = CyberGreen, contentColor = ObsidianBlack),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Approve", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = onReject,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberRed),
                modifier = Modifier.weight(1f)
            ) {
                Text("Reject", fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun DashboardClientRow(client: ClientEntity) {
    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceVariant)
                        .border(1.dp, NeonYellow.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = client.name.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = NeonYellow
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = client.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "${client.company} • ${client.packageName}", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
                }
            }
            StatusBadge(status = client.status)
        }
    }
}
