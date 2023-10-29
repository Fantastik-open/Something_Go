package com.example.somethinggo.ui.drawing;
import com.example.somethinggo.ui.camera.CameraFragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.somethinggo.databinding.FragmentDrawingBinding;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.io.File;

public class DrawingFragment extends Fragment {

    private FragmentDrawingBinding binding;

    private Uri imageUri;

    public File image = CameraFragment.image;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DrawingViewModel drawingViewModel =
                new ViewModelProvider(this).get(DrawingViewModel.class);

        binding = FragmentDrawingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Load your image into the PhotoView
        PhotoView photoView = binding.photoView;
        imageUri = FileProvider.getUriForFile(requireContext(), "com.example.somethinggo.fileprovider", image);/* Your image URI here */;

        Picasso.get().load(imageUri).into(photoView);

        // If you still want to use the TextView
        final TextView textView = binding.textDrawing;
        drawingViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
