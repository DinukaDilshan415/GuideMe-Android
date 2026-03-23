package me.dinuka.guideme.adapter;

import android.graphics.Color;
import android.util.Log;
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
import me.dinuka.guideme.model.RoomType;

public class RoomTypeSelectAdapter extends RecyclerView.Adapter<RoomTypeSelectAdapter.ViewHolder> {

    private List<RoomType> roomTypeList;
    private int selectedPosition = -1;

    public RoomTypeSelectAdapter(List<RoomType> roomTypeList) {
        this.roomTypeList = roomTypeList;
        for (int i = 0; i < roomTypeList.size(); i++) {
            if (roomTypeList.get(i).isSelected()) {
                selectedPosition = i;
                break;
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_room_type, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RoomType item = roomTypeList.get(position);

        holder.name.setText(item.getType());

        boolean isCurrentSelected = (position == selectedPosition);
        item.setSelected(isCurrentSelected);

        if (isCurrentSelected) {
            holder.card.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.md_theme_primary));
            holder.card.setStrokeWidth(4);
            holder.card.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.md_theme_inverseOnSurface));
        } else {
            holder.card.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.md_theme_secondary));
            holder.card.setStrokeWidth(2);
            holder.card.setCardBackgroundColor(Color.WHITE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (selectedPosition == position) return; // Already selected, do nothing

            int previousSelected = selectedPosition;
            selectedPosition = position;

            // Update data models
            if (previousSelected != -1) {
                roomTypeList.get(previousSelected).setSelected(false);
                notifyItemChanged(previousSelected);
            }

            item.setSelected(true);
            notifyItemChanged(selectedPosition);
        });
    }

    public RoomType getSelectedRoomType() {
        if (selectedPosition != -1) {
            return roomTypeList.get(selectedPosition);
        }
        return null;
    }

    @Override
    public int getItemCount() { return roomTypeList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView card;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card_room_type);
            name = itemView.findViewById(R.id.b_room_name);
        }
    }
}
