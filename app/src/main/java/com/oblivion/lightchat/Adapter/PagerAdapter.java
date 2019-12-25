package com.oblivion.lightchat.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


import com.oblivion.lightchat.Fragment.KontakFragment;
import com.oblivion.lightchat.Fragment.PesanFragment;

public class PagerAdapter extends FragmentPagerAdapter {

   private int numberTab;

    public PagerAdapter(@NonNull FragmentManager fm, int numberTab) {
        super(fm, numberTab);
        this.numberTab = numberTab;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new PesanFragment();
            case 1:
                return new KontakFragment();
                default:
                    return null;

        }


    }

    @Override
    public int getCount() {
        return numberTab;
    }
}
