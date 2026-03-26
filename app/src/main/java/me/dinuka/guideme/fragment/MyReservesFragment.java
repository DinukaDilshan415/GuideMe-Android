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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.dinuka.guideme.R;
import me.dinuka.guideme.adapter.ItemReservesListingAdapter;
import me.dinuka.guideme.adapter.MyBookingsListingAdapter;
import me.dinuka.guideme.api.BookingApi;
import me.dinuka.guideme.client.RetrofitClient;
import me.dinuka.guideme.databinding.FragmentMyReservesBinding;
import me.dinuka.guideme.model.ItemReserve;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MyReservesFragment extends Fragment {

    private FragmentMyReservesBinding binding;
    private String TAG = this.getClass().getSimpleName();
    private int listingId;
    private String listingType;
    private String listingTitle;
    private List<ItemReserve> itemReserves = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            listingId = getArguments().getInt("listingId");
            listingType = getArguments().getString("listingType");
            listingTitle = getArguments().getString("listingTitle");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyReservesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView imageView = binding.loadingImage;

        Glide.with(this)
                .load(R.drawable.loadingbuffering) // or R.raw.your_gif_file_name
                .into(imageView);

        binding.toolbarTitle.setText(listingTitle);

        binding.backBtn.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        loadItemReserves(listingId);
    }

    private void loadItemReserves(int listingId) {
        Retrofit retrofit = RetrofitClient.getInstance(getContext());
        BookingApi bookingApi = retrofit.create(BookingApi.class);

        Call<List<HashMap<String, Object>>> bookingCall = bookingApi.getItemReserves(listingId);
        bookingCall.enqueue(new Callback<List<HashMap<String, Object>>>() {
            @Override
            public void onResponse(Call<List<HashMap<String, Object>>> call, Response<List<HashMap<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.code() == 200) {
                        List<HashMap<String, Object>> responseBody = response.body();

                        for (HashMap<String, Object> l : responseBody) {
                            itemReserves.add(new ItemReserve(
                                    ((Number) l.get("id")).intValue(),
                                    l.get("booking_id").toString(),
                                    l.get("title").toString(),
                                    l.get("date_in").toString(),
                                    l.get("date_out").toString(),
                                    l.get("created_at").toString(),
                                    ((Number) l.get("adult_count")).intValue(),
                                    ((Number) l.get("child_count")).intValue(),
                                    ((Number) l.get("room_count")).intValue(),
                                    l.get("status").toString(),
                                    l.get("user_name").toString(),
                                    l.get("user_email").toString(),
                                    l.get("user_mobile").toString(),
                                    l.get("user_country").toString(),
                                    ((Number) l.get("categoryId")).intValue()
                            ));
                        }

                        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        binding.bookingsRecyclerView.setLayoutManager(layoutManager);

                        ItemReservesListingAdapter adapter = new ItemReservesListingAdapter(itemReserves, itemReserve -> {
                            Bundle bundle = new Bundle();
                            bundle.putString("title", itemReserve.getTitle());

                        });

                        binding.bookingsRecyclerView.setAdapter(adapter);

                        binding.loadingLayout.setVisibility(View.GONE);
                        binding.noContentLayout.setVisibility(View.GONE);
                        binding.contentLayout.setVisibility(View.VISIBLE);
                    } else if (response.code() == 204) {
                        binding.loadingLayout.setVisibility(View.GONE);
                        binding.noContentLayout.setVisibility(View.VISIBLE);
                    }

                } else {
                    Log.e(TAG, "Error fetching :" + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<HashMap<String, Object>>> call, Throwable t) {
                Log.e(TAG, "Error fetching :", t);
            }
        });
    }
}
