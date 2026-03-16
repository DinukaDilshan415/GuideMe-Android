package me.dinuka.guideme.adapter;

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

public class FacilitySelectAdapter extends RecyclerView.Adapter<FacilitySelectAdapter.ViewHolder> {

    private List<Facility> facilityList;

    public FacilitySelectAdapter(List<Facility> facilityList) {
        this.facilityList = facilityList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_facility, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Facility item = facilityList.get(position);
        holder.name.setText(item.getTitle());
        holder.icon.setImageResource(item.getIconRes());

        if (item.isSelected()) {
            holder.card.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.md_theme_primary)); // Or your primary color
            holder.card.setStrokeWidth(4);
            holder.card.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.md_theme_inverseOnSurface)); // Light blue tint
        } else {
            holder.card.setStrokeColor(Color.parseColor("#E0E0E0"));
            holder.card.setStrokeWidth(1);
            holder.card.setCardBackgroundColor(Color.WHITE);
        }

        holder.itemView.setOnClickListener(v -> {
            item.setSelected(!item.isSelected());
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() { return facilityList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView card;
        ImageView icon;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card_facility);
            icon = itemView.findViewById(R.id.facilityIcon);
            name = itemView.findViewById(R.id.facilityTitle);
        }
    }
}
