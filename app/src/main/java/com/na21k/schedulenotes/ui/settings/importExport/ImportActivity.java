package com.na21k.schedulenotes.ui.settings.importExport;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.na21k.schedulenotes.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class ImportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        startImport();
    }

    private void startImport() {
        ActivityResultLauncher<Intent> importActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Toast.makeText(getApplicationContext(),
                                R.string.importing_from_backup_toast, Toast.LENGTH_SHORT).show();

                        Intent data = result.getData();

                        if (data != null) {
                            Uri uri = data.getData();
                            readFromFile(uri);
                        } else {
                            Toast.makeText(this, R.string.unexpected_error_toast,
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                R.string.importing_from_backup_failed_toast,
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
        );

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        importActivityResultLauncher.launch(intent);
    }

    private void readFromFile(Uri uri) {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream =
                     getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.unexpected_error_toast, Toast.LENGTH_LONG).show();
        }

        Toast.makeText(this, stringBuilder.toString(), Toast.LENGTH_LONG).show();
        finish();
    }
}