package me.dinuka.guideme.fragment;

import android.net.Uri;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import me.dinuka.guideme.R;
import me.dinuka.guideme.adapter.FacilityAdapter;
import me.dinuka.guideme.adapter.FacilitySelectAdapter;
import me.dinuka.guideme.adapter.RoomTypeAdapter;
import me.dinuka.guideme.api.FacilityApi;
import me.dinuka.guideme.api.ListingApi;
import me.dinuka.guideme.api.PartnerApi;
import me.dinuka.guideme.client.RetrofitClient;
import me.dinuka.guideme.databinding.FragmentHotelListingDetailsBinding;
import me.dinuka.guideme.dto.FacilityDTO;
import me.dinuka.guideme.dto.PartnerAddressDTO;
import me.dinuka.guideme.model.Facility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import taimoor.sultani.sweetalert2.Sweetalert;

public class HotelListingDetailsFragment extends Fragment {

    private FragmentHotelListingDetailsBinding binding;
    private List<String> roomTypes = new ArrayList<>();
    private RoomTypeAdapter roomAdapter;
    private RecyclerView rvRoomTypes;
    private EditText etRoomInput;
    private int roomCount = 1;
    private int adultCount = 1;
    List<Facility> facilityList;
    ArrayList<Integer> selectedFacilities = new ArrayList<>();
    List<String> imageUris = new ArrayList<>();
    private List<FacilityDTO> facilityDTOS;
    HashMap<String, Object> commanListingData;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

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

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

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
                etRoomInput.setText("");
                updateRoomVisibility();
            }
        });

        loadFacilities();

        binding.nextFromHotelDetailBtn.setOnClickListener(v -> {

            for (Facility f : facilityList) {
                if (f.isSelected()) selectedFacilities.add(f.getId());
            }

            if (adultCount < 1){
                adultCount = 1;
            }

            if (roomCount < 1){
                roomCount = 1;
            }

            if (roomTypes.isEmpty()){
                new Sweetalert(requireContext(), Sweetalert.WARNING_TYPE)
                        .setTitleText("Warning")
                        .setContentText("Add Room Types")
                        .show();
                return;
            }

            if (selectedFacilities.isEmpty()){
                new Sweetalert(requireContext(), Sweetalert.WARNING_TYPE)
                        .setTitleText("Warning")
                        .setContentText("Select What Your Place Offer")
                        .show();
                return;
            }

            saveNewListing(firebaseAuth.getCurrentUser().getEmail());
        });
    }

    private void saveNewListing(String email) {
        Sweetalert pDialog = new Sweetalert(requireContext(), Sweetalert.PROGRESS_TYPE);
        pDialog.setTitleText("Uploading Data...");
        pDialog.setCancelable(false);
        pDialog.show();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        List<Uri> images = (List<Uri>) commanListingData.get("images");

        imageUris.clear();

        final int totalImages = images.size();
        final int[] processedCount = {0};

        for (Uri image : images) {
            UUID uuid = UUID.randomUUID();
            String fileName = "hotel_pic_" + uuid + ".jpg";

            StorageReference imageReference = storage.getReference("hotel-images")
                    .child(fileName);
            imageReference.putFile(image)
                    .addOnSuccessListener(taskSnapshot -> {

                        imageUris.add(fileName);

                        processedCount[0]++;
                        if (processedCount[0] == totalImages) {
                            sendDataToRetrofit(email, pDialog);
                        }

                    })
                    .addOnFailureListener(e -> {
                        Log.e("HotelListingDetailsFragment", "Error uploading image", e);

                        processedCount[0]++;

                        if (processedCount[0] == totalImages) {
                            sendDataToRetrofit(email, pDialog);
                        }
                    });
        }
    }

    private void sendDataToRetrofit(String email, Sweetalert pDialog) {
        Retrofit instance = RetrofitClient.getInstance(requireContext());
        ListingApi listingApi = instance.create(ListingApi.class);

        HashMap<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("title", commanListingData.get("title"));
        data.put("description", commanListingData.get("description"));
        data.put("address", commanListingData.get("address"));
        data.put("price", commanListingData.get("price"));
        data.put("latitude", commanListingData.get("latitude"));
        data.put("longitude", commanListingData.get("longitude"));
        data.put("locationName", commanListingData.get("locationName"));
        data.put("categoryName", commanListingData.get("categoryName"));
        data.put("categoryId", commanListingData.get("categoryId"));
        data.put("adultCount", adultCount);
        data.put("roomCount", roomCount);
        data.put("selectedFacilities", selectedFacilities);
        data.put("roomTypes", roomTypes);
        data.put("imageUris", imageUris);

        Call<HashMap<String, Object>> listingCall = listingApi.addNewListing(data);

        listingCall.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                pDialog.dismissWithAnimation();
                if (response.isSuccessful()) {
                    HashMap<String, Object> responseBody = response.body();
                    Log.d("HotelListingDetailsFragment", "Response: " + responseBody.get("message"));

                    new Sweetalert(requireContext(), Sweetalert.SUCCESS_TYPE)
                            .setTitleText("Success")
                            .setContentText("New Listing Added Successfully")
                            .show();

                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.partner_fragment_container, new PartnerHomeFragment())
                            .addToBackStack(null)
                            .commit();
                } else {
                    Log.e("HotelListingDetailsFragment", "Error: " + response);
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                Log.e("HotelListingDetailsFragment", "Error: " + t.getMessage());
                pDialog.dismissWithAnimation();
            }
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