package com.example.list7

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Represents a piece of hiking equipment.
 *
 * @property stringResourceId The string resource ID for the name of the equipment.
 * @property imageResourceId The drawable resource ID for the image of the equipment.
 */
data class Equipment(
    @StringRes val stringResourceId: Int,
    @DrawableRes val imageResourceId: Int
)