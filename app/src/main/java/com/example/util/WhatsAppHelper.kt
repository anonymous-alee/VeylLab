package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.net.URLEncoder

object WhatsAppHelper {

    fun sendWhatsAppMessage(
        context: Context,
        phoneNumber: String,
        message: String
    ) {
        val cleanNumber = phoneNumber.replace(Regex("[^0-9+]"), "")
        try {
            val encodedMessage = URLEncoder.encode(message, "UTF-8")
            val url = "https://api.whatsapp.com/send?phone=$cleanNumber&text=$encodedMessage"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Unable to open WhatsApp: ${e.localizedMessage}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun generatePaymentReminderText(
        clientName: String,
        packageName: String,
        amountPkr: Double,
        easyPaisaNumber: String = "0300-1234567",
        bankDetails: String = "Meezan Bank - PK00 MEZN 0001 0203 0405 06"
    ): String {
        return """
            Hi $clientName! 👋
            
            This is a friendly reminder from *ALEETRIX Digital Agency*.
            
            Your payment for *$packageName* (Amount: PKR ${String.format("%,.0f", amountPkr)}) is pending verification.
            
            💳 *Payment Methods:*
            • EasyPaisa: $easyPaisaNumber
            • Bank Transfer: $bankDetails
            
            Please reply with a screenshot/reference of your payment proof so we can activate your AI services immediately!
            
            Thank you for choosing ALEETRIX - Built To Run Without You. ⚡
        """.trimIndent()
    }

    fun generateLeadInquiryText(packageName: String): String {
        return "Hi ALEETRIX team! 👋 I'm interested in starting my project with the *$packageName*. Please guide me on onboarding and next steps!"
    }
}
