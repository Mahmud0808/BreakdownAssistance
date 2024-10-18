package com.habibur.breakdown_assistance.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.habibur.breakdown_assistance.R;
import com.habibur.breakdown_assistance.config.Prefs;
import com.habibur.breakdown_assistance.databinding.ActivityMainBinding;
import com.habibur.breakdown_assistance.ui.fragments.LandingFragment;
import com.habibur.breakdown_assistance.ui.fragments.MainFragment;
import com.habibur.breakdown_assistance.utils.LocaleHelper;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static FragmentManager fragmentManager;
    private boolean isLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> isLoading);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        initializeDummyMap();
        setContentView(binding.getRoot());
        setupEdgeToEdge();

        fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            if (Prefs.getBoolean("first_run", true)) {
                replaceFragment(new LandingFragment());
            } else {
                replaceFragment(new MainFragment());
            }
        }
    }

    private void setupEdgeToEdge() {
        try {
            AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
            appBarLayout.setStatusBarForeground(
                    MaterialShapeDrawable.createWithElevationOverlay(
                            getApplicationContext()
                    )
            );
        } catch (Exception ignored) {
        }

        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);
    }

    public static void replaceFragment(Fragment fragment) {
        String tag = fragment.getClass().getSimpleName();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);

        if (Objects.equals(tag, MainFragment.class.getSimpleName())) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else if (!Objects.equals(tag, LandingFragment.class.getSimpleName())) {
            fragmentTransaction.addToBackStack(tag);
        }

        fragmentTransaction.commit();
    }

    private void initializeDummyMap() {
        Executors.newSingleThreadExecutor().execute(() -> {
            CountDownLatch latch = new CountDownLatch(1);

            runOnUiThread(() -> {
                SupportMapFragment dummyMapFragment = new SupportMapFragment();
                getSupportFragmentManager().beginTransaction().add(dummyMapFragment, "dummyMapFragment").commit();

                dummyMapFragment.getMapAsync(googleMap -> {
                    Log.d(MainActivity.class.getSimpleName(), "Dummy map is ready");
                    latch.countDown();
                });
            });

            try {
                latch.await();
            } catch (InterruptedException ignored) {
            }

            runOnUiThread(() -> isLoading = false);
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }
}