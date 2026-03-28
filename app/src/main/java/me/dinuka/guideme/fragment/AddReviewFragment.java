package me.dinuka.guideme.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

import me.dinuka.guideme.R;
import me.dinuka.guideme.api.UserApi;
import me.dinuka.guideme.client.RetrofitClient;
import me.dinuka.guideme.databinding.FragmentAddReviewBinding;
import me.dinuka.guideme.dto.UserDTO;
import me.dinuka.guideme.model.MyBooking;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddReviewFragment extends Fragment {
    private FragmentAddReviewBinding binding;
    private String TAG = this.getClass().getSimpleName();
    private String title;
    private String image;
    private String location;
    private UserDTO currentUserDTO;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title");
            image = getArguments().getString("image");
            location = getArguments().getString("location");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddReviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        if (title != null && image != null && location != null) {
            binding.itemName.setText(title);
            binding.itemlocation.setText(location);

            Glide.with(this)
                    .load(image)
                    .centerCrop()
                    .into(binding.itemImage);
        }

        loadCurrentData(mAuth.getCurrentUser().getEmail());
    }

    private void loadCurrentData(String email) {

        Retrofit retrofit = RetrofitClient.getInstance(getContext());
        UserApi userApi = retrofit.create(UserApi.class);

        Call<UserDTO> currentUserCall = userApi.getCurrentUser(email);
        currentUserCall.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful()) {
                    currentUserDTO = response.body();

                    binding.userName.setText(currentUserDTO.getName());
                    binding.userCountry.setText(currentUserDTO.getCountry());

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    storage.getReference("profile-images/" + currentUserDTO.getImageUrl()).getDownloadUrl()
                            .addOnSuccessListener(uri -> {

                                Glide.with(AddReviewFragment.this)
                                        .load(uri)
                                        .circleCrop()
                                        .into(binding.userImage);

                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error getting image URL", e);
                            });

                } else {
                    Log.e(TAG, "Error fetching : " + response);
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Log.e(TAG, "Error fetching : ", t);
            }
        });
    }
}