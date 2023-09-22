package com.mvm.mibiblio.search;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.mvm.mibiblio.Collection;
import com.mvm.mibiblio.CollectionDbHelper;

import java.util.LinkedList;
import java.util.List;

public class CollectionBookSearch {
    private ContentValues params;
    private Collection collection;

    public Cursor search(Context context) {
        CollectionDbHelper dbHelper = this.collection.getDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        List<String> queryList = new LinkedList<String>();
        List<String> argsList = new LinkedList<String>();

        for(String key : this.params.keySet()) {
            queryList.add(key + " LIKE ?");
        }
        String queryString = TextUtils.join(" OR ", queryList);

        for(String key : this.params.keySet()) {
            argsList.add("%"  + this.params.getAsString(key) + "%");
        }
        String[] queryArgs = argsList.toArray(new String[]{});

        Cursor cursor = db.query("books", null, queryString, queryArgs,
                null, null, null, null);

        return cursor;
    }

    public CollectionBookSearch(Collection collection, ContentValues params) {
        this.collection = collection;
        this.params = params;
    }
}
