package com.example.roadmaintenance.services;

import android.util.Log;

import com.example.roadmaintenance.models.Pathway;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class RouteRequestMapper {

    private static final String Tag = "Route Request Mapper";

    public static RequestBody createRequestBody(Pathway path){

        JsonObject baseObject = new JsonObject();

        JsonArray points = new JsonArray();

        JsonArray point1 = new JsonArray();
        point1.add(path.getLatitude_1());
        point1.add(path.getLongitude_1());

        JsonArray point2 = new JsonArray();
        point2.add(path.getLatitude_2());
        point2.add(path.getLongitude_2());

        points.add(point1);
        points.add(point2);

        baseObject.add("points",points);

        JsonArray pointHints = new JsonArray();
        pointHints.add("Lindenschmitstraße");
        pointHints.add("Thalkirchener Str.");

        baseObject.add("point_hints",pointHints);

        JsonArray details = new JsonArray();
        details.add("street_name");
        baseObject.add("details",details);

        baseObject.addProperty("profile","foot");
        baseObject.addProperty("optimize","true");
        baseObject.addProperty("calc_points",true);
        baseObject.addProperty("points_encoded",false);

        Log.i(Tag,baseObject.toString());

        return RequestBody.create(MediaType.parse("application/json"), baseObject.toString());

    }
}
