package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val company: String,
    val phone: String,
    val email: String,
    val location: String = "Pakistan",
    val status: String = "Active", // "Lead", "Active", "Pending Payment", "Completed"
    val notes: String = "",
    val assignedTo: String = "Admin",
    val packageId: Long? = null,
    val packageName: String = "Growth AI Suite",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "packages")
data class PackageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val pricePkr: Double,
    val servicesJson: String, // Stored as JSON list of strings
    val billingCycle: String = "Monthly", // "Monthly", "One-time"
    val isFeatured: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clientId: Long,
    val clientName: String,
    val packageId: Long,
    val packageName: String,
    val amountPkr: Double,
    val paymentMethod: String, // "EasyPaisa", "Bank Transfer"
    val proofNote: String = "",
    val status: String = "Pending", // "Pending", "Approved", "Rejected"
    val date: Long = System.currentTimeMillis()
)

@Entity(tableName = "posts")
data class BlogPostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val category: String, // "AI Tools", "AI News", "Business Growth"
    val slug: String,
    val metaDescription: String = "",
    val author: String = "ALEETRIX AI Editorial",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "ai_logs")
data class AiLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val promptType: String, // "Blog", "Social Media", "Client Reply"
    val userQuery: String,
    val generatedOutput: String,
    val timestamp: Long = System.currentTimeMillis()
)

class StringListConverter {
    private val moshi = Moshi.Builder().build()
    private val type = Types.newParameterizedType(List::class.java, String::class.java)
    private val adapter = moshi.adapter<List<String>>(type)

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return adapter.toJson(value ?: emptyList())
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrEmpty()) return emptyList()
        return try {
            adapter.fromJson(value) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
