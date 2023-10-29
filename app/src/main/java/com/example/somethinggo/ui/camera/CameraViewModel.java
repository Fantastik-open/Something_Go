package com.example.somethinggo.ui.camera;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CameraViewModel extends ViewModel {
    private final MutableLiveData<String> mText;
    private final MutableLiveData<Bitmap> imageBitmap;

    public CameraViewModel() {
        imageBitmap = new MutableLiveData<>();
        mText = new MutableLiveData<>();
        mText.setValue("This is camera fragment");
    }

    public LiveData<Bitmap> getImage() {
        return imageBitmap;
    }

    public LiveData<String> getText() {
        return mText;
    }
}