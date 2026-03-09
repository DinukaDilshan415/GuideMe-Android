package me.dinuka.guideme.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import me.dinuka.guideme.R;
import me.dinuka.guideme.databinding.ActivityMainBinding;
import me.dinuka.guideme.fragment.HomeFragment;
import me.dinuka.guideme.fragment.MyBookingsFragment;
import me.dinuka.guideme.fragment.ProfileFragment;
import me.dinuka.guideme.fragment.WishlistFragment;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        NavigationBarView.OnItemSelectedListener{

    private ActivityMainBinding binding;
    private BottomNavigationView bottomNavigationView;


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
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d("MainActivity", "onNavigationItemSelected: "+item.getItemId());
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

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}