package com.example.somethinggo.ui.drawing;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DrawingViewModel extends ViewModel {
    private final MutableLiveData<String> mText;
    private final MutableLiveData<Bitmap> imageBitmap;

    public DrawingViewModel() {
        imageBitmap = new MutableLiveData<>();
        mText = new MutableLiveData<>();
        mText.setValue("This is drawing fragment");
    }

    public LiveData<Bitmap> getImage() {
        return imageBitmap;
    }

    public LiveData<String> getText() {
        return mText;
    }
}