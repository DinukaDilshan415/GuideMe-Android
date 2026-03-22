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

import java.util.HashMap;
import java.util.List;

import me.dinuka.guideme.R;
import me.dinuka.guideme.model.Service;
import me.dinuka.guideme.model.ThingsToDo;

public class ServiceListingAdapter extends RecyclerView.Adapter<ServiceListingAdapter.ViewHolder>{

    private List<Service> services;
    private OnListingItemClickListener listener;

    public ServiceListingAdapter(List<Service> services, OnListingItemClickListener listener) {
        this.services = services;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Service service = services.get(position);
        holder.serviceName.setText(service.getName());
        holder.servicePrice.setText("$ " + service.getPrice());
        holder.rating.setText(String.valueOf(service.getRating()));
        holder.reviewsCount.setText(String.valueOf(service.getReviewsCount()) + " reviews");
        holder.location.setText(service.getLocation());

        Glide.with(holder.itemView.getContext())
                .load(service.getImages().get(0))
                .centerCrop()
                .into(holder.serviceImage);

        holder.itemView.setOnClickListener(v->{

            Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.click_animation);
            v.startAnimation(animation);

            if (listener != null){
                listener.onListingItemClick(service.getDetailsMap());
            }
        });
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView serviceImage;
        TextView serviceName;
        TextView servicePrice;
        TextView rating;
        TextView reviewsCount;
        TextView location;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceImage = itemView.findViewById(R.id.serviceImage);
            serviceName = itemView.findViewById(R.id.serviceName);
            servicePrice = itemView.findViewById(R.id.servicePrice);
            rating = itemView.findViewById(R.id.service_rating);
            reviewsCount = itemView.findViewById(R.id.service_reviewsCount);
            location = itemView.findViewById(R.id.service_location);
        }
    }

    public interface OnListingItemClickListener{
        void onListingItemClick(HashMap<String, Object> detailsMap);
    }
}
