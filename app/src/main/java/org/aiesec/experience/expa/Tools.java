package org.aiesec.experience.expa;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by FanQuan on 2015/2/15 0015.
 * There are some general function here. DO NOT create instance.
 */
public class Tools {

    public static boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = mgr.getAllNetworkInfo();
        if (info != null)
        {
            for (int i = 0; i < info.length; i++)
            {
                if (info[i].getState() == NetworkInfo.State.CONNECTED)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static void moveFile(File from, File to) throws IOException
    {
        FileInputStream inputStream = new FileInputStream(from);
        int count = inputStream.available();
        byte[] temp = new byte[count];
        inputStream.read(temp);
        FileOutputStream outputStream = new FileOutputStream(to);
        outputStream.write(temp);
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    public static Date dateFromRFC3339(String RFC3339String) throws ParseException
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return formatter.parse(RFC3339String);
    }

    public static String startEndDateStringFromRFC3339(String startRFC3339, String endRFC3339) throws ParseException
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(dateFromRFC3339(startRFC3339)) + " ~ " + formatter.format(dateFromRFC3339(endRFC3339));
    }

    public static String formattedDateStringFromRFC3339(String RFC3339String) throws ParseException
    {
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date date = formatter1.parse(RFC3339String);
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
        return formatter2.format(date);
    }
}
