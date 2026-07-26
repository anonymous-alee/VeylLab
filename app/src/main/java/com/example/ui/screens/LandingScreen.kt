package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.PackageEntity
import com.example.ui.AleetrixViewModel
import com.example.ui.UserRole
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.WhatsAppButton
import com.example.ui.theme.*

@Composable
fun LandingScreen(
    viewModel: AleetrixViewModel,
    onNavigateToDashboard: () -> Unit,
    onNavigateToPackages: () -> Unit,
    onNavigateToBlog: () -> Unit
) {
    val packages by viewModel.packages.collectAsState()
    val whatsAppNumber by viewModel.whatsAppNumber.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // 1. Hero Showcase Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_banner_1785100947792),
                    contentDescription = "ALEETRIX Hero Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    ObsidianBlack.copy(alpha = 0.7f),
                                    ObsidianBlack
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Surface(
                        color = NeonYellow.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonYellow.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "⚡ PREMIER AI DIGITAL AGENCY SAAS",
                            color = NeonYellow,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "ALEETRIX",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp
                        ),
                        color = TextPrimary
                    )

                    Text(
                        text = "Built To Run Without You",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = NeonYellow
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Scale your digital agency with autonomous Gemini AI agents, Supabase database workflows, and instant WhatsApp customer acquisition.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 2
                    )
                }
            }
        }

        // 2. Quick Action CTA Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onNavigateToDashboard,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonYellow, contentColor = ObsidianBlack),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Dashboard, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Access Portal", fontWeight = FontWeight.Bold)
                }

                WhatsAppButton(
                    phoneNumber = whatsAppNumber,
                    message = "Hi ALEETRIX team! 👋 I'm interested in starting my agency project with you.",
                    buttonText = "WhatsApp Us",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 3. Agency Capabilities Grid
        item {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Core Autonomous Modules",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Text(
                    text = "Everything needed to convert leads and automate services",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CapabilityCard(
                        title = "Client CRM",
                        description = "Full lifecycle client directory & WhatsApp outreach",
                        icon = Icons.Default.People,
                        modifier = Modifier.weight(1f)
                    )
                    CapabilityCard(
                        title = "Gemini AI",
                        description = "Auto-write blogs, social captions & client replies",
                        icon = Icons.Default.AutoAwesome,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CapabilityCard(
                        title = "EasyPaisa Pay",
                        description = "Manual payment proofs & admin verification",
                        icon = Icons.Default.AccountBalanceWallet,
                        modifier = Modifier.weight(1f)
                    )
                    CapabilityCard(
                        title = "AEO Blogs",
                        description = "Google & Voice Search ranking content engine",
                        icon = Icons.Default.Article,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 4. Service Packages Section
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Featured Service Packages",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "Transparent pricing in Pakistani Rupees (PKR)",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    TextButton(onClick = onNavigateToPackages) {
                        Text("View All", color = NeonYellow, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        items(packages) { pkg ->
            LandingPackageCard(
                pkg = pkg,
                whatsAppNumber = whatsAppNumber,
                onSubscribe = onNavigateToPackages
            )
        }

        // 5. Why Choose ALEETRIX
        item {
            GlassmorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                borderColor = NeonYellow.copy(alpha = 0.3f)
            ) {
                Text(
                    text = "Why Modern SaaS Leaders Choose ALEETRIX",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = NeonYellow
                )
                Spacer(modifier = Modifier.height(8.dp))
                FeatureListItem("100% Autonomous Gemini AI content & response engine")
                FeatureListItem("Built-in Supabase PostgreSQL, Auth & Storage readiness")
                FeatureListItem("Seamless EasyPaisa & Meezan Bank payment processing")
                FeatureListItem("Direct 1-tap WhatsApp payment reminders & client notifications")
                FeatureListItem("Ready for Web (Next.js) & Native Android deployment")

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onNavigateToBlog,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant, contentColor = TextPrimary),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Read AI Agency Insights & Blog")
                }
            }
        }
    }
}

@Composable
private fun CapabilityCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    GlassmorphicCard(modifier = modifier) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(NeonYellow.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = NeonYellow, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = title, style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = description, style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
    }
}

@Composable
private fun LandingPackageCard(
    pkg: PackageEntity,
    whatsAppNumber: String,
    onSubscribe: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isFeatured = pkg.isFeatured

    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        borderColor = if (isFeatured) NeonYellow else DarkBorder
    ) {
        if (isFeatured) {
            Surface(
                color = NeonYellow,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "MOST POPULAR FOR AGENCY SCALE",
                    color = ObsidianBlack,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = pkg.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "PKR ${String.format("%,.0f", pkg.pricePkr)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = NeonYellow
            )
        }

        Text(
            text = "Billing: ${pkg.billingCycle}",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onSubscribe,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFeatured) NeonYellow else DarkSurfaceVariant,
                    contentColor = if (isFeatured) ObsidianBlack else TextPrimary
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Select Package", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            WhatsAppButton(
                phoneNumber = whatsAppNumber,
                message = "Hi ALEETRIX! 👋 I'm interested in subscribing to the *${pkg.name}* (PKR ${String.format("%,.0f", pkg.pricePkr)}). Please guide me!",
                buttonText = "Inquire",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun FeatureListItem(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
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
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary
        )
    }
}
