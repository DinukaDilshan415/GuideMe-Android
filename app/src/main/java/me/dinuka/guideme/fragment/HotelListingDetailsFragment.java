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
import java.util.List;

import me.dinuka.guideme.R;
import me.dinuka.guideme.adapter.FacilityAdapter;
import me.dinuka.guideme.adapter.FacilitySelectAdapter;
import me.dinuka.guideme.adapter.RoomTypeAdapter;
import me.dinuka.guideme.databinding.FragmentHotelListingDetailsBinding;
import me.dinuka.guideme.model.Facility;

public class HotelListingDetailsFragment extends Fragment {

    private FragmentHotelListingDetailsBinding binding;
    private List<String> roomTypes = new ArrayList<>();
    private RoomTypeAdapter roomAdapter;
    private RecyclerView rvRoomTypes;
    private EditText etRoomInput;

    private int roomCount = 1;
    private int adultCount = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        List<Facility> facilityList = new ArrayList<>();
        facilityList.add(new Facility(0, "Swimming Pool", R.drawable.pool_24px, false));
        facilityList.add(new Facility(1, "Gym", R.drawable.fitness_center_24px, false));
        facilityList.add(new Facility(2, "Spa", R.drawable.spa_24px, false));
        facilityList.add(new Facility(3, "Restaurant", R.drawable.restaurant_24px, false));
        facilityList.add(new Facility(4, "Bar", R.drawable.local_bar_24px, false));

        FacilitySelectAdapter adapter = new FacilitySelectAdapter(facilityList);
        binding.facilitySelectRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.facilitySelectRecyclerView.setAdapter(adapter);


        binding.nextFromHotelDetailBtn.setOnClickListener(v -> {
            ArrayList<String> selected = new ArrayList<>();
            for (Facility f : facilityList) {
                if (f.isSelected()) selected.add(f.getTitle());
            }

            for (String s : selected) {
                Log.d("SELECTED", s);
            }
        });
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