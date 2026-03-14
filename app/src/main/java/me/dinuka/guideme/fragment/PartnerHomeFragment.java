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
import me.dinuka.guideme.adapter.MyBookingsListingAdapter;
import me.dinuka.guideme.adapter.PartnerListingAdapter;
import me.dinuka.guideme.databinding.FragmentHomeBinding;
import me.dinuka.guideme.databinding.FragmentPartnerHomeBinding;
import me.dinuka.guideme.model.Listing;


public class PartnerHomeFragment extends Fragment {

    private FragmentPartnerHomeBinding binding;

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

        List<Listing> listings = new ArrayList<>();
        listings.add(new Listing("#9303489","Hotel","Ocean Breeze 1BR Suite| Infinity pool looking KLCC",120,"Lumpur, Malaysia","https://a0.muscache.com/im/pictures/miso/Hosting-1070670510975432808/original/fc37e80b-4db9-480c-be96-13a9fce1ef35.jpeg?im_w=1200","Mar 20, 2026 - 22, 2026"));
        listings.add(new Listing("#7567567","Service","International gourmet fusion by Brian",56,"Lumpur, Malaysia","https://a0.muscache.com/im/pictures/Mt/MtTemplate-6074083/original/3c9d07cf-cddf-489e-a850-ebe3ada9cf45.jpeg?im_w=480","Mar 20, 2026 - 22, 2026"));
        listings.add(new Listing("#3894545","Things To Do","Galle Colombo Kandy Sigiriya - Personalized Tour",98,"Galle Colombo ","https://a0.muscache.com/im/pictures/Mt/MtTemplate-4829640/original/e7aabe22-18a8-4d93-9794-dd4583a488c3.jpeg?im_w=480","Mar 20, 2026 - 22, 2026"));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.partnerListingsRecyclerView.setLayoutManager(layoutManager);

        PartnerListingAdapter adapter = new PartnerListingAdapter(listings, listing -> {
            Bundle bundle = new Bundle();
            bundle.putString("listingId", listing.getId());

        });

        binding.partnerListingsRecyclerView.setAdapter(adapter);

        binding.btnAddNewListing.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.partner_fragment_container, new ListingCategorySelectFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }
}