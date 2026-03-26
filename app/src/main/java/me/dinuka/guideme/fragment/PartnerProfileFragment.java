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
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.CancellationSignal;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;
import java.util.concurrent.Executors;

import me.dinuka.guideme.R;
import me.dinuka.guideme.activity.LoginActivity;
import me.dinuka.guideme.activity.PartnerLoginActivity;
import me.dinuka.guideme.adapter.FacilitySelectAdapter;
import me.dinuka.guideme.api.FacilityApi;
import me.dinuka.guideme.api.PartnerApi;
import me.dinuka.guideme.client.RetrofitClient;
import me.dinuka.guideme.databinding.FragmentPartnerProfileBinding;
import me.dinuka.guideme.databinding.FragmentProfileBinding;
import me.dinuka.guideme.dto.FacilityDTO;
import me.dinuka.guideme.dto.PartnerDTO;
import me.dinuka.guideme.model.Facility;
import me.dinuka.guideme.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PartnerProfileFragment extends Fragment {

    private FragmentPartnerProfileBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private PartnerDTO currentPartner;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPartnerProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        binding.editBtn1.setOnClickListener(v -> {
            EditProfileSheet editProfileSheet = new EditProfileSheet("partners");

            editProfileSheet.show(getParentFragmentManager(), "EditProfileTag");
        });

        binding.logoutBtn.setOnClickListener(v -> {
            openDialog();
        });

        binding.loginregisterBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PartnerLoginActivity.class);
            startActivity(intent);
        });

        binding.personalDetailsLayout.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.partner_fragment_container, new PartnerPersonalDetailsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        binding.partnershipLayout.setOnClickListener(v -> {
            PartnershipFragment fragment = new PartnershipFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("currentTypeId", currentPartner.getPartnerTypeId());
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.partner_fragment_container, fragment)
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

        if (currentUser != null) {
            binding.partnerProfileLoginBtnLayout.setVisibility(View.GONE);
            binding.partnerProfileDisplayLayout.setVisibility(View.VISIBLE);
            binding.logoutBtn.setVisibility(View.VISIBLE);
            binding.personalDetailsLayout.setVisibility(View.VISIBLE);

            String uid = currentUser.getUid();
            firebaseFirestore.collection("partners")
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
                                                    if (isAdded() && getContext() != null) {
                                                        Glide.with(getContext().getApplicationContext())
                                                                .load(uri)
                                                                .circleCrop()
                                                                .into(binding.profileImage);

                                                    }
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("PartnerProfileFragment", "Error getting image URL", e);
                                        });
                            }

                            loadCurrentData(user.getEmail());

                        } else {
                            Log.e("PartnerProfileFragment", "User data not found");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("PartnerProfileFragment", "Error getting user data", e);
                    });

        } else {
            binding.partnerProfileLoginBtnLayout.setVisibility(View.VISIBLE);
            binding.partnerProfileDisplayLayout.setVisibility(View.GONE);
            binding.logoutBtn.setVisibility(View.GONE);
            binding.personalDetailsLayout.setVisibility(View.GONE);
        }
    }

    private boolean loadCurrentData(String email) {

        final boolean[] isHavePartner = {false};

        if (getContext() != null){
            Retrofit retrofit = RetrofitClient.getInstance(getContext());
            PartnerApi partnerApi = retrofit.create(PartnerApi.class);

            Call<PartnerDTO> currentPartnerCall = partnerApi.getCurrentPartner(email);
            currentPartnerCall.enqueue(new Callback<PartnerDTO>() {
                @Override
                public void onResponse(Call<PartnerDTO> call, Response<PartnerDTO> response) {
                    if (response.isSuccessful()) {
                        currentPartner = response.body();

                        binding.partnerFrom.setText("Partner from : " + currentPartner.getCreatedAt());

                        isHavePartner[0] = true;
                    } else {
                        Log.e("PartnerProfileFragment", "Error fetching : " + response);
                    }
                }

                @Override
                public void onFailure(Call<PartnerDTO> call, Throwable t) {
                    Log.e("PartnerProfileFragment", "Error fetching : ", t);
                }
            });
        };

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

    private void logout() {
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
                        Log.e("PartnerProfileFragment", "Couldn't clear user credentials: " + e.getLocalizedMessage());
                    }
                });

        Intent intent = new Intent(getContext(), PartnerLoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}