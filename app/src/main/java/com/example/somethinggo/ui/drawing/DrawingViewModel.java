package com.example.somethinggo.ui.drawing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DrawingViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public DrawingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is drawing fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}