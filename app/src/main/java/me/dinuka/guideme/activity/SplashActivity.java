package me.dinuka.guideme.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import me.dinuka.guideme.R;
import me.dinuka.guideme.helper.WishlistHelper;
import me.dinuka.guideme.model.User;
import taimoor.sultani.sweetalert2.Sweetalert;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);

            WindowInsetsController controller = getWindow().getInsetsController();

            if(controller != null) {
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            }
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.activity_splash);

        ImageView imageView = findViewById(R.id.splash_logo);

        int nightModeFlags = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;

        int logoResourceId;
        if (nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            logoResourceId = R.drawable.logotpfordark;
        } else {
            logoResourceId = R.drawable.logotpforlight;
        }

        Glide.with(this)
                .asBitmap()
                .load(logoResourceId)
                .override(300)
                .into(imageView);

        new Handler(Looper.getMainLooper())
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isInternetAvailable(SplashActivity.this)){

                            FirebaseUser currentUser = mAuth.getCurrentUser();

                            if (currentUser != null){
                                String uid = currentUser.getUid();

                                firebaseFirestore.collection("partners")
                                        .document(uid)
                                        .get()
                                        .addOnSuccessListener(ds -> {
                                            if (ds.exists()) {

                                                Intent intent = new Intent(SplashActivity.this, PartnerMainActivity.class);
                                                startActivity(intent);
                                                finish();

                                            } else {
                                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                                Log.e(TAG, "Partner data not found");
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "Error getting user data", e);
                                        });
                            } else {
                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        } else {
                            new Sweetalert(SplashActivity.this, Sweetalert.WARNING_TYPE)
                                    .setTitleText("Warning")
                                    .setContentText("Device is not connected to the internet. Please connect to the internet and try again.")
                                    .show();
                        }



//                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//
//                        Intent intent = new Intent(SplashActivity.this, PartnerMainActivity.class);
//                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
//
//                        Intent intent = new Intent(SplashActivity.this, CompassActivity.class);
//
//                        WishlistHelper.clearWishlist(SplashActivity.this);
//                        startActivity(intent);
//                        finish();
                    }
                }, 2000);

    }

    public boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            Network network = cm.getActiveNetwork();
            if (network == null) return false;

            NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
            return capabilities != null &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        }
        return false;
    }
}