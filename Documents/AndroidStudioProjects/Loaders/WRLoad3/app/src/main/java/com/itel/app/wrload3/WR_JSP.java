package com.itel.app.wrload3;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.List;

public class WR_JSP {

    /*
    private static final String TAG_WR =   "waitingroom";

    public List<RowData> parseJSONFromResp(String response) throws JSONException {

        JSONObject jsonResponse = new JSONObject(response);

        JSONArray jsonMainNode = jsonResponse.optJSONArray(TAG_WR );

        String wr = jsonResponse.getString(TAG_WR );

        if (wr.length() == 0) {
            Log.v("APPTS", "No Appts");
            //return "No Appts";
        }

        Integer js_length = jsonMainNode.length();
        Log.d("JS_LENGTH", js_length.toString());



        for(int i = 0; i < jsonMainNode.length(); i++){

            JSONArray jsonArray = new JSONArray(wr);
            String row = jsonArray.getString(i);
            JSONObject object1 = (JSONObject) new JSONTokener(row).nextValue();
            String attendee_name = object1.getString("attendee_name");
            String actual_end_tm = object1.getString("actual_end_tm");
            String status = object1.getString("status");//json
            String arrive_tm = object1.getString("arrive_tm");//json
            String actual_start_tm = object1.getString("actual_start_tm");
            String duration = object1.getString("duration");
            String tokapikey = object1.getString("tokapikey");
            String toktoken = object1.getString("toktoken");
            String date_rfc2822  = object1.getString("date_rfc2822");

            JSONObject object2 = (JSONObject) new JSONTokener(attendee_name).nextValue();
            String patient = object2.getString("name");

            JSONObject object3 = (JSONObject) new JSONTokener(status).nextValue();
            String session_closed = object3.getString("session_closed");
            String online_status = object3.getString("online_status");

            RowData rowItem = new RowData();

            rowItem.setStatus(online_status);
            rowItem.setArrival_time(arrive_tm);
            rowItem.setSchedule_length(duration);
            rowItem.setName(attendee_name);
            rowItem.setSchedule_time(date_rfc2822);
            //wr_item.setImageUrl(); /* Need to do initial

            wrDataList.add(wr_item);


        }

        return wrDataList;

    }
    */

} //end WR_JSP
