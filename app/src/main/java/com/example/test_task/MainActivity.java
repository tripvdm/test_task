package com.example.test_task;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.test_task.fragment.ContactListFragment;
import com.example.test_task.fragment.StatisticFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private ActionBar actionBar;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.navigation)
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        actionBar = getSupportActionBar();
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.content, new ContactListFragment(false))
                    .commit();
        }
        bottomNavigationView.setOnItemSelectedListener(selectedListener);
    }

    private final NavigationBarView.OnItemSelectedListener selectedListener = new NavigationBarView.OnItemSelectedListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction fragmentTransaction;
            switch (menuItem.getItemId()) {
                case R.id.list:
                    actionBar.setTitle(R.string.app_name);
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.content, new ContactListFragment(false));
                    fragmentTransaction.commit();
                    return true;
                case R.id.delete:
                    actionBar.setTitle(R.string.titleDelete);
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.content, new ContactListFragment(true));
                    fragmentTransaction.commit();
                    return true;
                case R.id.statistic:
                    actionBar.setTitle(R.string.titleStatistic);
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.content, new StatisticFragment());
                    fragmentTransaction.commit();
                    return true;
            }
            return false;
        }
    };

}