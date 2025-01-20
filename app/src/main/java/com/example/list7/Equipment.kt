package com.example.list7

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Equipment(
    @StringRes val stringResourceId: Int,
    @DrawableRes val imageResourceId: Int, val priceId: Double,
    val description: String = "",
    val quantity: Int = 0,)