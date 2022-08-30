package com.example.roadmaintenance.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.roadmaintenance.models.LightPost
import com.example.roadmaintenance.models.RegisteredRoad
import kotlinx.coroutines.flow.Flow


@Dao
interface RoadDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertRoads(roads: List<RegisteredRoad>)

    @Insert(onConflict = REPLACE)
    suspend fun insertLightPosts(roads: List<LightPost>)

    @Query("SELECT * FROM road_tb")
    fun getAllRoads(): Flow<List<RegisteredRoad>>

    @Query("SELECT * FROM light_post_tb WHERE pathId = :roadId ")
    fun getAllLightPostsByRoadId(roadId: Double): Flow<List<LightPost>>

}