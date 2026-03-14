package com.dayforge.app

import android.app.Application
import com.dayforge.app.data.db.DayForgeDatabase
import com.dayforge.app.data.repository.DayForgeRepository

class DayForgeApplication : Application() {
    val database by lazy { DayForgeDatabase.getDatabase(this) }
    val repository by lazy { DayForgeRepository(database.dao()) }
}
