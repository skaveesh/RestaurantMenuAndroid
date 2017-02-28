package com.restaurantmenu.user.restaurantmenu.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.restaurantmenu.user.restaurantmenu.R;
import com.restaurantmenu.user.restaurantmenu.waiterservice.WaiterService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    private String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final EditText username_txt = (EditText) findViewById(R.id.username_text);
        final EditText password_txt = (EditText) findViewById(R.id.password_text);

        Button signin_btn = (Button) findViewById(R.id.signin_button);
        signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username_txt.getText().length() > 0 && password_txt.getText().length() > 0) {
                    username = username_txt.getText().toString();
                    password = password_txt.getText().toString();

                    //credentials will check in the background
                    new CheckCredentialsBackground().execute();
                }
            }
        });
    }

    class CheckCredentialsBackground extends AsyncTask<Void, Void, Void> {

        ProgressBar progressBarLogin = (ProgressBar) findViewById(R.id.progressBar);

        @Override
        protected void onPreExecute() {
            progressBarLogin.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                WaiterService signing = new WaiterService(getApplicationContext());
                Map<String,String> parameters = new HashMap<>();
                parameters.put("username",username);
                parameters.put("password",password);
                String result = signing.performHttpsPostCall("login", parameters);

                JSONArray jsonArr = new JSONArray(result);

                if (jsonArr.getJSONObject(0).get("message").equals("authentication") && jsonArr.getJSONObject(0).get("reason").equals("authentication success")) {
                    //authentication success
                    JSONObject token = new JSONObject(jsonArr.getJSONObject(0).getString("content"));

                    //save the token of the user for future use
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                            .putString("authToken", token.get("auth_token").toString())
                            .putString("username", username)
                            .apply();

                    //starting the next activity
                    Intent intent = new Intent(getApplicationContext(), TableManagerActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();

                } else if (jsonArr.getJSONObject(0).get("message").equals("authentication") && jsonArr.getJSONObject(0).get("reason").equals("authentication failed")) {
                    //login username or password incorrect

                    Snackbar.make(findViewById(android.R.id.content), "Incorrect Username or Password!", Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.WHITE)
                            .show();
                }

            } catch (JSONException e) {
                Snackbar.make(findViewById(android.R.id.content), "Something Went Wrong...!", Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.WHITE)
                        .show();
            } catch (Exception e) {
                e.printStackTrace();
                Snackbar.make(findViewById(android.R.id.content), "Something Went Wrong...!", Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.WHITE)
                        .show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBarLogin.setVisibility(View.GONE);
        }
    }

}
