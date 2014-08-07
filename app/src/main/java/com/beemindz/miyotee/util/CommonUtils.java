package com.beemindz.miyotee.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;

import com.beemindz.miyotee.R;

public class CommonUtils {

  /**
   * Return string date in specified format
   * 
   * @param calendar
   * @param dateFormat
   *          Date format
   * @return
   */
  public static String getStringDate(Calendar calendar, String dateFormat) {
    // Create a DateFormatter object for displaying date in specified format.
    DateFormat formatter = new SimpleDateFormat(dateFormat);
    return formatter.format(calendar.getTime());
  }

  public static String getStringDate(Date date, String dateFormat) {
    // Create a DateFormatter object for displaying date in specified format.
    DateFormat formatter = new SimpleDateFormat(dateFormat);
    return formatter.format(date);
  }

  /**
   * Return date in specified format
   * 
   * @param strDate
   * @param dateFormat
   * @return
   */
  public static Date getDate(String strDate, String dateFormat) {
    // Create a DateFormatter object for displaying date in specified format.
    DateFormat formatter = new SimpleDateFormat(dateFormat);

    try {
      return formatter.parse(strDate);
    } catch (ParseException e) {
      e.printStackTrace();
      return new Date();
    }
  }

  /**
   * Get date format System setting.
   *
   * @param context context activity.
   * @return date format.
   */
  public static String getDateFormatSystem(Context context) {
    String format = Settings.System.getString(context.getContentResolver(), Settings.System.DATE_FORMAT);
    if (TextUtils.isEmpty(format)) {
      format = Constant.DATE_FORMAT;
    }
    return format;
  }
  
  /**
   * Get time format System setting.
   * 
   * @param context context activity.
   * @return date format.
   */
  public static String getTimeFormatSystem(Context context) {
    String format = Settings.System.getString(context.getContentResolver(), Settings.System.TIME_12_24);
    return format;
  }
  
  /**
   * Confirm delete. 
   */
  public static AlertDialog confirmDelete(Context context, OnClickListener yesListener, OnClickListener noListener) {
    AlertDialog alertDialog = new AlertDialog.Builder(context)
    .setTitle(context.getResources().getString(R.string.menu_delete))
    .setMessage(context.getResources().getString(R.string.confirm_delete_task))
    .setPositiveButton(context.getResources().getString(android.R.string.ok), yesListener)
    .setNegativeButton(context.getResources().getString(android.R.string.cancel), noListener).create();
    return alertDialog;
  }

  /**
   * Confirm.
   */
  public static AlertDialog confirm(Context context, int restTitleId, int restMsgId, OnClickListener yesListener, OnClickListener noListener) {
    AlertDialog alertDialog = new AlertDialog.Builder(context)
        .setTitle(context.getResources().getString(restTitleId))
        .setMessage(context.getResources().getString(restMsgId))
        .setPositiveButton(context.getResources().getString(android.R.string.ok), yesListener)
        .setNegativeButton(context.getResources().getString(android.R.string.cancel), noListener).create();
    return alertDialog;
  }

  public static Intent getOpenFacebookIntent(Context context) {
    try {
      context.getPackageManager()
          .getPackageInfo("com.facebook.katana", 0);
      return new Intent(Intent.ACTION_VIEW,
          Uri.parse(String.format("fb://profile/%s", Constant.FB_MIYOTEE_ID)));
    } catch (Exception e) {
      return new Intent(Intent.ACTION_VIEW,
          Uri.parse(String.format("https://www.facebook.com/%s", Constant.FB_MIYOTEE_NAME)));
    }
  }
}
