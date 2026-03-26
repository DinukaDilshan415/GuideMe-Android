package me.dinuka.guideme.fragment;

import android.os.Bundle;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.dinuka.guideme.R;
import me.dinuka.guideme.adapter.HotelListingAdapter;
import me.dinuka.guideme.adapter.MyBookingsListingAdapter;
import me.dinuka.guideme.adapter.PartnerListingAdapter;
import me.dinuka.guideme.api.BookingApi;
import me.dinuka.guideme.api.ListingApi;
import me.dinuka.guideme.client.RetrofitClient;
import me.dinuka.guideme.databinding.FragmentMyBookingsBinding;
import me.dinuka.guideme.model.Listing;
import me.dinuka.guideme.model.MyBooking;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MyBookingsFragment extends Fragment {

    private FragmentMyBookingsBinding binding;
    private FirebaseAuth auth;
    List<MyBooking> bookings = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyBookingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView imageView = binding.loadingImage;

        Glide.with(this)
                .load(R.drawable.loadingbuffering) // or R.raw.your_gif_file_name
                .into(imageView);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null){
            binding.myBookingsLoggedDesign.setVisibility(View.VISIBLE);
            binding.myBookingsNotLoggedDesign.setVisibility(View.GONE);

            loadMyBookings(auth.getCurrentUser().getEmail());
        } else {
            binding.myBookingsLoggedDesign.setVisibility(View.GONE);
            binding.myBookingsNotLoggedDesign.setVisibility(View.VISIBLE);
        }

//        List<MyBooking> myBookings = new ArrayList<>();
//        myBookings.add(new MyBooking("#38472384","Room in boutique hotel in Ahangama",45,"Ahangama West, Sri Lanka",4.5,"https://a0.muscache.com/im/pictures/hosting/Hosting-1616481404202153296/original/c8166786-ba76-4847-b7bf-e40af1e50562.jpeg?im_w=1200","3/20/2026","3/20/2026",2,1,"3/20/2026 8:09AM"));
//        myBookings.add(new MyBooking("#343545","Cozy Upstairs Suite•10 Mins to Airport•Pvt Balcony",74,"Kimbulapitiya Negombo, Sri Lanka",3,"https://a0.muscache.com/im/pictures/hosting/Hosting-667189615689084189/original/9ece141d-d9b4-4903-870d-f9f68254e398.jpeg?im_w=1200","3/20/2026","3/20/2026",2,1,"3/20/2026 8:09AM"));
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
//        binding.mybookingsRecyclerView.setLayoutManager(layoutManager);
//
//        MyBookingsListingAdapter adapter = new MyBookingsListingAdapter(myBookings, myBooking -> {
//            Bundle bundle = new Bundle();
//            bundle.putString("hotelName", myBooking.getId());
//
//        });
//
//        binding.mybookingsRecyclerView.setAdapter(adapter);
    }

    private void loadMyBookings(String email) {
        Retrofit retrofit = RetrofitClient.getInstance(getContext());
        BookingApi bookingApi = retrofit.create(BookingApi.class);

        Call<List<HashMap<String, Object>>> bookingCall = bookingApi.getUserBookings(email);
        bookingCall.enqueue(new Callback<List<HashMap<String, Object>>>() {
            @Override
            public void onResponse(Call<List<HashMap<String, Object>>> call, Response<List<HashMap<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    if (response.code() == 200){
                        List<HashMap<String, Object>> responseBody = response.body();

                        int totalItems = responseBody.size();
                        final int[] processedCount = {0};

                        // 1. Create a temporary array or list with a fixed size to maintain order
                        MyBooking[] orderedMyBookings = new MyBooking[totalItems];

                        for (int i = 0; i < totalItems; i++) {
                            final int index = i; // Save the current position
                            HashMap<String, Object> l = responseBody.get(i);

                            String imageFolder = "";
                            int ltId = ((Number) l.get("categoryId")).intValue();
                            if (ltId == 1) imageFolder = "hotel-images";
                            else if (ltId == 2) imageFolder = "things-to-do-images";
                            else if (ltId == 3) imageFolder = "service-images";

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            String fullPath = imageFolder + "/" + l.get("imageUri").toString();

                            storage.getReference(fullPath).getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        // 2. Place the object in its SPECIFIC original index
                                        orderedMyBookings[index] = new MyBooking(
                                                ((Number) l.get("id")).intValue(),
                                                l.get("booking_id").toString(),
                                                l.get("title").toString(),
                                                Double.parseDouble(l.get("price").toString()),
                                                l.get("location_name").toString(),
                                                0,
                                                uri.toString(),
                                                l.get("date_in").toString(),
                                                l.get("date_out").toString(),
                                                ((Number) l.get("adultCount")).intValue(),
                                                ((Number) l.get("childCount")).intValue(),
                                                l.get("created_at").toString(),
                                                l.get("categoryName").toString(),
                                                l.get("status").toString()
                                        );

                                        checkAndRefresh(++processedCount[0], totalItems, orderedMyBookings);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Error", "Image failed", e);
                                        checkAndRefresh(++processedCount[0], totalItems, orderedMyBookings);
                                    });
                        }
                    } else if (response.code() == 204){
                        binding.loadingLayout.setVisibility(View.GONE);
                        binding.myBookingsLoggedDesign.setVisibility(View.GONE);
                        binding.noContentLayout.setVisibility(View.VISIBLE);
                    }

                }
            }

            private void checkAndRefresh(int current, int total, MyBooking[] orderedMyBookings) {
                if (current == total) {
                    bookings.clear();
                    // 3. Add all non-null items back to the main list in order
                    for (MyBooking item : orderedMyBookings) {
                        if (item != null) bookings.add(item);
                    }
                    setupRecyclerView();
                }
            }

            @Override
            public void onFailure(Call<List<HashMap<String, Object>>> call, Throwable t) {
                Log.e("MyBookingsFragment", "Error fetching :", t);
            }
        });
    }

    private void setupRecyclerView() {
        if (getContext() == null) return;

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.mybookingsRecyclerView.setLayoutManager(layoutManager);

        MyBookingsListingAdapter adapter = new MyBookingsListingAdapter(bookings, myBooking -> {
            Bundle bundle = new Bundle();
            bundle.putString("title", myBooking.getName());

        });

        binding.mybookingsRecyclerView.setAdapter(adapter);

        binding.loadingLayout.setVisibility(View.GONE);
        binding.myBookingsLoggedDesign.setVisibility(View.VISIBLE);
    }


}