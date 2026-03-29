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
import androidx.recyclerview.widget.LinearLayoutManager;

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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;
import me.dinuka.guideme.R;
import me.dinuka.guideme.adapter.FacilitySelectAdapter;
import me.dinuka.guideme.adapter.RoomTypeSelectAdapter;
import me.dinuka.guideme.api.BookingApi;
import me.dinuka.guideme.api.HotelApi;
import me.dinuka.guideme.api.ListingApi;
import me.dinuka.guideme.api.UserApi;
import me.dinuka.guideme.client.RetrofitClient;
import me.dinuka.guideme.databinding.FragmentBookingOptionsPickBinding;
import me.dinuka.guideme.dto.FacilityDTO;
import me.dinuka.guideme.dto.UserAddressDTO;
import me.dinuka.guideme.dto.UserDTO;
import me.dinuka.guideme.model.Facility;
import me.dinuka.guideme.model.RoomType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import taimoor.sultani.sweetalert2.Sweetalert;

public class BookingOptionsPickFragment extends Fragment {
    private FragmentBookingOptionsPickBinding binding;
    private HashMap<String, Object> detailsMap;
    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;
    private int roomCount = 1;
    private int adultCount = 2;
    private int childCount = 0;
    double price;
    double bookingFee;
    double subtotal;
    int nights = 1;
    String title;
    int perRoomCount;
    int listing_room_count;
    Date dateIn;
    Date dateOut;
    private List<RoomType> roomTypeList;
    RoomTypeSelectAdapter adapter;
    RoomType selectedRoomType;
    private Gson gson = new Gson();
    private boolean paymentActive;
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
        binding = FragmentBookingOptionsPickBinding.inflate(inflater, container, false);
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

        binding.roomsChangeBtn.setOnClickListener(v -> {
            if (binding.roomsChangeBody.getVisibility() == View.GONE) {
                binding.roomsChangeBody.setVisibility(View.VISIBLE);
                binding.roomsChangeBtn.setText("");
                binding.roomsChangeBtn.setIconResource(R.drawable.up_24px);
            } else {
                binding.roomsChangeBody.setVisibility(View.GONE);
                binding.roomsChangeBtn.setIconResource(0);
                binding.roomsChangeBtn.setText("Change");
            }
        });

        binding.datePickerBtn.setOnClickListener(v -> {
            showDateRangePicker();
        });

        binding.btnGuestsApply.setOnClickListener(v -> {
            binding.hotelCountAdult.setText(adultCount + " adult");
            binding.hotelCountChild.setText(childCount + " child");
        });

        binding.btnRoomsApply.setOnClickListener(v -> {
            binding.bookingHotelRoomCount.setText(roomCount + " ");
            selectedRoomType = adapter.getSelectedRoomType();
            if (selectedRoomType != null) {
                binding.bRoomType.setText(selectedRoomType.getType());
            }
        });

        loaddetails();

        loadCurrentData(firebaseAuth.getCurrentUser().getEmail());

        loadCurrentAddressData(firebaseAuth.getCurrentUser().getEmail());

