package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AleetrixViewModel
import com.example.ui.UserRole
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.*

@Composable
fun AuthScreen(
    viewModel: AleetrixViewModel,
    onLoginSuccess: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Admin, 1: Team Member, 2: Client Portal
    var emailInput by remember { mutableStateOf("admin@aleetrix.com") }
    var passwordInput by remember { mutableStateOf("••••••••") }
    var securityPin by remember { mutableStateOf("1234") }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            color = NeonYellow.copy(alpha = 0.2f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = "🔐 SUPABASE AUTHENTICATION",
                color = NeonYellow,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "ALEETRIX",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary
        )

        Text(
            text = "Portal Access & Security Gateway",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurfaceVariant,
            contentColor = NeonYellow
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = {
                    selectedTab = 0
                    emailInput = "admin@aleetrix.com"
                },
                text = { Text("Admin", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = {
                    selectedTab = 1
                    emailInput = "team@aleetrix.com"
                },
                text = { Text("Team", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = {
                    selectedTab = 2
                    emailInput = "client@nexusdynamics.pk"
                },
                text = { Text("Client Portal", fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        GlassmorphicCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = NeonYellow.copy(alpha = 0.3f)
        ) {
            OutlinedTextField(
                value = emailInput,
                onValueChange = { emailInput = it },
                label = { Text("Email / Account ID") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = NeonYellow) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = passwordInput,
                onValueChange = { passwordInput = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = NeonYellow) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = securityPin,
                onValueChange = { securityPin = it },
                label = { Text("Security Pin / 2FA") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = NeonYellow) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    when (selectedTab) {
                        0 -> viewModel.setUserRole(UserRole.ADMIN)
                        1 -> viewModel.setUserRole(UserRole.TEAM_MEMBER)
                        2 -> viewModel.setUserRole(UserRole.CLIENT_PORTAL)
                    }
                    Toast.makeText(context, "Authenticated successfully as ${viewModel.userRole.value.name}", Toast.LENGTH_SHORT).show()
                    onLoginSuccess()
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonYellow, contentColor = ObsidianBlack),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Secure Login to Dashboard", fontWeight = FontWeight.Bold)
            }
        }
    }
}
