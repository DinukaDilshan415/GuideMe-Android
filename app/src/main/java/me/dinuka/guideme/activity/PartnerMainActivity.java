package me.dinuka.guideme.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import me.dinuka.guideme.R;
import me.dinuka.guideme.databinding.ActivityPartnerMainBinding;
import me.dinuka.guideme.fragment.HomeFragment;
import me.dinuka.guideme.fragment.MyBookingsFragment;
import me.dinuka.guideme.fragment.PartnerHomeFragment;
import me.dinuka.guideme.fragment.PartnerNotificationsFragment;
import me.dinuka.guideme.fragment.PartnerProfileFragment;
import me.dinuka.guideme.fragment.ProfileFragment;
import me.dinuka.guideme.fragment.WishlistFragment;

public class PartnerMainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        NavigationBarView.OnItemSelectedListener{

    private ActivityPartnerMainBinding binding;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPartnerMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bottomNavigationView = binding.partnerBottomNavigationView;

        bottomNavigationView.setOnItemSelectedListener(this);

        if (savedInstanceState == null) {
            loadFragment(new PartnerHomeFragment());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Menu bottomNavMenu = bottomNavigationView.getMenu();

        for (int i = 0; i < bottomNavMenu.size(); i++) {
            bottomNavMenu.getItem(i).setChecked(false);
        }

        if (item.getItemId() == R.id.partner_bottom_nav_home) {
            loadFragment(new PartnerHomeFragment());
            bottomNavigationView.getMenu().findItem(R.id.partner_bottom_nav_home).setChecked(true);
        } else if (item.getItemId() == R.id.partner_bottom_nav_notifications) {
            loadFragment(new PartnerNotificationsFragment());
            bottomNavigationView.getMenu().findItem(R.id.partner_bottom_nav_notifications).setChecked(true);
        } else if (item.getItemId() == R.id.partner_bottom_nav_profile) {
            loadFragment(new PartnerProfileFragment());
            bottomNavigationView.getMenu().findItem(R.id.partner_bottom_nav_profile).setChecked(true);
        }

        return true;
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.partner_fragment_container, fragment)
                .commit();
    }
}