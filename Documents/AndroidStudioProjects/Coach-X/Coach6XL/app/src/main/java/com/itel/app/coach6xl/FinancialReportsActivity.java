package com.itel.app.coach6xl;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FinancialReportsActivity extends Activity {
    ListView list;
    TextView tag1;
    TextView tag2;
    TextView tag3;
    TextView tag4;
    TextView tag6;

    ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
    private static String url = "https://dev1.itelecoach.com/home/submittedReport.php";
    private static String url_detail = "https://dev1.itelecoach.com/home/billing_information.html";


    //JSON Node Names
    private static final String TAG_JID =   "reports";
    private static final String TAG_1 =     "sched_tm";
    private static final String TAG_2 =     "provider_fee";
    private static final String TAG_3 =     "total_fee";
    private static final String TAG_4 =     "patient_name";
    private static final String TAG_5 =     "schedule_id";
    private static final String TAG_6 =     "billed";

    JSONArray android = null;
    private String username = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financialreports);

        oslist = new ArrayList<HashMap<String, String>>();

        Intent i = getIntent();
        username = i.getStringExtra("username");
        Log.v("PASSED USERNAME", username);

        new JSONParse().execute();


    }

    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tag1 = (TextView)findViewById(R.id.tag1);
            tag2 = (TextView)findViewById(R.id.tag2);
            tag3 = (TextView)findViewById(R.id.tag3);
            tag4 = (TextView)findViewById(R.id.tag4);
            tag6 = (TextView)findViewById(R.id.tag6);

            pDialog = new ProgressDialog(FinancialReportsActivity.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            JSONParser jParser = new JSONParser();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("filter_type", "ALL"));
            params.add(new BasicNameValuePair("provider_id", username));
            String paramsString = URLEncodedUtils.format(params, "UTF-8");
            String get_url = url + "?" + paramsString;

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(get_url);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject jsonResponse) {
            pDialog.dismiss();
            try {
                JSONArray jsonMainNode = jsonResponse.optJSONArray(TAG_JID );

                String wr = jsonResponse.getString(TAG_JID);

                if (wr.length() == 0) {
                    Log.d("APPTS", "No Appts");
                    //return "No Appts";
                }

                Integer js_length = jsonMainNode.length();
                Log.d("JS_LENGTH", js_length.toString());

                for(int i = 0; i < jsonMainNode.length(); i++){
                    JSONArray jsonArray = new JSONArray(wr);
                    String row = jsonArray.getString(i);
                    Log.d("ROW", row);

                    JSONObject object1 = (JSONObject) new JSONTokener(row).nextValue();

                    // Adding value HashMap key => value
                    HashMap<String, String> map = new HashMap<String, String>();

                    map.put(TAG_1, object1.getString(TAG_1));
                    map.put(TAG_2, object1.getString(TAG_2));
                    map.put(TAG_3, object1.getString(TAG_3));
                    map.put(TAG_4, object1.getString(TAG_4));
                    map.put(TAG_5, object1.getString(TAG_5));

                    if (!object1.getString(TAG_6).equals("1")) {
                        map.put(TAG_6, "N");
                    } else {
                        map.put(TAG_6, "Y");
                    }

                    oslist.add(map);
                    list=(ListView)findViewById(R.id.list);

                    ListAdapter adapter = new SimpleAdapter(FinancialReportsActivity.this, oslist,
                            R.layout.financialreports_list,
                            new String[] { TAG_1,TAG_2, TAG_3, TAG_4, TAG_6, TAG_5 }, new int[] {
                            R.id.tag1,R.id.tag2, R.id.tag3, R.id.tag4, R.id.tag6});

                    list.setAdapter(adapter);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            Toast.makeText(FinancialReportsActivity.this, "You Clicked at " + oslist.get(+position).get(TAG_5), Toast.LENGTH_SHORT).show();
                            detailDialog(oslist.get(+position).get(TAG_5));

                        }
                    });

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private void detailDialog(String sched_id) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("iOS", "Y"));
        params.add(new BasicNameValuePair("schedule_id", sched_id));
        String paramsString = URLEncodedUtils.format(params, "UTF-8");
        String get_url = url_detail + "?" + paramsString;

        Dialog dialog    = new Dialog(FinancialReportsActivity.this);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vi = inflater.inflate(R.layout.webviewdialog, null);
        dialog.setContentView(vi);
        dialog.setTitle("Billing Detail");
        dialog.setCancelable(true);
        WebView wb = (WebView) vi.findViewById(R.id.webview);
        wb.getSettings().setJavaScriptEnabled(true);
        wb.loadUrl(get_url);
        //System.out.println("..loading url..");
        dialog.show();
    }

}