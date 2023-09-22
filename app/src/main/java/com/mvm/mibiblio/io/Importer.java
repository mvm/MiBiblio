package com.mvm.mibiblio.io;

import android.content.Context;
import android.renderscript.ScriptGroup;

import com.mvm.mibiblio.Collection;

import java.io.InputStream;

public abstract class Importer {
    public Importer(Context context, InputStream inputStream, Collection collection) {}
    public abstract void read() throws Exception;
}
