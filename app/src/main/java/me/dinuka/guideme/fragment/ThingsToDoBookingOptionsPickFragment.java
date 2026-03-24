package me.dinuka.guideme.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;
import me.dinuka.guideme.R;
import me.dinuka.guideme.adapter.RoomTypeSelectAdapter;
import me.dinuka.guideme.api.BookingApi;
import me.dinuka.guideme.api.HotelApi;
import me.dinuka.guideme.api.ThingsToDoApi;
import me.dinuka.guideme.api.UserApi;
import me.dinuka.guideme.client.RetrofitClient;
import me.dinuka.guideme.databinding.FragmentThingsToDoBookingOptionsPickBinding;
import me.dinuka.guideme.dto.UserAddressDTO;
import me.dinuka.guideme.dto.UserDTO;
import me.dinuka.guideme.model.RoomType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import taimoor.sultani.sweetalert2.Sweetalert;


public class ThingsToDoBookingOptionsPickFragment extends Fragment {

    private FragmentThingsToDoBookingOptionsPickBinding binding;
    private HashMap<String, Object> detailsMap;
    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;
    private int guestsCount = 1;
    double price;
    double bookingFee;
    String title;
    int perDayBookings;
    Date dateIn;
    Date dateOut;
    private UserDTO currentUserDTO;
    private UserAddressDTO addressDTO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            detailsMap = (HashMap<String, Object>) getArguments().getSerializable("detailsMap");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentThingsToDoBookingOptionsPickBinding.inflate(inflater, container, false);
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

        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        setupCounters();
        binding.guestChangeBtn.setOnClickListener(v -> {
            if (binding.guestsChangeBody.getVisibility() == View.GONE) {
                binding.guestsChangeBody.setVisibility(View.VISIBLE);
                binding.guestChangeBtn.setText("");
                binding.guestChangeBtn.setIconResource(R.drawable.up_24px);
            } else {
                binding.guestsChangeBody.setVisibility(View.GONE);
                binding.guestChangeBtn.setIconResource(0);
                binding.guestChangeBtn.setText("Change");
            }
        });

        binding.datePickerBtn.setOnClickListener(v -> {
            showDateRangePicker();
        });

        binding.btnGuestsApply.setOnClickListener(v -> {
            binding.thingToDoCountGuest.setText(guestsCount + " Guests");
        });

        loaddetails();

        loadCurrentData(firebaseAuth.getCurrentUser().getEmail());

        loadCurrentAddressData(firebaseAuth.getCurrentUser().getEmail());

