package com.example.wakeapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import com.example.wakeapp.ui.alarms.AlarmsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.wakeapp.databinding.ActivityMainBinding;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_smart,
                R.id.navigation_alarms,
                R.id.navigation_chrono,
                R.id.navigation_timer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        /*
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container, new AlarmsFragment());
        fragmentTransaction.commit();
*/
        initNotificationChannels();
    }

    // Open the Options Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Open the right Option activity
    public boolean onOptionsItemSelected (MenuItem item){
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener((SharedPreferences.OnSharedPreferenceChangeListener) this);
    }

    private void initNotificationChannels() {
        //creating notification channel only for API 26+ which means android oreo 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            String timerChannelID = "timer_channel";
            CharSequence timerName = getString(R.string.timer_channel);
            NotificationChannel timerChannel =
                    new NotificationChannel(timerChannelID, timerName, importance);

            String alarmChannelID = "alarm_channel";
            CharSequence alarmName = getString(R.string.alarm_channel);
            NotificationChannel alarmChannel =
                    new NotificationChannel(alarmChannelID, alarmName, importance);

            List<NotificationChannel> notificationChannelList = new ArrayList<>();
            notificationChannelList.add(timerChannel);
            notificationChannelList.add(alarmChannel);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            // NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationManager mNotificationManager = getSystemService(NotificationManager.class);

            //mNotificationManager.createNotificationChannel(timerChannel);
            //mNotificationManager.createNotificationChannel(alarmChannel);

            if (mNotificationManager != null){
                mNotificationManager.createNotificationChannels(notificationChannelList);
            }
        }
    }

}