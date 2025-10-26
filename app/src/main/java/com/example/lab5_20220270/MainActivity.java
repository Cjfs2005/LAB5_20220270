package com.example.lab5_20220270;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Build;
import android.Manifest;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab5_20220270.databinding.ActivityMainBinding;
import com.example.lab5_20220270.storage.PreferencesManager;

import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private PreferencesManager prefs;
    private final String PROFILE_FILE = "profile.jpg";

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) saveProfileImage(uri);
            }
    );

    private final ActivityResultLauncher<String> requestNotificationPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            granted -> {
                if (!granted) {
                    Toast.makeText(this, "Permiso de notificaciones no concedido. Algunas funciones podrán no mostrarse.", Toast.LENGTH_LONG).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefs = new PreferencesManager(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS);
        }

        binding.textGreeting.setText("¡Hola " + prefs.getUserName() + "!");
        binding.textMotivation.setText(prefs.getMotivationMessage());

        loadProfileImageIfExists();

        binding.imageProfile.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        binding.buttonViewCourses.setOnClickListener(v -> startActivity(new Intent(this, CoursesActivity.class)));
        binding.buttonSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.textGreeting.setText("¡Hola " + prefs.getUserName() + "!");
        binding.textMotivation.setText(prefs.getMotivationMessage());
    }

    private void saveProfileImage(Uri uri) {
        try (InputStream is = getContentResolver().openInputStream(uri);
             FileOutputStream fos = openFileOutput(PROFILE_FILE, MODE_PRIVATE)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = is.read(buffer)) != -1) fos.write(buffer, 0, len);
            loadProfileImageIfExists();
            Toast.makeText(this, "Imagen guardada", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error guardando imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProfileImageIfExists() {
        try (InputStream is = openFileInput(PROFILE_FILE)) {
            Bitmap bmp = BitmapFactory.decodeStream(is);
            if (bmp != null) binding.imageProfile.setImageBitmap(bmp);
        } catch (Exception ignored) {
        }
    }
}