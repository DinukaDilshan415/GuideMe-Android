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

public class CategoryChooseAdapter extends RecyclerView.Adapter<CategoryChooseAdapter.ViewHolder> {

    private List<MenuCategory> categoryList;
    private OnItemClickListener listener;
    private int selectedPosition = -1;

    public interface OnItemClickListener {
        void onItemClick(MenuCategory item, int position);
    }

    public CategoryChooseAdapter(List<MenuCategory> categoryList, OnItemClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_select_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuCategory item = categoryList.get(position);
        holder.categoryTitle.setText(item.getTitle());
        holder.categoryIcon.setImageResource(item.getIconRes());
        Context context = holder.itemView.getContext();

        if (selectedPosition == position) {
            holder.cardView.setStrokeColor(ContextCompat.getColor(context, R.color.md_theme_primary));
            holder.cardView.setStrokeWidth(4);
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.md_theme_inverseOnSurface));
        } else {
            holder.cardView.setStrokeColor(Color.parseColor("#E0E0E0"));
            holder.cardView.setStrokeWidth(1);
            holder.cardView.setCardBackgroundColor(Color.WHITE);
        }

        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);

            listener.onItemClick(item, selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView categoryTitle;
        ImageView categoryIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            categoryTitle = itemView.findViewById(R.id.categoryTitle);
            categoryIcon = itemView.findViewById(R.id.categoryIcon);
        }
    }
}
