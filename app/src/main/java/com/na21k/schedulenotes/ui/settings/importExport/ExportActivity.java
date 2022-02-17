package com.na21k.schedulenotes.ui.settings.importExport;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.na21k.schedulenotes.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class ExportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        startExport();
    }

    private void startExport() {
        Date today = new Date();
        String fileName = "scheduleAndNotesBackup_" + today.toString()
                .replace(' ', '_') + ".bak";

        ActivityResultLauncher<Intent> exportActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Toast.makeText(getApplicationContext(),
                                R.string.backing_up_toast, Toast.LENGTH_SHORT).show();

                        Intent data = result.getData();

                        if (data != null) {
                            Uri uri = data.getData();
                            writeDataToFile(uri);
                        } else {
                            Toast.makeText(this, R.string.unexpected_error,
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                R.string.backing_up_failed_toast, Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
        );

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("*/bak");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        exportActivityResultLauncher.launch(intent);
    }

    private void writeDataToFile(Uri uri) {
        try {
            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "w");
            FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
            fileOutputStream.write(("Overwritten at " + System.currentTimeMillis() +
                    "\n").getBytes());
            // Let the document provider know we're done by closing the stream.
            fileOutputStream.close();
            pfd.close();

            Toast.makeText(getApplicationContext(), R.string.backup_created_successfully_toast,
                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.unexpected_error, Toast.LENGTH_LONG).show();
        }

        finish();
    }
}