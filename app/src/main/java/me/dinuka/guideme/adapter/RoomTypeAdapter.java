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

public class RoomTypeAdapter extends RecyclerView.Adapter<RoomTypeAdapter.ViewHolder> {

    private List<String> roomList;
    private HotelListingDetailsFragment context;

    public RoomTypeAdapter(List<String> roomList, HotelListingDetailsFragment context) {
        this.roomList = roomList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room_type, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvRoomName.setText(roomList.get(position));
        holder.btnRemove.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            roomList.remove(currentPos);
            notifyItemRemoved(currentPos);
            notifyItemRangeChanged(currentPos, roomList.size());

            // Call the same visibility logic we used for images
            context.updateRoomVisibility();
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomName;
        ImageButton btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomName = itemView.findViewById(R.id.tv_room_name);
            btnRemove = itemView.findViewById(R.id.btn_remove_room);
        }
    }
}
