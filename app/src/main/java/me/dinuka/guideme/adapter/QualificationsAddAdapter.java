package me.dinuka.guideme.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import me.dinuka.guideme.R;
import me.dinuka.guideme.fragment.HotelListingDetailsFragment;
import me.dinuka.guideme.fragment.ServiceListingDetailsFragment;

public class QualificationsAddAdapter extends RecyclerView.Adapter<QualificationsAddAdapter.ViewHolder> {

    private List<String> qualifications;
    private ServiceListingDetailsFragment context;

    public QualificationsAddAdapter(List<String> qualifications, ServiceListingDetailsFragment context) {
        this.qualifications = qualifications;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_qualification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.qualification.setText(qualifications.get(position));
        holder.btnRemove.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            qualifications.remove(currentPos);
            notifyItemRemoved(currentPos);
            notifyItemRangeChanged(currentPos, qualifications.size());

            // Call the same visibility logic we used for images
            context.updateQualificationsVisibility();
        });
    }

    @Override
    public int getItemCount() {
        return qualifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView qualification;
        ImageButton btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            qualification = itemView.findViewById(R.id.qualification);
            btnRemove = itemView.findViewById(R.id.btn_remove_qualification);
        }
    }
}
