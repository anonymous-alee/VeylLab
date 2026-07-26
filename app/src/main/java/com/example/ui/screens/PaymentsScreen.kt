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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PaymentEntity
import com.example.ui.AleetrixViewModel
import com.example.ui.components.CopyableTextRow
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.WhatsAppButton
import com.example.ui.theme.*
import com.example.util.WhatsAppHelper

@Composable
fun PaymentsScreen(viewModel: AleetrixViewModel) {
    val payments by viewModel.payments.collectAsState()
    val clients by viewModel.clients.collectAsState()
    val packages by viewModel.packages.collectAsState()
    val easyPaisaNum by viewModel.easyPaisaNumber.collectAsState()
    val bankDetails by viewModel.bankAccountDetails.collectAsState()
    val whatsAppNum by viewModel.whatsAppNumber.collectAsState()

    var showSubmitDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 100.dp)
    ) {
        // 1. Header & Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Payments & Verification",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = "EasyPaisa, Bank Transfer & WhatsApp Reminders",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = { showSubmitDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonYellow, contentColor = ObsidianBlack),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Submit Proof", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 2. Tap-to-Copy Payment Methods Box
        item {
            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = NeonYellow.copy(alpha = 0.4f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountBalance, contentDescription = null, tint = NeonYellow)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Official Agency Payment Methods (Tap to Copy)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                Spacer(modifier = Modifier.height(12.dp))

                CopyableTextRow(label = "EasyPaisa Account Number", value = easyPaisaNum)
                Spacer(modifier = Modifier.height(8.dp))
                CopyableTextRow(label = "Bank Transfer (Meezan Bank)", value = bankDetails)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // 3. Payment Invoices List
        item {
            Text("Payment Invoices & Verifications", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (payments.isEmpty()) {
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Text("No payment records available.", color = TextSecondary)
                }
            }
        } else {
            items(payments, key = { it.id }) { payment ->
                PaymentInvoiceCard(
                    payment = payment,
                    easyPaisaNum = easyPaisaNum,
                    bankDetails = bankDetails,
                    onApprove = { viewModel.updatePaymentStatus(payment.id, "Approved") },
                    onReject = { viewModel.updatePaymentStatus(payment.id, "Rejected") }
                )
            }
        }
    }

    if (showSubmitDialog) {
        SubmitPaymentProofDialog(
            clientList = clients.map { it.name to it.id },
            packageList = packages.map { Triple(it.name, it.id, it.pricePkr) },
            onDismiss = { showSubmitDialog = false },
            onConfirm = { clientId, clientName, packageId, packageName, amountPkr, method, note ->
                viewModel.submitPaymentProof(clientId, clientName, packageId, packageName, amountPkr, method, note)
                showSubmitDialog = false
            }
        )
    }
}

@Composable
private fun PaymentInvoiceCard(
    payment: PaymentEntity,
    easyPaisaNum: String,
    bankDetails: String,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val reminderMessage = remember(payment) {
        WhatsAppHelper.generatePaymentReminderText(
            clientName = payment.clientName,
            packageName = payment.packageName,
            amountPkr = payment.amountPkr,
            easyPaisaNumber = easyPaisaNum,
            bankDetails = bankDetails
        )
    }

    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        borderColor = when (payment.status) {
            "Approved" -> CyberGreen.copy(alpha = 0.4f)
            "Pending" -> CyberOrange.copy(alpha = 0.5f)
            else -> DarkBorder
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = payment.clientName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = "${payment.packageName} • PKR ${String.format("%,.0f", payment.amountPkr)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NeonYellow)
            }
            StatusBadge(status = payment.status)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Payment Method: ${payment.paymentMethod}", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
        Text(text = "Proof Reference: ${payment.proofNote}", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (payment.status == "Pending") {
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
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberRed),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reject", fontSize = 12.sp)
                }
            }

            WhatsAppButton(
                phoneNumber = "+923001234567",
                message = reminderMessage,
                buttonText = "Send Reminder",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SubmitPaymentProofDialog(
    clientList: List<Pair<String, Long>>,
    packageList: List<Triple<String, Long, Double>>,
    onDismiss: () -> Unit,
    onConfirm: (clientId: Long, clientName: String, packageId: Long, packageName: String, amountPkr: Double, method: String, note: String) -> Unit
) {
    var clientName by remember { mutableStateOf(clientList.firstOrNull()?.first ?: "Ayesha Khan") }
    var clientId by remember { mutableStateOf(clientList.firstOrNull()?.second ?: 1L) }
    var packageName by remember { mutableStateOf(packageList.firstOrNull()?.first ?: "Growth Scale Suite") }
    var packageId by remember { mutableStateOf(packageList.firstOrNull()?.second ?: 1L) }
    var amountText by remember { mutableStateOf(packageList.firstOrNull()?.third?.toString() ?: "149000") }
    var method by remember { mutableStateOf("EasyPaisa") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Upload Payment Proof", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = clientName,
                    onValueChange = { clientName = it },
                    label = { Text("Client Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = packageName,
                    onValueChange = { packageName = it },
                    label = { Text("Package Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount Paid (PKR)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("EasyPaisa Txn ID / Bank Wire Ref") },
                    placeholder = { Text("e.g. EasyPaisa #998127361") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = method == "EasyPaisa",
                        onClick = { method = "EasyPaisa" },
                        label = { Text("EasyPaisa") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NeonYellow)
                    )
                    FilterChip(
                        selected = method == "Bank Transfer",
                        onClick = { method = "Bank Transfer" },
                        label = { Text("Bank Transfer") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NeonYellow)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 100000.0
                    onConfirm(clientId, clientName, packageId, packageName, amount, method, note)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonYellow, contentColor = ObsidianBlack)
            ) {
                Text("Submit Proof", fontWeight = FontWeight.Bold)
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
