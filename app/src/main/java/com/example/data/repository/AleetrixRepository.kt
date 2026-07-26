package com.example.data.repository

import com.example.data.local.*
import com.example.data.remote.GeminiNetworkClient
import kotlinx.coroutines.flow.Flow
import java.text.Normalizer
import java.util.Locale

class AleetrixRepository(
    private val clientDao: ClientDao,
    private val packageDao: PackageDao,
    private val paymentDao: PaymentDao,
    private val blogPostDao: BlogPostDao,
    private val aiLogDao: AiLogDao
) {
    // Client Flow Operations
    val allClients: Flow<List<ClientEntity>> = clientDao.getAllClients()
    val totalClientsCount: Flow<Int> = clientDao.getClientCount()
    val activeClientsCount: Flow<Int> = clientDao.getActiveClientCount()

    suspend fun addClient(client: ClientEntity): Long = clientDao.insertClient(client)
    suspend fun updateClient(client: ClientEntity) = clientDao.updateClient(client)
    suspend fun deleteClient(id: Long) = clientDao.deleteClientById(id)

    // Package Flow Operations
    val allPackages: Flow<List<PackageEntity>> = packageDao.getAllPackages()

    suspend fun addPackage(pkg: PackageEntity): Long = packageDao.insertPackage(pkg)
    suspend fun updatePackage(pkg: PackageEntity) = packageDao.updatePackage(pkg)
    suspend fun deletePackage(id: Long) = packageDao.deletePackageById(id)

    // Payment Flow Operations
    val allPayments: Flow<List<PaymentEntity>> = paymentDao.getAllPayments()
    val totalRevenue: Flow<Double?> = paymentDao.getTotalRevenue()

    suspend fun addPayment(payment: PaymentEntity): Long = paymentDao.insertPayment(payment)
    suspend fun updatePaymentStatus(id: Long, status: String) = paymentDao.updatePaymentStatus(id, status)

    // Blog Post Flow Operations
    val allPosts: Flow<List<BlogPostEntity>> = blogPostDao.getAllPosts()

    suspend fun addPost(post: BlogPostEntity): Long = blogPostDao.insertPost(post)
    suspend fun deletePost(id: Long) = blogPostDao.deletePostById(id)

    // AI Log Flow Operations
    val recentAiLogs: Flow<List<AiLogEntity>> = aiLogDao.getRecentAiLogs()

    // --- AI Automation Integrations ---

    suspend fun generateAiContent(
        promptType: String,
        userQuery: String
    ): Result<String> {
        val systemInstruction = when (promptType) {
            "Blog" -> """
                You are a senior AI Content Strategist and SEO/AEO Expert for ALEETRIX Digital Agency.
                Generate a comprehensive, engaging, SEO and Answer Engine Optimized (AEO) blog post in markdown format based on the topic.
                Include:
                - Catchy Title
                - Executive Summary / Quick Answer (for voice/AEO search)
                - Detailed Section Headings with bullet points
                - Strategic Business Insights
            """.trimIndent()
            "Social Media" -> """
                You are a high-converting Social Media Director for ALEETRIX Digital Agency.
                Write 3 compelling, viral social media captions (LinkedIn, Instagram, Twitter/X) complete with emojis, strong hook, call-to-action (CTA), and relevant trending hashtags for Pakistani & global tech entrepreneurs.
            """.trimIndent()
            "Client Reply" -> """
                You are an executive Client Success Manager at ALEETRIX Digital Agency.
                Draft a professional, persuasive, empathetic, and solution-oriented client reply email/WhatsApp response to address the user's scenario. Maintain an authoritative yet friendly agency tone.
            """.trimIndent()
            else -> "You are ALEETRIX AI Copilot, an expert agency assistant."
        }

        val result = GeminiNetworkClient.generateText(userQuery, systemInstruction)
        if (result.isSuccess) {
            val generatedText = result.getOrThrow()
            aiLogDao.insertAiLog(
                AiLogEntity(
                    promptType = promptType,
                    userQuery = userQuery,
                    generatedOutput = generatedText
                )
            )
        }
        return result
    }

    suspend fun generateAndPublishBlog(title: String, category: String): Result<BlogPostEntity> {
        val prompt = "Write a complete AEO/SEO blog post titled: '$title' in the category '$category'. Focus on high value for Pakistani tech entrepreneurs and SaaS builders."
        val result = generateAiContent("Blog", prompt)
        return if (result.isSuccess) {
            val content = result.getOrThrow()
            val slug = slugify(title)
            val newPost = BlogPostEntity(
                title = title,
                content = content,
                category = category,
                slug = slug,
                metaDescription = content.take(160).replace("\n", " ") + "...",
                author = "ALEETRIX Gemini AI"
            )
            val id = addPost(newPost)
            Result.success(newPost.copy(id = id))
        } else {
            Result.failure(result.exceptionOrNull() ?: Exception("Failed to generate blog"))
        }
    }

    private fun slugify(input: String): String {
        val nonLatin = Regex("[^\\w-]")
        val whitespace = Regex("[\\s]")
        val normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
        return whitespace.replace(normalized, "-")
            .let { nonLatin.replace(it, "") }
            .lowercase(Locale.ENGLISH)
    }
}
