package com.example.robien.beachbuddy;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;

/**
 * Created by Calvin on 2/24/2016.
 */
public class NavigationActivity extends AppCompatActivity {

    Button search;
    EditText searchClass;
    String JSON_String, studentName;
    JSONObject jsonObject;
    JSONArray jsonArray;
    StudentAdapter studentAdapter;
    ListView listView;

    public static Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_header_main);
        //studentName = LoginActivity.facebookName.getText().toString(); // pass this to search fb user
        search = (Button)findViewById(R.id.search);
        searchClass = (EditText)findViewById(R.id.classSearch);
        listView = (ListView)findViewById(R.id.listView);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                getJSON(v);

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Get name from the row (position) clicked and pass it to search for the facebook profile
                Student selectedStudent = (Student)studentAdapter.getItem(position);

                //Extract name from row
                String studentName = selectedStudent.getName();

                Toast.makeText(getApplicationContext(), selectedStudent.getName() + " " +
                        selectedStudent.getEmail(), Toast.LENGTH_LONG).show(); // Just to make sure...

                //Get the person's profile (the bs way)
                profile = Profile.getCurrentProfile(); // MUST BE CORRESPONDING TO THE NAME IN THE ROW!!!
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(profile.getLinkUri().toString()));
                startActivity(browserIntent);

                //Get the person's profile (the right way)
                //Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                //startActivity(profileIntent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    class SearchBackground extends AsyncTask<Void, Void, String> {
        String json_url;
        String cName = searchClass.getText().toString();
        String sName = LoginActivity.facebookName.getText().toString();

        @Override
        protected void onPreExecute() {
            json_url = "http://cecs492beachbuddy.site88.net/searchTest.php";;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String sData = URLEncoder.encode("cName", "UTF-8")+ "=" + URLEncoder.encode(cName, "UTF-8") + "&" +
                        URLEncoder.encode("sName", "UTF-8") + "=" + URLEncoder.encode(sName, "UTF-8");
                bufferedWriter.write(sData);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader((new InputStreamReader(inputStream)));
                StringBuilder stringBuilder = new StringBuilder();
                while((JSON_String = bufferedReader.readLine()) != null) {
                    stringBuilder.append(JSON_String + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            }
            catch(MalformedURLException e) {
                e.printStackTrace();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }

        @Override
        protected void onPostExecute(String result) {
            JSON_String = result;
            try {
                studentAdapter = new StudentAdapter(getBaseContext(), R.layout.row_layout);
                listView.setAdapter(null);
                jsonObject = new JSONObject(JSON_String);
                jsonArray = jsonObject.getJSONArray("students");
                int count = 0;
                String name, email;
                while(count < jsonArray.length()) {
                    JSONObject JO = jsonArray.getJSONObject(count);
                    name = JO.getString("sName");
                    email = JO.getString("sEmail");
                    Student student = new Student(name, email);
                    listView.setAdapter(studentAdapter);
                    studentAdapter.add(student);
                    count++;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void getJSON(View v) {

        new SearchBackground().execute();
    }
}
