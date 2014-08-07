package com.beemindz.miyotee.service.reminder;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.beemindz.miyotee.dao.TaskContentProvider;
import com.beemindz.miyotee.dao.TaskDao;
import com.beemindz.miyotee.util.Constant;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class OnBootReceiver extends BroadcastReceiver {

  private static final String[] TASK_PROJECTION = new String[]{TaskDao.Properties.Id.columnName, TaskDao.Properties.TaskId.columnName,
      TaskDao.Properties.TaskName.columnName, TaskDao.Properties.TaskDescription.columnName, TaskDao.Properties.ReminderDate.columnName,
      TaskDao.Properties.CreatedDate.columnName, TaskDao.Properties.ReminderDate.columnName, TaskDao.Properties.IsReminder.columnName, TaskDao.Properties.IsComplete.columnName};

  @SuppressLint("SimpleDateFormat")
  @Override
  public void onReceive(Context context, Intent intent) {
    // TODO Auto-generated method stub
    Log.d("==OnBootReceiver==", "==onReceive==");
    ReminderManager reminderMgr = new ReminderManager();

    Cursor cursor = context.getContentResolver().query(TaskContentProvider.CONTENT_URI, TASK_PROJECTION,
        TaskDao.Properties.IsReminder.columnName + " = ? AND " + TaskDao.Properties.IsComplete.columnName + " = ? ", new String[]{"1", "0"}, null);
    if (cursor != null) {
      cursor.moveToFirst();
      int rowIdColumnIndex = cursor.getColumnIndex(TaskDao.Properties.Id.columnName);
      int dateTimeColumnIndex = cursor.getColumnIndex(TaskDao.Properties.ReminderDate.columnName);
      int titleColumnIndex = cursor.getColumnIndex(TaskDao.Properties.TaskName.columnName);
      int bodyColumnIndex = cursor.getColumnIndex(TaskDao.Properties.TaskDescription.columnName);
      int reminderColumnIndex = cursor.getColumnIndex(TaskDao.Properties.IsReminder.columnName);
      int completeColumnIndex = cursor.getColumnIndex(TaskDao.Properties.IsComplete.columnName);
      while (!cursor.isAfterLast()) {
        Long rowId = cursor.getLong(rowIdColumnIndex);
        String dateTime = cursor.getString(dateTimeColumnIndex);
        String title = cursor.getString(titleColumnIndex);
        String body = cursor.getString(bodyColumnIndex);
        boolean isReminder = cursor.getInt(reminderColumnIndex) == 0 ? false
            : true;
        boolean isComplete = cursor.getInt(completeColumnIndex) == 0 ? false
            : true;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat(Constant.DATE_TIME_FORMAT);
        try {
          java.util.Date date = format.parse(dateTime);
          cal.setTime(date);
          if (isReminder && !isComplete) {
            reminderMgr.setReminder(context, rowId, cal, title, body);
          }
        } catch (Exception e) {
          Log.e("OnBootReceiver", e.getMessage(), e);
        }
        cursor.moveToNext();
      }
      cursor.close();
    }
  }

}
