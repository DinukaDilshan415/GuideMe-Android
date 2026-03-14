package me.dinuka.guideme.adapter;

import android.content.Context;
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
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import me.dinuka.guideme.R;
import me.dinuka.guideme.model.Facility;
import me.dinuka.guideme.model.Notification;
import me.dinuka.guideme.util.NotificationTimeFormater;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<Notification> notifications;
    private OnItemClickListener listener;
    private int selectedPosition = 0;

    public interface OnItemClickListener {
        void onItemClick(Notification notification, int position);
    }

    public NotificationAdapter(List<Notification> notifications, OnItemClickListener listener) {
        this.notifications = notifications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        Glide.with(holder.itemView.getContext())
                .load(notification.getImage())
                .centerCrop()
                .into(holder.notifyImage);
        holder.notifyTitle.setText(notification.getTitle());
        holder.notifyMsg.setText(notification.getMessage());
        holder.notifyTime.setText(NotificationTimeFormater.getTimeAgo(notification.getDate()));


        holder.itemView.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.click_animation);
            v.startAnimation(animation);

            if (listener != null){
                listener.onItemClick(notification, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView notifyImage;
        TextView notifyTitle;
        TextView notifyMsg;
        TextView notifyTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            notifyImage = itemView.findViewById(R.id.notifyImage);
            notifyTitle = itemView.findViewById(R.id.notifyTitle);
            notifyMsg = itemView.findViewById(R.id.notifyMsg);
            notifyTime = itemView.findViewById(R.id.notifyTime);
        }
    }
}
