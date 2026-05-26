@file:Suppress("UNCHECKED_CAST")

package com.mvi.core.injection

import androidx.compose.runtime.Composable
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import javax.inject.Provider
import kotlin.reflect.KClass

/**
 * Composable function to create and retrieve a ViewModel instance using DI.
 *
 * This function allows you to reify a ViewModel type directly in a composable composition
 * without manually creating the factory. The ViewModel will be stored in the composition
 * lifecycle and survives recompositions.
 *
 * @param provider A lambda that provides the Provider instance for the ViewModel type.
 *                 This provider is created within the composition scope.
 * @return An instance of the ViewModel type, stored in the composition lifecycle.
 *
 * @sample
 * ```kotlin
 * @Composable
 * fun MyScreen() {
 *     val myViewModel = composeViewModel {
 *         provideMyViewModelProvider()
 *     }
 * }
 * ```
 */
@Composable
inline fun <reified V : ViewModel> composeViewModel(
    crossinline provider: @Composable () -> Provider<V>,
) : V {
    val factory = provider().toFactory()
    return viewModel(factory = factory)
}

/**
 * Composable function to create a parameterized ViewModel instance.
 *
 * This function supports ViewModels that require constructor parameters by passing
 * an initialization function that takes the parameter and returns the ViewModel.
 *
 * @param param The parameter needed to initialize the ViewModel.
 * @param initialize A function that creates the ViewModel instance given the parameter.
 * @return An instance of the ViewModel type.
 *
 * @sample
 * ```kotlin
 * @Composable
 * fun MyScreen(param: String) {
 *     val myViewModel = composeParameterizedViewModel(param) { p ->
 *         MyViewModel(p)
 *     }
 * }
 * ```
 */
@Composable
inline fun <reified V : ViewModel, P> composeParameterizedViewModel(
    param: P,
    noinline initialize: (param: P) -> V,
): V {
    val factory = Factory(param, initialize)
    return viewModel(factory = factory)
}

/**
 * A custom ViewModelProvider.Factory that supports parameterized ViewModel creation.
 *
 * This factory allows passing constructor arguments or additional data to ViewModels
 * when they are created from a composable composition scope.
 *
 * @param param The parameter stored for the factory instance.
 * @param initialize A function that uses the stored parameter to create the ViewModel.
 */
class Factory<V : ViewModel, P>(
    private val param: P,
    private val initialize: (param: P) -> V,
) : ViewModelProvider.Factory {

    override fun <V : ViewModel> create(modelClass: Class<V>): V {
        return initialize(param) as V
    }
}

/**
 * Composable function to retrieve a ViewModel instance from the composition lifecycle.
 *
 * This function provides a simple way to access existing ViewModels in a Composable
 * without creating a new factory. It uses the ViewModelStoreOwner to get the
 * ViewModel instance, optionally with a key for unique instances and custom
 * CreationExtras for parameterized ViewModels.
 *
 * @param viewModelStoreOwner The owner of the ViewModelStore, typically obtained
 *                            from [LocalViewModelStoreOwner].
 * @param key An optional key to retrieve a specific ViewModel instance. When null,
 *            the ViewModel is retrieved by its type.
 * @param factory An optional custom factory to use for creating the ViewModel.
 *                If not provided, the default factory (if available) or an empty
 *                factory is used.
 * @param extras Optional CreationExtras for providing parameters to the ViewModel.
 * @return An instance of the ViewModel type [VM].
 *
 * @sample
 * ```kotlin
 * @Composable
 * fun MyScreen() {
 *     val viewModel = viewModel()
 * }
 * ```
 */
@Composable
inline fun <reified VM : ViewModel> viewModel(
    viewModelStoreOwner: ViewModelStoreOwner =
        checkNotNull(LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        },
    key: String? = null,
    factory: ViewModelProvider.Factory? = null,
    extras: CreationExtras =
        if (viewModelStoreOwner is HasDefaultViewModelProviderFactory) {
            viewModelStoreOwner.defaultViewModelCreationExtras
        } else {
            CreationExtras.Empty
        }
): VM = viewModelStoreOwner.get(VM::class, key, factory, extras)

/**
 * Extension function to retrieve a ViewModel instance from a ViewModelStoreOwner.
 *
 * This function provides a convenient way to get ViewModels from any
 * ViewModelStoreOwner, supporting parameterized ViewModels through the factory
 * and extras parameters. It internally uses [ViewModelProvider.create] to obtain
 * a provider and then retrieves the ViewModel from it.
 *
 * @param modelClass The Kotlin class of the ViewModel to retrieve.
 * @param key An optional key for unique ViewModel instances. When null,
 *            the ViewModel is retrieved by its type.
 * @param factory An optional custom factory. If not provided, attempts to use
 *                the default factory from the ViewModelStoreOwner, otherwise
 *                falls back to the no-argument factory.
 * @param extras Optional CreationExtras for parameterized ViewModels.
 * @return An instance of the requested ViewModel type.
 */
fun <VM : ViewModel> ViewModelStoreOwner.get(
    modelClass: KClass<VM>,
    key: String? = null,
    factory: ViewModelProvider.Factory? = null,
    extras: CreationExtras =
        if (this is HasDefaultViewModelProviderFactory) {
            this.defaultViewModelCreationExtras
        } else {
            CreationExtras.Empty
        }
): VM {
    val provider =
        if (factory != null) {
            ViewModelProvider.create(this.viewModelStore, factory, extras)
        } else if (this is HasDefaultViewModelProviderFactory) {
            ViewModelProvider.create(
                this.viewModelStore,
                this.defaultViewModelProviderFactory,
                extras
            )
        } else {
            ViewModelProvider.create(this)
        }
    return if (key != null) {
        provider[key, modelClass]
    } else {
        provider[modelClass]
    }
}

/**
 * Extension function to convert a Provider to a ViewModelProvider.Factory.
 *
 * This function creates a factory instance that wraps the Provider, allowing
 * the factory to retrieve the ViewModel instance by calling [get]. This is
 * useful when you need to provide a custom factory but the ViewModel is
 * already stored in a Provider.
 *
 * @return A ViewModelProvider.Factory that delegates to the Provider.
 */
fun <V : ViewModel> Provider<V>.toFactory() = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return this@toFactory.get() as T
    }
}
