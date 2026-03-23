package me.dinuka.guideme.fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import me.dinuka.guideme.R;
import me.dinuka.guideme.databinding.FragmentServiceBookingOptionsPickBinding;

public class ServiceBookingOptionsPickFragment extends Fragment {
private FragmentServiceBookingOptionsPickBinding binding;
    private HashMap<String, Object> detailsMap;
    private FirebaseStorage storage;
    private int guestsCount = 1;
    double price;
    double bookingFee;
    String title;
    int perDayBookings;
    Date dateIn;
    Date dateOut;

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
        binding = FragmentServiceBookingOptionsPickBinding.inflate(inflater, container, false);
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
            binding.serviceCountGuest.setText(guestsCount + " Guests");
        });

        loaddetails();
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

                binding.serviceCheckInDate.setText(formatDate(selection.first));
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

        binding.bServiceName.setText(title);
        binding.bServiceLocation.setText(locationName);
        binding.bServiceRating.setText(String.valueOf(rating));
        binding.bServiceRatingBar.setRating((float) rating);
        binding.serviceBookingSubtotal.setText("$ " + String.format("%.2f",price));

        List<String> images = (List<String>) detailsMap.get("imageUris");
        String imageUrl = images.get(0);
        String fullPath = "service-images/" + imageUrl;

        storage.getReference(fullPath).getDownloadUrl()
                .addOnSuccessListener(uri -> {

                    Glide.with(ServiceBookingOptionsPickFragment.this)
                            .load(uri)
                            .centerCrop()
                            .into(binding.bServiceImage);

                })
                .addOnFailureListener(e -> {
                    Log.e("ServiceBookingOptionsPickFragment", "Error getting image URL", e);
                });

        loadPaymentDetails();
    }

    private void loadPaymentDetails() {
        double bookingCommisonPrecentage = 5;
        bookingFee = Math.round(price * bookingCommisonPrecentage / 100);

        binding.serviceBookingFee.setText("$ " + String.format("%.2f",bookingFee));

        double total = price + bookingFee;
        binding.serviceBookingTotal.setText("$ "+String.format("%.2f",total));
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