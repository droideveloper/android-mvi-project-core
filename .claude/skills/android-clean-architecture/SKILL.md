---
name: android-clean-architecture
description: Clean Architecture patterns for Android and Kotlin Multiplatform projects — module structure, dependency rules, UseCases, Repositories, and data layer patterns.
origin: droideveloper
---

# Android Clean Architecture

Clean Architecture patterns for Android and KMP projects. Covers module boundaries, dependency inversion, UseCase/Repository patterns, and data layer design with Room, SQLDelight, and Ktor.

## When to Activate

- Structuring Android or KMP project modules
- Implementing UseCases, Repositories, or DataSources
- Designing data flow between layers (domain, data, ui) feature-module
- Designing data flow between layers (gateway, implementation) core-module
- Setting up dependency injection with Koin or Dagger2
- Working with Room, SQLDelight, or Ktor/Retrofit in a layered architecture

## Module Structure

### Recommended Layout

```
project/
├── app/                        # Android entry point, DI wiring, Application class
└── core/                       # Shared utilities, base classes, error types
    └── network/                # Utility module seperate ex network to re-use
        ├── gateway/            # Utility module base classes and definitions (pure kotlin)
        ├── implementation/     # Implements gateway module on library terms (android library or pure kotlin)
    └── ui/                     # Reusable Compose components, theme, typography (android library with compose)
    └── auth/                   # Shared utilities, base classes, error types (pure kotlin or android library)
├── design-system/              # Reusable Compose components, theme, typography
└── auth/                       # Feature modules (optional, for larger projects)
    ├── data/                   # Repository implementations, DataSources, DB, network (:auth:data) (pure kotlin)
    ├── domain/                 # UseCases, domain models, repository interfaces (:auth:domain) (pure kotlin)
    └── ui/                     # Screens, ViewModels, UI models, navigation (:auth:ui)
```

### Dependency Rules

```
app → feature, core
feature → freature:domain, feature:data, core
feature:data → feature:domain, core:gateway
feature:domain → core:gateway (or no dependencies)
core → (nothing)
```

**Critical**: `:feature:domain` must NEVER depend on `:feature:data`, `feature:ui`, or any framework. It contains pure Kotlin only.
**Critical**: `:feature:data` must NEVER depend on `:feature:ui`, or any framework. It contains pure Kotlin only.

### UseCase Pattern

Each UseCase represents one business operation. Use `operator fun invoke` for clean call sites:

```kotlin
class GetItemsByCategoryUseCase(
    private val repository: ItemRepository
) {
    suspend operator fun invoke(category: String): Result<List<Item>> {
        return repository.getItemsByCategory(category)
    }
}

// Flow-based UseCase for reactive streams
class ObserveUserProgressUseCase(
    private val repository: UserRepository
) {
    operator fun invoke(userId: String): Flow<UserProgress> {
        return repository.observeProgress(userId)
    }
}
```

### Domain Models

Domain models are plain Kotlin data classes — no framework annotations:

```kotlin
data class Item(
    val id: String,
    val title: String,
    val description: String,
    val tags: List<String>,
    val status: Status,
    val category: String
)

enum class Status { DRAFT, ACTIVE, ARCHIVED }
```

### Repository Interfaces

Defined in domain, implemented in data:

```kotlin
interface ItemRepository {
    suspend fun getItemsByCategory(category: String): Result<List<Item>>
    suspend fun saveItem(item: Item): Result<Unit>
    fun observeItems(): Flow<List<Item>>
}
```

## Data Layer

### Repository Implementation

Coordinates between local and remote data sources:

```kotlin
class ItemRepositoryImpl(
    private val localDataSource: ItemLocalDataSource,
    private val remoteDataSource: ItemRemoteDataSource
) : ItemRepository {

    override suspend fun getItemsByCategory(category: String): Result<List<Item>> {
        return runCatching {
            val remote = remoteDataSource.fetchItems(category)
            localDataSource.insertItems(remote.map { it.toData() })
            localDataSource.getItemsByCategory(category).map { it.toDomain() }
        }
    }

    override suspend fun saveItem(item: Item): Result<Unit> {
        return runCatching {
            localDataSource.insertItems(listOf(item.toData()))
        }
    }

    override fun observeItems(): Flow<List<Item>> {
        return localDataSource.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
```

### Mapper Pattern

Keep mappers as extension functions near the data models:

```kotlin
// In data layer
fun ItemEntity.toDomain() = Item(
    id = id,
    title = title,
    description = description,
    tags = tags.split("|"),
    status = Status.valueOf(status),
    category = category
)

fun JsonItem.toData() = ItemEntity(
    id = id,
    title = title,
    description = description,
    tags = tags.joinToString("|"),
    status = status,
    category = category
)
```

### Room Database (Android)

