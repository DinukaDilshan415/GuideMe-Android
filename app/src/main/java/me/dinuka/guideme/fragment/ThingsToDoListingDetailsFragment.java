package me.dinuka.guideme.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.dinuka.guideme.R;
import me.dinuka.guideme.databinding.FragmentThingsToDoListingDetailsBinding;

public class ThingsToDoListingDetailsFragment extends Fragment {

    private FragmentThingsToDoListingDetailsBinding binding;
    private int bookingCount = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentThingsToDoListingDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupCounters();
    }

    private void setupCounters() {

        // Bookings Row
        binding.booingCountChanger.tvLabel.setText("Bookings");
        binding.booingCountChanger.tvCount.setText(String.valueOf(bookingCount));

        binding.booingCountChanger.btnPlus.setOnClickListener(v -> {
            bookingCount++;
            binding.booingCountChanger.tvCount.setText(String.valueOf(bookingCount));
        });

        binding.booingCountChanger.btnMinus.setOnClickListener(v -> {
            if (bookingCount > 1) { // Prevents going below 1
                bookingCount--;
                binding.booingCountChanger.tvCount.setText(String.valueOf(bookingCount));
            }
        });
    }
}