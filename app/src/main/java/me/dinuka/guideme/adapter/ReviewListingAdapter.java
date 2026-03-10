package me.dinuka.guideme.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import me.dinuka.guideme.R;
import me.dinuka.guideme.model.Hotel;
import me.dinuka.guideme.model.Review;

public class ReviewListingAdapter extends RecyclerView.Adapter<ReviewListingAdapter.ViewHolder>{

    private List<Review> reviews;
    private OnListingItemClickListener listener;

    public ReviewListingAdapter(List<Review> reviews, OnListingItemClickListener listener) {
        this.reviews = reviews;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.reviewerName.setText(review.getUser());
        holder.reviewerLocation.setText(review.getLocation());
        holder.hotel_review_rating.setRating((float) review.getRating());
        holder.review_rating.setText(String.valueOf(review.getRating()));
        holder.review_date.setText(review.getDate());
        holder.review_text.setText(review.getReview());

        Glide.with(holder.itemView.getContext())
                .load(review.getImage())
                .circleCrop()
                .into(holder.reviewerImage);

        holder.itemView.setOnClickListener(v->{

            Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.click_animation);
            v.startAnimation(animation);

            if (listener != null){
                listener.onListingItemClick(review);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView reviewerImage;
        TextView reviewerName;
        TextView reviewerLocation;
        RatingBar hotel_review_rating;
        TextView review_rating;
        TextView review_date;
        TextView review_text;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewerImage = itemView.findViewById(R.id.reviewerImage);
            reviewerName = itemView.findViewById(R.id.reviewerName);
            reviewerLocation = itemView.findViewById(R.id.reviewerLocation);
            hotel_review_rating = itemView.findViewById(R.id.hotel_review_rating);
            review_rating = itemView.findViewById(R.id.review_rating);
            review_date = itemView.findViewById(R.id.review_date);
            review_text = itemView.findViewById(R.id.review_text);
        }
    }

    public interface OnListingItemClickListener{
        void onListingItemClick(Review review);
    }
}
