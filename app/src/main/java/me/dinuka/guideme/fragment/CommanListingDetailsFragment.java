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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.richeditor.RichEditor;
import me.dinuka.guideme.R;
import me.dinuka.guideme.adapter.SelectedImagesAdapter;
import me.dinuka.guideme.databinding.FragmentCommanListingDetailsBinding;
import taimoor.sultani.sweetalert2.Sweetalert;


public class CommanListingDetailsFragment extends Fragment implements OnMapReadyCallback {

    private FragmentCommanListingDetailsBinding binding;
    private String categoryName;
    private int categoryId;
    private RichEditor mEditor;
    private TextView mPreview;
    private List<Uri> selectedImages = new ArrayList<>();
    private SelectedImagesAdapter adapter;
    private RecyclerView recyclerView;
    private MapView mapView;
    private GoogleMap mMap;
    private double latitude = 6.9270408;
    private double longitude = 79.8557599;
    String locationName = "default";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryName = getArguments().getString("categoryName");
            categoryId = getArguments().getInt("categoryId");
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

        mapView = binding.locationView;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        getActivity().findViewById(R.id.partnerBottomNavigationView).setVisibility(View.GONE);

        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        getParentFragmentManager().setFragmentResultListener("requestKey", getViewLifecycleOwner(), (requestKey, bundle) -> {
            latitude = bundle.getDouble("latitude");
            longitude = bundle.getDouble("longitude");
            locationName = bundle.getString("locationName");

            if (mMap != null) {
                LatLng selectedLatLng = new LatLng(latitude, longitude);
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(selectedLatLng).title(locationName));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 15f));
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

        binding.locationSelectLayout.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.partner_fragment_container, new MapViewFragment())
                    .addToBackStack(null)
                    .commit();
        });

        binding.nextFromCommanDetailBtn.setOnClickListener(v -> {
            String title = binding.listingTitle.getText().toString().trim();
            String htmlDescription = mEditor.getHtml();
            String address = binding.listngAddress.getText().toString().trim();
            String price = binding.listingPrice.getText().toString().trim();

//            Log.d("TITLE", title);
//            Log.d("HTML", htmlDescription);
//            Log.d("ADDRESS", address);
//            Log.d("PRICE", price);
//            Log.d("LATITUDE", String.valueOf(latitude));
//            Log.d("LONGITUDE", String.valueOf(longitude));
//            Log.d("LOCATION", locationName);

            if (title.isEmpty()){
                binding.listingTitle.setError("Title is required");
                binding.listingTitle.requestFocus();
                return;
            }

            if (isEditorEmpty(htmlDescription)){
                mEditor.requestFocus();
                new Sweetalert(requireContext(), Sweetalert.WARNING_TYPE)
                        .setTitleText("Warning")
                        .setContentText("Discription is required")
                        .show();
                return;
            }

            if (locationName.equals("default")){
                new Sweetalert(requireContext(), Sweetalert.WARNING_TYPE)
                        .setTitleText("Warning")
                        .setContentText("Select Location")
                        .show();
                return;
            }

            if (address.isEmpty()){
                binding.listngAddress.setError("Address is required");
                binding.listngAddress.requestFocus();
                return;
            }

            if (selectedImages.isEmpty()){
                new Sweetalert(requireContext(), Sweetalert.WARNING_TYPE)
                        .setTitleText("Warning")
                        .setContentText("Select Images Of Your Listing")
                        .show();
                return;
            }

            if (price.isEmpty()){
                binding.listingPrice.setError("Price is required");
                binding.listingPrice.requestFocus();
                return;
            }

            HashMap<String, Object> listingData = new HashMap<>();
            listingData.put("title", title);
            listingData.put("description", htmlDescription);
            listingData.put("address", address);
            listingData.put("price", price);
            listingData.put("latitude", latitude);
            listingData.put("longitude", longitude);
            listingData.put("locationName", locationName);
            listingData.put("images", selectedImages);
            listingData.put("categoryName", categoryName);
            listingData.put("categoryId", categoryId);

            Bundle bundle = new Bundle();
            bundle.putSerializable("listingData", listingData);

            if (categoryName.equals("Hotels")){

                HotelListingDetailsFragment hotelListingDetailsFragment = new HotelListingDetailsFragment();
                hotelListingDetailsFragment.setArguments(bundle);

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.partner_fragment_container, hotelListingDetailsFragment)
                        .addToBackStack(null)
                        .commit();

            } else if (categoryName.equals("Things To Do")){

                ThingsToDoListingDetailsFragment thingsToDoListingDetailsFragment = new ThingsToDoListingDetailsFragment();
                thingsToDoListingDetailsFragment.setArguments(bundle);

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.partner_fragment_container, thingsToDoListingDetailsFragment)
                        .addToBackStack(null)
                        .commit();

            } else if (categoryName.equals("Services")){

                ServiceListingDetailsFragment serviceListingDetailsFragment = new ServiceListingDetailsFragment();
                serviceListingDetailsFragment.setArguments(bundle);

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.partner_fragment_container, serviceListingDetailsFragment)
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
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);

        LatLng clt = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(clt).title("Colombo Lotus Tower"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(clt, 15f));
    }

    public boolean isEditorEmpty(String html) {
        if (html == null || html.trim().isEmpty()) {
            return true;
        }

        // Convert HTML to plain text to strip tags like <p> or <br>
        String plainText;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            plainText = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            plainText = Html.fromHtml(html).toString();
        }

        // Check if the actual text (without tags) is empty
        return plainText.trim().isEmpty();
    }
}