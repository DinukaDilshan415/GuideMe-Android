package me.dinuka.guideme.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.dinuka.guideme.R;
import me.dinuka.guideme.adapter.ListingSliderAdapter;
import me.dinuka.guideme.adapter.ReviewListingAdapter;
import me.dinuka.guideme.api.UserApi;
import me.dinuka.guideme.client.RetrofitClient;
import me.dinuka.guideme.databinding.FragmentThingsToDoDetailsBinding;
import me.dinuka.guideme.dto.UserAddressDTO;
import me.dinuka.guideme.dto.UserDTO;
import me.dinuka.guideme.helper.WishlistHelper;
import me.dinuka.guideme.model.Review;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import taimoor.sultani.sweetalert2.Sweetalert;


public class ThingsToDoDetailsFragment extends Fragment implements OnMapReadyCallback {
    private FragmentThingsToDoDetailsBinding binding;
    private HashMap<String, Object> detailsMap;
    private FirebaseAuth firebaseAuth;
    List<String> listingImages = new ArrayList<>();
    private FirebaseStorage storage;
    private Gson gson = new Gson();
    private MapView mapView;
    private GoogleMap mMap;
    private double latitude;
    private double longitude;
    private String locationName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            detailsMap = (HashMap<String, Object>) getArguments().getSerializable("selectedThingToDo");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentThingsToDoDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        mapView = binding.dMapView;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.GONE);

        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        if (detailsMap != null) {
            loadDetails();
        }

        List<Review> reviews = new ArrayList<>();
        reviews.add(new Review(0, "Dinuka Dilshan", "data:image/jpeg;base64,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", "Sri Lanka", "January 2026", "Beautiful hotel with great amenities, comfortable rooms, high security and very helpful staff", 4.5));
        reviews.add(new Review(1, "Saman kumra", "data:image/jpeg;base64,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", "America", "february 2065", "Gogg place", 3));

        RecyclerView recyclerView1 = binding.reviewsRecyclerView;
        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        ReviewListingAdapter reviewListingAdapter1 = new ReviewListingAdapter(reviews, review -> {

        });

        recyclerView1.setAdapter(reviewListingAdapter1);

        binding.reserveBtn.setOnClickListener(v -> {

            FirebaseUser currentUser = firebaseAuth.getCurrentUser();

            if (currentUser == null) {
                new Sweetalert(requireContext(), Sweetalert.WARNING_TYPE)
                        .setTitleText("Warning")
                        .setContentText("Please login to continue")
                        .show();
            } else {
                checkUserEligibility(currentUser.getEmail());
            }
        });

        binding.backBtn.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        binding.addWishListBtn.setOnClickListener(v -> {
            List<HashMap<String, Object>> wishlist = WishlistHelper.getWishlist(getContext());

            if (wishlist.isEmpty()) {
                wishlist.add(detailsMap);
                WishlistHelper.saveWishlist(getContext(), wishlist);
                Toast.makeText(getContext(), "Added to wishlist", Toast.LENGTH_SHORT).show();
            } else {
                for (HashMap<String, Object> item : wishlist) {
                    int id = ((Number) item.get("l_id")).intValue();
                    int thisId = ((Number) detailsMap.get("l_id")).intValue();

                    if (id == thisId) {
                        Toast.makeText(getContext(), "Already in wishlist", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                wishlist.add(detailsMap);
                WishlistHelper.saveWishlist(getContext(), wishlist);
                Toast.makeText(getContext(), "Added to wishlist", Toast.LENGTH_SHORT).show();
            }

        });

        binding.callBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + binding.dPartnerMobile.getText().toString().trim()));
            startActivity(intent);
        });

        binding.sendMailBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);

            intent.setData(Uri.parse("mailto:" + binding.dPartnerEmail.getText().toString().trim()));

            startActivity(intent);
        });
    }

    private void checkUserEligibility(String email) {
        loadCurrentData(email);
    }

    private void loadCurrentData(String email) {
        Retrofit retrofit = RetrofitClient.getInstance(getContext());
        UserApi userApi = retrofit.create(UserApi.class);

        Call<UserDTO> currentUserCall = userApi.getCurrentUser(email);
        currentUserCall.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful()) {
                    UserDTO userDTO = response.body();

                    if (userDTO.getMobile().equals("empty")
                            || userDTO.getImageUrl().equals("empty")
                            || userDTO.getCountry().equals("empty")
                            || userDTO.getStatusId() != 1) {

                        new Sweetalert(requireContext(), Sweetalert.WARNING_TYPE)
                                .setTitleText("Warning")
                                .setContentText("Complete Your Personal Details To Continue")
                                .show();
                    } else {
                        loadCurrentAddressData(email);
                    }
                } else {
                    Log.e("ThingsToDoDetailsFragment", "Error fetching : " + response);
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Log.e("ThingsToDoDetailsFragment", "Error fetching : ", t);
            }
        });
    }

    private void loadCurrentAddressData(String email) {
        Retrofit retrofit = RetrofitClient.getInstance(getContext());
        UserApi userApi = retrofit.create(UserApi.class);

        Call<UserAddressDTO> currentUserCall = userApi.getCurrentUserAddress(email);
        currentUserCall.enqueue(new Callback<UserAddressDTO>() {
            @Override
            public void onResponse(Call<UserAddressDTO> call, Response<UserAddressDTO> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200) {

                        ThingsToDoBookingOptionsPickFragment fragment = new ThingsToDoBookingOptionsPickFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("detailsMap", detailsMap);
                        fragment.setArguments(bundle);

                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack(null)
                                .commit();

                    } else if (response.code() == 204) {
                        new Sweetalert(requireContext(), Sweetalert.WARNING_TYPE)
                                .setTitleText("Warning")
                                .setContentText("Complete Your Address To Continue")
                                .show();
                    } else {
                        Log.e("ThingsToDoDetailsFragment", "Error fetching : " + response);
                    }
                } else {
                    Log.e("ThingsToDoDetailsFragment", "Error fetching : " + response);
                }
            }

            @Override
            public void onFailure(Call<UserAddressDTO> call, Throwable t) {
                Log.e("ThingsToDoDetailsFragment", "Error fetching : ", t);
            }
        });
    }

    private void loadDetails() {
        String title = detailsMap.get("title").toString();
        String description = detailsMap.get("description").toString();
        locationName = detailsMap.get("location_name").toString();
        latitude = Double.parseDouble(detailsMap.get("latitude").toString());
        longitude = Double.parseDouble(detailsMap.get("longitude").toString());
        String price = detailsMap.get("price").toString();
        double rating = Double.parseDouble(detailsMap.get("rating").toString());

        int per_day_bookings = ((Number) detailsMap.get("per_day_bookings")).intValue();
        String guest_requirements = detailsMap.get("guest_requirements").toString();
        String activity_level = detailsMap.get("activity_level").toString();
        String what_to_bring = detailsMap.get("what_to_bring").toString();

        String partnerName = detailsMap.get("partner_name").toString();
        String partnerEmail = detailsMap.get("partnerEmail").toString();
        String partnerMobile = detailsMap.get("partnerMobile").toString();
        String partnerImageUrl = detailsMap.get("partnerImageUrl").toString();
        String partnerCreated_at = detailsMap.get("partnerCreated_at").toString();

        binding.dThingstodoName.setText(title);
        binding.dDescription.setText(Html.fromHtml(description));
        binding.dLocation.setText(locationName);
        binding.dProductRatingBar.setRating((float) rating);
        binding.dRating.setText(String.valueOf(rating));
        binding.dPrice.setText("$ " + price);
        binding.dAddress.setText(locationName);

        binding.guestRequirements.setText(guest_requirements);
        binding.activityLevel.setText(activity_level);
        binding.whatToBring.setText(what_to_bring);

        binding.dPartnerName.setText("Hosted By " + partnerName);
        binding.dHostingPeriod.setText("Hosting From " + partnerCreated_at);
        binding.dPartnerEmail.setText(partnerEmail);
        binding.dPartnerMobile.setText(partnerMobile);

        loadListingImages();

        if (partnerImageUrl != null) {
            storage.getReference("profile-images/" + partnerImageUrl).getDownloadUrl()
                    .addOnSuccessListener(uri -> {

                        Glide.with(ThingsToDoDetailsFragment.this)
                                .load(uri)
                                .centerCrop()
                                .into(binding.hosterImage);

                    })
                    .addOnFailureListener(e -> {
                        Log.e("ThingsToDoDetailsFragment", "Error getting image URL", e);
                    });
        }
    }

    private void loadListingImages() {
        List<String> images = (List<String>) detailsMap.get("imageUris");

        listingImages.clear();

        final int totalImages = images.size();
        final int[] processedCount = {0};

        for (String imageUri : images) {
            storage.getReference("things-to-do-images/" + imageUri).getDownloadUrl()
                    .addOnSuccessListener(uri -> {

                        listingImages.add(uri.toString());
                        processedCount[0]++;
                        if (processedCount[0] == totalImages) {
                            Log.d("ThingsToDoDetailsFragment", "All images loaded");
                            ListingSliderAdapter adapter = new ListingSliderAdapter(listingImages);
                            binding.productImageSlider.setAdapter(adapter);

                            binding.dotsIndicator.attachTo(binding.productImageSlider);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ThingsToDoDetailsFragment", "Error getting image URL", e);
                    });
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);

        if (mMap != null) {
            LatLng selectedLatLng = new LatLng(latitude, longitude);
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(selectedLatLng).title(locationName));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 15f));
        }
    }
}