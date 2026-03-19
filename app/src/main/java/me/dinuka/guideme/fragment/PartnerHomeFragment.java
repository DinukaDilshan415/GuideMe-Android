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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import me.dinuka.guideme.R;
import me.dinuka.guideme.activity.PartnerRegisterActivity;
import me.dinuka.guideme.adapter.MyBookingsListingAdapter;
import me.dinuka.guideme.adapter.PartnerListingAdapter;
import me.dinuka.guideme.api.PartnerApi;
import me.dinuka.guideme.client.RetrofitClient;
import me.dinuka.guideme.databinding.FragmentHomeBinding;
import me.dinuka.guideme.databinding.FragmentPartnerHomeBinding;
import me.dinuka.guideme.dto.PartnerAddressDTO;
import me.dinuka.guideme.dto.PartnerDTO;
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

            addNewListing(firebaseAuth.getCurrentUser().getEmail());

        });
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
                            || currentPartnerDTO.getStatusId() != 1){

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
                    if (response.code() == 200){

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