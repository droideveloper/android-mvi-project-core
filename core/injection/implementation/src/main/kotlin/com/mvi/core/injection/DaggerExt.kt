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

@Composable
inline fun <reified V : ViewModel> composeViewModel(
    crossinline provider : @Composable () -> Provider<V>,
) : V  {
    val factory = provider().toFactory()
    return viewModel(factory = factory)
}

@Composable
inline fun <reified V : ViewModel, P> composeParameterizedViewModel(
    param: P,
    noinline initialize: (param: P) -> V,
): V {
    val factory = Factory(param, initialize)
    return viewModel(factory = factory)
}

class Factory<V : ViewModel, P>(
    private val param: P,
    private val initialize: (param: P) -> V,
) : ViewModelProvider.Factory {

    override fun <V : ViewModel> create(modelClass: Class<V>): V {
        return initialize(param) as V
    }
}

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

fun <V : ViewModel> Provider<V>.toFactory() = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return this@toFactory.get() as T
    }
}
