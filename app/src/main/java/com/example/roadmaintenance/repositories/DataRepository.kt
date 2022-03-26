package com.example.roadmaintenance.repositories

import android.database.sqlite.SQLiteDatabase
import com.example.roadmaintenance.TRANSPORTATION_TABLE
import com.example.roadmaintenance.models.LightPost
import com.example.roadmaintenance.models.LightPostSideEnum
import com.example.roadmaintenance.models.Road

class DataRepository(private val dataBase: SQLiteDatabase) {

    private fun initDataBaseTable() {
        dataBase.execSQL(
            "CREATE TABLE IF NOT EXISTS $TRANSPORTATION_TABLE (location VARCHAR," +
                    " width INT(2)," +
                    "light_post_height INT(2)," +
                    "distance_between_light_posts INT(2)," +
                    "light_post_power INT," +
                    "light_post_production_light VARCHAR," +
                    "light_post_sides VARCHAR)"
        )
    }

    fun insertData(road: Road) {
        initDataBaseTable()
        dataBase.execSQL(
            "INSERT INTO $TRANSPORTATION_TABLE (location ," +
                    " width ," +
                    "light_post_height ," +
                    "distance_between_light_posts ," +
                    "light_post_power ," +
                    "light_post_production_light ," +
                    "light_post_sides ) VALUES ('${road.location}','${road.width}','${road.lightPost.height}'," +
                    "'${road.distanceEachLightPost}','${road.lightPost.power}','${road.lightPost.lightProductionType}','${road.lightPost.lightProductionType.toString()}')"
        )
    }

    fun retrieveAllData(): Road? {
        var cursor = dataBase.rawQuery("SELECT * FROM $TRANSPORTATION_TABLE", null)
        var locationIndex = cursor.getColumnIndex("location")
        var width_index = cursor.getColumnIndex("width")
        var light_post_height_index = cursor.getColumnIndex("light_post_height")
        var distance_between_light_posts_index =
            cursor.getColumnIndex("distance_between_light_posts")
        var light_post_power_index = cursor.getColumnIndex("light_post_power")
        var light_post_production_light_index =
            cursor.getColumnIndex("light_post_production_light")
        var light_post_sides_index = cursor.getColumnIndex("light_post_sides")

        cursor.moveToFirst()

        var locationValue = cursor.getString(locationIndex)
        var width_value = cursor.getInt(width_index)
        var light_post_height_value = cursor.getInt(light_post_height_index)
        var distance_between_light_posts_value =
            cursor.getInt(distance_between_light_posts_index)
        var light_post_power_value = cursor.getInt(light_post_power_index)
        var light_post_production_light_value =
            cursor.getString(light_post_production_light_index)

        var light_post_sides_value = cursor.getString(light_post_sides_index)
        cursor.close();

        var lightPostSides: LightPostSideEnum =
            if (light_post_sides_value.equals("one_side", true)) LightPostSideEnum.ONE_SIDE
            else LightPostSideEnum.TWO_SIDES

        return Road(
            locationValue, width_value, distance_between_light_posts_value,
            LightPost(
                0,
                light_post_height_value,
                light_post_power_value,
                light_post_production_light_value,
                lightPostSides
            )
        )
    }

    fun checkDataBase(): Boolean {
        val query =
            "select DISTINCT tbl_name from sqlite_master where tbl_name = '" + TRANSPORTATION_TABLE + "'"
        dataBase.rawQuery(query, null).use { cursor ->
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    return true
                }
            }
            return false
        }
    }

}