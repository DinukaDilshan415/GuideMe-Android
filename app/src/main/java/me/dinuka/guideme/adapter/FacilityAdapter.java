package me.dinuka.guideme.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import me.dinuka.guideme.R;
import me.dinuka.guideme.model.Facility;
import me.dinuka.guideme.model.MenuCategory;

public class FacilityAdapter extends RecyclerView.Adapter<FacilityAdapter.ViewHolder> {

    private List<Facility> facilities;
    private OnItemClickListener listener;
    private int selectedPosition = 0;

    public interface OnItemClickListener {
        void onItemClick(Facility item, int position);
    }

    public FacilityAdapter(List<Facility> facilities, OnItemClickListener listener) {
        this.facilities = facilities;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_facility, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Facility item = facilities.get(position);
        holder.facilityTitle.setText(item.getTitle());
        holder.facilityIcon.setImageResource(item.getIconRes());
        Context context = holder.itemView.getContext();

        holder.itemView.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return facilities.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView facilityTitle;
        ImageView facilityIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            facilityTitle = itemView.findViewById(R.id.facilityTitle);
            facilityIcon = itemView.findViewById(R.id.facilityIcon);
        }
    }
}
