package com.example.roadmaintenance.data.repository

import com.example.roadmaintenance.data.db.RoadDataBase
import com.example.roadmaintenance.models.LightPost
import com.example.roadmaintenance.models.RegisteredRoad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OfflineModeRepository(private val roadDataBase: RoadDataBase) :
    RegisteredRoadRepository(roadDataBase) {

    suspend fun saveInOfflineMode(road: RegisteredRoad) {
        super.setRoadLightPostsCountsBeforeSaving(road)
        val tempList = arrayListOf(road)
        super.insertRoadsAndLightPosts(tempList)
    }

    suspend fun getNotSyncedRoads(): List<RegisteredRoad> {
        return withContext(Dispatchers.IO) {
            roadDao.getNotSyncedRoads()
        }
    }

    suspend fun getLightPostsByIdAsList(id: Double): List<LightPost> {
        return withContext(Dispatchers.IO) {
            roadDao.getLightPostsByIdAsList(id)
        }
    }

    suspend fun getNotSyncedLightPosts(): List<LightPost> {
        return withContext(Dispatchers.IO) {
            roadDao.getNotSyncedLightPosts()
        }
    }

    suspend fun saveLightPostsInCache(lpList: List<LightPost>) {
        lpList.takeUnless { it.isNullOrEmpty() }?.apply {
            insertLightPosts(lpList)
        }
    }

    suspend fun clearRoadSyncFlag(roadId: Double) {
        roadDao.clearRoadSyncFlag(roadId)

    }

    suspend fun clearLightPostSyncFlag(roadId: Double) {
        roadDao.clearLightPostSyncFlag(roadId)
    }
}