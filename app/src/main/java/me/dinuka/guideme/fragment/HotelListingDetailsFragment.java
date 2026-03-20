package me.dinuka.guideme.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.dinuka.guideme.R;
import me.dinuka.guideme.adapter.FacilityAdapter;
import me.dinuka.guideme.adapter.FacilitySelectAdapter;
import me.dinuka.guideme.adapter.RoomTypeAdapter;
import me.dinuka.guideme.api.FacilityApi;
import me.dinuka.guideme.client.RetrofitClient;
import me.dinuka.guideme.databinding.FragmentHotelListingDetailsBinding;
import me.dinuka.guideme.dto.FacilityDTO;
import me.dinuka.guideme.model.Facility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HotelListingDetailsFragment extends Fragment {

    private FragmentHotelListingDetailsBinding binding;
    private List<String> roomTypes = new ArrayList<>();
    private RoomTypeAdapter roomAdapter;
    private RecyclerView rvRoomTypes;
    private EditText etRoomInput;
    private int roomCount = 1;
    private int adultCount = 1;
    List<Facility> facilityList;
    private List<FacilityDTO> facilityDTOS;
    HashMap<String, Object> commanListingData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            commanListingData = (HashMap<String, Object>) getArguments().getSerializable("listingData");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHotelListingDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupCounters();

        facilityList = new ArrayList<>();

        binding.facilitySelectContainer.setOnClickListener(v -> {
            if (binding.facilitySelectRecyclerView.getVisibility() == View.GONE) {
                binding.facilitySelectRecyclerView.setVisibility(View.VISIBLE);
                binding.facilityTitleArrow.setRotation(0);
            } else {
                binding.facilitySelectRecyclerView.setVisibility(View.GONE);
                binding.facilityTitleArrow.setRotation(180);
            }
        });

        etRoomInput = binding.etRoomTypeInput;
        rvRoomTypes = binding.recyclerRoomTypes;
        roomAdapter = new RoomTypeAdapter(roomTypes, this);

        rvRoomTypes.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvRoomTypes.setAdapter(roomAdapter);

        binding.btnAddRoom.setOnClickListener(v -> {
            String name = etRoomInput.getText().toString().trim();
            if (!name.isEmpty()) {
                roomTypes.add(name);
                roomAdapter.notifyItemInserted(roomTypes.size() - 1);
                etRoomInput.setText(""); // Clear input
                updateRoomVisibility();
            }
        });

        loadFacilities();

        binding.nextFromHotelDetailBtn.setOnClickListener(v -> {

            Log.i("HotelListingDetailsFragment", "title: " + commanListingData.get("title"));
            Log.i("HotelListingDetailsFragment", "description: " + commanListingData.get("description"));
            Log.i("HotelListingDetailsFragment", "address: " + commanListingData.get("address"));
            Log.i("HotelListingDetailsFragment", "price: " + commanListingData.get("price"));
            Log.i("HotelListingDetailsFragment", "latitude: " + commanListingData.get("latitude"));
            Log.i("HotelListingDetailsFragment", "longitude: " + commanListingData.get("longitude"));
            Log.i("HotelListingDetailsFragment", "locationName: " + commanListingData.get("locationName"));
            Log.i("HotelListingDetailsFragment", "categoryName: " + commanListingData.get("categoryName"));
            Log.i("HotelListingDetailsFragment", "categoryId: " + commanListingData.get("categoryId"));

            for (Object image : (List<Object>) commanListingData.get("images")) {
                Log.i("HotelListingDetailsFragment", "image: " + image.toString());
            }


//            ArrayList<String> selected = new ArrayList<>();
//            for (Facility f : facilityList) {
//                if (f.isSelected()) selected.add(f.getTitle());
//            }
//
//            for (String s : selected) {
//                Log.d("SELECTED", s);
//            }
        });
    }

    private void loadFacilities() {
        Retrofit retrofit = RetrofitClient.getInstance(getContext());
        FacilityApi facilityApi = retrofit.create(FacilityApi.class);
        Call<List<FacilityDTO>> listCall = facilityApi.getAllFacilities();
        listCall.enqueue(new Callback<List<FacilityDTO>>() {
            @Override
            public void onResponse(Call<List<FacilityDTO>> call, Response<List<FacilityDTO>> response) {
                if (response.isSuccessful()) {
                    facilityDTOS = response.body();
                    for (FacilityDTO f : facilityDTOS) {
                        String resourceName = f.getIcon();
                        String entryName = resourceName.substring(resourceName.lastIndexOf('.') + 1);

                        int resId = getResources().getIdentifier(entryName, "drawable", getContext().getPackageName());

                        if (resId != 0) {
                            facilityList.add(new Facility(f.getId(), f.getType(), resId, false));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<FacilityDTO>> call, Throwable t) {
                Log.e("HotelListingDetailsFragment", "Error fetching facilities", t);
            }
        });

//        facilityList.add(new Facility(0, "Swimming Pool", R.drawable.pool_24px, false));
//        facilityList.add(new Facility(1, "Gym", R.drawable.fitness_center_24px, false));
//        facilityList.add(new Facility(2, "Spa", R.drawable.spa_24px, false));
//        facilityList.add(new Facility(3, "Restaurant", R.drawable.restaurant_24px, false));
//        facilityList.add(new Facility(4, "Bar", R.drawable.local_bar_24px, false));

        FacilitySelectAdapter adapter = new FacilitySelectAdapter(facilityList);
        binding.facilitySelectRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.facilitySelectRecyclerView.setAdapter(adapter);
    }

    public void updateRoomVisibility() {
        if (roomTypes.isEmpty()) {
            rvRoomTypes.setVisibility(View.GONE);
        } else {
            rvRoomTypes.setVisibility(View.VISIBLE);
        }
    }

    private void setupCounters() {

        // Adult Row
        binding.peopleCountChanger.tvLabel.setText("Adult");
        binding.peopleCountChanger.tvCount.setText(String.valueOf(adultCount));

        binding.peopleCountChanger.btnPlus.setOnClickListener(v -> {
            adultCount++;
            binding.peopleCountChanger.tvCount.setText(String.valueOf(adultCount));
        });

        binding.peopleCountChanger.btnMinus.setOnClickListener(v -> {
            if (adultCount > 1) { // Prevents going below 1
                adultCount--;
                binding.peopleCountChanger.tvCount.setText(String.valueOf(adultCount));
            }
        });

        // Room Row
        binding.roomsCountChanger.tvLabel.setText("Rooms");
        binding.roomsCountChanger.tvCount.setText(String.valueOf(roomCount));

        binding.roomsCountChanger.btnPlus.setOnClickListener(v -> {
            roomCount++;
            binding.roomsCountChanger.tvCount.setText(String.valueOf(roomCount));
        });

        binding.roomsCountChanger.btnMinus.setOnClickListener(v -> {
            if (roomCount > 1) { // Prevents going below 1
                roomCount--;
                binding.roomsCountChanger.tvCount.setText(String.valueOf(roomCount));
            }
        });
    }
}