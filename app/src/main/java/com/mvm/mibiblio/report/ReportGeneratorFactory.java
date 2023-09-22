package com.mvm.mibiblio.report;

import android.content.Context;

import com.mvm.mibiblio.Collection;

import java.io.OutputStream;

public class ReportGeneratorFactory {
    public static ReportGenerator newInstance(String format, Context context,
                                              OutputStream outputStream, Collection collection)
    {
        if(format.equalsIgnoreCase("html"))
            return new HTMLReportGenerator(context, outputStream, collection);
        return null;
    }
}
