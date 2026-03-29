package me.dinuka.guideme.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBindings;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import me.dinuka.guideme.R;
import me.dinuka.guideme.api.PartnerApi;
import me.dinuka.guideme.api.UserApi;
import me.dinuka.guideme.client.RetrofitClient;
import me.dinuka.guideme.databinding.DialogEditProfileBinding;
import me.dinuka.guideme.dto.PartnerDTO;
import me.dinuka.guideme.dto.UserDTO;
import me.dinuka.guideme.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EditProfileSheet extends BottomSheetDialogFragment {
    private DialogEditProfileBinding binding;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private Uri selectedUri;
    private String changedName;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String ui;

    @Setter
    @Getter
    private String imageUri;

    public EditProfileSheet(String ui) {
        this.ui = ui;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                selectedUri = uri;

                binding.profileImage.setImageURI(uri);

                Log.d("PhotoPicker", "Selected URI: " + uri);
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogEditProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseFirestore.collection(ui)
                .document(firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(ds -> {
                    if (ds.exists()) {
                        User user = ds.toObject(User.class);
                        binding.nameEditField.setText(user.getName());

                        setImageUri(user.getProfilePicUrl());

                        if (user.getProfilePicUrl() != null) {
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            storage.getReference("profile-images/" + user.getProfilePicUrl()).getDownloadUrl()
                                    .addOnSuccessListener(uri -> {

                                        Glide.with(EditProfileSheet.this)
                                                .load(uri)
                                                .circleCrop()
                                                .into(binding.profileImage);

                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("EditProfileSheet", "Error getting image URL", e);
                                    });
                        }
                    }
                });

        binding.btnSave.setOnClickListener(v -> {
            Context appContext = requireContext().getApplicationContext();

            String name = binding.nameEditField.getText().toString().trim();

            if (name.isEmpty()) {
                binding.nameEditField.setError("Name is required");
                binding.nameEditField.requestFocus();
                return;
            }

            Bundle result = new Bundle();

            UUID uuid = UUID.randomUUID();
            String fileName = "profile_pic_" + uuid + ".jpg";

            FirebaseStorage storage = FirebaseStorage.getInstance();

            if (ui.equals("partners")) {
                if (selectedUri != null) {
                    StorageReference imageReference = storage.getReference("profile-images")
                            .child(fileName);
                    imageReference.putFile(selectedUri)
                            .addOnSuccessListener(taskSnapshot -> {
                                firebaseFirestore.collection("partners")
                                        .document(firebaseAuth.getCurrentUser().getUid())
                                        .update("profilePicUrl", fileName, "name", name)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("EditProfileSheet", "Profile picture updated successfully");
                                        });

                                updatePartnerData(appContext, firebaseAuth.getCurrentUser().getEmail(), name, fileName);
                            });
                    result.putString("bundleKey", selectedUri.toString());
                } else {
                    firebaseFirestore.collection("partners")
                            .document(firebaseAuth.getCurrentUser().getUid())
                            .update("name", name)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("EditProfileSheet", "Profile updated successfully");
                            });

                    updatePartnerData(appContext, firebaseAuth.getCurrentUser().getEmail(), name, getImageUri());
                }
            } else if (ui.equals("users")) {
                if (selectedUri != null) {
                    StorageReference imageReference = storage.getReference("profile-images")
                            .child(fileName);
                    imageReference.putFile(selectedUri)
                            .addOnSuccessListener(taskSnapshot -> {
                                firebaseFirestore.collection("users")
                                        .document(firebaseAuth.getCurrentUser().getUid())
                                        .update("profilePicUrl", fileName, "name", name)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("EditProfileSheet", "Profile picture updated successfully");
                                        });

                                updateUserData(appContext, firebaseAuth.getCurrentUser().getEmail(), name, fileName);
                            });
                    result.putString("bundleKey", selectedUri.toString());
                } else {
                    firebaseFirestore.collection("users")
                            .document(firebaseAuth.getCurrentUser().getUid())
                            .update("name", name)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("EditProfileSheet", "Profile updated successfully");
                            });

                    updateUserData(appContext, firebaseAuth.getCurrentUser().getEmail(), name, getImageUri());
                }
            }

            result.putString("updatedName", name);
            getParentFragmentManager().setFragmentResult("requestKey", result);

            dismiss();
        });

        binding.btnEditPhoto.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });
    }

    private void updatePartnerData(Context context, String email, String name, String fileName) {
        if (context == null) return;

        Retrofit instance = RetrofitClient.getInstance(context);
        PartnerApi partnerApi = instance.create(PartnerApi.class);

        PartnerDTO dto = PartnerDTO.builder()
                .email(email)
                .name(name)
                .imageUrl(fileName)
                .build();

        Call<HashMap<String, Object>> partnerDTOCall = partnerApi.partnerUpdateNameAndImage(dto);

        partnerDTOCall.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {

                if (response.isSuccessful()) {
                    HashMap<String, Object> responseBody = response.body();
                    Log.d("EditProfileSheet", "Response: " + responseBody.get("message"));
                } else {
                    Log.e("EditProfileSheet", "Error: " + response);
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                Log.e("EditProfileSheet", "Error: " + t.getMessage());
            }
        });
    }

    private void updateUserData(Context context, String email, String name, String fileName) {
        if (context == null) return;

        Retrofit instance = RetrofitClient.getInstance(context);
        UserApi userApi = instance.create(UserApi.class);

        UserDTO dto = UserDTO.builder()
                .email(email)
                .name(name)
                .imageUrl(fileName)
                .build();

        Call<HashMap<String, Object>> userDTOCall = userApi.userUpdateNameAndImage(dto);

        userDTOCall.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {

                if (response.isSuccessful()) {
                    HashMap<String, Object> responseBody = response.body();
                    Log.d("EditProfileSheet", "Response: " + responseBody.get("message"));
                } else {
                    Log.e("EditProfileSheet", "Error: " + response);
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                Log.e("EditProfileSheet", "Error: " + t.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public int getTheme() {
        return R.style.CustomBottomSheetDialog;
    }
}
