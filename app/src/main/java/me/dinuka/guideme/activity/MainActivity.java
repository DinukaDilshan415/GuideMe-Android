package me.dinuka.guideme.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import me.dinuka.guideme.R;
import me.dinuka.guideme.databinding.ActivityMainBinding;
import me.dinuka.guideme.fragment.CustomCompassFragment;
import me.dinuka.guideme.fragment.HomeFragment;
import me.dinuka.guideme.fragment.MyBookingsFragment;
import me.dinuka.guideme.fragment.ProfileFragment;
import me.dinuka.guideme.fragment.WishlistFragment;
import me.dinuka.guideme.helper.ShakeDetector;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        NavigationBarView.OnItemSelectedListener {

    private ActivityMainBinding binding;
    private BottomNavigationView bottomNavigationView;
    private static final String TAG = MainActivity.class.getSimpleName();

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Notifications permission rejected", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bottomNavigationView = binding.bottomNavigationView;

        bottomNavigationView.setOnItemSelectedListener(this);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        checkAndRequestNotificationsPermission();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        Log.d(TAG, "Token: " + token);
                    }
                });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();

        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake() {
                showDialog();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d("MainActivity", "onNavigationItemSelected: " + item.getItemId());
        Menu bottomNavMenu = bottomNavigationView.getMenu();

        for (int i = 0; i < bottomNavMenu.size(); i++) {
            bottomNavMenu.getItem(i).setChecked(false);
        }

        if (item.getItemId() == R.id.bottom_nav_home) {
            loadFragment(new HomeFragment());
            bottomNavigationView.getMenu().findItem(R.id.bottom_nav_home).setChecked(true);
        } else if (item.getItemId() == R.id.bottom_nav_bookings) {
            loadFragment(new MyBookingsFragment());
            bottomNavigationView.getMenu().findItem(R.id.bottom_nav_bookings).setChecked(true);
        } else if (item.getItemId() == R.id.bottom_nav_wishlist) {
            loadFragment(new WishlistFragment());
            bottomNavigationView.getMenu().findItem(R.id.bottom_nav_wishlist).setChecked(true);
        } else if (item.getItemId() == R.id.bottom_nav_profile) {
            loadFragment(new ProfileFragment());
            bottomNavigationView.getMenu().findItem(R.id.bottom_nav_profile).setChecked(true);
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void checkAndRequestNotificationsPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void showDialog() {
        CustomCompassFragment dialog = new CustomCompassFragment();
        dialog.show(getSupportFragmentManager(), "CompassDialog");

    }
}