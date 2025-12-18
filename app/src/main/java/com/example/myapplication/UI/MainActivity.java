package com.example.myapplication.UI;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.R;
import com.example.myapplication.fragment.LoginFragment;
import com.example.myapplication.interfaces.NavigationHost;

public class  MainActivity extends AppCompatActivity implements NavigationHost {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_host);

        if (savedInstanceState == null) {
            navigateTo(new LoginFragment(), false);
        }
    }

    @Override
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
}



