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
        notifications.add(new Notification(1,"New Booking","Birding in Colombo-Kindly message for any other Birding Hotspots & National Parks",formater("Sat Mar 14 20:20:00 IST 2026"),"https://a0.muscache.com/im/pictures/Mt/MtTemplate-2529014/original/cf2c1e2d-85b6-4938-8a13-0b8bd1ba97d1.jpeg?im_w=960"));
        notifications.add(new Notification(1,"Guide Me Support","https://github.com/DinukaDilshan415/images/blob/main/logo.png?raw=true",formater("Sat Mar 07 20:20:00 IST 2026"),"https://github.com/DinukaDilshan415/images/blob/main/logo.png?raw=true"));
        notifications.add(new Notification(1,"New Booking","For the price, it’s great!",formater("Sat Mar 14 19:20:00 IST 2026"),"https://cf.bstatic.com/xdata/images/hotel/max1024x768/679652580.jpg?k=b03105932f297da973667859ba5717136626041e43a4a82cb61994344701646d&o="));

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