        binding.proceedToReserveBtn.setOnClickListener(v -> {

            checkEligibility();

            if (selectedRoomType != null) {
                Log.d("Selection", "Selected Room ID: " + selectedRoomType.getId());
                // Proceed with 'selected'
            } else {
                // Show warning that a selection is required
            }

        });
    }

    private void checkEligibility() {

        if (dateIn == null || dateOut == null) {
            new Sweetalert(requireContext(), Sweetalert.WARNING_TYPE)
                    .setTitleText("Warning")
                    .setContentText("Please select Dates you want to stay")
                    .show();
            return;
        }

        if (adultCount < 1 || roomCount < 1) {
            new Sweetalert(requireContext(), Sweetalert.WARNING_TYPE)
                    .setTitleText("Warning")
                    .setContentText("Please select at least one adult and one room")
                    .show();
            return;
        }

        if (selectedRoomType == null) {
            new Sweetalert(requireContext(), Sweetalert.WARNING_TYPE)
                    .setTitleText("Warning")
                    .setContentText("Please select a room type")
                    .show();
            return;
        }

        if (adultCount > perRoomCount) {
            int wantRoomCount = (int) Math.ceil((double) adultCount / perRoomCount);
            if (roomCount < wantRoomCount) {
                new Sweetalert(requireContext(), Sweetalert.WARNING_TYPE)
                        .setTitleText("Warning")
                        .setContentText("You Need to Book at least " + wantRoomCount + " Rooms for " + adultCount + " Adults")
                        .show();
                return;
            }

        }

        checkAvailability();
    }

    private void checkAvailability() {
        int id = ((Number) detailsMap.get("id")).intValue();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateInStr = sdf.format(dateIn);
        String dateOutStr = sdf.format(dateOut);

        Retrofit retrofit = RetrofitClient.getInstance(getContext());
        HotelApi hotelApi = retrofit.create(HotelApi.class);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("hotel_id", id);
        hashMap.put("room_count", roomCount);
        hashMap.put("date_in", dateInStr);
        hashMap.put("date_out", dateOutStr);

        Call<HashMap<String, Object>> hashMapCall = hotelApi.checkAvailability(hashMap);
        hashMapCall.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                if (response.isSuccessful()) {
                    HashMap<String, Object> body = response.body();
                    if (response.code() == 200) {
                        boolean success = (boolean) body.get("success");
                        Log.i("BookingOptionsPickFragment", "Response: " + body.get("message"));

                        if (success){
                           processPayment();
                        } else {
                            new Sweetalert(requireContext(), Sweetalert.WARNING_TYPE)
                                    .setTitleText("Warning")
                                    .setContentText(body.get("message").toString())
                                    .show();
                        }

                    } else {
                        Log.e("BookingOptionsPickFragment", "Error fetching : " + response);
                    }
                } else {
                    Log.e("BookingOptionsPickFragment", "Error fetching : " + response);
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                Log.e("BookingOptionsPickFragment", "Error fetching : ", t);
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
        data.put("hotel_id", detailsMap.get("id"));
        data.put("title", detailsMap.get("title"));
        data.put("price", price);
        data.put("booking_fee", bookingFee);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        data.put("date_in", sdf.format(dateIn));
        data.put("date_out", sdf.format(dateOut));
        data.put("adultCount", adultCount);
        data.put("childCount", childCount);
        data.put("roomCount", roomCount);
        data.put("nights", nights);
        data.put("roomTypeId", selectedRoomType.getId());
        data.put("categoryId", 1);

        Call<HashMap<String, Object>> bookingCall = bookingApi.addNewBooking(data);

        bookingCall.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                pDialog.dismissWithAnimation();
                if (response.isSuccessful()) {
                    HashMap<String, Object> responseBody = response.body();
                    Log.d("BookingOptionsPickFragment", "Response: " + responseBody.get("message"));

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
                    Log.e("BookingOptionsPickFragment", "Error: " + response);
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                Log.e("BookingOptionsPickFragment", "Error: " + t.getMessage());
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
                    Log.e("BookingOptionsPickFragment", "Error fetching : " + response);
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Log.e("BookingOptionsPickFragment", "Error fetching : ", t);
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
                    Log.e("BookingOptionsPickFragment", "Error fetching : " + response);
                }
            }

            @Override
            public void onFailure(Call<UserAddressDTO> call, Throwable t) {
                Log.e("BookingOptionsPickFragment", "Error fetching : ", t);
            }
        });
    }

    private void loaddetails() {
        title = detailsMap.get("title").toString();
        String locationName = detailsMap.get("location_name").toString();
        price = Double.parseDouble(detailsMap.get("price").toString());
        double rating = Double.parseDouble(detailsMap.get("rating").toString());

        perRoomCount = ((Number) detailsMap.get("per_room_count")).intValue();
        listing_room_count = ((Number) detailsMap.get("room_count")).intValue();

        binding.bHotelName.setText(title);
        binding.bHotelLocation.setText(locationName);
        binding.bHotelRating.setText(String.valueOf(rating));
        binding.bHotelRatingBar.setRating((float) rating);
        binding.hotelBookingSubtotal.setText("$ " + String.format("%.2f", price * nights));

        List<String> images = (List<String>) detailsMap.get("imageUris");
        String imageUrl = images.get(0);
        String fullPath = "hotel-images/" + imageUrl;

        storage.getReference(fullPath).getDownloadUrl()
                .addOnSuccessListener(uri -> {

                    Glide.with(BookingOptionsPickFragment.this)
                            .load(uri)
                            .centerCrop()
                            .into(binding.bHotelImage);

                })
                .addOnFailureListener(e -> {
                    Log.e("BookingOptionsPickFragment", "Error getting image URL", e);
                });

        loadPaymentDetails();

        loadRoomTypes();
    }

    private void loadRoomTypes() {
        if (roomTypeList == null) {
            roomTypeList = new ArrayList<>();
        } else {
            roomTypeList.clear();
        }

        List<HashMap<String, Object>> rawroomTypes = (List<HashMap<String, Object>>) detailsMap.get("roomTypes");

        if (rawroomTypes != null) {
            for (Object obj : rawroomTypes) {
                String json = gson.toJson(obj);
                RoomType roomType = gson.fromJson(json, RoomType.class);

                roomTypeList.add(new RoomType(roomType.getId(), roomType.getType(), false));
            }
        }

        adapter = new RoomTypeSelectAdapter(roomTypeList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.roomTypeRecyclerView.setLayoutManager(layoutManager);
        binding.roomTypeRecyclerView.setAdapter(adapter);
    }

    private void loadPaymentDetails() {
        double bookingCommisonPrecentage = 5;
        bookingFee = Math.round((price * bookingCommisonPrecentage) / 100);

        binding.hotelBookingFee.setText("$ " + String.format("%.2f", bookingFee));

        double total = price + bookingFee;
        binding.hotelBookingTotal.setText("$ " + String.format("%.2f", total));
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

                LocalDate start = dateIn.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate end = dateOut.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                nights = Math.toIntExact(ChronoUnit.DAYS.between(start, end));

                if (nights > 0) {
                    subtotal = price * nights;

                    binding.nightCount.setText("( " + nights + " Nights )");
                    binding.hotelBookingSubtotal.setText("$ " + String.format("%.2f", subtotal));
                    binding.hotelBookingTotal.setText("$ " + String.format("%.2f", subtotal + bookingFee));

                    binding.hotelCheckInDate.setText(formatDate(selection.first));
                    binding.hotwlCheckOutDate.setText(formatDate(selection.second));
                } else {
                    dateIn = null;
                    dateOut = null;
                    Toast.makeText(getContext(), "Invalid Date Range", Toast.LENGTH_SHORT).show();
                }
            }
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    private String formatDate(Long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    private void setupCounters() {
        // Child Row
        binding.rowChildren.tvLabel.setText("Childrens");
        binding.rowChildren.tvCount.setText(String.valueOf(childCount));

        binding.rowChildren.btnPlus.setOnClickListener(v -> {
            childCount++;
            binding.rowChildren.tvCount.setText(String.valueOf(childCount));
        });

        binding.rowChildren.btnMinus.setOnClickListener(v -> {
            if (childCount > 1) {
                childCount--;
                binding.rowChildren.tvCount.setText(String.valueOf(childCount));
            }
        });

        // Adult Row
        binding.rowAdults.tvLabel.setText("Adult");
        binding.rowAdults.tvCount.setText(String.valueOf(adultCount));

        binding.rowAdults.btnPlus.setOnClickListener(v -> {
            adultCount++;
            binding.rowAdults.tvCount.setText(String.valueOf(adultCount));
        });

        binding.rowAdults.btnMinus.setOnClickListener(v -> {
            if (adultCount > 1) {
                adultCount--;
                binding.rowAdults.tvCount.setText(String.valueOf(adultCount));
            }
        });

        // Room Row
        binding.rowRooms.tvLabel.setText("Rooms");
        binding.rowRooms.tvCount.setText(String.valueOf(roomCount));

        binding.rowRooms.btnPlus.setOnClickListener(v -> {
            roomCount++;
            binding.rowRooms.tvCount.setText(String.valueOf(roomCount));
        });

        binding.rowRooms.btnMinus.setOnClickListener(v -> {
            if (roomCount > 1) {
                roomCount--;
                binding.rowRooms.tvCount.setText(String.valueOf(roomCount));
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