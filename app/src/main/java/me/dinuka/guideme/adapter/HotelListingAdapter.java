package me.dinuka.guideme.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import me.dinuka.guideme.R;
import me.dinuka.guideme.model.Hotel;

public class HotelListingAdapter extends RecyclerView.Adapter<HotelListingAdapter.ViewHolder>{

    private List<Hotel> hotels;
    private OnListingItemClickListener listener;

    public HotelListingAdapter(List<Hotel> hotels, OnListingItemClickListener listener) {
        this.hotels = hotels;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hotel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hotel hotel = hotels.get(position);
        holder.hotelName.setText(hotel.getName());
        holder.hotelPrice.setText("$ " + hotel.getPrice());
        holder.rating.setText(String.valueOf(hotel.getRating()));
        holder.reviewsCount.setText(String.valueOf(hotel.getReviewsCount()) + " reviews");
        holder.location.setText(hotel.getLocation());

        Glide.with(holder.itemView.getContext())
                .load(hotel.getImages().get(0))
                .centerCrop()
                .into(holder.hotelImage);

        holder.itemView.setOnClickListener(v->{

            Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.click_animation);
            v.startAnimation(animation);

            if (listener != null){
                listener.onListingItemClick(hotel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hotels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView hotelImage;
        TextView hotelName;
        TextView hotelPrice;
        TextView rating;
        TextView reviewsCount;
        TextView location;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            hotelImage = itemView.findViewById(R.id.hotelImage);
            hotelName = itemView.findViewById(R.id.hotelName);
            hotelPrice = itemView.findViewById(R.id.hotelPrice);
            rating = itemView.findViewById(R.id.rating);
            reviewsCount = itemView.findViewById(R.id.reviewsCount);
            location = itemView.findViewById(R.id.location);
        }
    }

    public interface OnListingItemClickListener{
        void onListingItemClick(Hotel hotel);
    }
}
