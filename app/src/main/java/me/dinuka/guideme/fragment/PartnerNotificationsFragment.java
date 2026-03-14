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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.dinuka.guideme.R;
import me.dinuka.guideme.adapter.MyBookingsListingAdapter;
import me.dinuka.guideme.adapter.NotificationAdapter;
import me.dinuka.guideme.databinding.FragmentPartnerNotificationsBinding;
import me.dinuka.guideme.model.Notification;

public class PartnerNotificationsFragment extends Fragment {

    private FragmentPartnerNotificationsBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPartnerNotificationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Notification> notifications = new ArrayList<>();
        notifications.add(new Notification(1,"New Booking","Uncover the beauty & diversity of Sri Lanka with a",formater("Sat Mar 14 20:20:00 IST 2026"),"https://a0.muscache.com/im/pictures/Mt/MtTemplate-4829640/original/255e759c-47c9-41df-880a-7817a1d4640c.jpeg?im_w=480"));
        notifications.add(new Notification(1,"Guide Me Support","he output of the java.util.Date() constructor is a Date object initialized to the current date and time on the system where the code is run",formater("Sat Mar 07 20:20:00 IST 2026"),"https://a0.muscache.com/im/pictures/Mt/MtTemplate-4829640/original/c2d47788-9021-459b-80ed-a0c66c9dd6a2.jpeg?im_w=480"));
        notifications.add(new Notification(1,"New Booking","Explore Colombo hidden island temple, the city's largest park, ",formater("Sat Mar 14 19:20:00 IST 2026"),"https://a0.muscache.com/im/pictures/Mt/MtTemplate-6942972/original/e5c658ad-38fa-4526-9163-e28ac0232ac5.jpeg?im_w=480"));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.notificationsRecyclerView.setLayoutManager(layoutManager);

        NotificationAdapter adapter = new NotificationAdapter(notifications, (notification, position) -> {

        });

        binding.notificationsRecyclerView.setAdapter(adapter);

    }

    public long formater(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return 0L;
        }

        try {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .appendPattern("EEE MMM dd HH:mm:ss ")
                    .appendZoneText(java.time.format.TextStyle.SHORT) // Handles "IST"
                    .appendPattern(" yyyy")
                    .toFormatter(Locale.ENGLISH);

            ZonedDateTime zdt = ZonedDateTime.parse(dateString, formatter);

            return zdt.toInstant().toEpochMilli();

        } catch (Exception e) {
            android.util.Log.e("DateParsing", "Modern parse failed: " + e.getMessage());
            return 0L;
        }
    }
}