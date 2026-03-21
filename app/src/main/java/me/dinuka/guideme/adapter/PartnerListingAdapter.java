package me.dinuka.guideme.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import me.dinuka.guideme.R;
import me.dinuka.guideme.model.Listing;
import me.dinuka.guideme.model.MyBooking;

public class PartnerListingAdapter extends RecyclerView.Adapter<PartnerListingAdapter.ViewHolder>{

    private List<Listing> listings;
    private OnListingItemClickListener listener;

    public PartnerListingAdapter(List<Listing> listings, OnListingItemClickListener listener) {
        this.listings = listings;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_partner_listing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Listing listing = listings.get(position);

        String category = listing.getCategory();

        int categoryIcon = R.drawable.hotel_svgrepo_com;

        holder.listingNumber.setText(listing.getListingId());
        holder.listingStatus.setText(listing.getStatus());
        holder.listingCategory.setText(category);

        if (category.equals("Hotels")){
            categoryIcon = R.drawable.hotel_svgrepo_com;
        } else if (category.equals("Things To Do")){
            categoryIcon = R.drawable.things_to_do;
        } else if (category.equals("Services")) {
            categoryIcon = R.drawable.service_svgrepo_com;
        }

        holder.listingCategoryIcon.setImageResource(categoryIcon);
        holder.listingName.setText(listing.getName());
        holder.listingPrice.setText("$ " +String.valueOf(listing.getPrice()));
        holder.listingDatetime.setText(listing.getDatetime());
        holder.listinglocation.setText(listing.getLocation());

        Glide.with(holder.itemView.getContext())
                .load(listing.getImages())
                .centerCrop()
                .into(holder.listingImage);

        holder.itemView.setOnClickListener(v->{

            Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.click_animation);
            v.startAnimation(animation);

            if (listener != null){
                listener.onListingItemClick(listing);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView listingImage;
        TextView listingName;
        TextView listingNumber;
        TextView listingStatus;
        TextView listingCategory;
        ImageView listingCategoryIcon;
        TextView listinglocation;
        TextView listingPrice;
        TextView listingDatetime;
        ImageButton listingRemoveBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            listingImage = itemView.findViewById(R.id.listingImage);
            listingName = itemView.findViewById(R.id.listingName);
            listingNumber = itemView.findViewById(R.id.listingNumber);
            listingStatus = itemView.findViewById(R.id.listingStatus);
            listingCategory = itemView.findViewById(R.id.listingCategory);
            listingCategoryIcon = itemView.findViewById(R.id.listingCategoryIcon);
            listinglocation = itemView.findViewById(R.id.listinglocation);
            listingPrice = itemView.findViewById(R.id.listingPrice);
            listingDatetime = itemView.findViewById(R.id.listingDatetime);
            listingRemoveBtn = itemView.findViewById(R.id.listingRemoveBtn);

        }
    }

    public interface OnListingItemClickListener{
        void onListingItemClick(Listing listing);
    }
}
