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
import me.dinuka.guideme.fragment.ServiceListingDetailsFragment;

public class QualificationsViewAdapter extends RecyclerView.Adapter<QualificationsViewAdapter.ViewHolder> {

    private List<String> qualifications;

    public QualificationsViewAdapter(List<String> qualifications) {
        this.qualifications = qualifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_qualification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.qualification.setText(qualifications.get(position));
    }

    @Override
    public int getItemCount() {
        return qualifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView qualification;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            qualification = itemView.findViewById(R.id.qualification_name);
        }
    }
}
