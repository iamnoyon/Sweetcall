package com.noyon.sweetcall;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

    }
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()){
                case R.id.navigation_home:
                    Intent home = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(home);
                    break;
                case R.id.navigation_settings:
                    Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(settings);
                    break;
                case R.id.navigation_notifications:
                    Intent notifications = new Intent(MainActivity.this, NotificationsActivity.class);
                    startActivity(notifications);
                    break;
                case R.id.navigation_logout:
                    FirebaseAuth.getInstance().signOut();
                    Intent logout =  new Intent(MainActivity.this,RegistrationActivity.class);
                    startActivity(logout);
                    finish();
                    break;
            }
            return true;
        }
    };

}
