package com.mvm.mibiblio.io;

import android.content.Context;

import com.mvm.mibiblio.Collection;

import java.io.OutputStream;

public class ExporterFactory {
    public static Exporter newInstance(String format, Context context,
                                       OutputStream outputStream, Collection collection) {
        if(format.equalsIgnoreCase("xml")) {
            return new XMLExporter(context, outputStream, collection);
        }
        return null;
    }
}
