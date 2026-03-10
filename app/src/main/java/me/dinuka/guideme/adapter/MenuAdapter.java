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
import me.dinuka.guideme.model.MenuCategory;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private List<MenuCategory> menuList;
    private OnItemClickListener listener;
    private int selectedPosition = 0;

    public interface OnItemClickListener {
        void onItemClick(MenuCategory item, int position);
    }

    public MenuAdapter(List<MenuCategory> menuList, OnItemClickListener listener) {
        this.menuList = menuList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuCategory item = menuList.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.ivIcon.setImageResource(item.getIconRes());
        Context context = holder.itemView.getContext();

        // --- SELECTION LOGIC ---
        if (selectedPosition == position) {
            // Appearance for Selected state
            holder.cardView.setStrokeColor(ContextCompat.getColor(context, R.color.md_theme_primary)); // Or your primary color
            holder.cardView.setStrokeWidth(4);
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.md_theme_inverseOnSurface)); // Light blue tint
        } else {
            // Appearance for Unselected state
            holder.cardView.setStrokeColor(Color.parseColor("#E0E0E0"));
            holder.cardView.setStrokeWidth(1);
            holder.cardView.setCardBackgroundColor(Color.WHITE);
        }

        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            // Refresh only the items that changed for better performance
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);

            listener.onItemClick(item, selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView tvTitle;
        ImageView ivIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivIcon = itemView.findViewById(R.id.ivIcon);
        }
    }
}
