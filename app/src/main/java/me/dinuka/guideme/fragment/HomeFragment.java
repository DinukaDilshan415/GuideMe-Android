package me.dinuka.guideme.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import me.dinuka.guideme.R;
import me.dinuka.guideme.adapter.MenuAdapter;
import me.dinuka.guideme.databinding.CategoryMenusBinding;
import me.dinuka.guideme.databinding.FragmentHomeBinding;
import me.dinuka.guideme.model.MenuCategory;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private DrawerLayout drawerLayout;
    private EditText searchBar;
    private CategoryMenusBinding categoryMenusBinding;
    private static final String APP_DATA = "my_current_data";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences currentsharedPreferences = requireContext().getSharedPreferences(APP_DATA, Context.MODE_PRIVATE);
        String currentlocationName = currentsharedPreferences.getString("locationName", null);
        float currentlatitude = currentsharedPreferences.getFloat("latitude", 0);
        float currentlongitude = currentsharedPreferences.getFloat("longitude", 0);

        if (currentlocationName != null && currentlatitude != 0 && currentlongitude != 0){
            binding.locationTag.setText(currentlocationName);
        }

        getParentFragmentManager().setFragmentResultListener("requestKey", getViewLifecycleOwner(), (requestKey, bundle) -> {
            String locationName = bundle.getString("locationName");
            double latitude = bundle.getDouble("latitude");
            double longitude = bundle.getDouble("longitude");

            if (locationName != null && latitude != 0 && longitude != 0){
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences(APP_DATA, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("locationName", locationName);
                editor.putFloat("latitude", (float) latitude);
                editor.putFloat("longitude", (float) longitude);
                editor.apply();
                Log.i("HomeFragment", "Loacation Details Saved in SharedPreferences");
            }

            binding.locationTag.setText(locationName);
        });

        drawerLayout = binding.homeDrawerLayout;
        ImageView searchIcon = binding.searchIconOnHome;
        ImageView notificationIcon = binding.notificationIconOnHome;
        searchBar = binding.textInputSearch;

        notificationIcon.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            } else {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        searchIcon.setOnClickListener(v -> {
            if (searchBar.getVisibility() == View.VISIBLE) {
                searchBar.setVisibility(View.GONE);
            } else {
                searchBar.setVisibility(View.VISIBLE);
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                callback.setEnabled(true);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                callback.setEnabled(false);
            }
        });

        createCategoryMenu();

        binding.changeLocationBtn.setOnClickListener(v -> {
            CustomDialogFragment dialog = new CustomDialogFragment();
            if (currentlocationName != null && currentlatitude != 0 && currentlongitude != 0){
                dialog = new CustomDialogFragment(currentlocationName, currentlatitude, currentlongitude);
            }
            dialog.show(getParentFragmentManager(), "ShareDialog");
        });

    }

    private void createCategoryMenu() {
        List<MenuCategory> categories = new ArrayList<>();
        categories.add(new MenuCategory(0, "Hotels", R.drawable.hotel_svgrepo_com));
        categories.add(new MenuCategory(1, "Things To Do", R.drawable.things_to_do));
        categories.add(new MenuCategory(2, "Services", R.drawable.service_svgrepo_com));

        RecyclerView recyclerView = binding.categoryMenuSection.categoryMenuContainer;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        MenuAdapter adapter = new MenuAdapter(categories, (item, position) -> {
            loadFragment(item.getId());
        });

        recyclerView.setAdapter(adapter);

        loadFragment(0);
    }

    private void loadFragment(int id) {
        Fragment selectedFragment;

        switch (id) {
            case 0:
                selectedFragment = new HotelsFragment();
                break;
            case 1:
                selectedFragment = new ThingsToDoFragment();
                break;
            case 2:
                selectedFragment = new ServicesFragment();
                break;
            default:
                selectedFragment = new HotelsFragment();
        }

        getChildFragmentManager().beginTransaction()
                .replace(R.id.category_fragment_container, selectedFragment)
                .commit();
    }
}