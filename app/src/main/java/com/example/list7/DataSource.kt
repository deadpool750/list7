package com.example.list7

class DataSource {
    fun loadEquipment(): List<Equipment> {
        return listOf(
            Equipment(R.string.equipment1, R.drawable.hiker_boots, 50.0,"Brand New"),
            Equipment(R.string.equipment2, R.drawable.backpack, 50.00,"Brand New"),
            Equipment(R.string.equipment3, R.drawable.hiking_shorts, 50.00,"Brand New"),
            Equipment(R.string.equipment4, R.drawable.compass,50.00,"Brand New"),
            Equipment(R.string.equipment5, R.drawable.portable_water_filter,50.00,"Brand New"),
            Equipment(R.string.equipment6, R.drawable.water_bottle,50.00,"Brand New"),
            Equipment(R.string.equipment7, R.drawable.first_aid_kit,50.00,"Brand New"),
            Equipment(R.string.equipment8, R.drawable.firestarter_pack,50.00,"Brand New"),
            Equipment(R.string.equipment9, R.drawable.sleeping_bag,50.00,"Brand New"),
            Equipment(R.string.equipment10, R.drawable.bug_repellent,50.00,"Brand New"),
            Equipment(R.string.equipment11, R.drawable.trekking_poles,50.00,"Brand New")
        )

    }
}