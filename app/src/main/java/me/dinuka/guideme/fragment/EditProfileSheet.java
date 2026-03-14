package me.dinuka.guideme.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBindings;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import me.dinuka.guideme.R;
import me.dinuka.guideme.databinding.DialogEditProfileBinding;

public class EditProfileSheet extends BottomSheetDialogFragment {
    private DialogEditProfileBinding binding;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                // This is the URI of the image the user selected
                binding.profileImage.setImageURI(uri);

                Bundle result = new Bundle();
                result.putString("bundleKey", uri.toString());
                getParentFragmentManager().setFragmentResult("requestKey", result);
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

        binding.btnSave.setOnClickListener(v -> {
            String name = binding.etName.getText().toString();
            // Perform your update logic here
            dismiss();
        });

        binding.btnEditPhoto.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        return binding.getRoot();
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
