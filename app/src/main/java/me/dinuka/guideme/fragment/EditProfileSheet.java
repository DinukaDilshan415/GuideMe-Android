package me.dinuka.guideme.fragment;

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

import java.util.UUID;

import me.dinuka.guideme.R;
import me.dinuka.guideme.databinding.DialogEditProfileBinding;
import me.dinuka.guideme.model.User;

public class EditProfileSheet extends BottomSheetDialogFragment {
    private DialogEditProfileBinding binding;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private Uri selectedUri;
    private String changedName;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String ui;

    public EditProfileSheet(String ui) {
        this.ui = ui;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                selectedUri = uri;
                // This is the URI of the image the user selected
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
            String name = binding.nameEditField.getText().toString();
            Bundle result = new Bundle();

            UUID uuid = UUID.randomUUID();
            String fileName = "profile_pic_" + uuid + ".jpg";

            FirebaseStorage storage = FirebaseStorage.getInstance();
            if (selectedUri != null) {
                if (ui.equals("partners")) {
                    StorageReference imageReference = storage.getReference("profile-images")
                            .child(fileName);
                    imageReference.putFile(selectedUri)
                            .addOnSuccessListener(taskSnapshot -> {
                                firebaseFirestore.collection("partners")
                                        .document(firebaseAuth.getCurrentUser().getUid())
                                        .update("profilePicUrl", fileName)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("EditProfileSheet", "Profile picture updated successfully");
                                        });
                            });
                } else if (ui.equals("users")) {
                    StorageReference imageReference = storage.getReference("profile-images")
                            .child(fileName);
                    imageReference.putFile(selectedUri)
                            .addOnSuccessListener(taskSnapshot -> {
                                firebaseFirestore.collection("users")
                                        .document(firebaseAuth.getCurrentUser().getUid())
                                        .update("profilePicUrl", fileName)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("EditProfileSheet", "Profile picture updated successfully");
                                        });
                            });
                }
                result.putString("bundleKey", selectedUri.toString());
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks
    }

    @Override
    public int getTheme() {
        return R.style.CustomBottomSheetDialog;
    }
}
