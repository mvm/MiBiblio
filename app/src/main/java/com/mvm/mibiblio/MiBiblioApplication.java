package com.mvm.mibiblio;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class MiBiblioApplication extends Application {
    private Collection currentCollection;

    public MiBiblioApplication() {
        this.currentCollection = new Collection("default");
    }

    public Collection getCurrentCollection() {
        return currentCollection;
    }

    public void setCurrentCollection(Collection currentCollection) {
        this.currentCollection = currentCollection;
    }
}