```kotlin
@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val tags: String,
    val status: String,
    val category: String
)

@Dao
interface ItemDao {
    @Query("SELECT * FROM items WHERE category = :category")
    suspend fun getByCategory(category: String): List<ItemEntity>

    @Upsert
    suspend fun upsert(items: List<ItemEntity>)

    @Query("SELECT * FROM items")
    fun observeAll(): Flow<List<ItemEntity>>
}
```

### SQLDelight (KMP)

```sql
-- Item.sq
CREATE TABLE ItemEntity (
    id TEXT NOT NULL PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    tags TEXT NOT NULL,
    status TEXT NOT NULL,
    category TEXT NOT NULL
);

getByCategory:
SELECT * FROM ItemEntity WHERE category = ?;

upsert:
INSERT OR REPLACE INTO ItemEntity (id, title, description, tags, status, category)
VALUES (?, ?, ?, ?, ?, ?);

observeAll:
SELECT * FROM ItemEntity;
```

### Ktor Network Client (KMP)

```kotlin
class ItemRemoteDataSource(private val client: HttpClient) {

    suspend fun fetchItems(category: String): List<ItemDto> {
        return client.get("api/items") {
            parameter("category", category)
        }.body()
    }
}

// HttpClient setup with content negotiation
val httpClient = HttpClient {
    install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    install(Logging) { level = LogLevel.HEADERS }
    defaultRequest { url("https://api.example.com/") }
}
```

## Dependency Injection

### Koin (KMP-friendly)

```kotlin
// Domain module
val domainModule = module {
    factory { GetItemsByCategoryUseCase(get()) }
    factory { ObserveUserProgressUseCase(get()) }
}

// Data module
val dataModule = module {
    single<ItemRepository> { ItemRepositoryImpl(get(), get()) }
    single { ItemLocalDataSource(get()) }
    single { ItemRemoteDataSource(get()) }
}

// Presentation module
val presentationModule = module {
    viewModelOf(::ItemListViewModel)
    viewModelOf(::DashboardViewModel)
}
```

### Hilt (Android-only)

```kotlin
@Module
class RepositoryModule {
    @Provides
    internal fun bindItemRepository(impl: ItemRepositoryImpl): ItemRepository = impl
}

class ItemListViewModel @Inject constructor(
    private val getItems: GetItemsByCategoryUseCase
) : FlowMviViewModel<ItemState, ItemEvent>(
    initialState = ItemState()
)
```

## Error Handling

### Result/Try Pattern

Use `Result<T>` or a custom sealed type for error propagation:

```kotlin
sealed interface Try<out T> {
    data class Success<T>(val value: T) : Try<T>
    data class Failure(val error: AppError) : Try<Nothing>
}

sealed interface AppError {
    data class Network(val message: String) : AppError
    data class Database(val message: String) : AppError
    data object Unauthorized : AppError
}

// In ViewModel — map to UI state
viewModelScope.launch {
    when (val result = getItems(category)) {
        is Try.Success -> _state.update { it.copy(items = result.value, isLoading = false) }
        is Try.Failure -> _state.update { it.copy(error = result.error.toMessage(), isLoading = false) }
    }
}
```

## Convention Plugins (Gradle)

For KMP and Android projects, use convention plugins to reduce build file duplication:

Apply in modules:

```kotlin
// feature:data/build.gradle.kts
plugins {
    alias(libs.plugins.mvi.data)
}

dependencies {
    implementation(projects.feature.domain)
}

// feature:domain/build.gradle.kts
plugins {
    alias(libs.plugins.mvi.domain)
}

// feature:ui/build.gradle.kts
plugins {
    alias(libs.plugins.mvi.ui)
}

android {
    namespace = "<package-name>"
}

dependencies {
    api(projects.feature.data)
    api(projects.feature.domain)
}

// core:network/gateway/build.gradle.kts
plugins {
    alias(libs.plugins.mvi.common)
}
// core:network:implementation/build.gradle.kts
plugins {
    alias(libs.plugins.mvi.library)
}

android {
    namespace = "<package-name>"
}

dependencies {
    api(projects.core.network.gateway)
}

// core:ui/build.gradle.kts
plugins {
    alias(libs.plugins.mvi.ui)
}

android {
    namespace = "<package-name>"
    buildFeature {
        compose = true
    }
}
```

## Anti-Patterns to Avoid

- Importing Android framework classes in `:feature:domain`, `:feature:data`, `:core:gateway` — keep it pure Kotlin
- Exposing database entities or DTOs to the UI layer — always map to domain models
- Putting business logic in ViewModels — extract to UseCases
- Using `GlobalScope` or unstructured coroutines — use `viewModelScope` or structured concurrency
- Fat repository implementations — split into focused DataSources
- Circular module dependencies — if A depends on B, B must not depend on A

## References

See skill: `compose-multiplatform-patterns` for UI patterns.
See skill: `kotlin-coroutines-flows` for async patterns.
