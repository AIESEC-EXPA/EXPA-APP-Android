package org.aiesec.experience.expa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

/**
 * Created by FanQuan on 2015/2/14 0014.
 * This is the Fragment used to display users' basic info.
 */
public class MyProfileFragment extends Fragment {

    private View view;
    private String token = "4989e6425e3e117950c08ead2cb1069c7a3f5564542667c23a433470f7b9f14b";
    private String url_1 = "https://gis-api.aiesec.org/v1/current_person.json";
    private String url_2 = "https://gis-api.aiesec.org:443/v1/people/";
    private String user_id;

    private ImageView profileImgView;
    private ImageView genderImgView;
    private TextView fullNameOfPersonTextView;
    private LinearLayout programmesLinearLayout;
    private TextView fullNameOfCommitteeTextView;
    private TextView currentPositionNameTextView;
    private String _currentPositionNameTextView;    // Make all of views refresh at one time
    private TextView startEndDateTextView;
    private String _startDate;
    private String _endDate;
    private String _startEndDateTextView;
    private TextView dateOfBirthTextView;
    private TextView introductionTextView;
    private ImageView introductionDetailImagView;
    private RelativeLayout introductionRelativeLayout;
    private TextView phoneTextView;
    private TextView emailTextView;
    private TextView createUpdateDateTextView;
    private RelativeLayout seeMoreRelativeLayout;

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(android.os.Bundle)} and {@link #onActivityCreated(android.os.Bundle)}.
     * <p/>
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.my_profile_fragment, container, false);

        profileImgView = (ImageView) view.findViewById(R.id.profileImgView);
        genderImgView = (ImageView) view.findViewById(R.id.genderImgView);
        fullNameOfPersonTextView = (TextView) view.findViewById(R.id.fullNameOfPersonTextView);
        programmesLinearLayout = (LinearLayout) view.findViewById(R.id.programmesLinearLayout);
        fullNameOfCommitteeTextView = (TextView) view.findViewById(R.id.fullNameOfCommitteeTextView);
        currentPositionNameTextView = (TextView) view.findViewById(R.id.currentPositionNameTextView);
        startEndDateTextView = (TextView) view.findViewById(R.id.startEndDateTextView);
        dateOfBirthTextView = (TextView) view.findViewById(R.id.dateOfBirthTextView);
        introductionTextView = (TextView) view.findViewById(R.id.introductionTextView);
        introductionDetailImagView = (ImageView) view.findViewById(R.id.introductionoDetailImgView);
        phoneTextView = (TextView) view.findViewById(R.id.phoneTextView);
        emailTextView = (TextView) view.findViewById(R.id.emailTextView);
        createUpdateDateTextView = (TextView) view.findViewById(R.id.createUpdateDateTextView);
        introductionRelativeLayout = (RelativeLayout) view.findViewById(R.id.introductionRelativeLayout);
        seeMoreRelativeLayout = (RelativeLayout) view.findViewById(R.id.seeMoreRelativeLayout);

        //If cached data exist
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("base", Activity.MODE_PRIVATE);
        final int ID_cached = sharedPreferences.getInt("ID", -1);
        if(ID_cached != -1)
        {
            SQLiteDatabase database = getActivity().openOrCreateDatabase("EXPA.db", Context.MODE_PRIVATE, null);

            String query = "SELECT * FROM persons WHERE ID=?";
            Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(ID_cached)});
            if(cursor.moveToFirst())
            {
                //user data exist
                //profile photo
                if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                {
                    File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/EXPA/profilePhoto");
                    if(dir.exists())
                    {
                        File dest = new File(dir, ID_cached + ".jpg");
                        if(dest.exists())
                        {
                            profileImgView.setImageBitmap(BitmapFactory.decodeFile(dest.getAbsolutePath()));
                        }
                    }
                }

                //gender
                String gender = cursor.getString(cursor.getColumnIndex("gender"));
                if (gender.equals("Male"))
                {
                    genderImgView.setImageResource(R.drawable.contact_male);
                }
                else if (gender.equals("Female"))
                {
                    genderImgView.setImageResource(R.drawable.contact_female);
                }

                fullNameOfPersonTextView.setText(cursor.getString(cursor.getColumnIndex("full_name")));
                fullNameOfCommitteeTextView.setText(cursor.getString(cursor.getColumnIndex("current_committee_name")));
                currentPositionNameTextView.setText(cursor.getString(cursor.getColumnIndex("current_position_name")));
                try
                {
                    startEndDateTextView.setText(Tools.startEndDateStringFromRFC3339(cursor.getString(cursor.getColumnIndex("start_date")), cursor.getString(cursor.getColumnIndex("end_date"))));
                }
                catch (ParseException e)
                {
                    Log.e("EXPA", "Some error occurs when parse rfc3339", e);
                }
                dateOfBirthTextView.setText(cursor.getString(cursor.getColumnIndex("dob")));

                //introduction
                final String _introductionTextView = cursor.getString(cursor.getColumnIndex("introduction"));
                if (_introductionTextView.equals("None"))
                {
                    introductionTextView.setText("None");
                    if(isAdded())
                        introductionTextView.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    introductionDetailImagView.setVisibility(View.INVISIBLE);
                    //introductionRelativeLayout.setOnClickListener(null);
                    introductionRelativeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), IntroductionActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("introduction", _introductionTextView);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });
                }
                else
                {
                    introductionTextView.setText(_introductionTextView);
                    if(isAdded())
                        introductionTextView.setTextColor(getResources().getColor(android.R.color.black));
                    introductionDetailImagView.setVisibility(View.VISIBLE);
                    introductionDetailImagView.setImageResource(R.drawable.icon_more);
                    introductionRelativeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), IntroductionActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("introduction", _introductionTextView);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });
                }

                phoneTextView.setText(cursor.getString(cursor.getColumnIndex("phone")));
                emailTextView.setText(cursor.getString(cursor.getColumnIndex("email")));

                try
                {
                    createUpdateDateTextView.setText("Created At " + Tools.formattedDateStringFromRFC3339(cursor.getString(cursor.getColumnIndex("created_at"))) + " | Updated At " + Tools.formattedDateStringFromRFC3339(cursor.getString(cursor.getColumnIndex("updated_at"))));
                }
                catch (ParseException e)
                {
                    Log.e("EXPA", "Some error occurs when parse rfc3339", e);
                }
            }

            query = "SELECT * FROM programmes WHERE user_ID=?";
            cursor = database.rawQuery(query, new String[]{String.valueOf(ID_cached)});
            programmesLinearLayout.removeAllViews();
            while(cursor.moveToNext())
            {
                Tools.addProgrammeItem(programmesLinearLayout, cursor.getString(cursor.getColumnIndex("short_name")), getActivity());
            }
            cursor.close();

            seeMoreRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try
                    {
                        Intent intent = new Intent(getActivity(), PositionsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("ID", String.valueOf(ID_cached));
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                    catch(Exception e)
                    {
                        Log.e("EXPA", "aaaaaaaaaaaaaa", e);
                    }
                }
            });
        }


        //HTTP request
        //Check network status first
        if (!Tools.isNetworkAvailable(getActivity()))
        {
            Toast.makeText(getActivity(), "No Network Available!", Toast.LENGTH_LONG).show();
            return view;
        }

        //If network is available, process the HTTP request 1
        final AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        client.setResponseTimeout(30 * 1000);
        //client.setConnectTimeout(20 * 1000);
        client.setMaxRetriesAndTimeout(5, 1000);

        client.get(url_1 + "?access_token=" + token, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response1) {
                try {
                    Toast.makeText(getActivity(), "we get a JSONObject:" + response1.getJSONObject("person").getString("id"), Toast.LENGTH_SHORT).show();
                    user_id = response1.getJSONObject("person").getString("id");

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("ID", Integer.valueOf(user_id));
                    editor.apply();

                    _currentPositionNameTextView = response1.getJSONObject("current_position").getString("position_name");

                    //Format date in RFC3339
                    _startDate = response1.getJSONObject("current_position").getString("start_date");
                    _endDate = response1.getJSONObject("current_position").getString("end_date");
                    _startEndDateTextView = Tools.startEndDateStringFromRFC3339(_startDate, _endDate);

                    //HTTP request 2
                    client.get(url_2 + user_id + ".json?access_token=" + token, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response2) {
                            try {
                                Toast.makeText(getActivity(), response2.getString("email"), Toast.LENGTH_SHORT).show();

                                //Get and set profile photo
                                client.get(response2.getJSONObject("profile_photo_urls").getString("original"), new FileAsyncHttpResponseHandler(getActivity()) {
                                    @Override
                                    public void onFailure(int i, Header[] headers, Throwable throwable, File file) {
                                        Toast.makeText(getActivity(), "Unable to load profile image.", Toast.LENGTH_SHORT).show();
                                        Log.e("EXPA", "Unable to load profile image.", throwable);
                                    }

                                    @Override
                                    public void onSuccess(int i, Header[] headers, File file) {
                                        //Set profileImgView's image
                                        profileImgView.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));

                                        //Save profile photo into SDCard
                                        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                                        {
                                            File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/EXPA/profilePhoto");
                                            if (!dir.exists())
                                            {
                                                dir.mkdirs();
                                            }
                                            File dest = new File(dir, user_id + ".jpg");
                                            if (!dest.exists())
                                            {
                                                try {
                                                    dest.createNewFile();
                                                    Tools.moveFile(file, dest);
                                                }
                                                catch (IOException e)
                                                {
                                                    Log.e("EXPA", "Some error occurs when move file", e);
                                                }
                                            }
                                        }
                                        else
                                        {
                                            new AlertDialog.Builder(getActivity())
                                                    //.setIcon(android.R.drawable.ic_dialog_alert)
                                                    .setTitle("SDCard Unavailable!")
                                                    .setMessage("We can't cache data without SDCard.")
                                                    .setPositiveButton("I know", null)
                                                    .show();
                                        }
                                    }
                                });

                                //decide which gender pic to display
                                String gender = response2.getString("gender");
                                if (gender.equals("Male"))
                                {
                                    genderImgView.setImageResource(R.drawable.contact_male);
                                }
                                else if (gender.equals("Female"))
                                {
                                    genderImgView.setImageResource(R.drawable.contact_female);
                                }

                                //programmes
                                programmesLinearLayout.removeAllViews();
                                JSONArray programmes = response2.getJSONArray("programmes");
                                for(int i = 0; i < programmes.length(); i++)
                                {
                                    Tools.addProgrammeItem(programmesLinearLayout, ((JSONObject) programmes.get(i)).getString("short_name"), getActivity());
                                }

                                fullNameOfPersonTextView.setText(response2.getString("full_name"));
                                fullNameOfCommitteeTextView.setText(response2.getJSONObject("current_office").getString("full_name"));
                                currentPositionNameTextView.setText(_currentPositionNameTextView);
                                startEndDateTextView.setText(_startEndDateTextView);
                                dateOfBirthTextView.setText(response2.getString("dob"));

                                //IntroductionTextView
                                final String _introductionTextView = response2.getString("introduction");
                                if (_introductionTextView.equals("null"))
                                {
                                    introductionTextView.setText("None");
                                    if(isAdded())
                                        introductionTextView.setTextColor(getResources().getColor(android.R.color.darker_gray));
                                    introductionDetailImagView.setVisibility(View.INVISIBLE);
                                    //introductionRelativeLayout.setOnClickListener(null);
                                    introductionRelativeLayout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(getActivity(), IntroductionActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("introduction", _introductionTextView);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        }
                                    });
                                }
                                else
                                {
                                    introductionTextView.setText(_introductionTextView);
                                    if(isAdded())
                                        introductionTextView.setTextColor(getResources().getColor(android.R.color.black));
                                    introductionDetailImagView.setVisibility(View.VISIBLE);
                                    introductionDetailImagView.setImageResource(R.drawable.icon_more);
                                    introductionRelativeLayout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(getActivity(), IntroductionActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("introduction", _introductionTextView);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        }
                                    });
                                }

                                phoneTextView.setText(response2.getJSONObject("contact_info").getString("phone"));
                                emailTextView.setText(response2.getString("email"));
                                createUpdateDateTextView.setText("Created At " + Tools.formattedDateStringFromRFC3339(response2.getString("created_at")) + " | Updated At " + Tools.formattedDateStringFromRFC3339(response2.getString("updated_at")));

                                //save to SQLite
                                SQLiteDatabase database = getActivity().openOrCreateDatabase("EXPA.db", Context.MODE_PRIVATE, null);

                                //persons
                                String query = "CREATE TABLE IF NOT EXISTS persons(ID INTEGER PRIMARY KEY, first_name TEXT, last_name TEXT, full_name TEXT, dob TEXT, introduction TEXT, gender TEXT, email TEXT, phone TEXT, current_position_name TEXT, current_committee_name TEXT, start_date TEXT, end_date TEXT, created_at TEXT, updated_at TEXT)";
                                database.execSQL(query);

                                query = "DELETE FROM persons WHERE ID=?";
                                database.execSQL(query, new Object[]{user_id});

                                query = "INSERT INTO persons(ID, first_name, last_name, full_name, dob, introduction, gender, email, phone, current_position_name, current_committee_name, start_date, end_date, created_at, updated_at) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                                Object params1[] = {user_id, response2.getString("first_name"), response2.getString("last_name"), response2.getString("full_name"), dateOfBirthTextView.getText(), introductionTextView.getText(), gender, response2.getString("email"), phoneTextView.getText(), _currentPositionNameTextView, fullNameOfCommitteeTextView.getText(), _startDate, _endDate, response2.getString("created_at"), response2.getString("updated_at")};
                                database.execSQL(query, params1);

                                //positions
                                query = "CREATE TABLE IF NOT EXISTS positions(user_ID INTEGER, position_ID INTEGER PRIMARY KEY, position_name TEXT, start_date TEXT, end_date TEXT, team_ID INTEGER, team_title TEXT)";
                                database.execSQL(query);

                                query = "DELETE FROM positions WHERE user_ID=?";
                                database.execSQL(query, new Object[]{user_id});

                                query = "INSERT INTO positions(user_ID, position_ID, position_name, start_date, end_date, team_ID, team_title) VALUES(?,?,?,?,?,?,?)";
                                JSONArray positions = response2.getJSONArray("positions");
                                for(int i = 0; i < positions.length(); i++)
                                {
                                    JSONObject t = (JSONObject) positions.get(i);
                                    database.execSQL(query, new Object[]{user_id, t.getString("id"), t.getString("position_name"), t.getString("start_date"), t.getString("end_date"), t.getJSONObject("team").getString("id"), t.getJSONObject("team").getString("title")});
                                }

                                seeMoreRelativeLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getActivity(), PositionsActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("ID", user_id);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                });

                                //programmes
                                query = "CREATE TABLE IF NOT EXISTS programmes(user_ID INTEGER, programme_ID INTEGER, short_name TEXT)";
                                database.execSQL(query);

                                query = "DELETE FROM programmes WHERE user_ID=?";
                                database.execSQL(query, new Object[]{user_id});

                                query = "INSERT INTO programmes(user_ID, programme_ID, short_name) VALUES(?,?,?)";
                                for(int i = 0; i < programmes.length(); i++)
                                {
                                    JSONObject t = (JSONObject) programmes.get(i);
                                    database.execSQL(query, new Object[]{user_id, t.getString("id"), t.getString("short_name")});
                                }

                                database.close();
                            }
                            catch (SQLException e)
                            {
                                Log.e("EXPA", "Some error occurs when save to SQLite", e);
                            }
                            catch (JSONException e)
                            {
                                Log.e("EXPA", "Some error occurs when extract data from JSON 2", e);
                            }
                            catch (ParseException e)
                            {
                                Log.e("EXPA", "Some error occurs when parse RFC3339 date", e);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.e("EXPA", "Some error occurs when HTTP request 2.", throwable);
                            //Log.e("EXPA", "The following is the JSON string:" + errorResponse.toString());
                            //TODO: refresh token
                        }
                    });
                }
                catch (JSONException e)
                {
                    Log.e("EXPA", "Some error occurs when extract data from JSON 1", e);
                }
                catch (ParseException e)
                {
                    Log.e("EXPA", "Some error occurs when parse RFC3339 date", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("EXPA", "Some error occurs when HTTP request 1.", throwable);
                //Log.e("EXPA", "The following is the JSON string:" + errorResponse.toString());
                //TODO: refresh token
            }
        });

        getActivity().setTitle("My Profile");

        return view;
    }
}
