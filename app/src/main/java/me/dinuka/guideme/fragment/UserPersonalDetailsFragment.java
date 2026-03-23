package me.dinuka.guideme.fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;

import me.dinuka.guideme.R;
import me.dinuka.guideme.api.PartnerApi;
import me.dinuka.guideme.api.UserApi;
import me.dinuka.guideme.client.RetrofitClient;
import me.dinuka.guideme.databinding.FragmentUserPersonalDetailsBinding;
import me.dinuka.guideme.dto.PartnerAddressDTO;
import me.dinuka.guideme.dto.PartnerDTO;
import me.dinuka.guideme.dto.UserAddressDTO;
import me.dinuka.guideme.dto.UserDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserPersonalDetailsFragment extends Fragment {
private FragmentUserPersonalDetailsBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private UserDTO currentUserDTO;
    private UserAddressDTO addressDTO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserPersonalDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.GONE);

        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        binding.backBtn.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        binding.mobileLayoutToggle.setOnClickListener(v -> {
            if (binding.mobileLayout.getVisibility() == View.GONE) {
                binding.mobileLayout.setVisibility(View.VISIBLE);
                binding.mobileNumber.setVisibility(View.GONE);
                binding.mobileTitleArrow.setRotation(0);
            } else {
                binding.mobileLayout.setVisibility(View.GONE);
                binding.mobileNumber.setVisibility(View.VISIBLE);
                binding.mobileTitleArrow.setRotation(180);
            }
        });

        binding.addressLayoutToggle.setOnClickListener(v -> {
            if (binding.addressLayout.getVisibility() == View.GONE) {
                binding.addressLayout.setVisibility(View.VISIBLE);
                binding.address.setVisibility(View.GONE);
                binding.addressTitleArrow.setRotation(0);
            } else {
                binding.addressLayout.setVisibility(View.GONE);
                binding.address.setVisibility(View.VISIBLE);
                binding.addressTitleArrow.setRotation(180);
            }
        });

        binding.countryLayoutToggle.setOnClickListener(v -> {
            if (binding.countryLayout.getVisibility() == View.GONE) {
                binding.countryLayout.setVisibility(View.VISIBLE);
                binding.country.setVisibility(View.GONE);
                binding.countryTitleArrow.setRotation(0);
            } else {
                binding.countryLayout.setVisibility(View.GONE);
                binding.country.setVisibility(View.VISIBLE);
                binding.countryTitleArrow.setRotation(180);
            }
        });

        binding.mobileLayoutToggle.performClick();
        binding.addressLayoutToggle.performClick();
        binding.countryLayoutToggle.performClick();

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        String email = currentUser.getEmail();
        binding.currentEmail.setText(email);

        loadCurrentData(email);

        loadCurrentAddressData(email);

        CountryCodePicker ccp = binding.ccp;
        CountryCodePicker countrySelector = binding.countrySelector;

        binding.numberSaveBtn.setOnClickListener(v -> {
            String number = binding.editNumber.getText().toString().trim();
            if (!number.isEmpty()) {
                String phoneNumber = ccp.getSelectedCountryCodeWithPlus() + number;
                binding.mobileNumber.setText(phoneNumber);

                updateMobileNumber(phoneNumber,email);
                binding.mobileLayoutToggle.performClick();
            } else {
                binding.editNumber.setError("Please enter a phone number");
                binding.editNumber.requestFocus();
            }

        });

        binding.countrySaveBtn.setOnClickListener(v -> {
            String countryName = countrySelector.getSelectedCountryName()+"-"+countrySelector.getSelectedCountryNameCode();

            binding.country.setText(countryName);

            updateCountry(countryName,email);
            binding.countryLayoutToggle.performClick();
        });

        binding.addressSaveBtn.setOnClickListener(v -> {
            String address = binding.editAddress.getText().toString().trim();
            String city = binding.editCity.getText().toString().trim();
            String postalCode = binding.editPostalCode.getText().toString().trim();

            if (address.isEmpty()){
                binding.editAddress.setError("Please enter an address");
                binding.editAddress.requestFocus();
                return;
            }

            if (city.isEmpty()){
                binding.editCity.setError("Please enter a city");
                binding.editCity.requestFocus();
                return;
            }

            if (postalCode.isEmpty()){
                binding.editPostalCode.setError("Please enter a postal code");
                binding.editPostalCode.requestFocus();
                return;
            }

            if (!address.isEmpty() && !city.isEmpty() && !postalCode.isEmpty()) {
                binding.address.setText(address + ", " + city + ", " + postalCode);

                saveOrUpdateAddress(email, address, city, postalCode);

                binding.addressLayoutToggle.performClick();
            }
        });

    }

    private void loadCurrentData(String email) {

        Retrofit retrofit = RetrofitClient.getInstance(getContext());
        UserApi userApi = retrofit.create(UserApi.class);

        Call<UserDTO> currentUserCall = userApi.getCurrentUser(email);
        currentUserCall.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful()) {
                    currentUserDTO = response.body();
                    if (currentUserDTO.getMobile().equals("empty")){
                        binding.mobileNumber.setText("Add Your Phone Number");
                    } else {
                        binding.mobileNumber.setText(currentUserDTO.getMobile());
                    }
                    String country = currentUserDTO.getCountry();

                    if (!country.equals("empty")) {
                        String[] parts = country.split("-");
                        binding.country.setText(country);
                        binding.countrySelector.setCountryForNameCode(parts[1]);
                    }
                } else {
                    Log.e("UserPersonalDetailsFragment", "Error fetching : " + response);
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Log.e("UserPersonalDetailsFragment", "Error fetching : ", t);
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
                    if (response.code() == 200){
                        addressDTO = response.body();

                        binding.address.setText(addressDTO.getAddress() + ", " + addressDTO.getCity() + ", " + addressDTO.getPostalCode());

                        binding.editAddress.setText(addressDTO.getAddress());
                        binding.editCity.setText(addressDTO.getCity());
                        binding.editPostalCode.setText(addressDTO.getPostalCode());

                    }
                } else {
                    Log.e("UserPersonalDetailsFragment", "Error fetching : " + response);
                }
            }

            @Override
            public void onFailure(Call<UserAddressDTO> call, Throwable t) {
                Log.e("UserPersonalDetailsFragment", "Error fetching : ", t);
            }
        });
    }

    private void updateMobileNumber(String phoneNumber, String email) {
        Retrofit instance = RetrofitClient.getInstance(requireContext());

        UserApi userApi = instance.create(UserApi.class);

        UserDTO dto = UserDTO.builder()
                .email(email)
                .mobile(phoneNumber)
                .build();

        Call<HashMap<String, Object>> userDTOCall = userApi.userUpdateMobile(dto);

        userDTOCall.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {

                if (response.isSuccessful()) {
                    HashMap<String, Object> responseBody = response.body();
                    Log.d("UserPersonalDetailsFragment", "Response: " + responseBody.get("message"));
                } else {
                    Log.e("UserPersonalDetailsFragment", "Error: " + response);
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                Log.e("UserPersonalDetailsFragment", "Error: " + t.getMessage());
            }
        });
    }

    private void updateCountry(String countryName, String email) {
        Retrofit instance = RetrofitClient.getInstance(requireContext());
        UserApi userApi = instance.create(UserApi.class);

        UserDTO dto = UserDTO.builder()
                .email(email)
                .country(countryName)
                .build();

        Call<HashMap<String, Object>> userDTOCall= userApi.userUpdateCountry(dto);

        userDTOCall.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {

                if (response.isSuccessful()) {
                    HashMap<String, Object> responseBody = response.body();
                    Log.d("UserPersonalDetailsFragment", "Response: " + responseBody.get("message"));
                } else {
                    Log.e("UserPersonalDetailsFragment", "Error: " + response);
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                Log.e("UserPersonalDetailsFragment", "Error: " + t.getMessage());
            }
        });
    }

    private void saveOrUpdateAddress(String email, String address, String city, String postalCode) {
        Retrofit instance = RetrofitClient.getInstance(requireContext());
        UserApi userApi = instance.create(UserApi.class);

        UserAddressDTO dto = UserAddressDTO.builder()
                .address(address)
                .city(city)
                .postalCode(postalCode)
                .userEmail(email)
                .build();

        Call<HashMap<String, Object>> userDTOCall = userApi.userSaveOrUpdateAddress(dto);

        userDTOCall.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {

                if (response.isSuccessful()) {
                    HashMap<String, Object> responseBody = response.body();
                    Log.d("UserPersonalDetailsFragment", "Response: " + responseBody.get("message"));
                } else {
                    Log.e("UserPersonalDetailsFragment", "Error: " + response);
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                Log.e("UserPersonalDetailsFragment", "Error: " + t.getMessage());
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.GONE);
    }
}