package com.restaurantmenu.user.restaurantmenu.activity;

import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.restaurantmenu.user.restaurantmenu.R;
import com.restaurantmenu.user.restaurantmenu.waiterservice.WaiterService;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableManagerActivity extends AppCompatActivity {

    List<String> tablesList;
    ListView mTableList;
    ArrayAdapter<String> arrayAdapter;
    private String selectedTableName = "none";
    TextView mSelectedTableName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_manager);

        mSelectedTableName = (TextView) findViewById(R.id.selected_table_name);
        mSelectedTableName.setText(selectedTableName);

        mTableList = (ListView) findViewById(R.id.listTables);
        //getting table names when loading the activity
        new getTableTask().execute();

        mTableList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedTableName = (String)arrayAdapter.getItem(position);
                mSelectedTableName.setText(selectedTableName);
            }
        });
    }

    class getTableTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String valueToken = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("authToken", "defaultStringIfNothingFound");
            String valueUsername = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("username", "defaultStringIfNothingFound");

            WaiterService waiterServe = new WaiterService(getApplicationContext());
            Map<String,String> parameters = new HashMap<>();
            parameters.put("auth_token",valueToken);
            parameters.put("username", valueUsername);
            String jsonArrayResultString = waiterServe.performHttpsPostCall("gettables",parameters);

            try{
                JSONArray jsonArrayResult = new JSONArray(jsonArrayResultString);

                if(jsonArrayResult.getJSONObject(0).get("message").equals("tables")){
                    JSONArray jsonArrayTables = jsonArrayResult.getJSONObject(0).getJSONObject("content").getJSONArray("tables");
                    tablesList = new ArrayList<String>();

                    for(int i = 0; i<jsonArrayTables.length(); i++){
                        tablesList.add(jsonArrayTables.get(i).toString());
                    }

                }
            }catch (JSONException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            arrayAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, tablesList);
            mTableList.setAdapter(arrayAdapter);
            super.onPostExecute(aVoid);
        }
    }
}
