package me.dinuka.guideme.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.dinuka.guideme.R;
import me.dinuka.guideme.databinding.FragmentBookingOptionsPickBinding;

public class BookingOptionsPickFragment extends Fragment {
    private FragmentBookingOptionsPickBinding binding;

    private int roomCount = 1;
    private int adultCount = 2;
    private int childCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    public void showDateRangePicker() {
        // 1. Create the builder for a Range Picker
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select Date");

        // 2. Build the picker
        MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();

        // 3. Handle the selection
        datePicker.addOnPositiveButtonClickListener(selection -> {
            // selection.first is Check-in (Long timestamp)
            // selection.second is Check-out (Long timestamp)

            String startDate = formatDate(selection.first);
            String endDate = formatDate(selection.second);

            binding.tvCheckInDate.setText(startDate);
            binding.tvCheckOutDate.setText(endDate);
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    // Helper to format the timestamp to "Nov 12, 2024"
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
            if (childCount > 1) { // Prevents going below 1
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
            if (adultCount > 1) { // Prevents going below 1
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
            if (roomCount > 1) { // Prevents going below 1
                roomCount--;
                binding.rowRooms.tvCount.setText(String.valueOf(roomCount));
            }
        });
    }
}