package me.dinuka.guideme.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import me.dinuka.guideme.R;
import me.dinuka.guideme.model.Hotel;
import me.dinuka.guideme.model.MyBooking;

public class MyBookingsListingAdapter extends RecyclerView.Adapter<MyBookingsListingAdapter.ViewHolder>{

    private List<MyBooking> myBookings;
    private OnListingItemClickListener listener;

    public MyBookingsListingAdapter(List<MyBooking> myBookings, OnListingItemClickListener listener) {
        this.myBookings = myBookings;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyBooking myBooking = myBookings.get(position);

        String category = myBooking.getCategory();
        int categoryIcon = R.drawable.hotel_svgrepo_com;

        holder.myBookingDatetime.setText(myBooking.getDatetime());
        holder.myBookingNumber.setText(myBooking.getBookingId());
        holder.myBookingName.setText(myBooking.getName());
        holder.myBookingStatus.setText(myBooking.getStatus());
        holder.myBookinglocation.setText(myBooking.getLocation());

        holder.myBookingCheckInDate.setText(myBooking.getCheckInDate());
        holder.MyBookingCheckOutDate.setText(myBooking.getCheckOutDate());
        holder.myBookingDate.setText(myBooking.getCheckInDate());
        holder.myBookingCountAdult.setText(myBooking.getAdults() + " Adult");
        holder.myBookingCountChild.setText(myBooking.getChildren() + " Children");
        holder.myBookingCountGuests.setText(myBooking.getAdults() + " Guests");

        if (category.equals("Hotels")){
            categoryIcon = R.drawable.hotel_svgrepo_com;
            holder.hotelDateLayout.setVisibility(View.VISIBLE);
            holder.nonHotelDateLayout.setVisibility(View.GONE);
            holder.hotelGuestLayout.setVisibility(View.VISIBLE);
            holder.nonHotelGuestLayout.setVisibility(View.GONE);

            holder.myBookingPrice.setText("$ " +myBooking.getPrice()+ " / per night");

        } else if (category.equals("Things To Do")){
            categoryIcon = R.drawable.things_to_do;
            holder.hotelDateLayout.setVisibility(View.GONE);
            holder.nonHotelDateLayout.setVisibility(View.VISIBLE);
            holder.hotelGuestLayout.setVisibility(View.GONE);
            holder.nonHotelGuestLayout.setVisibility(View.VISIBLE);

            holder.myBookingPrice.setText("$ " +myBooking.getPrice());

        } else if (category.equals("Services")) {
            categoryIcon = R.drawable.service_svgrepo_com;
            holder.hotelDateLayout.setVisibility(View.GONE);
            holder.nonHotelDateLayout.setVisibility(View.VISIBLE);
            holder.hotelGuestLayout.setVisibility(View.GONE);
            holder.nonHotelGuestLayout.setVisibility(View.VISIBLE);

            holder.myBookingPrice.setText("$ " +myBooking.getPrice());
        }

        holder.bookingCategory.setText(category);
        holder.bookingCategoryIcon.setImageResource(categoryIcon);

        Glide.with(holder.itemView.getContext())
                .load(myBooking.getImages())
                .centerCrop()
                .into(holder.myBookingImage);

        holder.itemView.setOnClickListener(v->{

            Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.click_animation);
            v.startAnimation(animation);

            if (listener != null){
                listener.onListingItemClick(myBooking);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myBookings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView myBookingImage;
        TextView myBookingNumber;
        TextView myBookingName;
        TextView myBookingPrice;
        TextView myBookingDatetime;
        TextView myBookinglocation;
        TextView myBookingCheckInDate;
        TextView MyBookingCheckOutDate;
        TextView myBookingDate;
        TextView myBookingCountGuests;
        TextView myBookingCountAdult;
        TextView myBookingCountChild;
        TextView myBookingStatus;
        ImageView bookingCategoryIcon;
        TextView bookingCategory;
        Button myBookingReviewBtn;
        TextView bookingStatus;
        LinearLayout hotelDateLayout;
        LinearLayout nonHotelDateLayout;
        LinearLayout hotelGuestLayout;
        LinearLayout nonHotelGuestLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            myBookingNumber = itemView.findViewById(R.id.mybookingNumber);
            myBookingImage = itemView.findViewById(R.id.myBookingImage);
            myBookingName = itemView.findViewById(R.id.mybookingName);
            myBookingPrice = itemView.findViewById(R.id.myBookingPrice);
            myBookingDatetime = itemView.findViewById(R.id.myBookingDatetime);
            myBookinglocation = itemView.findViewById(R.id.myBookinglocation);
            myBookingCheckInDate = itemView.findViewById(R.id.myBookingCheckInDate);
            MyBookingCheckOutDate = itemView.findViewById(R.id.MyBookingCheckOutDate);
            myBookingCountAdult = itemView.findViewById(R.id.myBookingCountAdult);
            myBookingCountChild = itemView.findViewById(R.id.myBookingCountChild);
            myBookingReviewBtn = itemView.findViewById(R.id.myBookingReviewBtn);
            bookingCategoryIcon = itemView.findViewById(R.id.bookingCategoryIcon);
            bookingCategory = itemView.findViewById(R.id.bookingCategory);
            myBookingStatus = itemView.findViewById(R.id.bookingStatus);
            myBookingDate = itemView.findViewById(R.id.myBookingDate);
            myBookingCountGuests = itemView.findViewById(R.id.myBookingCountGuests);
            bookingStatus = itemView.findViewById(R.id.bookingStatus);

            hotelDateLayout = itemView.findViewById(R.id.hotelDateLayout);
            nonHotelDateLayout = itemView.findViewById(R.id.nonHotelDateLayout);
            hotelGuestLayout = itemView.findViewById(R.id.hotelGuestLayout);
            nonHotelGuestLayout = itemView.findViewById(R.id.nonHotelGuestLayout);
        }
    }

    public interface OnListingItemClickListener{
        void onListingItemClick(MyBooking myBooking);
    }
}
