package me.dinuka.guideme.fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.dinuka.guideme.R;
import me.dinuka.guideme.adapter.HotelListingAdapter;
import me.dinuka.guideme.databinding.FragmentHotelsBinding;
import me.dinuka.guideme.model.Hotel;

public class HotelsFragment extends Fragment {

    private FragmentHotelsBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHotelsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Hotel> hotels = new ArrayList<>();
        hotels.add(new Hotel(0, "New Ella Nature View Ella Nature View", 57, "Ella", 4.5, 228, new ArrayList<>(Arrays.asList("https://cf.bstatic.com/xdata/images/hotel/max1024x768/679226546.jpg?k=93984560a6eea303c7a8978fa3884e125c29a03629bd06b5f0e7e20b865853a4&o="))));
        hotels.add(new Hotel(1, "Cinnamon Wild Yala", 64, "Yala", 5, 22, new ArrayList<>(Arrays.asList("https://cf.bstatic.com/xdata/images/hotel/max1024x768/645050529.jpg?k=0ada0d186488560371fe60634ad0d95a4aed8d6114f3e988ac683bbfdd8b9ca2&o="))));
        hotels.add(new Hotel(2, "Cinnamon Life at City of Dreams", 560, "Colombo, Sri Lanka", 4.5, 235, new ArrayList<>(Arrays.asList("https://cf.bstatic.com/xdata/images/hotel/max1024x768/588945741.jpg?k=0c6c7b0fd96d38c70101f6b57ddb2e75eeece3889616d3a28877f5cc3d5fd7ef&o="))));


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.nearHotelSection.hotelsSectionContainer.setLayoutManager(layoutManager);

        HotelListingAdapter adapter = new HotelListingAdapter(hotels, hotel -> {
            Bundle bundle = new Bundle();
            bundle.putString("hotelName", hotel.getName());

            HotelDetailsFragment fragment = new HotelDetailsFragment();
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();

        });

        binding.nearHotelSection.hotelSectionTitle.setText("Near By Hotels");
        binding.nearHotelSection.hotelsSectionContainer.setAdapter(adapter);

        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true){
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }
}