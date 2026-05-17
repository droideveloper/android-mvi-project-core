---
name: compose-multiplatform-patterns
description: Compose Multiplatform and Jetpack Compose patterns for KMP projects — state management, navigation, theming, performance, and platform-specific UI.
origin: droideveloper
---

# Compose Multiplatform Patterns

Patterns for building shared UI across Android, iOS, Desktop, and Web using Compose Multiplatform and Jetpack Compose. Covers state management, navigation, theming, and performance.

## When to Activate

- Building Compose UI (Jetpack Compose or Compose Multiplatform)
- Managing UI state with ViewModels and Compose state
- Implementing navigation in KMP or Android projects
- Designing reusable composables and design systems
- Optimizing recomposition and rendering performance

## State Management

### ViewModel + Single State Object with StateFlow

Use data class(es) for screen state. Expose it as `StateFlow` and collect in Compose:

````kotlin
sealed interface UiState {
    data object Loading : UiState
    data interface Failure : UiState {
        data class Text(val message: String) : Failure
        data class Res(val stringRes: Int) : Failure
    }
    data class Success(val items: List<Item>)
}

@Stable
data class ItemQueryState(
    query: String,
) {
    val query by mutableStateOf(query)
        private set

    val canClear: Boolean
        get() = query.isNotEmpty()

    fun updateQuery(query: String) {
        this.squery = query
    }

    fun clear() {
        this.query = ""
    }
}

data class ItemState(
    val uiState: UiState = UiState.Loading,
    val queryState: ItemQueryState = ItemQueryState(query = ""),
)

sealed interface ItemEvent {
    data class OnQueryChanged(val query: String) : ItemEvent
    data object OnBackClicked : ItemEvent
}

class ItemListViewModel @Inject constructor(
    private val getItems: GetItemsUseCase,
    private val featureRouter: FeatureRouter,
) : FlowMviViewModel<ItemState, ItemEvent>(
    initialState = ItemState()
) {

    init {
        on<ItemEvent.OnQueryChanged> { event ->
            initialState.update { state ->
                state.copy(uiState =  UiState.Loading)
            }
            collectItems(query = event.query)
        }
        onClick<ItemEvent.OnBackClicked> {
            featureRouter.back()
        }
    }

    private suspend fun collectItems(query: String) {
        val result = getItems(query)
        result.fold(
            onSuccess = { items ->
                internalState.update { state ->
                    state.copy(uiState = UiState.Success(items = items))
                }
            },
            onFailure = { error ->
                internalState.update { state ->
                    state.copy(uiState = UiState.Failure.Text(message = error.message ?: "Error"))
                }
            }
        )
    }
}
````
### Collecting State in Compose

````kotlin
@FeatureScope
@Component(
    dependencies = [
        AppComponent::class,
    ],
    modules = [
        DataModule::class,
        NavigationModule::class,
    ],
)
interface ItemListComponent {
    val itemListViewModelProvider: Provider<ItemListViewModel>

    @Component.Factory
    interface Factory {
        fun create(
            appComponent: AppComponent,
            @BindsInstance navController: NavController,
        ): ItemListComponent
    }

    companion object {
        fun create(
            appComponent: AppComponent,
            navController: NavController,
        ): ItemListComponent =
            DaggerItemListComponent.factory()
                .create(
                    appComponent = appComponent,
                    navController = navController,
                )
    }
}

@Composable
fun ItemListScreen() {
    val navController = rememberNavController()
    val appComponent = LocalAppComponent.current

    val component = remember {
        ItemListComponent.create(
            appComponent = appComponent,
            navController = navController,
        )
    }

    val viewModel = composeViewModel {
        component.itemListViewModelProvider
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    ItemListComponent(state = state, dispatch = viewModel::dispatch)
}

@Composable
private fun ItemListContent(
    state: ItemState,
    dispatch: (ItemEvent) -> Unit,
) {
    // Stateless composable — easy to preview and test
}
````
### Event Sink Pattern

For complex screens, use a sealed interface for events instead of multiple callback lambdas:

````kotlin
sealed interface ItemEvent {
    data class OnQueryChanged(val query: String) : ItemEvent
    data object OnBackClicked : ItemEvent
}

// In ViewModel
init {
    on<ItemEvent.OnQueryChanged> { event ->
        initialState.update { state ->
            state.copy(uiState =  UiState.Loading)
        }
        collectItems(query = event.query)
    }
    onClick<ItemEvent.OnBackClicked> {
        featureRouter.back()
    }
}

// In Composable - single lambda instead of many
ItemListComponent(state = state, dispatch = viewModel::dispatch)
````
## Navigation

### Type-Safe Navigation (Compose Navigation 2.8+)

Define routes as `@Serializable` objects:

````kotlin
@Serializable data object HomeRoute
@Serializable data class DetailRoute(val id: String)
@Serializable data object SettingsRoute

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController, startDestination = HomeRoute) {
        composable<HomeRoute> { HomeScreen() }
        composable<DetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<DetailRoute>()
            DetailScreen(id = route.id)
        }
        composable<SettingsRoute> { SettingsScreen() }
    }
}
````

