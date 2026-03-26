package me.dinuka.guideme.fragment;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.dinuka.guideme.R;
import me.dinuka.guideme.api.DirectionApi;
import me.dinuka.guideme.databinding.FragmentDirectionMapViewBinding;
import me.dinuka.guideme.databinding.FragmentMapViewBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DirectionMapViewFragment extends Fragment implements OnMapReadyCallback {

    private FragmentDirectionMapViewBinding binding;
    private GoogleMap mMap;
    private Marker markerPin;
    private double latitude = 6.9270408;
    private double longitude = 79.8557599;
    private String locationName = "Colombo Lotus Tower";
    private Polyline polyline;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            latitude = getArguments().getDouble("latitude");
            longitude = getArguments().getDouble("longitude");
            locationName = getArguments().getString("locationName");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDirectionMapViewBinding.inflate(inflater, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.directionMapViewMap);
        mapFragment.getMapAsync(this);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.GONE);

        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();
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
        Marker currentMarker = mMap.addMarker(new MarkerOptions().position(clt).title(locationName));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(clt, 15f));

        mMap.setOnMapLongClickListener(latLng -> {
            double latitude = latLng.latitude;
            double longitude = latLng.longitude;

            if (markerPin == null) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.man));
                markerPin = mMap.addMarker(markerOptions);
            } else {
                markerPin.setPosition(latLng);
            }

            getDirectionWithApi(clt, latLng);

        });
    }

    private final ExecutorService service = Executors.newSingleThreadExecutor();
    public void getDirectionWithApi(LatLng start, LatLng end) {

        service.execute(() -> {
            String key = "AIzaSyAmCWUX4X6qtE70dZIWAgkHDBnaaO6GbMg";

            String origin = start.latitude + "," + start.longitude;
            String destination = end.latitude + "," + end.longitude;

            try (GeoApiContext context = new GeoApiContext.Builder().apiKey(key).build()) {

                try {
                    DirectionsResult result = DirectionsApi.newRequest(context)
                            .mode(TravelMode.DRIVING)
                            .origin(origin)
                            .destination(destination)
                            .departureTimeNow()
                            .alternatives(true)
                            .await();

                    if (result.routes.length > 0) {
                        String encodedPath = result.routes[0].overviewPolyline.getEncodedPath();

                        List<LatLng> points = PolyUtil.decode(encodedPath);

                        requireActivity().runOnUiThread(() -> {
                            if (polyline == null) {
                                PolylineOptions polylineOptions = new PolylineOptions();
                                polylineOptions.width(20);
                                int color = ContextCompat.getColor(requireContext(), R.color.polyline_color);
                                polylineOptions.color(color);
                                polylineOptions.addAll(points);

                                polyline = mMap.addPolyline(polylineOptions);
                            } else {
                                polyline.setPoints(points);
                            }
                        });

                    }

                } catch (ApiException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.GONE);
    }
}