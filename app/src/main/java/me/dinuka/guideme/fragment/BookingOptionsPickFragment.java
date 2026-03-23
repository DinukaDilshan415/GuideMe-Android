package me.dinuka.guideme.fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import me.dinuka.guideme.R;
import me.dinuka.guideme.adapter.FacilitySelectAdapter;
import me.dinuka.guideme.adapter.RoomTypeSelectAdapter;
import me.dinuka.guideme.databinding.FragmentBookingOptionsPickBinding;
import me.dinuka.guideme.dto.FacilityDTO;
import me.dinuka.guideme.model.Facility;
import me.dinuka.guideme.model.RoomType;

public class BookingOptionsPickFragment extends Fragment {
    private FragmentBookingOptionsPickBinding binding;
    private HashMap<String, Object> detailsMap;
    private FirebaseStorage storage;
    private int roomCount = 1;
    private int adultCount = 2;
    private int childCount = 0;
    double price;
    double bookingFee;
    String title;
    int perRoomCount;
    int room_count;
    Date dateIn;
    Date dateOut;
    ArrayList<Integer> selectedFacilities = new ArrayList<>();
    private List<RoomType> roomTypeList;
    RoomTypeSelectAdapter adapter;
    RoomType selectedRoomType;
    private Gson gson = new Gson();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            detailsMap = (HashMap<String, Object>) getArguments().getSerializable("detailsMap");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBookingOptionsPickBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.GONE);

        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        storage = FirebaseStorage.getInstance();

        setupCounters();

        binding.guestChangeBtn.setOnClickListener(v -> {
            if (binding.guestsChangeBody.getVisibility() == View.GONE) {
                binding.guestsChangeBody.setVisibility(View.VISIBLE);
                binding.guestChangeBtn.setText("");
                binding.guestChangeBtn.setIconResource(R.drawable.up_24px);
            } else {
                binding.guestsChangeBody.setVisibility(View.GONE);
                binding.guestChangeBtn.setIconResource(0);
                binding.guestChangeBtn.setText("Change");
            }
        });

        binding.roomsChangeBtn.setOnClickListener(v -> {
            if (binding.roomsChangeBody.getVisibility() == View.GONE) {
                binding.roomsChangeBody.setVisibility(View.VISIBLE);
                binding.roomsChangeBtn.setText("");
                binding.roomsChangeBtn.setIconResource(R.drawable.up_24px);
            } else {
                binding.roomsChangeBody.setVisibility(View.GONE);
                binding.roomsChangeBtn.setIconResource(0);
                binding.roomsChangeBtn.setText("Change");
            }
        });

        binding.datePickerBtn.setOnClickListener(v -> {
            showDateRangePicker();
        });

        binding.btnGuestsApply.setOnClickListener(v -> {
            binding.hotelCountAdult.setText(adultCount + " adult");
            binding.hotelCountChild.setText(childCount + " child");
        });

        binding.btnRoomsApply.setOnClickListener(v -> {
            binding.bookingHotelRoomCount.setText(roomCount + " ");
            selectedRoomType = adapter.getSelectedRoomType();
            if (selectedRoomType != null){
                binding.bRoomType.setText(selectedRoomType.getType());
            }
        });

        loaddetails();

        binding.proceedToReserveBtn.setOnClickListener(v -> {
            if (selectedRoomType != null) {
                Log.d("Selection", "Selected Room ID: " + selectedRoomType.getId());
                // Proceed with 'selected'
            } else {
                // Show warning that a selection is required
            }




        });
    }

    private void loaddetails() {
        title = detailsMap.get("title").toString();
        String locationName = detailsMap.get("location_name").toString();
        price = Double.parseDouble(detailsMap.get("price").toString());
        double rating = Double.parseDouble(detailsMap.get("rating").toString());

        perRoomCount = ((Number) detailsMap.get("per_room_count")).intValue();
        room_count = ((Number) detailsMap.get("room_count")).intValue();

        binding.bHotelName.setText(title);
        binding.bHotelLocation.setText(locationName);
        binding.bHotelRating.setText(String.valueOf(rating));
        binding.bHotelRatingBar.setRating((float) rating);
        binding.hotelBookingSubtotal.setText("$ " + price);

        List<String> images = (List<String>) detailsMap.get("imageUris");
        String imageUrl = images.get(0);
        String fullPath = "hotel-images/" + imageUrl;

        storage.getReference(fullPath).getDownloadUrl()
                .addOnSuccessListener(uri -> {

                    Glide.with(BookingOptionsPickFragment.this)
                            .load(uri)
                            .centerCrop()
                            .into(binding.bHotelImage);

                })
                .addOnFailureListener(e -> {
                    Log.e("BookingOptionsPickFragment", "Error getting image URL", e);
                });

        loadPaymentDetails();

        loadRoomTypes();
    }

    private void loadRoomTypes() {
        if (roomTypeList == null) {
            roomTypeList = new ArrayList<>();
        } else {
            roomTypeList.clear();
        }

        List<HashMap<String,Object>> rawroomTypes = (List<HashMap<String, Object>>) detailsMap.get("roomTypes");

        if (rawroomTypes != null) {
            for (Object obj : rawroomTypes) {
                String json = gson.toJson(obj);
                RoomType roomType = gson.fromJson(json, RoomType.class);

                roomTypeList.add(new RoomType(roomType.getId(), roomType.getType(), false));
            }
        }

        adapter = new RoomTypeSelectAdapter(roomTypeList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.roomTypeRecyclerView.setLayoutManager(layoutManager);
        binding.roomTypeRecyclerView.setAdapter(adapter);
    }

    private void loadPaymentDetails() {
        double bookingCommisonPrecentage = 5;
        bookingFee = price * bookingCommisonPrecentage / 100;

        binding.hotelBookingFee.setText("$ " + bookingFee);

        double total = price + bookingFee;
        binding.hotelBookingTotal.setText("$ " + total);
    }

    public void showDateRangePicker() {
        long today = MaterialDatePicker.todayInUtcMilliseconds();

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(DateValidatorPointForward.from(today));

        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select Date");
        builder.setCalendarConstraints(constraintsBuilder.build());

        MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            if (selection.first != null && selection.second != null) {
                dateIn = new Date(selection.first);
                dateOut = new Date(selection.second);

                binding.hotelCheckInDate.setText(formatDate(selection.first));
                binding.hotwlCheckOutDate.setText(formatDate(selection.second));
            }
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    // Helper to format the timestamp to "Nov 12, 2024"
    private String formatDate(Long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    private void setupCounters() {
        // Child Row
        binding.rowChildren.tvLabel.setText("Childrens");
        binding.rowChildren.tvCount.setText(String.valueOf(childCount));

        binding.rowChildren.btnPlus.setOnClickListener(v -> {
            childCount++;
            binding.rowChildren.tvCount.setText(String.valueOf(childCount));
        });

        binding.rowChildren.btnMinus.setOnClickListener(v -> {
            if (childCount > 1) { // Prevents going below 1
                childCount--;
                binding.rowChildren.tvCount.setText(String.valueOf(childCount));
            }
        });

        // Adult Row
        binding.rowAdults.tvLabel.setText("Adult");
        binding.rowAdults.tvCount.setText(String.valueOf(adultCount));

        binding.rowAdults.btnPlus.setOnClickListener(v -> {
            adultCount++;
            binding.rowAdults.tvCount.setText(String.valueOf(adultCount));
        });

        binding.rowAdults.btnMinus.setOnClickListener(v -> {
            if (adultCount > 1) { // Prevents going below 1
                adultCount--;
                binding.rowAdults.tvCount.setText(String.valueOf(adultCount));
            }
        });

        // Room Row
        binding.rowRooms.tvLabel.setText("Rooms");
        binding.rowRooms.tvCount.setText(String.valueOf(roomCount));

        binding.rowRooms.btnPlus.setOnClickListener(v -> {
            roomCount++;
            binding.rowRooms.tvCount.setText(String.valueOf(roomCount));
        });

        binding.rowRooms.btnMinus.setOnClickListener(v -> {
            if (roomCount > 1) { // Prevents going below 1
                roomCount--;
                binding.rowRooms.tvCount.setText(String.valueOf(roomCount));
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.GONE);
    }
}