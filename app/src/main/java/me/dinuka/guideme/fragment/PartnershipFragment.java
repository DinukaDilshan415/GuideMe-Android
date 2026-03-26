package me.dinuka.guideme.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;
import me.dinuka.guideme.R;
import me.dinuka.guideme.api.PartnerApi;
import me.dinuka.guideme.client.RetrofitClient;
import me.dinuka.guideme.databinding.FragmentPartnershipBinding;
import me.dinuka.guideme.dto.PartnerAddressDTO;
import me.dinuka.guideme.dto.PartnerDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import taimoor.sultani.sweetalert2.Sweetalert;

public class PartnershipFragment extends Fragment {

    private FragmentPartnershipBinding binding;
    private String TAG = this.getClass().getSimpleName();
    private FirebaseAuth mAuth;
    private int currentTypeId;
    private PartnerDTO currentPartnerDTO;
    private PartnerAddressDTO addressDTO;
    private final double PREMIUM_MEMBERSHIP_FEE = 10;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentTypeId = getArguments().getInt("currentTypeId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPartnershipBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        String email = mAuth.getCurrentUser().getEmail();

        loadCurrentData(email);

        loadCurrentAddressData(email);

        binding.backBtn.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        if (currentTypeId == 1) {
            binding.currentPlan.setText("GuideMe Free");
            binding.currentPlanIcon.setImageResource(R.drawable.free_24px);
            binding.planUpgradeBtn.setVisibility(View.VISIBLE);
            binding.planCancel.setVisibility(View.GONE);

        } else if (currentTypeId == 2) {
            binding.currentPlan.setText("GuideMe Premium");
            binding.currentPlanIcon.setImageResource(R.drawable.premium_24px);
            binding.planUpgradeBtn.setVisibility(View.GONE);
            binding.planCancel.setVisibility(View.VISIBLE);

        } else {
            Log.e(TAG, "Invalid currentTypeId: " + currentTypeId);
        }

        binding.planUpgradeBtn.setOnClickListener(v -> {

            if (currentPartnerDTO.getMobile().equals("empty")
                    || currentPartnerDTO.getImageUrl().equals("empty")
                    || currentPartnerDTO.getCountry().equals("empty")
                    || currentPartnerDTO.getStatusId() != 1) {

                new Sweetalert(requireContext(), Sweetalert.WARNING_TYPE)
                        .setTitleText("Warning")
                        .setContentText("Complete Your Personal Details To Upgrade Your Plan")
                        .show();
            } else {
                if(addressDTO == null){
                    new Sweetalert(requireContext(), Sweetalert.WARNING_TYPE)
                            .setTitleText("Warning")
                            .setContentText("Complete Your Address To Upgrade Your Plan")
                            .show();
                } else {
                    processPayment();
                }
            }
        });

        binding.planCancel.setOnClickListener(v -> {
            cancelPartnership(email);
        });

    }

    private void loadCurrentData(String email) {
        Retrofit retrofit = RetrofitClient.getInstance(getContext());
        PartnerApi partnerApi = retrofit.create(PartnerApi.class);

        Call<PartnerDTO> currentPartnerCall = partnerApi.getCurrentPartner(email);
        currentPartnerCall.enqueue(new Callback<PartnerDTO>() {
            @Override
            public void onResponse(Call<PartnerDTO> call, Response<PartnerDTO> response) {
                if (response.isSuccessful()) {
                    currentPartnerDTO = response.body();
                } else {
                    Log.e(TAG, "Error fetching : " + response);
                }
            }

            @Override
            public void onFailure(Call<PartnerDTO> call, Throwable t) {
                Log.e(TAG, "Error fetching : ", t);
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
                        addressDTO = response.body();
                    } else if (response.code() == 204) {
                        addressDTO = null;
                    }
                } else {
                    Log.e(TAG, "Error fetching : " + response);
                }
            }

            @Override
            public void onFailure(Call<PartnerAddressDTO> call, Throwable t) {
                Log.e(TAG, "Error fetching : ", t);
            }
        });
    }

    private void upgradeToPremium(String email) {
        Retrofit instance = RetrofitClient.getInstance(requireContext());
        PartnerApi partnerApi = instance.create(PartnerApi.class);

        PartnerDTO dto = PartnerDTO.builder()
                .email(email)
                .build();

        Call<HashMap<String, Object>> partnerDTOCall = partnerApi.partnerUpgradePlan(dto);

        partnerDTOCall.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {

                if (response.isSuccessful()) {
                    HashMap<String, Object> responseBody = response.body();

                    new Sweetalert(requireContext(), Sweetalert.SUCCESS_TYPE)
                            .setTitleText("Success")
                            .setContentText("Partner plan upgraded successfully")
                            .show();

                    requireActivity().getSupportFragmentManager().popBackStack();

                    Log.d(TAG, "Response: " + responseBody.get("message"));
                } else {
                    Log.e(TAG, "Error: " + response);
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    private void cancelPartnership(String email) {
        Retrofit instance = RetrofitClient.getInstance(requireContext());
        PartnerApi partnerApi = instance.create(PartnerApi.class);

        PartnerDTO dto = PartnerDTO.builder()
                .email(email)
                .build();

        Call<HashMap<String, Object>> partnerDTOCall = partnerApi.partnerCancelPlan(dto);

        partnerDTOCall.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {

                if (response.isSuccessful()) {
                    HashMap<String, Object> responseBody = response.body();

                    new Sweetalert(requireContext(), Sweetalert.SUCCESS_TYPE)
                            .setTitleText("Success")
                            .setContentText("Partner plan cancelled successfully")
                            .show();

                    requireActivity().getSupportFragmentManager().popBackStack();

                    Log.d(TAG, "Response: " + responseBody.get("message"));
                } else {
                    Log.e(TAG, "Error: " + response);
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    private void processPayment() {
        InitRequest req = new InitRequest();
        req.setSandBox(true);

        req.setMerchantId("1223992");
        req.setMerchantSecret("MzU4NTA0NTMxNzc3NjE0NDU0OTQyNDgyNzgyMTIyMDEwMzYxMzkx");
        req.setCurrency("USD");
        req.setAmount(PREMIUM_MEMBERSHIP_FEE);
        req.setOrderId("GM-MF" + System.currentTimeMillis());

        req.setItemsDescription(currentPartnerDTO.getEmail() + " - Premium Membership");

        req.getCustomer().setFirstName(currentPartnerDTO.getName());
        req.getCustomer().setLastName("");
        req.getCustomer().setEmail(currentPartnerDTO.getEmail());
        req.getCustomer().setPhone(currentPartnerDTO.getMobile());
        req.getCustomer().getAddress().setAddress(addressDTO.getAddress());
        req.getCustomer().getAddress().setCity(addressDTO.getCity());
        req.getCustomer().getAddress().setCountry(currentPartnerDTO.getCountry());

        Intent intent = new Intent(getActivity(), PHMainActivity.class);
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);

        payhereLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> payhereLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            Intent data = result.getData();
            if (data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
                PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);

                if (response != null && response.isSuccess()) {

                    StatusResponse statusResponse = response.getData();

                    // upgrade to premium
                    upgradeToPremium(currentPartnerDTO.getEmail());

                    Log.i("PAYHERE", "Payment Success!");
                    Toast.makeText(getContext(), "Payment Success!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("PAYHERE", response.getData().getMessage());
                    Toast.makeText(getContext(), response.getData().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
            Log.e("PAYHERE", "Payment Canceled!");
            Toast.makeText(getContext(), "Payment Canceled!", Toast.LENGTH_SHORT).show();
        }

    });

}