### Dialog and Bottom Sheet Navigation

Use `dialog()`

````kotlin
NavHost(navController, startDestination = HomeRoute) {
    composable<HomeRoute> { /* ... */ }
    dialog<ConfirmDeleteRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<ConfirmDeleteRoute>()
        ConfirmDeleteDialog(itemId = route.itemId)
    }
}
````
## Composable Design

### Slot-Based APIs

Design composables with slot parameters for flexibility:

```kotlin
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Card(modifier = modifier) {
        Column {
            header()
            Column(content = content)
            Row(horizontalArrangement = Arrangement.End, content = actions)
        }
    }
}
```

### Modifier Ordering

Modifier order matters — apply in this sequence:

```kotlin
Text(
    text = "Hello",
    modifier = Modifier
        .padding(16.dp)          // 1. Layout (padding, size)
        .clip(RoundedCornerShape(8.dp))  // 2. Shape
        .background(Color.White) // 3. Drawing (background, border)
        .clickable { }           // 4. Interaction
)
```

## KMP Platform-Specific UI

### expect/actual for Platform Composables

```kotlin
// commonMain
@Composable
expect fun PlatformStatusBar(darkIcons: Boolean)

// androidMain
@Composable
actual fun PlatformStatusBar(darkIcons: Boolean) {
    val systemUiController = rememberSystemUiController()
    SideEffect { systemUiController.setStatusBarColor(Color.Transparent, darkIcons) }
}

// iosMain
@Composable
actual fun PlatformStatusBar(darkIcons: Boolean) {
    // iOS handles this via UIKit interop or Info.plist
}
```

## Performance

### Stable Types for Skippable Recomposition

Mark classes as `@Stable` or `@Immutable` when all properties are stable:

```kotlin
@Immutable
data class ItemUiModel(
    val id: String,
    val title: String,
    val description: String,
    val progress: Float
)
```

### Use `key()` and Lazy Lists Correctly

```kotlin
LazyColumn {
    items(
        items = items,
        key = { it.id }  // Stable keys enable item reuse and animations
    ) { item ->
        ItemRow(item = item)
    }
}
```

### Defer Reads with `derivedStateOf`

```kotlin
val listState = rememberLazyListState()
val showScrollToTop by remember {
    derivedStateOf { listState.firstVisibleItemIndex > 5 }
}
```

### Avoid Allocations in Recomposition

```kotlin
// BAD — new lambda and list every recomposition
items.filter { it.isActive }.forEach { ActiveItem(it, onClick = { handle(it) }) }

// GOOD — key each item so callbacks stay attached to the right row
val activeItems = remember(items) { items.filter { it.isActive } }
activeItems.forEach { item ->
    key(item.id) {
        ActiveItem(item, onClick = { handle(item) })
    }
}
```

## Theming

### Material 3 Dynamic Theming

```kotlin
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(LocalContext.current)
            else dynamicLightColorScheme(LocalContext.current)
        }
        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }

    MaterialTheme(colorScheme = colorScheme, content = content)
}
```

## Anti-Patterns to Avoid

- Heavy computation inside `@Composable` functions — move to ViewModel or `remember {}`
- Using `LaunchedEffect(Unit)` as a substitute for ViewModel init — it re-runs on configuration change in some setups
- Creating new object instances in composable parameters — causes unnecessary recomposition

## References

See skill: `android-clean-architecture` for module structure and layering.
See skill: `kotlin-coroutines-flows` for coroutine and Flow patterns.
