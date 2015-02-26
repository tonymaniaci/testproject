package com.itel.app.coach6xl;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.CalendarContract;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

public class ScheduleAppointmentActivity extends Activity {

    ListView list;

    private static final String TAG_1 =     "date";
    private static final String TAG_2 =     "start_time";
    private static final String TAG_3 =     "end_time";
    private static final String TAG_4 =     "duration";

    ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();

    long calendarID;
    long duration  = 60L*1000L;
    Calendar calendar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.openslots_activity);
        getOpenSlots openSlots = new getOpenSlots();
        openSlots.execute();
    }

    private class Slot {
        private Long slot_start;
        private Long slot_end;
        private int slot_minutes;
        private int no_of_slots;

        private Slot(Long slot_start, Long slot_end, int slot_minutes, int no_of_slots) {
            this.slot_start = slot_start;
            this.slot_minutes = slot_minutes;
            this.slot_end = slot_end;
            this.no_of_slots = no_of_slots;
        }


    }

    private class BlockedTimes {
        private int day_of_week;
        private Time start_of_day;
        private Time end_of_day;


    }


    private class getOpenSlots extends AsyncTask<String, Void, List<Object>>

    {

        @Override
        protected List<Object> doInBackground(String... params) {

            List<Object> osList = new ArrayList<Object>();

            calendar = Calendar.getInstance();
            //TimeZone time_zone = calendar.getTimeZone();
            //TimeZone default_time_zone = TimeZone.getDefault();

            calendar.setTimeZone(TimeZone.getTimeZone("America/Phoenix"));

            calendar.set(2015, Calendar.JANUARY, 13, 8, 0, 0);
            long startDay = calendar.getTimeInMillis();
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            Log.d("*** start dayOfWeek ***", String.valueOf(dayOfWeek));

            calendar.set(2015, Calendar.JANUARY, 13, 16, 59, 59);
            long endDay = calendar.getTimeInMillis();
            dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            Log.d("*** end dayOfWeek ***", String.valueOf(dayOfWeek));

            String[] projection = new String[]{BaseColumns._ID, CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND};
            String selection = CalendarContract.Events.DTSTART + " >= ? AND " + CalendarContract.Events.DTSTART + "<= ?";
            String[] selectionArgs = new String[]{Long.toString(startDay), Long.toString(endDay)};

            Cursor cur = getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, selection, selectionArgs, null);

            int num_slots = 0; int slot_minutes = 0;
            long slot_start; long slot_end;
            int numQuants = 0;
            //long prev_beginVal = 0L;
            long prev_endVal = 0L;
            long beginVal = 0;
            long endVal = 0;
            long milli2mins = 1000L*60L;


            while (cur.moveToNext()) {
                beginVal = cur.getLong(2);
                endVal = cur.getLong(3);

                if (num_slots == 0) {
                    slot_start = startDay;
                    slot_end = beginVal;
                    //prev_beginVal = beginVal;
                    prev_endVal = endVal;
                }

                else {
                    slot_start = prev_endVal;
                    slot_end = beginVal;
                    //prev_beginVal = beginVal;
                    prev_endVal = endVal;
                }

                slot_minutes = (int) ((slot_end - slot_start)/milli2mins);
                numQuants = (int) ((slot_end - slot_start) / duration);
                osList.add(new Slot(slot_start, slot_end, slot_minutes, numQuants));
                num_slots++;

                calendar.setTimeInMillis(slot_start);
                calendar.setTimeInMillis(slot_end);
            }

            //cursor last one
            slot_start = endVal;
            slot_end = endDay;
            slot_minutes = (int) ((slot_end - slot_start)/milli2mins);
            numQuants = (int) ((slot_end - slot_start) / duration);
            osList.add(new Slot(slot_start, slot_end, slot_minutes, numQuants));
            num_slots++;

            calendar.setTimeInMillis(slot_start);
            calendar.setTimeInMillis(slot_end);

            return osList;
        }

        @Override
        protected void onPostExecute(List<Object> result) {

            Iterator<Object> iterator = result.iterator();

            while (iterator.hasNext()) {

                Slot slot = (Slot) iterator.next();

                HashMap<String, String> map = new HashMap<String, String>();

                //Slot Start Date
                calendar.setTimeInMillis(slot.slot_start);
                SimpleDateFormat format_date = new SimpleDateFormat("MM-dd-yyyy");
                format_date.setTimeZone(TimeZone.getTimeZone("America/Phoenix"));
                String slot_date = format_date.format(calendar.getTime());

                //Slot Start Time
                calendar.setTimeInMillis(slot.slot_start);
                SimpleDateFormat format_start_time = new SimpleDateFormat("HH:mm");
                format_start_time.setTimeZone(TimeZone.getTimeZone("America/Phoenix"));
                String slot_start_time = format_start_time.format(calendar.getTime());

                //Slot End Time
                calendar.setTimeInMillis(slot.slot_end);
                SimpleDateFormat format_end_time = new SimpleDateFormat("HH:mm");
                format_end_time.setTimeZone(TimeZone.getTimeZone("America/Phoenix"));
                String slot_end_time = format_end_time.format(calendar.getTime());

                //Slot Duration
                String slot_minutes = String.valueOf(slot.slot_minutes);

                map.put(TAG_1, slot_date);
                map.put(TAG_2, slot_start_time);
                map.put(TAG_3, slot_end_time);
                map.put(TAG_4, slot_minutes);

                oslist.add(map);
                list=(ListView)findViewById(R.id.list);

                ListAdapter adapter = new SimpleAdapter(ScheduleAppointmentActivity.this, oslist,
                        R.layout.openslots_list,
                        new String[] { TAG_1,TAG_2, TAG_3 }, new int[] {
                        R.id.tag1,R.id.tag2, R.id.tag3});

                list.setAdapter(adapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        //Toast.makeText(ScheduleAppointmentActivity.this, "You Clicked at " + oslist.get(+position).get(TAG_2), Toast.LENGTH_SHORT).show();
                        //detailDialog(oslist.get(+position).get(TAG_5));
                        Intent AddAppointment = new Intent(getApplicationContext(), AddAppointment.class);
                        AddAppointment.putExtra("appt_date", oslist.get(+position).get(TAG_1));
                        AddAppointment.putExtra("start_time", oslist.get(+position).get(TAG_2));
                        AddAppointment.putExtra("end_time", oslist.get(+position).get(TAG_3));
                        AddAppointment.putExtra("duration", oslist.get(+position).get(TAG_4));

                        startActivity(AddAppointment);

                    }
                });

            }

        }
    }


}
