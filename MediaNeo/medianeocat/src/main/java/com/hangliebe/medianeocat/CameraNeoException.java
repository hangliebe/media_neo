package com.hangliebe.medianeocat;

import android.util.AndroidException;

import androidx.annotation.Nullable;

public class CameraNeoException extends AndroidException {
    public CameraNeoException() {
        super();
    }

    public CameraNeoException(String name) {
        super(name);
    }
}
