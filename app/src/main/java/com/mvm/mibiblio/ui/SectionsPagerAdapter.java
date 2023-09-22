package com.mvm.mibiblio.ui;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.mvm.mibiblio.adapter.CollectionBookAdapter;
import com.mvm.mibiblio.R;
import com.mvm.mibiblio.ui.booklist.BookListFragment;
import com.mvm.mibiblio.ui.operations.OperationsFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;
    private CollectionBookAdapter.OnItemSelect callback;

    public SectionsPagerAdapter(Context context, FragmentManager fm, CollectionBookAdapter.OnItemSelect callback) {
        super(fm);
        mContext = context;
        this.callback = callback;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            return BookListFragment.newInstance(this.callback, null);
        } else {
            return OperationsFragment.newInstance();
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }
}