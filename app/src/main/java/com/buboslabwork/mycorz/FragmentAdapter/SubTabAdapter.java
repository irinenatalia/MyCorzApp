package com.buboslabwork.mycorz.FragmentAdapter;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.buboslabwork.mycorz.CompletedClass;
import com.buboslabwork.mycorz.OngoingClassFragment;

import java.util.List;

public class SubTabAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "In Progress", "Completed"};
    String ongoingJSON,completedJSON,mentor_username;
    private Context context;

    public SubTabAdapter(FragmentManager fm, Context context, String ongoingJSON, String completedJSON, String mentor_username) {
        super(fm);
        this.context = context;
        this.ongoingJSON = ongoingJSON;
        this.completedJSON = completedJSON;
        this.mentor_username = mentor_username;
        Log.v("Ongoing JSON",ongoingJSON);
        Log.v("Completed JSON",completedJSON);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = null;
        switch(position){
            case 0:
            {
                f = new OngoingClassFragment();
                // set arguments here, if required
                Bundle args = new Bundle();
                args.putString("ONGOING_JSON", ongoingJSON);
                args.putString("mentor", mentor_username);
                f.setArguments(args);

                break;
            }
            case 1:
            {
                f = new CompletedClass();
                // set arguments here, if required
                Bundle args = new Bundle();
                args.putString("COMPLETED_JSON", completedJSON);
                args.putString("mentor", mentor_username);
                f.setArguments(args);
                break;
            }
            default:
                throw new IllegalArgumentException("not this many fragments: " + position);
        }
        return f;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

}