        binding.proceedToReserveBtn.setOnClickListener(v -> {
            checkEligibility();
        });

    }

    private void checkEligibility() {

        if (dateIn == null || dateOut == null) {
            new Sweetalert(requireContext(), Sweetalert.WARNING_TYPE)
                    .setTitleText("Warning")
                    .setContentText("Please select Date you want")
                    .show();
            return;
        }

        if (guestsCount< 1) {
            new Sweetalert(requireContext(), Sweetalert.WARNING_TYPE)
                    .setTitleText("Warning")
                    .setContentText("Please select at least one guest")
                    .show();
            return;
        }

        checkAvailability();
    }

    private void checkAvailability() {
        int id = ((Number) detailsMap.get("id")).intValue();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateInStr = sdf.format(dateIn);
        String dateOutStr = sdf.format(dateOut);

        Retrofit retrofit = RetrofitClient.getInstance(getContext());
        ThingsToDoApi thingsToDoApi = retrofit.create(ThingsToDoApi.class);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("thingstodo_id", id);
        hashMap.put("guest_count", guestsCount);
        hashMap.put("date_in", dateInStr);
        hashMap.put("date_out", dateOutStr);

        Call<HashMap<String, Object>> hashMapCall = thingsToDoApi.checkAvailability(hashMap);
        hashMapCall.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                if (response.isSuccessful()) {
                    HashMap<String, Object> body = response.body();
                    if (response.code() == 200) {
                        boolean success = (boolean) body.get("success");
                        Log.i("ThingsToDoBookingOptionsPickFragment", "Response: " + body.get("message"));

                        if (success){

                            processPayment();

                        } else {
                            new Sweetalert(requireContext(), Sweetalert.WARNING_TYPE)
                                    .setTitleText("Warning")
                                    .setContentText(body.get("message").toString())
                                    .show();
                        }

                    } else {
                        Log.e("ThingsToDoBookingOptionsPickFragment", "Error fetching : " + response);
                    }
                } else {
                    Log.e("ThingsToDoBookingOptionsPickFragment", "Error fetching : " + response);
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                Log.e("ThingsToDoBookingOptionsPickFragment", "Error fetching : ", t);
            }
        });
    }

    private void processPayment() {
        InitRequest req = new InitRequest();
        req.setSandBox(true);

        req.setMerchantId("1223992");
        req.setMerchantSecret("MzU4NTA0NTMxNzc3NjE0NDU0OTQyNDgyNzgyMTIyMDEwMzYxMzkx");
        req.setCurrency("USD");
        req.setAmount(bookingFee);
        req.setOrderId("GM-" + System.currentTimeMillis());

        req.setItemsDescription(detailsMap.get("title").toString());

        req.getCustomer().setFirstName(currentUserDTO.getName());
        req.getCustomer().setLastName("");
        req.getCustomer().setEmail(currentUserDTO.getEmail());
        req.getCustomer().setPhone(currentUserDTO.getMobile());
        req.getCustomer().getAddress().setAddress(addressDTO.getAddress());
        req.getCustomer().getAddress().setCity(addressDTO.getCity());
        req.getCustomer().getAddress().setCountry(currentUserDTO.getCountry());

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

                    // Save Booking in DB
                    addNewBooking(firebaseAuth.getCurrentUser().getEmail(), statusResponse);

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

    private void addNewBooking(String email, StatusResponse statusResponse) {
        Sweetalert pDialog = new Sweetalert(requireContext(), Sweetalert.PROGRESS_TYPE);
        pDialog.setTitleText("Booking On Process...");
        pDialog.setCancelable(false);
        pDialog.show();

        Retrofit instance = RetrofitClient.getInstance(requireContext());
        BookingApi bookingApi = instance.create(BookingApi.class);

        HashMap<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("thingstodo_id", detailsMap.get("id"));
        data.put("title", detailsMap.get("title"));
        data.put("price", price);
        data.put("booking_fee", bookingFee);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        data.put("date_in", sdf.format(dateIn));
        data.put("date_out", sdf.format(dateOut));
        data.put("guest_count", guestsCount);
        data.put("categoryId", 2);

        Call<HashMap<String, Object>> bookingCall = bookingApi.addNewBooking(data);

        bookingCall.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                pDialog.dismissWithAnimation();
                if (response.isSuccessful()) {
                    HashMap<String, Object> responseBody = response.body();
                    Log.d("ThingsToDoBookingOptionsPickFragment", "Response: " + responseBody.get("message"));

                    new Sweetalert(requireContext(), Sweetalert.SUCCESS_TYPE)
                            .setTitleText("Success")
                            .setContentText("New Booking Added Successfully")
                            .show();

                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new MyBookingsFragment())
                            .addToBackStack(null)
                            .commit();

                    BottomNavigationView navigationView = requireActivity().findViewById(R.id.bottomNavigationView);
                    navigationView.getMenu().getItem(2).setChecked(true);
                } else {
                    Log.e("ThingsToDoBookingOptionsPickFragment", "Error: " + response);
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                Log.e("ThingsToDoBookingOptionsPickFragment", "Error: " + t.getMessage());
                pDialog.dismissWithAnimation();
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

                } else {
                    Log.e("ThingsToDoBookingOptionsPickFragment", "Error fetching : " + response);
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Log.e("ThingsToDoBookingOptionsPickFragment", "Error fetching : ", t);
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
                        addressDTO = response.body();
                    }
                } else {
                    Log.e("ThingsToDoBookingOptionsPickFragment", "Error fetching : " + response);
                }
            }

            @Override
            public void onFailure(Call<UserAddressDTO> call, Throwable t) {
                Log.e("ThingsToDoBookingOptionsPickFragment", "Error fetching : ", t);
            }
        });
    }

    private void setupCounters() {
        // Guests Row
        binding.rowGuests.tvLabel.setText("Guest");
        binding.rowGuests.tvCount.setText(String.valueOf(guestsCount));

        binding.rowGuests.btnPlus.setOnClickListener(v -> {
            guestsCount++;
            binding.rowGuests.tvCount.setText(String.valueOf(guestsCount));
        });

        binding.rowGuests.btnMinus.setOnClickListener(v -> {
            if (guestsCount > 1) { // Prevents going below 1
                guestsCount--;
                binding.rowGuests.tvCount.setText(String.valueOf(guestsCount));
            }
        });
    }

    public void showDateRangePicker() {
        long today = MaterialDatePicker.todayInUtcMilliseconds();

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(DateValidatorPointForward.from(today));

        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select Date");
        builder.setCalendarConstraints(constraintsBuilder.build());

        MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            if (selection.first != null && selection.second != null) {
                dateIn = new Date(selection.first);
                dateOut = new Date(selection.second);

                binding.thingToDoCheckInDate.setText(formatDate(selection.first));
            }
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    private String formatDate(Long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    private void loaddetails() {
        title = detailsMap.get("title").toString();
        String locationName = detailsMap.get("location_name").toString();
        price = Double.parseDouble(detailsMap.get("price").toString());
        double rating = Double.parseDouble(detailsMap.get("rating").toString());

        perDayBookings = ((Number) detailsMap.get("per_day_bookings")).intValue();

        binding.bThingsToDoName.setText(title);
        binding.bThingToDoLocation.setText(locationName);
        binding.bThingToDoRating.setText(String.valueOf(rating));
        binding.bThingToDoRatingBar.setRating((float) rating);
        binding.thingToDoBookingSubtotal.setText("$ " + String.format("%.2f",price));

        List<String> images = (List<String>) detailsMap.get("imageUris");
        String imageUrl = images.get(0);
        String fullPath = "things-to-do-images/" + imageUrl;

        storage.getReference(fullPath).getDownloadUrl()
                .addOnSuccessListener(uri -> {

                    Glide.with(ThingsToDoBookingOptionsPickFragment.this)
                            .load(uri)
                            .centerCrop()
                            .into(binding.bThingToDoImage);

                })
                .addOnFailureListener(e -> {
                    Log.e("ThingsToDoBookingOptionsPickFragment", "Error getting image URL", e);
                });

        loadPaymentDetails();
    }

    private void loadPaymentDetails() {
        double bookingCommisonPrecentage = 5;
        bookingFee = Math.round(price * bookingCommisonPrecentage / 100);

        binding.thingToDoBookingFee.setText("$ " + String.format("%.2f",bookingFee));

        double total = price + bookingFee;
        binding.thingToDoBookingTotal.setText("$ "+String.format("%.2f",total));
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