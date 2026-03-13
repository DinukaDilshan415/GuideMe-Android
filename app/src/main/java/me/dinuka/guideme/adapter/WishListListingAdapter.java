package me.dinuka.guideme.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import me.dinuka.guideme.R;
import me.dinuka.guideme.model.Hotel;

public class WishListListingAdapter extends RecyclerView.Adapter<WishListListingAdapter.ViewHolder>{

    private List<Hotel> hotels;
    private OnListingItemClickListener listener;

    public WishListListingAdapter(List<Hotel> hotels, OnListingItemClickListener listener) {
        this.hotels = hotels;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wishlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hotel hotel = hotels.get(position);
        holder.wishlisthotelName.setText(hotel.getName());
        holder.wishlisthotelPrice.setText("$ " + hotel.getPrice());
        holder.wishlistrating.setText(String.valueOf(hotel.getRating()));
        holder.wishlistratingbar.setRating((float) hotel.getRating());
        holder.wishlistlocation.setText(hotel.getLocation());

        Glide.with(holder.itemView.getContext())
                .load(hotel.getImages().get(0))
                .centerCrop()
                .into(holder.wishlisthotelImage);

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
        ImageButton wishlistRemoveBtn;
        ImageView wishlisthotelImage;
        TextView wishlisthotelName;
        TextView wishlisthotelPrice;
        TextView wishlistrating;
        RatingBar wishlistratingbar;
        TextView wishlistlocation;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            wishlistRemoveBtn = itemView.findViewById(R.id.wishlistRemoveBtn);
            wishlisthotelImage = itemView.findViewById(R.id.wishlist_hotelImage);
            wishlisthotelName = itemView.findViewById(R.id.wishlist_hotelName);
            wishlisthotelPrice = itemView.findViewById(R.id.wishlist_hotelPrice);
            wishlistrating = itemView.findViewById(R.id.wishlist_rating);
            wishlistratingbar = itemView.findViewById(R.id.wishlist_rating_bar);
            wishlistlocation = itemView.findViewById(R.id.wishlist_location);
        }
    }

    public interface OnListingItemClickListener{
        void onListingItemClick(Hotel hotel);
    }
}
