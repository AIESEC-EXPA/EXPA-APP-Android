package org.aiesec.experience.expa;

import android.app.AlertDialog;
import android.content.Intent;
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
    private String token = "3aa355382ac0c9f2cc1ef7510e92496f01f9127c4ecf374a54bbe2b67cc4afb2";
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
    private String _startEndDateTextView;
    private TextView dateOfBirthTextView;
    private TextView introductionTextView;
    private ImageView introductionDetailImagView;
    private RelativeLayout introductionRelativeLayout;
    private TextView phoneTextView;
    private TextView emailTextView;
    private TextView createUpdateDateTextView;

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
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Toast.makeText(getActivity(), "we get a JSONObject:" + response.getJSONObject("person").getString("id"), Toast.LENGTH_LONG).show();
                    user_id = response.getJSONObject("person").getString("id");

                    _currentPositionNameTextView = response.getJSONObject("current_position").getString("position_name");

                    //Format date in RFC3339
                    _startEndDateTextView = Tools.startEndDateStringFromRFC3339(response.getJSONObject("current_position").getString("start_date"), response.getJSONObject("current_position").getString("end_date"));

                    //HTTP request 2
                    client.get(url_2 + user_id + ".json?access_token=" + token, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                Toast.makeText(getActivity(), response.getString("email"), Toast.LENGTH_LONG).show();

                                //Get and set profile photo
                                client.get(response.getJSONObject("profile_photo_urls").getString("original"), new FileAsyncHttpResponseHandler(getActivity()) {
                                    @Override
                                    public void onFailure(int i, Header[] headers, Throwable throwable, File file) {
                                        Log.e(getString(R.string.app_name), "Unable to load profile image.", throwable);
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
                                                    Log.e(getString(R.string.app_name), "Some error occurs when move file", e);
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
                                String gender = response.getString("gender");
                                if (gender.equals("Male"))
                                {
                                    genderImgView.setImageResource(R.drawable.contact_male);
                                }
                                else if (gender.equals("Female"))
                                {
                                    genderImgView.setImageResource(R.drawable.contact_female);
                                }

                                fullNameOfPersonTextView.setText(response.getString("full_name"));
                                fullNameOfCommitteeTextView.setText(response.getJSONObject("current_office").getString("full_name"));
                                currentPositionNameTextView.setText(_currentPositionNameTextView);
                                startEndDateTextView.setText(_startEndDateTextView);
                                dateOfBirthTextView.setText(response.getString("dob"));

                                //IntroductionTextView
                                final String _introductionTextView = response.getString("introduction");
                                if (_introductionTextView.equals("null"))
                                {
                                    introductionTextView.setText("None");
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
                                    //todo: set listener to un-null for outer layout
                                }

                                phoneTextView.setText(response.getJSONObject("contact_info").getString("phone"));
                                emailTextView.setText(response.getString("email"));
                                createUpdateDateTextView.setText("Created At " + Tools.formattedDateStringFromRFC3339(response.getString("created_at")) + " | Updated At " + Tools.formattedDateStringFromRFC3339(response.getString("updated_at")));

                                //TODO: programmesLinearLayout
                            }
                            catch (JSONException e)
                            {
                                Log.e(getString(R.string.app_name), "Some error occurs when extract data from JSON 2", e);
                            }
                            catch (ParseException e)
                            {
                                Log.e(getString(R.string.app_name), "Some error occurs when parse RFC3339 date", e);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.e(getString(R.string.app_name), "Some error occurs when HTTP request 2.", throwable);
                            Log.e(getString(R.string.app_name), "The following is the JSON string:" + errorResponse.toString());
                            //TODO: refresh token
                        }
                    });
                }
                catch (JSONException e)
                {
                    Log.e(getString(R.string.app_name), "Some error occurs when extract data from JSON 1", e);
                }
                catch (ParseException e)
                {
                    Log.e(getString(R.string.app_name), "Some error occurs when parse RFC3339 date", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(getString(R.string.app_name), "Some error occurs when HTTP request 1.", throwable);
                Log.e(getString(R.string.app_name), "The following is the JSON string:" + errorResponse.toString());
                //TODO: refresh token
            }
        });

        return view;
    }
}
