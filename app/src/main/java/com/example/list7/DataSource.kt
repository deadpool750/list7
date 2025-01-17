package com.example.list7

class DataSource {
    fun loadEquipment(): List<Equipment> {
        return listOf(
            Equipment(R.string.equipment1, R.drawable.hiker_boots),
            Equipment(R.string.equipment2, R.drawable.backpack),
            Equipment(R.string.equipment3, R.drawable.hiking_shorts),
            Equipment(R.string.equipment4, R.drawable.compass),
            Equipment(R.string.equipment5, R.drawable.portable_water_filter),
            Equipment(R.string.equipment6, R.drawable.water_bottle),
            Equipment(R.string.equipment7, R.drawable.first_aid_kit),
            Equipment(R.string.equipment8, R.drawable.firestarter_pack),
            Equipment(R.string.equipment9, R.drawable.sleeping_bag),
            Equipment(R.string.equipment10, R.drawable.bug_repellent),
            Equipment(R.string.equipment11, R.drawable.trekking_poles)
        )

    }
}