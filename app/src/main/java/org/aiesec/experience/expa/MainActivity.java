package org.aiesec.experience.expa;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;


public class MainActivity extends FragmentActivity {

    private ResideMenu resideMenu;
    private ResideMenuItem One;
    private ResideMenuItem Two;
    private ResideMenuItem Three;
    private int frag = 0;

    /**
     * Called to process touch screen events.  You can override this to
     * intercept all touch screen events before they are dispatched to the
     * window.  Be sure to call this implementation for touch screen events
     * that should be handled normally.
     *
     * @param ev The touch screen event.
     * @return boolean Return true if this event was consumed.
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //Initialize ResideMenu
        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.menu_background);
        resideMenu.attachToActivity(this);

        // create menu items;
        String titles[] = { "Profile", "Opp.", "My Opp.", "Settings" };
        int icon[] = {R.drawable.icon_profile, R.drawable.icon_home, R.drawable.icon_calendar, R.drawable.icon_settings };  //todo: change the icon



        One = new ResideMenuItem(this, icon[0], titles[0]);
        One.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new MyProfileFragment());
                frag = 0;
                invalidateOptionsMenu();
                resideMenu.closeMenu();
            }
        });
        resideMenu.addMenuItem(One, ResideMenu.DIRECTION_LEFT);

        Two = new ResideMenuItem(this, icon[1], titles[1]);
        Two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new SearchOpportunityFragment());
                frag = 1;
                invalidateOptionsMenu();
                resideMenu.closeMenu();
            }
        });
        resideMenu.addMenuItem(Two, ResideMenu.DIRECTION_LEFT);

        Three = new ResideMenuItem(this, icon[2], titles[2]);
        Three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new MyOpportunitiesFragment());
                frag = 2;
                invalidateOptionsMenu();
                resideMenu.closeMenu();
            }
        });
        resideMenu.addMenuItem(Three, ResideMenu.DIRECTION_LEFT);

        for (int i = 3; i < titles.length; i++){
            ResideMenuItem item = new ResideMenuItem(this, icon[i], titles[i]);
            item.setOnClickListener(null);
            resideMenu.addMenuItem(item,  ResideMenu.DIRECTION_LEFT); // or  ResideMenu.DIRECTION_RIGHT
        }

        //disable right menu
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

        //Initialize fragment
        if (savedInstanceState == null)
        {
            changeFragment(new MyProfileFragment());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(frag == 2)
            getMenuInflater().inflate(R.menu.menu_my_opportunities, menu);
        else
            getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.newOpportunityMenuItem) {
            Intent intent = new Intent(this, newOpportunityActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void changeFragment(Fragment targetFragment)
    {
        resideMenu.clearIgnoredViewList();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
}
