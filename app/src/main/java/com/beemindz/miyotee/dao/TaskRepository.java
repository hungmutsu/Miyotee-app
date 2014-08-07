package com.beemindz.miyotee.dao;

import android.content.Context;

import com.beemindz.miyotee.MiyoteeApplication;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Sony on 7/31/2014.
 */
public class TaskRepository {
  public static long insertOrUpdate(Context context, Task task) {
    return getTaskDao(context).insertOrReplace(task);
  }

  public static void clearTasks(Context context) {
    getTaskDao(context).deleteAll();
  }

  public static void deleteTaskWithId(Context context, long id) {
    getTaskDao(context).delete(getTaskForId(context, id));
  }

  public static List<Task> getAllTasks(Context context) {
    return getTaskDao(context).loadAll();
  }

  public static Task getTaskForId(Context context, long id) {
    return getTaskDao(context).load(id);
  }

  private static TaskDao getTaskDao(Context c) {
    return ((MiyoteeApplication) c.getApplicationContext()).getDaoSession().getTaskDao();
  }

}
