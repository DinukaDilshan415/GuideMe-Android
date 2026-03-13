package me.dinuka.guideme.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
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
        holder.myBookingName.setText(myBooking.getName());
        holder.myBiikingPrice.setText("$ " +String.valueOf(myBooking.getPrice()));
        holder.myBookingDatetime.setText(myBooking.getDatetime());
        holder.myBiikinglocation.setText(myBooking.getLocation());
        holder.my_booking_rating_bar.setRating((float) myBooking.getRating());
        holder.my_booking_rating.setText(String.valueOf(myBooking.getRating()));
        holder.myBookingCheckInDate.setText(myBooking.getCheckInDate());
        holder.MyBookingCheckOutDate.setText(myBooking.getCheckOutDate());
        holder.myBookingCountAdult.setText(String.valueOf(myBooking.getAdults())+ " Adult");
        holder.myBookingCountChild.setText(String.valueOf(myBooking.getChildren())+ " Children");

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
        TextView myBookingName;
        TextView myBiikingPrice;
        TextView myBookingDatetime;
        TextView myBiikinglocation;
        RatingBar my_booking_rating_bar;
        TextView my_booking_rating;
        TextView myBookingCheckInDate;
        TextView MyBookingCheckOutDate;
        TextView myBookingCountAdult;
        TextView myBookingCountChild;
        Button myBookingReviewBtn;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            myBookingImage = itemView.findViewById(R.id.myBookingImage);
            myBookingName = itemView.findViewById(R.id.mybookingName);
            myBiikingPrice = itemView.findViewById(R.id.myBiikingPrice);
            myBookingDatetime = itemView.findViewById(R.id.myBookingDatetime);
            myBiikinglocation = itemView.findViewById(R.id.myBiikinglocation);
            my_booking_rating_bar = itemView.findViewById(R.id.my_booking_rating_bar);
            my_booking_rating = itemView.findViewById(R.id.my_booking_rating);
            myBookingCheckInDate = itemView.findViewById(R.id.myBookingCheckInDate);
            MyBookingCheckOutDate = itemView.findViewById(R.id.MyBookingCheckOutDate);
            myBookingCountAdult = itemView.findViewById(R.id.myBookingCountAdult);
            myBookingCountChild = itemView.findViewById(R.id.myBookingCountChild);
            myBookingReviewBtn = itemView.findViewById(R.id.myBookingReviewBtn);
        }
    }

    public interface OnListingItemClickListener{
        void onListingItemClick(MyBooking myBooking);
    }
}
