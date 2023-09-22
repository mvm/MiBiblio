package com.mvm.mibiblio.io;

import android.content.Context;

import com.mvm.mibiblio.Collection;

import java.io.InputStream;

public class ImporterFactory {
    public static Importer newInstance(String format, Context context, InputStream inputStream,
                                       Collection collection) {
        if(format.equalsIgnoreCase("xml"))
            return new XMLImporter(context, inputStream, collection);
        return null;
    }
}
