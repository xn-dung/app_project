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
import com.example.myapplication.fragment.FoundFoodFragment;
import com.example.myapplication.fragment.HomeFragment;
import com.example.myapplication.fragment.ProfileFragment;
import com.example.myapplication.fragment.ReelFragment;
import com.example.myapplication.fragment.SearchFoodByNameFragment;
import com.example.myapplication.fragment.SearchFragment;
import com.example.myapplication.interfaces.NavigationHost;
import com.example.myapplication.model.BaiDang;
import com.example.myapplication.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public class HomeActivity extends AppCompatActivity implements HomeFragment.OnFoodItemSelectedListener,
        SearchFragment.OnSearchByNameClickedListener, NavigationHost {

    private static final String TAG_HOME = "HomeFragment";
    private static final String TAG_SEARCH = "SearchFragment";
    private static final String TAG_ADD = "AddFoodFragment";
    private static final String TAG_PROFILE = "ProfileFragment";
    private static final String TAG_REEL = "ReelFragment";


    private BottomNavigationView bottomNav;
    private Fragment homeFragment, searchFragment, addFragment, profileFragment, reelFragment;
    private Fragment activeFragment;
    private final Deque<Integer> tabHistory = new ArrayDeque<>();
    private int currentTabId = R.id.menuHome;

    private User user;
    private boolean isBackNavigation = false;

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

            if (!isBackNavigation) {
                if (tabHistory.isEmpty() || tabHistory.peekFirst() != currentTabId) {
                    tabHistory.push(currentTabId);
                }
            }
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            switchFragment(newTabId, true);
            currentTabId = newTabId;
            return true;
        });
        String tab = getIntent().getStringExtra("tab");
        if("profile".equals(tab)){
            bottomNav.setSelectedItemId(R.id.menuProfile);
        }
        else bottomNav.setSelectedItemId(R.id.menuHome);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                    activeFragment = getFragmentByTabId(currentTabId);
                    return;
                }

                if (!tabHistory.isEmpty()) {
                    Integer previousTab = tabHistory.pop();
                    while (previousTab != null && previousTab == bottomNav.getSelectedItemId() && !tabHistory.isEmpty()) {
                        previousTab = tabHistory.pop();
                    }

                    if (previousTab != null && previousTab != bottomNav.getSelectedItemId()) {
                        isBackNavigation = true;
                        bottomNav.setSelectedItemId(previousTab);
                        isBackNavigation = false;
                        return;
                    }
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
        reelFragment = fm.findFragmentByTag(TAG_REEL);

        FragmentTransaction tx = fm.beginTransaction();

        if (homeFragment == null) {
            homeFragment = HomeFragment.newInstance(user);
            tx.add(R.id.fragment_container, homeFragment, TAG_HOME);
        }
        if (searchFragment == null) {
            searchFragment = SearchFragment.newInstance(user);
            tx.add(R.id.fragment_container, searchFragment, TAG_SEARCH).hide(searchFragment);
        }
        if (addFragment == null) {
            addFragment = AddFoodFragment.newInstance(user);
            tx.add(R.id.fragment_container, addFragment, TAG_ADD).hide(addFragment);
        }
        if (profileFragment == null) {
            profileFragment = ProfileFragment.newInstance(user);
            tx.add(R.id.fragment_container, profileFragment, TAG_PROFILE).hide(profileFragment);
        }
        if (reelFragment == null) {
            reelFragment = ReelFragment.newInstance(user);
            tx.add(R.id.fragment_container, reelFragment, TAG_REEL).hide(reelFragment);
        }

        tx.commit();

        activeFragment = homeFragment;
        currentTabId = R.id.menuHome;
    }

    private void switchFragment(int tabId, boolean animate) {
        Fragment target = getFragmentByTabId(tabId);
        if (target == null || target == activeFragment) return;

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
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
        else if (tabId == R.id.menuReels) {
            return reelFragment;
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

        tx.add(R.id.fragment_container, searchFoodByNameFragment, SearchFoodByNameFragment.TAG)
                .hide(activeFragment)
                .addToBackStack(SearchFoodByNameFragment.TAG)
                .commit();

        activeFragment = searchFoodByNameFragment;

    }

    public void navigateTo(Fragment fragment, boolean addToBackstack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (addToBackstack) {
            fragmentTransaction.replace(R.id.fragment_container, fragment).addToBackStack(null);
        } else {
            fragmentTransaction.replace(R.id.fragment_container, fragment);
        }
        fragmentTransaction.commit();
    }

    public void openFoundFoodFragment(ArrayList<BaiDang> bd, User user, int type) {
        FoundFoodFragment fragment = FoundFoodFragment.newInstance(bd, user,type);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, fragment, "FoundFood")
                .hide(activeFragment)
                .addToBackStack("FoundFood")
                .commit();

        activeFragment = fragment;
    }
    public void linkFoundFoodFragment(String search, User user, int type){
        FoundFoodFragment fragment = FoundFoodFragment.newInstance2(search, user,type);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, fragment, "FoundFood")
                .hide(activeFragment)
                .addToBackStack("FoundFood")
                .commit();

        activeFragment = fragment;

    }

}