package me.dinuka.guideme.fragment;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import me.dinuka.guideme.R;
import me.dinuka.guideme.databinding.ActivityMapsBinding;
import me.dinuka.guideme.databinding.FragmentMapViewBinding;

public class MapViewFragment extends Fragment implements OnMapReadyCallback {

    private FragmentMapViewBinding binding;
    private GoogleMap mMap;
    private Marker markerPin;
    private double latitude = 6.9270408;
    private double longitude = 79.8557599;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMapViewBinding.inflate(inflater, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapViewMap);
        mapFragment.getMapAsync(this);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        markerPin = mMap.addMarker(new MarkerOptions().position(clt).title("Colombo Lotus Tower"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(clt, 10f));

        mMap.setOnMapLongClickListener(latLng -> {
            double latitude = latLng.latitude;
            double longitude = latLng.longitude;

            if (markerPin == null) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
//                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.location_on_24px));
                markerPin = mMap.addMarker(markerOptions);
            } else {
                markerPin.setPosition(latLng);
            }

            String address = getAddressFromLatLng(latLng);

            if (address != null) {
                Bundle result = new Bundle();
                result.putDouble("latitude", latLng.latitude);
                result.putDouble("longitude", latLng.longitude);
                result.putString("locationName", address);
                getParentFragmentManager().setFragmentResult("requestKey", result);
//                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0);
                return address;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}