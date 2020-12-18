package com.project.ptv_widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import androidx.annotation.RequiresApi;

/**
 * Implementation of App Widget functionality.
 */
public class widget extends AppWidgetProvider {

    private static final String ACTION_UPDATE_REFRESH = "action.UPDATE_REFRESH";


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void getData(Context context, int appWidgetId, AppWidgetManager appWidgetManager, String url){
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        List<String> jsonResponses = new ArrayList<>();

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("departures");
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String direction = jsonObject.getString("direction_id");
                        if (direction.contains("34")){
                            String arrival = jsonObject.getString("scheduled_departure_utc");
                            jsonResponses.add(arrival);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                List<String> nextTrams = new ArrayList<String>();
                int count = 0;
                for(int i=0; i<jsonResponses.size(); i++){
                    Instant tramArrival = Instant.parse(jsonResponses.get(i));
                    Instant currTime = Instant.now();
                    int value = tramArrival.compareTo(currTime);
                    if (value > 0 && count < 2){
                        nextTrams.add(tramArrival.toString());
                        count += 1;
                    }
                }

                List<String> nexttrams_mins = new ArrayList<String>();
                for(int i=0; i<nextTrams.size(); i++){
                    Instant firstInstant = Instant.parse(nextTrams.get(i));
                    Instant secondInstant = Instant.now();
                    Duration between = Duration.between(secondInstant, firstInstant);
                    long absoluteResult = between.abs().toMinutes();
                    String mins = Long.toString(absoluteResult);
                    nexttrams_mins.add(mins);
                }

                String info = "Spencer/Lonsdale:   " + nexttrams_mins.get(0) + " mins  ||  " + nexttrams_mins.get(1) + " mins";
                views.setTextViewText(R.id.tramData1, info);
                appWidgetManager.updateAppWidget(appWidgetId, views);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void getData2(Context context, int appWidgetId, AppWidgetManager appWidgetManager, String url){
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        List<String> jsonResponses = new ArrayList<>();

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("departures");
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String direction = jsonObject.getString("direction_id");
                        if (direction.contains("16") || direction.contains("18")){
                            String arrival = jsonObject.getString("scheduled_departure_utc");
                            jsonResponses.add(arrival);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                List<String> nextTrams = new ArrayList<String>();
                int count = 0;
                for(int i=0; i<jsonResponses.size(); i++){
                    Instant tramArrival = Instant.parse(jsonResponses.get(i));
                    Instant currTime = Instant.now();
                    int value = tramArrival.compareTo(currTime);
                    if (value > 0 && count < 2){
                        nextTrams.add(tramArrival.toString());
                        count += 1;
                    }
                }

                List<String> nexttrams_mins = new ArrayList<String>();
                for(int i=0; i<nextTrams.size(); i++){
                    Instant firstInstant = Instant.parse(nextTrams.get(i));
                    Instant secondInstant = Instant.now();
                    Duration between = Duration.between(secondInstant, firstInstant);
                    long absoluteResult = between.abs().toMinutes();
                    String mins = Long.toString(absoluteResult);
                    nexttrams_mins.add(mins);
                }

                String info = "Spencer/LaTrobe:    " + nexttrams_mins.get(0) + " mins  ||  " + nexttrams_mins.get(1) + " mins";
                views.setTextViewText(R.id.tramData2, info);
                appWidgetManager.updateAppWidget(appWidgetId, views);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Intent intentUpdate = new Intent(context, widget.class);
        intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int[] idArray = new int[]{appWidgetId};
        intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);

        //Wrap the intent as a PendingIntent, using PendingIntent.getBroadcast()//
        PendingIntent pendingUpdate = PendingIntent.getBroadcast(
                context, appWidgetId, intentUpdate,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //Send the pending intent in response to the user tapping the ‘Update’ TextView//
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        views.setOnClickPendingIntent(R.id.button, pendingUpdate);
        //Request that the AppWidgetManager updates the application widget//
        appWidgetManager.updateAppWidget(appWidgetId, views);


        getData(context, appWidgetId, appWidgetManager, "https://timetableapi.ptv.vic.gov.au/v3/departures/route_type/1/stop/2053?devid=3001739&signature=6000D0B0C25AB5E1484A0AE87828FA29138B93A2");
        getData2(context, appWidgetId, appWidgetManager, "https://timetableapi.ptv.vic.gov.au/v3/departures/route_type/1/stop/2868?devid=3001739&signature=10E661B31CFCE7FC42B026FE0DF02274FA88CC72");
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            Toast.makeText(context, "Tram times have been updated! ", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}