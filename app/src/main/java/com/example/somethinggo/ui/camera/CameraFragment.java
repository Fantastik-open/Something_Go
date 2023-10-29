package com.example.somethinggo.ui.camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.net.Uri;
import java.text.SimpleDateFormat;
import java.util.Date;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.somethinggo.R;
import com.example.somethinggo.databinding.FragmentCameraBinding;
import com.example.somethinggo.ui.drawing.DrawingFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

// The CameraFragment class represents the fragment responsible for handling camera functionalities.
public class CameraFragment extends Fragment {

    public static File image;
    // Binding object for the fragment's view.
    private FragmentCameraBinding binding;
    // ViewModel instance to manage the UI-related data in a lifecycle-conscious way.
    private CameraViewModel cameraViewModel;
    // Constants used for permissions.
    private static final int REQUEST_CAMERA_PERMISSION = 1234;
    private static final int REQUEST_STORAGE_PERMISSION = 4321;

    // Launcher for receiving results from the camera intent.

    // Define a member variable for the image file URI
    private Uri imageUri;

    public static File imageFile;

    // Method to create a file to store the image

    // Remove the default constructor - it's unnecessary.

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        imageFile = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        imageUri = FileProvider.getUriForFile(requireContext(), "com.example.somethinggo.fileprovider", imageFile);
        return imageFile;
    }

    // Method to launch the camera intent
    @SuppressLint("QueryPermissionsNeeded")
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Handle error
                Toast.makeText(requireContext(), "Error occurred while creating the file", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                mTakePictureLauncher.launch(takePictureIntent);
            }
        }
    }

    // And then in your ActivityResultLauncher
    private final ActivityResultLauncher<Intent> mTakePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    DrawingFragment drawingFragment = new DrawingFragment();
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, drawingFragment)  // 'fragment_container' is your fragment container ID
                            .addToBackStack(null)
                            .commit();
                }


            });

    // Called to have the fragment instantiate its user interface view.
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        binding = FragmentCameraBinding.inflate(inflater, container, false);

        // Retrieve ViewModel instance.
        cameraViewModel = new ViewModelProvider(this).get(CameraViewModel.class);
        // Observe changes to the text and update the TextView accordingly.
        cameraViewModel.getText().observe(getViewLifecycleOwner(), binding.textCamera::setText);

        // Set an OnClickListener for the ImageView to check for camera permissions and take a picture.
        // binding.CameraView.setOnClickListener(v -> checkCameraPermissionAndTakePicture());

        binding.captureButton.setOnClickListener(v -> checkCameraPermissionAndTakePicture());

        binding.saveButton.setOnClickListener(v -> {
            // You can save the image to a permanent location, or do any other actions as required.
            Toast.makeText(requireContext(), "Image saved", Toast.LENGTH_SHORT).show();

            // Optionally hide the save and retake buttons after saving
            binding.saveButton.setVisibility(View.GONE);
            binding.retakeButton.setVisibility(View.GONE);
        });

        binding.retakeButton.setOnClickListener(v -> {
            // Relaunch the camera to retake the photo
            checkCameraPermissionAndTakePicture();
            binding.saveButton.setVisibility(View.GONE); // Hide the buttons until a new photo is taken
            binding.retakeButton.setVisibility(View.GONE);
        });


        return binding.getRoot();
    }

    // Method to check for camera and storage permissions and request if they are not granted.
    // Method to check for camera and storage permissions and request if they are not granted.
    private void checkCameraPermissionAndTakePicture() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // If permissions were previously denied, show rationale to the user.
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(requireContext(), "These permissions are necessary to capture and save the image. Please grant them.", Toast.LENGTH_LONG).show();
            }

            // Request the permissions
            requestPermissionLauncher.launch(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE});
        } else {
            dispatchTakePictureIntent();
        }
    }
    // Launcher to handle the result of permission request.
    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean isCameraGranted = result.get(Manifest.permission.CAMERA);
                Boolean isStorageGranted = result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (isCameraGranted != null && isCameraGranted &&
                        isStorageGranted != null && isStorageGranted) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(requireContext(), "Both camera and storage permissions are required for this functionality.", Toast.LENGTH_SHORT).show();
                }
            });

    // Method to save the captured image as a bitmap to external storage.
    public void saveBitmap(Bitmap bitmap, String filename) {
        // Get the public pictures directory.
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(directory, filename);

        // Save the bitmap to a file.
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Toast.makeText(requireContext(), "Image saved at: " + imageFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Error saving the image!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Cleanup resources when the view is destroyed.
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
