package com.example.roadmaintenance.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.roadmaintenance.models.LightPost
import com.example.roadmaintenance.models.RegisteredRoad

@Database(
    entities = [RegisteredRoad::class, LightPost::class],
    version = 1,
    exportSchema = false
)
abstract class RoadDataBase : RoomDatabase() {

    abstract fun roadDao(): RoadDao

    companion object {
        @Volatile
        private var INSTANCE: RoadDataBase? = null

        fun getDatabase(context: Context): RoadDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    RoadDataBase::class.java,
                    "road_db"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}