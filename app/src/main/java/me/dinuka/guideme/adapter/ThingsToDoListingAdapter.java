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
import me.dinuka.guideme.model.Hotel;
import me.dinuka.guideme.model.ThingsToDo;

public class ThingsToDoListingAdapter extends RecyclerView.Adapter<ThingsToDoListingAdapter.ViewHolder>{

    private List<ThingsToDo> thingsToDos;
    private OnListingItemClickListener listener;

    public ThingsToDoListingAdapter(List<ThingsToDo> thingsToDos, OnListingItemClickListener listener) {
        this.thingsToDos = thingsToDos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_things_to_do, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ThingsToDo thingToDo = thingsToDos.get(position);
        holder.thingsToDoName.setText(thingToDo.getName());
        holder.thingsToDoPrice.setText("$ " + thingToDo.getPrice());
        holder.rating.setText(String.valueOf(thingToDo.getRating()));
        holder.reviewsCount.setText(String.valueOf(thingToDo.getReviewsCount()) + " reviews");
        holder.location.setText(thingToDo.getLocation());

        Glide.with(holder.itemView.getContext())
                .load(thingToDo.getImages().get(0))
                .centerCrop()
                .into(holder.thingsToDoImage);

        holder.itemView.setOnClickListener(v->{

            Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.click_animation);
            v.startAnimation(animation);

            if (listener != null){
                listener.onListingItemClick(thingToDo.getDetailsMap());
            }
        });
    }

    @Override
    public int getItemCount() {
        return thingsToDos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView thingsToDoImage;
        TextView thingsToDoName;
        TextView thingsToDoPrice;
        TextView rating;
        TextView reviewsCount;
        TextView location;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thingsToDoImage = itemView.findViewById(R.id.thingsToDoImage);
            thingsToDoName = itemView.findViewById(R.id.thingToDoName);
            thingsToDoPrice = itemView.findViewById(R.id.thingsToDoPrice);
            rating = itemView.findViewById(R.id.thingToDo_rating);
            reviewsCount = itemView.findViewById(R.id.thingToDo_reviewsCount);
            location = itemView.findViewById(R.id.thingToDo_location);
        }
    }

    public interface OnListingItemClickListener{
        void onListingItemClick(HashMap<String, Object> detailsMap);
    }
}
