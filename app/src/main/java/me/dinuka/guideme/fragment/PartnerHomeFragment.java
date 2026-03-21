package me.dinuka.guideme.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.dinuka.guideme.R;
import me.dinuka.guideme.activity.PartnerRegisterActivity;
import me.dinuka.guideme.adapter.FacilitySelectAdapter;
import me.dinuka.guideme.adapter.MyBookingsListingAdapter;
import me.dinuka.guideme.adapter.PartnerListingAdapter;
import me.dinuka.guideme.api.FacilityApi;
import me.dinuka.guideme.api.ListingApi;
import me.dinuka.guideme.api.PartnerApi;
import me.dinuka.guideme.client.RetrofitClient;
import me.dinuka.guideme.databinding.FragmentHomeBinding;
import me.dinuka.guideme.databinding.FragmentPartnerHomeBinding;
import me.dinuka.guideme.dto.FacilityDTO;
import me.dinuka.guideme.dto.PartnerAddressDTO;
import me.dinuka.guideme.dto.PartnerDTO;
import me.dinuka.guideme.model.Facility;
import me.dinuka.guideme.model.Listing;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import taimoor.sultani.sweetalert2.Sweetalert;


public class PartnerHomeFragment extends Fragment {

    private FragmentPartnerHomeBinding binding;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    List<Listing> listings = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPartnerHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        ImageView imageView = binding.loadingImage;

        Glide.with(this)
                .load(R.drawable.loadingbuffering) // or R.raw.your_gif_file_name
                .into(imageView);

        loadPartnerListings(firebaseAuth.getCurrentUser().getEmail());

        binding.btnAddNewListing.setOnClickListener(v -> {

            addNewListing(firebaseAuth.getCurrentUser().getEmail());

        });
    }

    private void loadPartnerListings(String email) {
        Retrofit retrofit = RetrofitClient.getInstance(getContext());
        ListingApi listingApi = retrofit.create(ListingApi.class);

        Call<List<HashMap<String, Object>>> listingCall = listingApi.getPartnerListings(email);
        listingCall.enqueue(new Callback<List<HashMap<String, Object>>>() {
            @Override
            public void onResponse(Call<List<HashMap<String, Object>>> call, Response<List<HashMap<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<HashMap<String, Object>> responseBody = response.body();

                    int totalItems = responseBody.size();
                    final int[] processedCount = {0};

                    // 1. Create a temporary array or list with a fixed size to maintain order
                    Listing[] orderedListings = new Listing[totalItems];

                    for (int i = 0; i < totalItems; i++) {
                        final int index = i; // Save the current position
                        HashMap<String, Object> l = responseBody.get(i);

                        String imageFolder = "";
                        int ltId = ((Number) l.get("listing_type_id")).intValue();
                        if (ltId == 1) imageFolder = "hotel-images";
                        else if (ltId == 2) imageFolder = "things-to-do-images";
                        else if (ltId == 3) imageFolder = "service-images";

                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        String fullPath = imageFolder + "/" + l.get("image").toString();

                        storage.getReference(fullPath).getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    // 2. Place the object in its SPECIFIC original index
                                    orderedListings[index] = new Listing(
                                            ((Number) l.get("id")).intValue(),
                                            l.get("list_id").toString(),
                                            l.get("status").toString(),
                                            l.get("listing_type").toString(),
                                            l.get("title").toString(),
                                            Double.parseDouble(l.get("price").toString()),
                                            l.get("location_name").toString(),
                                            uri.toString(),
                                            l.get("created_at").toString()
                                    );

                                    checkAndRefresh(++processedCount[0], totalItems, orderedListings);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Error", "Image failed", e);
                                    checkAndRefresh(++processedCount[0], totalItems, orderedListings);
                                });
                    }
                }
            }

            private void checkAndRefresh(int current, int total, Listing[] orderedListings) {
                if (current == total) {
                    listings.clear();
                    // 3. Add all non-null items back to the main list in order
                    for (Listing item : orderedListings) {
                        if (item != null) listings.add(item);
                    }
                    setupRecyclerView();
                }
            }

            @Override
            public void onFailure(Call<List<HashMap<String, Object>>> call, Throwable t) {
                Log.e("PartnerHomeFragment", "Error fetching :", t);
            }
        });
    }

    private void setupRecyclerView() {
        if (getContext() == null) return;

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.partnerListingsRecyclerView.setLayoutManager(layoutManager);

        PartnerListingAdapter adapter = new PartnerListingAdapter(listings, listing -> {
            Bundle bundle = new Bundle();
            bundle.putInt("listingId", listing.getId());
            // Handle click...
        });

        binding.partnerListingsRecyclerView.setAdapter(adapter);

        binding.loadingLayout.setVisibility(View.GONE);
        binding.partnerListingsRecyclerView.setVisibility(View.VISIBLE);
    }

    private void addNewListing(String email) {
        loadCurrentData(email);
    }

    private void loadCurrentData(String email) {

        Retrofit retrofit = RetrofitClient.getInstance(getContext());
        PartnerApi partnerApi = retrofit.create(PartnerApi.class);

        Call<PartnerDTO> currentPartnerCall = partnerApi.getCurrentPartner(email);
        currentPartnerCall.enqueue(new Callback<PartnerDTO>() {
            @Override
            public void onResponse(Call<PartnerDTO> call, Response<PartnerDTO> response) {
                if (response.isSuccessful()) {
                    PartnerDTO currentPartnerDTO = response.body();

                    if (currentPartnerDTO.getMobile().equals("empty")
                            || currentPartnerDTO.getImageUrl().equals("empty")
                            || currentPartnerDTO.getCountry().equals("empty")
                            || currentPartnerDTO.getStatusId() != 1) {

                        new Sweetalert(requireContext(), Sweetalert.WARNING_TYPE)
                                .setTitleText("Warning")
                                .setContentText("Complete Your Personal Details First To Add New Listing")
                                .show();
                    } else {
                        loadCurrentAddressData(email);
                    }
                } else {
                    Log.e("PartnerHomeFragment", "Error fetching : " + response);
                }
            }

            @Override
            public void onFailure(Call<PartnerDTO> call, Throwable t) {
                Log.e("PartnerHomeFragment", "Error fetching : ", t);
            }
        });
    }

    private void loadCurrentAddressData(String email) {
        Retrofit retrofit = RetrofitClient.getInstance(getContext());
        PartnerApi partnerApi = retrofit.create(PartnerApi.class);

        Call<PartnerAddressDTO> currentPartnerCall = partnerApi.getCurrentPartnerAddress(email);
        currentPartnerCall.enqueue(new Callback<PartnerAddressDTO>() {
            @Override
            public void onResponse(Call<PartnerAddressDTO> call, Response<PartnerAddressDTO> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200) {

                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.partner_fragment_container, new ListingCategorySelectFragment())
                                .addToBackStack(null)
                                .commit();

                    } else if (response.code() == 204) {
                        new Sweetalert(requireContext(), Sweetalert.WARNING_TYPE)
                                .setTitleText("Warning")
                                .setContentText("Complete Your Address First To Add New Listing")
                                .show();
                    } else {
                        Log.e("PartnerHomeFragment", "Error fetching : " + response);
                    }
                } else {
                    Log.e("PartnerHomeFragment", "Error fetching : " + response);
                }
            }

            @Override
            public void onFailure(Call<PartnerAddressDTO> call, Throwable t) {
                Log.e("PartnerHomeFragment", "Error fetching : ", t);
            }
        });
    }
}