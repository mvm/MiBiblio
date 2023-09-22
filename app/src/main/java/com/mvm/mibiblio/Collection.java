package com.mvm.mibiblio;

import android.content.Context;

public class Collection {
    private String name;
    private CollectionDbHelper dbHelper;

    public Collection(String name) {
        this.name = name;
    }

    public CollectionDbHelper getDbHelper(Context context) {
        if(this.dbHelper == null) {
            this.dbHelper = new CollectionDbHelper(context, this.name);
        }
        return this.dbHelper;
    }

    public String getName() {
        return this.name;
    }
}
