package me.dinuka.guideme.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.credentials.ClearCredentialStateRequest;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.exceptions.ClearCredentialException;
import androidx.fragment.app.Fragment;

import android.os.CancellationSignal;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.concurrent.Executors;

import me.dinuka.guideme.R;
import me.dinuka.guideme.activity.LoginActivity;
import me.dinuka.guideme.api.PartnerApi;
import me.dinuka.guideme.api.UserApi;
import me.dinuka.guideme.client.RetrofitClient;
import me.dinuka.guideme.databinding.FragmentProfileBinding;
import me.dinuka.guideme.dto.PartnerDTO;
import me.dinuka.guideme.dto.UserDTO;
import me.dinuka.guideme.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private UserDTO currentUserDTO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        binding.editBtn1.setOnClickListener(v -> {
            EditProfileSheet editProfileSheet = new EditProfileSheet("users");

            editProfileSheet.show(getParentFragmentManager(), "EditProfileTag");
        });

        binding.logoutBtn.setOnClickListener(v -> {
            openDialog();
        });

        binding.loginregisterBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        });

        binding.personalDetailsLayout.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new UserPersonalDetailsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        getParentFragmentManager().setFragmentResultListener("requestKey", getViewLifecycleOwner(), (requestKey, bundle) -> {
            String imageUriString = bundle.getString("bundleKey");
            String updatedName = bundle.getString("updatedName");

            binding.currentUserName.setText(updatedName);

            if (imageUriString != null) {
                Uri imageUri = Uri.parse(imageUriString);
                binding.profileImage.setImageURI(imageUri);
            }
        });

        if (currentUser != null){
            binding.profileLoginBtnLayout.setVisibility(View.GONE);
            binding.profileDisplayLayout.setVisibility(View.VISIBLE);
            binding.logoutBtn.setVisibility(View.VISIBLE);
            binding.personalDetailsLayout.setVisibility(View.VISIBLE);

            String uid = currentUser.getUid();
            firebaseFirestore.collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(ds -> {
                        if (ds.exists()) {
                            User user = ds.toObject(User.class);
                            binding.currentUserName.setText(user.getName());

                            if (user.getProfilePicUrl() != null) {
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                storage.getReference("profile-images/" + user.getProfilePicUrl()).getDownloadUrl()
                                        .addOnSuccessListener(uri -> {

                                            Glide.with(ProfileFragment.this)
                                                    .load(uri)
                                                    .circleCrop()
                                                    .into(binding.profileImage);

                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("ProfileFragment", "Error getting image URL", e);
                                        });
                            }

                            loadCurrentData(user.getEmail());

                        } else {
                            Log.e("ProfileFragment", "User data not found");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ProfileFragment", "Error getting user data", e);
                    });

        } else {
            binding.profileLoginBtnLayout.setVisibility(View.VISIBLE);
            binding.profileDisplayLayout.setVisibility(View.GONE);
            binding.logoutBtn.setVisibility(View.GONE);
            binding.personalDetailsLayout.setVisibility(View.GONE);
        }
    }

    private boolean loadCurrentData(String email) {

        final boolean[] isHavePartner = {false};

        Retrofit retrofit = RetrofitClient.getInstance(getContext());
        UserApi userApi = retrofit.create(UserApi.class);

        Call<UserDTO> currentUserCall = userApi.getCurrentUser(email);
        currentUserCall.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful()) {
                    currentUserDTO = response.body();

                    binding.memberFrom.setText("Member From : " + currentUserDTO.getCreatedAt());

                    isHavePartner[0] = true;
                } else {
                    Log.e("ProfileFragment", "Error fetching : " + response);
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Log.e("ProfileFragment", "Error fetching : ", t);
            }
        });
        return isHavePartner[0];
    }

    private void openDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_logout, null);
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        }

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> alertDialog.dismiss());
        dialogView.findViewById(R.id.btnLogOut).setOnClickListener(v -> {

            logout();

            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    private void logout(){
        mAuth.signOut();

        CredentialManager credentialManager = CredentialManager.create(getContext());
        // When a user signs out, clear the current user credential state from all credential providers.
        ClearCredentialStateRequest clearRequest = new ClearCredentialStateRequest();
        credentialManager.clearCredentialStateAsync(
                clearRequest,
                new CancellationSignal(),
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<>() {
                    @Override
                    public void onResult(@NonNull Void result) {
//                        updateUI(null);
                    }

                    @Override
                    public void onError(@NonNull ClearCredentialException e) {
                        Log.e("ProfileFragment", "Couldn't clear user credentials: " + e.getLocalizedMessage());
                    }
                });

        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}