package com.mvi.core.ui.preview

import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    showBackground = true,
    device = "${Devices.PHONE}, orientation=portrait",
)
@Preview(
    showBackground = true,
    device = "${Devices.PHONE}, orientation=landscape",
)
@Preview(
    showBackground = true,
    device = "${Devices.TABLET}, orientation=portrait",
)
@Preview(
    showBackground = true,
    device = "${Devices.TABLET}, orientation=landscape",
)
annotation class Previews()
