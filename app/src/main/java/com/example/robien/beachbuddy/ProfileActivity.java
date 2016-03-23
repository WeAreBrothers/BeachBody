package com.example.robien.beachbuddy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Robien on 3/20/2016.
 */
public class ProfileActivity extends AppCompatActivity {

    private CallbackManager callbackManager;

    TextView name, email, gender;
    Button createGroup, addToGroup, sendMessage, viewProfile;
    private ProfilePictureView profilePictureView;
    URL img_url;
    Bitmap bmp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        profilePictureView = (ProfilePictureView)findViewById(R.id.profilePic);
        name = (TextView)findViewById(R.id.nameText);
        email = (TextView)findViewById(R.id.emailText);
        gender = (TextView)findViewById(R.id.genderText);
        createGroup = (Button)findViewById(R.id.createGroup);
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        addToGroup = (Button)findViewById(R.id.sendInvite);
        addToGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        sendMessage = (Button)findViewById(R.id.sendMessage);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        viewProfile = (Button)findViewById(R.id.viewProfile);
        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        try {
            img_url = new URL("https://graph.facebook.com/" + NavigationActivity.ID + "/picture");
            bmp = BitmapFactory.decodeStream(img_url.openConnection().getInputStream());
            profilePictureView.setDefaultProfilePicture(bmp);
            profilePictureView.setPresetSize(ProfilePictureView.NORMAL);
            profilePictureView.setVisibility(View.VISIBLE);

            GraphRequest request = GraphRequest.newMeRequest
                    (AccessToken.getCurrentAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    try {
                                        name.setText(object.getString("name"));
                                        email.setText(object.getString("email"));
                                        gender.setText(object.getString("gender"));
                                        name.setVisibility(View.VISIBLE);
                                        gender.setVisibility(View.VISIBLE);
                                        email.setVisibility(View.VISIBLE);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender");
            request.setParameters(parameters);
            request.executeAsync();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void setProfileToView(JSONObject jsonObject) {
        try {
            name.setText(jsonObject.getString("name"));
            email.setText(jsonObject.getString("email"));
            gender.setText(jsonObject.getString("gender"));

            profilePictureView.setPresetSize(ProfilePictureView.NORMAL);
            profilePictureView.setProfileId(jsonObject.getString("id"));
            //relLayout.setVisibility(View.INVISIBLE);
            //infoLayout.setVisibility(View.VISIBLE);
            //classButt.setVisibility(View.VISIBLE);
            //searchButt.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }}
}

