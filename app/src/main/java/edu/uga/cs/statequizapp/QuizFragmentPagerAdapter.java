package edu.uga.cs.statequizapp;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class QuizFragmentPagerAdapter extends FragmentStateAdapter {

    public QuizFragmentPagerAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @Override
    public Fragment createFragment(int position) {
        if (position < 6) {
            return QuizSwipeFragment.newInstance(position);
        } else {
            return new QuizCompletionFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 7; // 6 quiz swipes + 1 completion swipe
    }
}
