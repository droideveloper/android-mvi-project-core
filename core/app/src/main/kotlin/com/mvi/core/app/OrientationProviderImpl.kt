package com.mvi.core.app

import android.content.res.Configuration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class OrientationProviderImpl @Inject constructor() : OrientationProvider {

    private val values = MutableStateFlow<Int>(Configuration.ORIENTATION_PORTRAIT)

    override suspend fun setCurrentOrientation(orientation: Int) {
        values.tryEmit( orientation)
    }

    override fun invoke(): StateFlow<Int> = values
}
