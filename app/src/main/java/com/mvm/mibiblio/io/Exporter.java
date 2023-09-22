package com.mvm.mibiblio.io;

import android.content.Context;
import android.icu.util.Output;

import com.mvm.mibiblio.Collection;

import java.io.OutputStream;

public abstract class Exporter {
    public Exporter(Context context, OutputStream outputStream, Collection collection) {
    }

    public abstract void save() throws Exception;
}
