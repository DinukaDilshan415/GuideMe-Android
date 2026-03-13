package me.dinuka.guideme.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.dinuka.guideme.R;
import me.dinuka.guideme.adapter.HotelListingAdapter;
import me.dinuka.guideme.adapter.MyBookingsListingAdapter;
import me.dinuka.guideme.databinding.FragmentMyBookingsBinding;
import me.dinuka.guideme.model.MyBooking;

public class MyBookingsFragment extends Fragment {

    private FragmentMyBookingsBinding binding;

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

        List<MyBooking> myBookings = new ArrayList<>();
        myBookings.add(new MyBooking("#38472384","Room in boutique hotel in Ahangama",45,"Ahangama West, Sri Lanka",4.5,"https://a0.muscache.com/im/pictures/hosting/Hosting-1616481404202153296/original/c8166786-ba76-4847-b7bf-e40af1e50562.jpeg?im_w=1200","3/20/2026","3/20/2026",2,1,"3/20/2026 8:09AM"));
        myBookings.add(new MyBooking("#343545","Cozy Upstairs Suite•10 Mins to Airport•Pvt Balcony",74,"Kimbulapitiya Negombo, Sri Lanka",3,"https://a0.muscache.com/im/pictures/hosting/Hosting-667189615689084189/original/9ece141d-d9b4-4903-870d-f9f68254e398.jpeg?im_w=1200","3/20/2026","3/20/2026",2,1,"3/20/2026 8:09AM"));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.mybookingsRecyclerView.setLayoutManager(layoutManager);

        MyBookingsListingAdapter adapter = new MyBookingsListingAdapter(myBookings, myBooking -> {
            Bundle bundle = new Bundle();
            bundle.putString("hotelName", myBooking.getId());

        });

        binding.mybookingsRecyclerView.setAdapter(adapter);
    }
}