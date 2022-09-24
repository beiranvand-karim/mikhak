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
    suspend fun insertRoad(roads: RegisteredRoad)

    @Insert(onConflict = REPLACE)
    suspend fun insertLightPosts(lpList: List<LightPost>)

    @Query("SELECT * FROM road_tb")
    fun getAllRoads(): Flow<List<RegisteredRoad>>

    @Query("SELECT * FROM light_post_tb WHERE roadId = :id ")
    fun getAllLightPostsByRoadIdAsFlows(id: Double): Flow<List<LightPost>>

    @Query("SELECT * FROM road_tb WHERE isSyncWithServer = 'false' ")
    suspend fun getNotSyncedRoads(): List<RegisteredRoad>

    @Query("SELECT * FROM light_post_tb WHERE roadId = :id ")
    suspend fun getLightPostsByIdAsList(id: Double): List<LightPost>

    @Query("SELECT * FROM light_post_tb WHERE isSyncWithServer = 'false' ")
    suspend fun getNotSyncedLightPosts(): List<LightPost>

    @Query("SELECT COUNT() FROM light_post_tb WHERE roadId = :id ")
    suspend fun isRoadExists(id: Double): Int

    @Query("UPDATE road_tb SET isSyncWithServer = 1 where roadId = :id")
    fun clearRoadSyncFlag(id: Double)

    @Query("UPDATE light_post_tb SET isSyncWithServer = 1 where roadId = :id")
    fun clearLightPostSyncFlag(id: Double)
}