package com.mvm.mibiblio.report;

import android.content.Context;

import com.mvm.mibiblio.Collection;

import java.io.OutputStream;

public abstract class ReportGenerator {
    public ReportGenerator(Context context, OutputStream outputStream, Collection collection) {}
    public abstract void save() throws Exception;
}
