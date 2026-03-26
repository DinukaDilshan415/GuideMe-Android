package me.dinuka.guideme.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import me.dinuka.guideme.R;
import me.dinuka.guideme.model.ItemReserve;
import me.dinuka.guideme.model.MyBooking;

public class ItemReservesListingAdapter extends RecyclerView.Adapter<ItemReservesListingAdapter.ViewHolder>{

    private List<ItemReserve> itemReserves;
    private OnListingItemClickListener listener;

    public ItemReservesListingAdapter(List<ItemReserve> itemReserves, OnListingItemClickListener listener) {
        this.itemReserves = itemReserves;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_reserve, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemReserve itemReserve = itemReserves.get(position);

        int categoryId = itemReserve.getCategoryId();

        holder.bookingDatetime.setText(itemReserve.getCreated_at());
        holder.bookingNumber.setText(itemReserve.getBookingId());
        holder.bookingStatus.setText(itemReserve.getStatus());
        holder.bookingCheckInDate.setText(itemReserve.getCheckInDate());
        holder.bookingCheckOutDate.setText(itemReserve.getCheckOutDate());
        holder.bookingDate.setText(itemReserve.getCheckInDate());
        holder.bookingCountAdult.setText(itemReserve.getAdults() + " Adult");
        holder.bookingCountChild.setText(itemReserve.getChildren() + " Children");
        holder.bookingCountGuests.setText(itemReserve.getAdults() + " Guests");
        holder.roomCount.setText(itemReserve.getRooms() + " Rooms");

        holder.user_name.setText(itemReserve.getUser_name());
        holder.user_email.setText(itemReserve.getUser_email());
        holder.user_phone.setText(itemReserve.getUser_phone());
        holder.user_country.setText(itemReserve.getUser_country());

        if (categoryId == 1){
            holder.hotelDateLayout.setVisibility(View.VISIBLE);
            holder.nonHotelDateLayout.setVisibility(View.GONE);
            holder.hotelGuestLayout.setVisibility(View.VISIBLE);
            holder.nonHotelGuestLayout.setVisibility(View.GONE);

        } else if (categoryId == 2){
            holder.hotelDateLayout.setVisibility(View.GONE);
            holder.nonHotelDateLayout.setVisibility(View.VISIBLE);
            holder.hotelGuestLayout.setVisibility(View.GONE);
            holder.nonHotelGuestLayout.setVisibility(View.VISIBLE);

        } else if (categoryId == 3) {
            holder.hotelDateLayout.setVisibility(View.GONE);
            holder.nonHotelDateLayout.setVisibility(View.VISIBLE);
            holder.hotelGuestLayout.setVisibility(View.GONE);
            holder.nonHotelGuestLayout.setVisibility(View.VISIBLE);

        }

        holder.itemView.setOnClickListener(v->{

            Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.click_animation);
            v.startAnimation(animation);

            if (listener != null){
                listener.onListingItemClick(itemReserve);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemReserves.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView bookingNumber;
        TextView bookingDatetime;
        TextView bookingCheckInDate;
        TextView bookingCheckOutDate;
        TextView bookingDate;
        TextView bookingCountGuests;
        TextView bookingCountAdult;
        TextView bookingCountChild;
        TextView roomCount;
        TextView user_name;
        TextView user_email;
        TextView user_phone;
        TextView user_country;
        TextView bookingStatus;
        LinearLayout hotelDateLayout;
        LinearLayout nonHotelDateLayout;
        LinearLayout hotelGuestLayout;
        LinearLayout nonHotelGuestLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookingNumber = itemView.findViewById(R.id.bookingNumber);
            bookingDatetime = itemView.findViewById(R.id.bookingDatetime);
            bookingCheckInDate = itemView.findViewById(R.id.bookingCheckInDate);
            bookingCheckOutDate = itemView.findViewById(R.id.bookingCheckOutDate);
            bookingDate = itemView.findViewById(R.id.bookingDate);
            bookingCountGuests = itemView.findViewById(R.id.bookingCountGuests);
            bookingCountAdult = itemView.findViewById(R.id.bookingCountAdult);
            bookingCountChild = itemView.findViewById(R.id.bookingCountChild);
            roomCount = itemView.findViewById(R.id.bookingCountRoom);
            user_name = itemView.findViewById(R.id.userName);
            user_email = itemView.findViewById(R.id.userEmail);
            user_phone = itemView.findViewById(R.id.userMobile);
            user_country = itemView.findViewById(R.id.userCountry);
            bookingStatus = itemView.findViewById(R.id.bookingStatus);

            hotelDateLayout = itemView.findViewById(R.id.hotelDateLayout);
            nonHotelDateLayout = itemView.findViewById(R.id.nonHotelDateLayout);
            hotelGuestLayout = itemView.findViewById(R.id.hotelGuestLayout);
            nonHotelGuestLayout = itemView.findViewById(R.id.nonHotelGuestLayout);
        }
    }

    public interface OnListingItemClickListener{
        void onListingItemClick(ItemReserve itemReserve);
    }
}
