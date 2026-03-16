package me.dinuka.guideme.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import me.dinuka.guideme.R;
import me.dinuka.guideme.adapter.QualificationsAddAdapter;
import me.dinuka.guideme.adapter.RoomTypeAdapter;
import me.dinuka.guideme.databinding.FragmentServiceListingDetailsBinding;

public class ServiceListingDetailsFragment extends Fragment {

    private FragmentServiceListingDetailsBinding binding;
    private int bookingCount = 1;
    private List<String> qualifications = new ArrayList<>();
    private QualificationsAddAdapter adapter;
    private RecyclerView qualificationsRv;
    private EditText qualificationsInput;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentServiceListingDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupCounters();

        qualificationsInput = binding.qualificationsInput;
        qualificationsRv = binding.recyclerQualifications;
        adapter = new QualificationsAddAdapter(qualifications, this);

        qualificationsRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        qualificationsRv.setAdapter(adapter);

        binding.btnAddQualifications.setOnClickListener(v -> {
            String name = qualificationsInput.getText().toString().trim();
            if (!name.isEmpty()) {
                qualifications.add(name);
                adapter.notifyItemInserted(qualifications.size() - 1);
                qualificationsInput.setText(""); // Clear input
                updateQualificationsVisibility();
            }
        });

        binding.nextFromThingsToDetailBtn.setOnClickListener(v -> {
            for (String s : qualifications) {
                Log.d("QUALIFICATION", s);
            }
        });
    }

    public void updateQualificationsVisibility() {
        if (qualifications.isEmpty()) {
            qualificationsRv.setVisibility(View.GONE);
        } else {
            qualificationsRv.setVisibility(View.VISIBLE);
        }
    }

    private void setupCounters() {

        // Bookings Row
        binding.bookingCountChanger.tvLabel.setText("Bookings");
        binding.bookingCountChanger.tvCount.setText(String.valueOf(bookingCount));

        binding.bookingCountChanger.btnPlus.setOnClickListener(v -> {
            bookingCount++;
            binding.bookingCountChanger.tvCount.setText(String.valueOf(bookingCount));
        });

        binding.bookingCountChanger.btnMinus.setOnClickListener(v -> {
            if (bookingCount > 1) { // Prevents going below 1
                bookingCount--;
                binding.bookingCountChanger.tvCount.setText(String.valueOf(bookingCount));
            }
        });
    }
}