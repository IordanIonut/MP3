package com.example.mp3;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

public class InitialScreenActivity extends AppCompatActivity {
    private EditText etForm, etFormOne;
    private static final int REQUEST_CODE_FOLDER_PICKER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_screen);

        etForm = findViewById(R.id.etForm);
        etFormOne = findViewById(R.id.etFormOne);
        etFormOne.setOnClickListener(view -> openFolderPicker());
    }

    private void openFolderPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        startActivityForResult(intent, REQUEST_CODE_FOLDER_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FOLDER_PICKER && resultCode == RESULT_OK) {
            Uri folderUri = data.getData();
            if (folderUri != null) {
                String folderPath = getFullPathFromTreeUri(folderUri);
                if (folderPath != null) {
                    etFormOne.setText(folderPath);
                } else {
                    Toast.makeText(this, "Failed to retrieve folder path", Toast.LENGTH_SHORT).show();
                }
                Log.d("1234asd", "Folder URI: " + folderUri.toString());
            }
        }
    }

    public void downloadSong(View view) {
        String formValue = etForm.getText().toString().trim();
        String formOneValue = etFormOne.getText().toString().trim();
        if (isYouTubeUrl(formValue)) {
            if (!TextUtils.isEmpty(formOneValue)) {
                Uri folderUri = Uri.parse(formOneValue);
                String folderPath = getFullPathFromTreeUri(folderUri);

                Intent intent = new Intent(this, GrabbingStageActivity.class);
                intent.putExtra("formValue", formValue);
                intent.putExtra("formOneValue", formOneValue);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please select a folder", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Invalid YouTube URL", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFullPathFromTreeUri(Uri treeUri) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                DocumentFile documentFile = DocumentFile.fromTreeUri(this, treeUri);
                if (documentFile != null) {
                    return documentFile.getUri().getPath();
                }
            } else {
                String docId = DocumentsContract.getTreeDocumentId(treeUri);
                return "/" + docId.replace(":", "/");
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    private boolean isYouTubeUrl(String url) {
        return url.startsWith("https://www.youtube.com/watch?v=") || url.startsWith("https://www.youtu.com/watch?v=");
    }
}
