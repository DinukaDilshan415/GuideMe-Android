package me.dinuka.guideme.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.richeditor.RichEditor;
import me.dinuka.guideme.R;
import me.dinuka.guideme.adapter.SelectedImagesAdapter;
import me.dinuka.guideme.databinding.FragmentCommanListingDetailsBinding;


public class CommanListingDetailsFragment extends Fragment {

    private FragmentCommanListingDetailsBinding binding;
    private String categoryName;
    private RichEditor mEditor;
    private TextView mPreview;

    private List<Uri> selectedImages = new ArrayList<>();
    private SelectedImagesAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryName = getArguments().getString("categoryName");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCommanListingDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().findViewById(R.id.partnerBottomNavigationView).setVisibility(View.GONE);

        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        descriptionEditorCreate();

        // Setup RecyclerView
        recyclerView = binding.recyclerPreviews;
        adapter = new SelectedImagesAdapter(selectedImages, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

        // Setup Gallery Launcher
        ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        if (result.getData().getClipData() != null) {
                            // Multiple images selected
                            int count = result.getData().getClipData().getItemCount();
                            for (int i = 0; i < count; i++) {
                                selectedImages.add(result.getData().getClipData().getItemAt(i).getUri());
                            }
                        } else if (result.getData().getData() != null) {
                            // Single image selected
                            selectedImages.add(result.getData().getData());
                        }

                        adapter.notifyDataSetChanged();
                        updateImageVisibility();
                    }
                }
        );

        // Button to trigger selection
        binding.btnPickImages.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            galleryLauncher.launch(Intent.createChooser(intent, "Select Photos for GuideMe"));
        });

        binding.nextFromCommanDetailBtn.setOnClickListener(v -> {
            if (categoryName.equals("Hotels")){
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.partner_fragment_container, new HotelListingDetailsFragment())
                        .addToBackStack(null)
                        .commit();
            } else if (categoryName.equals("Things To Do")){
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.partner_fragment_container, new ThingsToDoListingDetailsFragment())
                        .addToBackStack(null)
                        .commit();
            } else if (categoryName.equals("Services")){
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.partner_fragment_container, new ServiceListingDetailsFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        binding.backBtn.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

    }

    public void updateImageVisibility() {
        if (!selectedImages.isEmpty()) {
            binding.emptyImageView.setVisibility(View.GONE);
            binding.recyclerPreviews.setVisibility(View.VISIBLE);
        } else {
            binding.emptyImageView.setVisibility(View.VISIBLE);
            binding.recyclerPreviews.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().findViewById(R.id.partnerBottomNavigationView).setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().findViewById(R.id.partnerBottomNavigationView).setVisibility(View.GONE);
    }

    private void descriptionEditorCreate(){
        mEditor = (RichEditor) binding.stylishTextEditor.editor;
        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(18);
        mEditor.setPadding(10, 10, 10, 10);
        mEditor.setPlaceholder("Insert text here...");
        //mEditor.setInputEnabled(false);

        binding.stylishTextEditor.actionUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.undo();
            }
        });

        binding.stylishTextEditor.actionRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.redo();
            }
        });

        binding.stylishTextEditor.actionBold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBold();
            }
        });

        binding.stylishTextEditor.actionItalic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setItalic();
            }
        });

        binding.stylishTextEditor.actionUnderline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setUnderline();
            }
        });

        binding.stylishTextEditor.actionHeading1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(1);
            }
        });

        binding.stylishTextEditor.actionHeading2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(2);
            }
        });


        binding.stylishTextEditor.actionInsertBullets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBullets();
            }
        });
    }

}