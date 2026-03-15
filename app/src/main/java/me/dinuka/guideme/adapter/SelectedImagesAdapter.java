package me.dinuka.guideme.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import me.dinuka.guideme.R;
import me.dinuka.guideme.fragment.CommanListingDetailsFragment;
import me.dinuka.guideme.model.Facility;

public class SelectedImagesAdapter extends RecyclerView.Adapter<SelectedImagesAdapter.ViewHolder> {

    private CommanListingDetailsFragment context;
    private List<Uri> uriList;

    public interface OnItemClickListener {
        void onItemClick(Facility item, int position);
    }

    public SelectedImagesAdapter(List<Uri> uriList, CommanListingDetailsFragment context) {
        this.context = context;
        this.uriList = uriList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selected_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri imageUri = uriList.get(position);

        Glide.with(holder.itemView.getContext())
                .load(imageUri)
                .centerCrop()
                .into(holder.ivPreview);

        holder.btnRemove.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                uriList.remove(currentPos);

                notifyItemRemoved(currentPos);
                notifyItemRangeChanged(currentPos, uriList.size());

                if (context != null) {
                    context.updateImageVisibility();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return uriList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPreview;
        ImageButton btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPreview = itemView.findViewById(R.id.iv_preview);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }
    }
}
