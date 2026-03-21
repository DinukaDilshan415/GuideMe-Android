package me.dinuka.guideme.fragment;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import me.dinuka.guideme.R;
import me.dinuka.guideme.api.ListingApi;
import me.dinuka.guideme.client.RetrofitClient;
import me.dinuka.guideme.databinding.FragmentThingsToDoListingDetailsBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import taimoor.sultani.sweetalert2.Sweetalert;

public class ThingsToDoListingDetailsFragment extends Fragment {

    private FragmentThingsToDoListingDetailsBinding binding;
    private int bookingCount = 1;
    List<String> imageUris = new ArrayList<>();
    HashMap<String, Object> commanListingData;
    private String listingGR;
    private String listingAL;
    private String listingWB;
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
        binding = FragmentThingsToDoListingDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        setupCounters();

        binding.saveListingBtn.setOnClickListener(v -> {
            listingGR = binding.listingGR.getText().toString().trim();
            listingAL = binding.listingAL.getText().toString().trim();
            listingWB = binding.listingWB.getText().toString().trim();

            if(bookingCount < 1){
                bookingCount = 1;
            }

            if(listingGR.isEmpty()) {
                binding.listingGR.setError("Guest Requirements is required");
                binding.listingGR.requestFocus();
                return;
            }

            if(listingAL.isEmpty()) {
                binding.listingAL.setError("Activity Level is required");
                binding.listingAL.requestFocus();
                return;
            }

            if(listingWB.isEmpty()) {
                binding.listingWB.setError("What To Bring is required");
                binding.listingWB.requestFocus();
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
            String fileName = "things_to_do_pics_" + uuid + ".jpg";

            StorageReference imageReference = storage.getReference("things-to-do-images")
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
                        Log.e("ThingsToDoListingDetailsFragment", "Error uploading image", e);

                        processedCount[0]++;
                        // We still increment even on failure so the process can finish
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
        data.put("bookingCount", bookingCount);
        data.put("listingGR", listingGR);
        data.put("listingAL", listingAL);
        data.put("listingWB", listingWB);

        data.put("imageUris", imageUris);

        Call<HashMap<String, Object>> listingCall = listingApi.addNewListing(data);

        listingCall.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                pDialog.dismissWithAnimation();
                if (response.isSuccessful()) {
                    HashMap<String, Object> responseBody = response.body();
                    Log.d("ThingsToDoListingDetailsFragment", "Response: " + responseBody.get("message"));

                    new Sweetalert(requireContext(), Sweetalert.SUCCESS_TYPE)
                            .setTitleText("Success")
                            .setContentText("New Listing Added Successfully")
                            .show();

                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.partner_fragment_container, new PartnerHomeFragment())
                            .addToBackStack(null)
                            .commit();
                } else {
                    Log.e("ThingsToDoListingDetailsFragment", "Error: " + response);
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                Log.e("ThingsToDoListingDetailsFragment", "Error: " + t.getMessage());
                pDialog.dismissWithAnimation();
            }
        });
    }

    private void setupCounters() {

        // Bookings Row
        binding.booingCountChanger.tvLabel.setText("Bookings");
        binding.booingCountChanger.tvCount.setText(String.valueOf(bookingCount));

        binding.booingCountChanger.btnPlus.setOnClickListener(v -> {
            bookingCount++;
            binding.booingCountChanger.tvCount.setText(String.valueOf(bookingCount));
        });

        binding.booingCountChanger.btnMinus.setOnClickListener(v -> {
            if (bookingCount > 1) { // Prevents going below 1
                bookingCount--;
                binding.booingCountChanger.tvCount.setText(String.valueOf(bookingCount));
            }
        });
    }
}