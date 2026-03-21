package me.dinuka.guideme.fragment;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import me.dinuka.guideme.R;
import me.dinuka.guideme.adapter.HotelListingAdapter;
import me.dinuka.guideme.api.HotelApi;
import me.dinuka.guideme.api.ListingApi;
import me.dinuka.guideme.client.RetrofitClient;
import me.dinuka.guideme.databinding.FragmentHotelsBinding;
import me.dinuka.guideme.model.Hotel;
import me.dinuka.guideme.model.Listing;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HotelsFragment extends Fragment {

    private FragmentHotelsBinding binding;
    List<Hotel> hotels = new ArrayList<>();

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

        ImageView imageView = binding.loadingImage;

        Glide.with(this)
                .load(R.drawable.loadingbuffering) // or R.raw.your_gif_file_name
                .into(imageView);

        loadMainHotels("key");

//        hotels.add(new Hotel(0, "New Ella Nature View Ella Nature View", 57, "Ella", 4.5, 228, new ArrayList<>(Arrays.asList("https://cf.bstatic.com/xdata/images/hotel/max1024x768/679226546.jpg?k=93984560a6eea303c7a8978fa3884e125c29a03629bd06b5f0e7e20b865853a4&o="))));
//        hotels.add(new Hotel(1, "Cinnamon Wild Yala", 64, "Yala", 5, 22, new ArrayList<>(Arrays.asList("https://cf.bstatic.com/xdata/images/hotel/max1024x768/645050529.jpg?k=0ada0d186488560371fe60634ad0d95a4aed8d6114f3e988ac683bbfdd8b9ca2&o="))));
//        hotels.add(new Hotel(2, "Cinnamon Life at City of Dreams", 560, "Colombo, Sri Lanka", 4.5, 235, new ArrayList<>(Arrays.asList("https://cf.bstatic.com/xdata/images/hotel/max1024x768/588945741.jpg?k=0c6c7b0fd96d38c70101f6b57ddb2e75eeece3889616d3a28877f5cc3d5fd7ef&o="))));



        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true){
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void loadMainHotels(String key) {
        Retrofit retrofit = RetrofitClient.getInstance(getContext());
        final HotelApi hotelApi = retrofit.create(HotelApi.class);

        Call<List<HashMap<String, Object>>> listingCall = hotelApi.getMainHotels(key);
        listingCall.enqueue(new Callback<List<HashMap<String, Object>>>() {
            @Override
            public void onResponse(Call<List<HashMap<String, Object>>> call, Response<List<HashMap<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<HashMap<String, Object>> responseBody = response.body();
                    List<Task<Uri>> tasks = new ArrayList<>();

                    for (HashMap<String, Object> hotel : responseBody) {
                        List<String> images = (List<String>) hotel.get("imageUris");
                        String imageUrl = images.get(0);
                        String fullPath = "hotel-images/" + imageUrl;

                        tasks.add(FirebaseStorage.getInstance().getReference(fullPath).getDownloadUrl());
                    }

                    Tasks.whenAllComplete(tasks).addOnCompleteListener(allTasks -> {

                        hotels.clear(); // Clear existing list to avoid duplicates

                        for (int i = 0; i < responseBody.size(); i++) {
                            HashMap<String, Object> hotelMap = responseBody.get(i);
                            Task<Uri> currentTask = tasks.get(i);

                            String finalImageUrl = "";
                            if (currentTask.isSuccessful()) {
                                finalImageUrl = currentTask.getResult().toString();
                            } else {
                                finalImageUrl = "default_placeholder_url";
                                Log.e("Firebase", "Failed to get URL for hotel: " + hotelMap.get("title"));
                            }

                            hotels.add(new Hotel(
                                    ((Number) hotelMap.get("id")).intValue(),
                                    hotelMap.get("title").toString(),
                                    Double.parseDouble(hotelMap.get("price").toString()),
                                    hotelMap.get("location_name").toString(),
                                    Double.parseDouble(hotelMap.get("rating").toString()),
                                    ((Number) hotelMap.get("reviewCount")).intValue(),
                                    new ArrayList<>(Arrays.asList(finalImageUrl)),
                                    hotelMap
                            ));
                        }
                        setupRecyclerView();
                    });
                }
            }

            @Override
            public void onFailure(Call<List<HashMap<String, Object>>> call, Throwable t) {
                Log.e("HotelsFragment", "Error fetching :", t);
            }
        });
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.nearHotelSection.hotelsSectionContainer.setLayoutManager(layoutManager);

        HotelListingAdapter adapter = new HotelListingAdapter(hotels, selectedHotel -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("selectedHotel", selectedHotel);

            HotelDetailsFragment fragment = new HotelDetailsFragment();
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.nearHotelSection.hotelSectionTitle.setText("For You");
        binding.nearHotelSection.hotelsSectionContainer.setAdapter(adapter);

        binding.loadingLayout.setVisibility(View.GONE);
        binding.recyclerViewLayout.setVisibility(View.VISIBLE);
    }


}