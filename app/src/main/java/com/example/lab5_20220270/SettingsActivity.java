package com.example.lab5_20220270;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.lab5_20220270.databinding.ActivitySettingsBinding;
import com.example.lab5_20220270.storage.PreferencesManager;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding;
    private PreferencesManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefs = new PreferencesManager(this);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Configuración");
        }

        binding.editTextUserName.setText(prefs.getUserName());
        binding.editTextMotivation.setText(prefs.getMotivationMessage());
        binding.editTextMotivationInterval.setText(String.valueOf(prefs.getMotivationIntervalHours()));

        binding.buttonSaveSettings.setOnClickListener(v -> {
            prefs.saveUserName(binding.editTextUserName.getText().toString().trim());
            prefs.saveMotivationMessage(binding.editTextMotivation.getText().toString().trim());
            int hours = Integer.parseInt(binding.editTextMotivationInterval.getText().toString().isEmpty() ? "24" : binding.editTextMotivationInterval.getText().toString());
            prefs.saveMotivationIntervalHours(hours);
            Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
