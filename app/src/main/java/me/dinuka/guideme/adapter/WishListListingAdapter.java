package me.dinuka.guideme.adapter;

import android.util.Log;
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

import java.util.HashMap;
import java.util.List;

import me.dinuka.guideme.R;
import me.dinuka.guideme.helper.WishlistHelper;
import me.dinuka.guideme.model.Hotel;
import me.dinuka.guideme.model.WishlistItem;

public class WishListListingAdapter extends RecyclerView.Adapter<WishListListingAdapter.ViewHolder>{

    private List<WishlistItem> wishlistItems;
    private OnListingItemClickListener listener;
    private Runnable onDeleteListener;
    public WishListListingAdapter(List<WishlistItem> wishlistItems, OnListingItemClickListener listener) {
        this.wishlistItems = wishlistItems;
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
        WishlistItem wishlistItem = wishlistItems.get(position);
        holder.wishlistitemName.setText(wishlistItem.getName());
        holder.wishlistPrice.setText("$ " + wishlistItem.getPrice());
        holder.wishlistrating.setText(String.valueOf(wishlistItem.getRating()));
        holder.wishlistratingbar.setRating((float) wishlistItem.getRating());
        holder.wishlistlocation.setText(wishlistItem.getLocation());

        Glide.with(holder.itemView.getContext())
                .load(wishlistItem.getImage())
                .centerCrop()
                .into(holder.wishlistImage);

        int categoryId = ((Number) wishlistItem.getDetailsMap().get("categoryId")).intValue();

        int categoryIcon = R.drawable.hotel_svgrepo_com;
        String category = "";
        if (categoryId == 1 ){
            categoryIcon = R.drawable.hotel_svgrepo_com;
            category = "Hotels";
        } else if (categoryId == 2){
            categoryIcon = R.drawable.things_to_do;
            category = "Things To Do";
        } else if (categoryId == 3) {
            categoryIcon = R.drawable.service_svgrepo_com;
            category = "Services";
        }

        holder.wishlistCategory.setText(category);
        holder.wishlistCategoryIcon.setImageResource(categoryIcon);

        holder.itemView.setOnClickListener(v->{

            Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.click_animation);
            v.startAnimation(animation);

            if (listener != null){
                listener.onListingItemClick(wishlistItem.getDetailsMap());
            }
        });

        holder.wishlistRemoveBtn.setOnClickListener(v->{
            int currentPos = holder.getAdapterPosition();

            if (currentPos != RecyclerView.NO_POSITION) {
                Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.click_animation);
                v.startAnimation(animation);

                WishlistItem itemToRemove = wishlistItems.get(currentPos);

                WishlistHelper.removeFromWishlist(v.getContext(), String.valueOf(wishlistItem.getDetailsMap().get("l_id")));

                wishlistItems.remove(currentPos);
                notifyItemRemoved(currentPos);

                if (onDeleteListener != null) {
                    onDeleteListener.run();
                }

                notifyItemRangeChanged(currentPos, wishlistItems.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return wishlistItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageButton wishlistRemoveBtn;
        ImageView wishlistImage;
        TextView wishlistitemName;
        TextView wishlistPrice;
        TextView wishlistrating;
        RatingBar wishlistratingbar;
        TextView wishlistlocation;
        ImageView wishlistCategoryIcon;
        TextView wishlistCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            wishlistRemoveBtn = itemView.findViewById(R.id.wishlistRemoveBtn);
            wishlistitemName = itemView.findViewById(R.id.wishlist_itemName);
            wishlistPrice = itemView.findViewById(R.id.wishlist_Price);
            wishlistImage = itemView.findViewById(R.id.wishlist_Image);
            wishlistrating = itemView.findViewById(R.id.wishlist_rating);
            wishlistratingbar = itemView.findViewById(R.id.wishlist_rating_bar);
            wishlistlocation = itemView.findViewById(R.id.wishlist_location);
            wishlistCategoryIcon = itemView.findViewById(R.id.wishlistCategoryIcon);
            wishlistCategory = itemView.findViewById(R.id.wishlist_Category);
        }
    }

    public void setOnDeleteListener(Runnable listener) {
        this.onDeleteListener = listener;
    }
    public interface OnListingItemClickListener{
        void onListingItemClick(HashMap<String, Object> detailsMap);
    }
}
