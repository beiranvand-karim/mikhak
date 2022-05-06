package com.example.roadmaintenance.services;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class RouteResponseMapper {

    private static final String Tag = "Route Response Mapper";

    public static List<LatLng> jsonObject(JsonObject obj) {

        try {
            JsonArray paths = obj.get("paths").getAsJsonArray();
            JsonObject points = new JsonObject();
            JsonArray coordinates = new JsonArray();

            for (JsonElement path : paths) {
                if (path.isJsonObject()) {
                    points = path.getAsJsonObject().get("points").getAsJsonObject();
                }
            }

            if (!points.isJsonNull()) {
                coordinates = points.get("coordinates").getAsJsonArray();
            }

            List<LatLng> latLngList = parseJsonCoordinates(coordinates);

            Log.i(Tag, coordinates.toString());

            return latLngList;
        } catch (Exception e) {
            return null;
        }
    }

    private static List<LatLng> parseJsonCoordinates(JsonArray coordinates) {
        List<LatLng> latLngList = new ArrayList<>();

        for (JsonElement coordinate : coordinates) {
            if (coordinate.isJsonArray()) {
                latLngList.add(getLatlng(coordinate.getAsJsonArray()));
            }
        }
        return latLngList;
    }

    private static LatLng getLatlng(JsonArray latlngJson) {
        return new LatLng(latlngJson.get(0).getAsDouble(), latlngJson.get(1).getAsDouble());
    }
}