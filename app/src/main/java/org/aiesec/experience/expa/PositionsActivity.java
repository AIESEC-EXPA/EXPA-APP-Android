package org.aiesec.experience.expa;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PositionsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.positions_activity);

        try
        {
            String ID = getIntent().getExtras().getString("ID");
            SQLiteDatabase database = openOrCreateDatabase("EXPA.db", MODE_PRIVATE, null);
            String query = "SELECT * FROM positions WHERE user_ID=?";
            Cursor cursor = database.rawQuery(query, new String[]{ID});

            ListView listView = (ListView) findViewById(R.id.positionsListView);

            List<Map<String, Object>> list = new ArrayList<>();
            while(cursor.moveToNext())
            {
                Map<String, Object> map = new HashMap<>();
                map.put("position_name", cursor.getString(cursor.getColumnIndex("position_name")));
                map.put("team_title", cursor.getString(cursor.getColumnIndex("team_title")));
                try
                {
                    map.put("startEndDate", Tools.startEndDateStringFromRFC3339(cursor.getString(cursor.getColumnIndex("start_date")), cursor.getString(cursor.getColumnIndex("end_date"))));
                }
                catch (ParseException e)
                {
                    Log.e(getString(R.string.app_name), "some error occurs when parse RFC3339", e);
                }
                list.add(map);
            }
            cursor.close();
            database.close();
            SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.positions_listview_item, new String[]{"position_name", "team_title", "startEndDate"}, new int[]{R.id.positionNameTextView, R.id.teamTitleTextView, R.id.startEndDateTextView});
            listView.setAdapter(adapter);
        }
        catch (Exception e)
        {
            Log.e(getString(R.string.app_name), "aaaaaaaaaaaaaaaaa", e);
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_positions, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
