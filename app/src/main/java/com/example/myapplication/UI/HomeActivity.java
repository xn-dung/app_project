package com.example.myapplication.UI;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.R;
import com.example.myapplication.fragment.AddFoodFragment;
import com.example.myapplication.fragment.DetailFoodFragment;
import com.example.myapplication.fragment.HomeFragment;
import com.example.myapplication.fragment.ProfileFragment;
import com.example.myapplication.fragment.SearchFoodByNameFragment;
import com.example.myapplication.fragment.SearchFragment;
import com.example.myapplication.model.BaiDang;
import com.example.myapplication.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayDeque;
import java.util.Deque;

public class HomeActivity extends AppCompatActivity implements HomeFragment.OnFoodItemSelectedListener,
        SearchFragment.OnSearchByNameClickedListener {

    private static final String TAG_HOME = "HomeFragment";
    private static final String TAG_SEARCH = "SearchFragment";
    private static final String TAG_ADD = "AddFoodFragment";
    private static final String TAG_PROFILE = "ProfileFragment";

    private BottomNavigationView bottomNav;
    private Fragment homeFragment, searchFragment, addFragment, profileFragment;
    private Fragment activeFragment;
    private final Deque<Integer> tabHistory = new ArrayDeque<>();
    private int currentTabId = R.id.menuHome;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_host);

        user = (User) getIntent().getSerializableExtra("user");
        bottomNav = findViewById(R.id.bottomNavView);

        setupFragments();

        bottomNav.setOnItemSelectedListener(item -> {
            int newTabId = item.getItemId();
            if (newTabId == currentTabId) return true;

            if (tabHistory.peekFirst() == null || tabHistory.peekFirst() != currentTabId) {
                tabHistory.offerFirst(currentTabId);
            }
            switchFragment(newTabId, true);
            currentTabId = newTabId;
            return true;
        });

        bottomNav.setSelectedItemId(R.id.menuHome);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // 1. Pop fragments from the internal back stack first (e.g., DetailFoodFragment, SearchFoodByNameFragment)
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                    return;
                }

                // 2. Navigate through the tab history
                Integer previousTab = tabHistory.pollFirst();
                if (previousTab != null) {
                    bottomNav.setSelectedItemId(previousTab);
                    switchFragment(previousTab, false);
                    currentTabId = previousTab;
                    return;
                }
                finish();
            }
        });
    }

    private void setupFragments() {
        FragmentManager fm = getSupportFragmentManager();
        homeFragment = fm.findFragmentByTag(TAG_HOME);
        searchFragment = fm.findFragmentByTag(TAG_SEARCH);
        addFragment = fm.findFragmentByTag(TAG_ADD);
        profileFragment = fm.findFragmentByTag(TAG_PROFILE);

        if (homeFragment == null) homeFragment = HomeFragment.newInstance(user);
        if (searchFragment == null) searchFragment = SearchFragment.newInstance(user);
        if (addFragment == null) addFragment = AddFoodFragment.newInstance(user);
        if (profileFragment == null) profileFragment = ProfileFragment.newInstance(user);

        fm.beginTransaction()
                .add(R.id.fragment_container, homeFragment, TAG_HOME)
                .add(R.id.fragment_container, searchFragment, TAG_SEARCH).hide(searchFragment)

                .add(R.id.fragment_container, addFragment, TAG_ADD).hide(addFragment)
                .add(R.id.fragment_container, profileFragment, TAG_PROFILE).hide(profileFragment)
                .commit();

        activeFragment = homeFragment;
        currentTabId = R.id.menuHome;
    }

    private void switchFragment(int tabId, boolean animate) {
        Fragment target = getFragmentByTabId(tabId);
        if (target == null || target == activeFragment) return;

        FragmentManager fm = getSupportFragmentManager();
        // Clear back stack before switching to a new root tab fragment
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        FragmentTransaction tx = fm.beginTransaction();
        tx.hide(activeFragment).show(target).commit();
        activeFragment = target;
    }

    private Fragment getFragmentByTabId(int tabId) {
        if (tabId == R.id.menuHome) {
            return homeFragment;
        } else if (tabId == R.id.menuSearch) {
            return searchFragment;
        } else if (tabId == R.id.menuAdd) {
            return addFragment;
        } else if (tabId == R.id.menuProfile) {
            return profileFragment;
        }
        return null;
    }

    @Override
    public void onFoodItemSelected(BaiDang baiDang) {
        DetailFoodFragment detailFragment = DetailFoodFragment.newInstance(baiDang, user);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, detailFragment)
                .hide(activeFragment)
                .addToBackStack(DetailFoodFragment.TAG)
                .commit();
    }
    @Override
    public void onSearchByNameClicked(User user) {
        SearchFoodByNameFragment searchFoodByNameFragment = SearchFoodByNameFragment.newInstance(user);
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();

        tx.hide(activeFragment)
                .add(R.id.fragment_container, searchFoodByNameFragment, SearchFoodByNameFragment.TAG)
                .show(searchFoodByNameFragment)
                .addToBackStack(SearchFoodByNameFragment.TAG)
                .commit();
    }
}