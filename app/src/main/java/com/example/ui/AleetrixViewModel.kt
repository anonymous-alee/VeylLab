package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.*
import com.example.data.repository.AleetrixRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class UserRole {
    ADMIN,
    TEAM_MEMBER,
    CLIENT_PORTAL
}

sealed interface AiGenerationState {
    object Idle : AiGenerationState
    object Loading : AiGenerationState
    data class Success(val output: String, val promptType: String) : AiGenerationState
    data class Error(val message: String) : AiGenerationState
}

class AleetrixViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AleetrixDatabase.getDatabase(application, viewModelScope)
    val repository = AleetrixRepository(
        clientDao = db.clientDao(),
        packageDao = db.packageDao(),
        paymentDao = db.paymentDao(),
        blogPostDao = db.blogPostDao(),
        aiLogDao = db.aiLogDao()
    )

    // User Role State
    private val _userRole = MutableStateFlow(UserRole.ADMIN)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    fun setUserRole(role: UserRole) {
        _userRole.value = role
    }

    // Filters
    val clientFilter = MutableStateFlow("All")
    val blogCategoryFilter = MutableStateFlow("All")
    val searchQuery = MutableStateFlow("")

    // Settings
    val whatsAppNumber = MutableStateFlow("+923001234567")
    val easyPaisaNumber = MutableStateFlow("0300-1234567")
    val bankAccountDetails = MutableStateFlow("Meezan Bank - PK00 MEZN 0001 0203 0405 06")

    // Database Reactive Flows
    val packages: StateFlow<List<PackageEntity>> = repository.allPackages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val clients: StateFlow<List<ClientEntity>> = combine(
        repository.allClients,
        clientFilter,
        searchQuery
    ) { list, filter, query ->
        list.filter { client ->
            val matchesFilter = when (filter) {
                "All" -> true
                else -> client.status.equals(filter, ignoreCase = true)
            }
            val matchesQuery = query.isBlank() ||
                    client.name.contains(query, ignoreCase = true) ||
                    client.company.contains(query, ignoreCase = true) ||
                    client.email.contains(query, ignoreCase = true) ||
                    client.phone.contains(query, ignoreCase = true)
            matchesFilter && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val payments: StateFlow<List<PaymentEntity>> = repository.allPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val blogPosts: StateFlow<List<BlogPostEntity>> = combine(
        repository.allPosts,
        blogCategoryFilter
    ) { posts, category ->
        if (category == "All") posts
        else posts.filter { it.category.equals(category, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalRevenue: StateFlow<Double> = repository.totalRevenue
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalClients: StateFlow<Int> = repository.totalClientsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val activeClients: StateFlow<Int> = repository.activeClientsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val recentAiLogs: StateFlow<List<AiLogEntity>> = repository.recentAiLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI Generation State
    private val _aiState = MutableStateFlow<AiGenerationState>(AiGenerationState.Idle)
    val aiState: StateFlow<AiGenerationState> = _aiState.asStateFlow()

    fun generateAiContent(promptType: String, userQuery: String) {
        viewModelScope.launch {
            _aiState.value = AiGenerationState.Loading
            val result = repository.generateAiContent(promptType, userQuery)
            if (result.isSuccess) {
                _aiState.value = AiGenerationState.Success(result.getOrThrow(), promptType)
            } else {
                _aiState.value = AiGenerationState.Error(
                    result.exceptionOrNull()?.localizedMessage ?: "AI generation failed"
                )
            }
        }
    }

    fun generateAndPublishBlog(title: String, category: String) {
        viewModelScope.launch {
            _aiState.value = AiGenerationState.Loading
            val result = repository.generateAndPublishBlog(title, category)
            if (result.isSuccess) {
                _aiState.value = AiGenerationState.Success(
                    "Blog published successfully: ${result.getOrThrow().title}",
                    "Blog"
                )
            } else {
                _aiState.value = AiGenerationState.Error(
                    result.exceptionOrNull()?.localizedMessage ?: "Failed to generate blog"
                )
            }
        }
    }

    fun clearAiState() {
        _aiState.value = AiGenerationState.Idle
    }

    // CRUD Actions
    fun addClient(name: String, company: String, phone: String, email: String, location: String, packageName: String, notes: String) {
        viewModelScope.launch {
            repository.addClient(
                ClientEntity(
                    name = name,
                    company = company,
                    phone = phone,
                    email = email,
                    location = location.ifBlank { "Pakistan" },
                    packageName = packageName,
                    notes = notes,
                    status = "Lead"
                )
            )
        }
    }

    fun updateClientStatus(client: ClientEntity, newStatus: String) {
        viewModelScope.launch {
            repository.updateClient(client.copy(status = newStatus))
        }
    }

    fun deleteClient(id: Long) {
        viewModelScope.launch {
            repository.deleteClient(id)
        }
    }

    fun addPackage(name: String, pricePkr: Double, services: List<String>, cycle: String) {
        viewModelScope.launch {
            val moshiAdapter = StringListConverter()
            val json = moshiAdapter.fromStringList(services)
            repository.addPackage(
                PackageEntity(
                    name = name,
                    pricePkr = pricePkr,
                    servicesJson = json,
                    billingCycle = cycle
                )
            )
        }
    }

    fun deletePackage(id: Long) {
        viewModelScope.launch {
            repository.deletePackage(id)
        }
    }

    fun submitPaymentProof(
        clientId: Long,
        clientName: String,
        packageId: Long,
        packageName: String,
        amountPkr: Double,
        method: String,
        proofNote: String
    ) {
        viewModelScope.launch {
            repository.addPayment(
                PaymentEntity(
                    clientId = clientId,
                    clientName = clientName,
                    packageId = packageId,
                    packageName = packageName,
                    amountPkr = amountPkr,
                    paymentMethod = method,
                    proofNote = proofNote,
                    status = "Pending"
                )
            )
        }
    }

    fun updatePaymentStatus(paymentId: Long, newStatus: String) {
        viewModelScope.launch {
            repository.updatePaymentStatus(paymentId, newStatus)
        }
    }

    fun addBlogPost(title: String, content: String, category: String) {
        viewModelScope.launch {
            val slug = title.lowercase().replace(" ", "-").replace(Regex("[^a-z0-9-]"), "")
            repository.addPost(
                BlogPostEntity(
                    title = title,
                    content = content,
                    category = category,
                    slug = slug,
                    metaDescription = content.take(150) + "..."
                )
            )
        }
    }

    fun deleteBlogPost(id: Long) {
        viewModelScope.launch {
            repository.deletePost(id)
        }
    }
}
