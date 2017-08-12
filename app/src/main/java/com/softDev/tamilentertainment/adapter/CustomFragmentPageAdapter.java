package com.softDev.tamilentertainment.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.softDev.tamilentertainment.fragment.FavouritesFragment;
import com.softDev.tamilentertainment.fragment.PlayerFragment;
import com.softDev.tamilentertainment.fragment.StationsFragment;

public class CustomFragmentPageAdapter extends FragmentPagerAdapter{

    private static final String TAG = CustomFragmentPageAdapter.class.getSimpleName();

    private static final int FRAGMENT_COUNT = 3;

    public CustomFragmentPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new PlayerFragment();
            case 1:
                return new StationsFragment();
            case 2:
                return new FavouritesFragment();
            /*case 1:
                return new PlaylistFragment();
            case 2:
                return new AlbumFragment();
            case 3:
                return new ArtistFragment();*/
        }
        return null;
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "FM Player";
            case 1:
                return "Stations";
            case 2:
                return "Favourites";

        }
        return null;
    }
}
