package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ClientEntity::class,
        PackageEntity::class,
        PaymentEntity::class,
        BlogPostEntity::class,
        AiLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(StringListConverter::class)
abstract class AleetrixDatabase : RoomDatabase() {

    abstract fun clientDao(): ClientDao
    abstract fun packageDao(): PackageDao
    abstract fun paymentDao(): PaymentDao
    abstract fun blogPostDao(): BlogPostDao
    abstract fun aiLogDao(): AiLogDao

    companion object {
        @Volatile
        private var INSTANCE: AleetrixDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AleetrixDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AleetrixDatabase::class.java,
                    "aleetrix_saas_db"
                )
                    .addCallback(AleetrixDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AleetrixDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

        suspend fun populateInitialData(db: AleetrixDatabase) {
            val pkgDao = db.packageDao()
            val clientDao = db.clientDao()
            val paymentDao = db.paymentDao()
            val blogDao = db.blogPostDao()

            // 1. Initial Packages
            val starterPkg = PackageEntity(
                name = "Starter AI Launch",
                pricePkr = 149000.0,
                servicesJson = "[\"Custom AI Chatbot Integration\", \"Responsive Web & Mobile Landing Page\", \"Standard SEO Setup\", \"Monthly Content Calendar (10 Posts)\", \"WhatsApp API Integration\"]",
                billingCycle = "One-time",
                isFeatured = false
            )
            val growthPkg = PackageEntity(
                name = "Growth Scale Suite",
                pricePkr = 349000.0,
                servicesJson = "[\"Custom SaaS Workflow Engine\", \"Full Gemini AI Auto-Responder\", \"Supabase Auth & Database Config\", \"EasyPaisa & Bank Transfer Integration\", \"Weekly AI Blog Generator (AEO Optimized)\", \"Dedicated Agency Manager\"]",
                billingCycle = "Monthly",
                isFeatured = true
            )
            val enterprisePkg = PackageEntity(
                name = "Enterprise Automation Core",
                pricePkr = 749000.0,
                servicesJson = "[\"Autonomous Business Operations Suite\", \"Multi-channel AI Agents (WhatsApp & Web)\", \"Custom Fine-Tuned Gemini Models\", \"Full Mobile App (Android/iOS Ready)\", \"24/7 Priority VIP Support & SLA\", \"Dedicated AI Automation Architect\"]",
                billingCycle = "Monthly",
                isFeatured = false
            )

            val pkgId1 = pkgDao.insertPackage(starterPkg)
            val pkgId2 = pkgDao.insertPackage(growthPkg)
            val pkgId3 = pkgDao.insertPackage(enterprisePkg)

            // 2. Initial Clients
            val client1 = ClientEntity(
                name = "Hamza Farooq",
                company = "Nexus Dynamics PK",
                phone = "+923001234567",
                email = "hamza@nexusdynamics.pk",
                location = "Lahore, Pakistan",
                status = "Active",
                notes = "Onboarded for Growth Scale Suite. Needs custom AI WhatsApp auto-responder.",
                assignedTo = "Zain Malik",
                packageId = pkgId2,
                packageName = "Growth Scale Suite"
            )
            val client2 = ClientEntity(
                name = "Ayesha Khan",
                company = "Luxe Vogue E-Commerce",
                phone = "+923219876543",
                email = "ayesha@luxevogue.com",
                location = "Karachi, Pakistan",
                status = "Pending Payment",
                notes = "Requested Starter AI Launch package. EasyPaisa payment proof pending verification.",
                assignedTo = "Sarah Ahmed",
                packageId = pkgId1,
                packageName = "Starter AI Launch"
            )
            val client3 = ClientEntity(
                name = "Usman Malik",
                company = "Apex Financial Solutions",
                phone = "+923335557788",
                email = "usman@apexfinancials.io",
                location = "Islamabad, Pakistan",
                status = "Active",
                notes = "Enterprise Client. Autonomous workflow integration active.",
                assignedTo = "Zain Malik",
                packageId = pkgId3,
                packageName = "Enterprise Automation Core"
            )
            val client4 = ClientEntity(
                name = "Bilal Tariq",
                company = "CloudScale Logistics",
                phone = "+923124449911",
                email = "bilal@cloudscale.pk",
                location = "Faisalabad, Pakistan",
                status = "Lead",
                notes = "Inquired via WhatsApp for custom CRM & Gemini API automation.",
                assignedTo = "Unassigned",
                packageId = pkgId2,
                packageName = "Growth Scale Suite"
            )

            val cId1 = clientDao.insertClient(client1)
            val cId2 = clientDao.insertClient(client2)
            val cId3 = clientDao.insertClient(client3)
            clientDao.insertClient(client4)

            // 3. Initial Payments
            paymentDao.insertPayment(
                PaymentEntity(
                    clientId = cId1,
                    clientName = "Hamza Farooq",
                    packageId = pkgId2,
                    packageName = "Growth Scale Suite",
                    amountPkr = 349000.0,
                    paymentMethod = "Bank Transfer",
                    proofNote = "Meezan Bank TxnRef: #PK99281726",
                    status = "Approved"
                )
            )
            paymentDao.insertPayment(
                PaymentEntity(
                    clientId = cId2,
                    clientName = "Ayesha Khan",
                    packageId = pkgId1,
                    packageName = "Starter AI Launch",
                    amountPkr = 149000.0,
                    paymentMethod = "EasyPaisa",
                    proofNote = "EasyPaisa Txn ID: 9982716301",
                    status = "Pending"
                )
            )
            paymentDao.insertPayment(
                PaymentEntity(
                    clientId = cId3,
                    clientName = "Usman Malik",
                    packageId = pkgId3,
                    packageName = "Enterprise Automation Core",
                    amountPkr = 749000.0,
                    paymentMethod = "Bank Transfer",
                    proofNote = "HBL Wire Ref: #HB88192837",
                    status = "Approved"
                )
            )

            // 4. Initial Blog Posts
            blogDao.insertPost(
                BlogPostEntity(
                    title = "How AI Autonomous Agents Are Transforming Businesses in 2026",
                    content = "Artificial intelligence has shifted from passive tools to active autonomous agents. Businesses using automated workflow engines like ALEETRIX reduce operational overhead by up to 80% while scaling customer engagement 24/7. Key strategies include integrating custom Gemini models with real-time database backends and instant WhatsApp response pipelines.",
                    category = "AI Tools",
                    slug = "ai-autonomous-agents-transforming-businesses-2026",
                    metaDescription = "Discover how autonomous AI agents streamline business operations, boost customer conversion, and scale modern digital agencies."
                )
            )
            blogDao.insertPost(
                BlogPostEntity(
                    title = "Mastering Answer Engine Optimization (AEO) for Voice & Generative Search",
                    content = "As traditional search engine algorithms give way to Answer Engines like Gemini and Perplexity, businesses must optimize content for direct concise answers rather than simple keyword stuffing. Structured schema markup, authoritative tone, and question-based headings form the cornerstone of next-generation AEO marketing.",
                    category = "Business Growth",
                    slug = "mastering-answer-engine-optimization-aeo",
                    metaDescription = "Learn the essential framework for Answer Engine Optimization (AEO) to dominate Gemini and AI search rankings."
                )
            )
            blogDao.insertPost(
                BlogPostEntity(
                    title = "Google Gemini 3.5 Flash: The New Benchmark for SaaS Automation",
                    content = "The latest Gemini model release delivers sub-100ms response latency paired with ultra-accurate multimodal reasoning. At ALEETRIX, we leverage Gemini 3.5 Flash to power automated client replies, dynamic SEO content engines, and smart CRM data extraction.",
                    category = "AI News",
                    slug = "google-gemini-3-5-flash-benchmark-saas",
                    metaDescription = "Explore the groundbreaking features of Google Gemini 3.5 Flash and how it revolutionizes enterprise SaaS systems."
                )
            )
        }
    }
}
