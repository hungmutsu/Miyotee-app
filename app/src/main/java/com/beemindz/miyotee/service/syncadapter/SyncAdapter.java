package com.beemindz.miyotee.service.syncadapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.beemindz.miyotee.dao.TaskContentProvider;
import com.beemindz.miyotee.dao.TaskDao;
import com.beemindz.miyotee.dao.TaskDraftContentProvider;
import com.beemindz.miyotee.dao.TaskDraftDao;
import com.beemindz.miyotee.util.Constant;
import com.beemindz.miyotee.util.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

  private final String TAG = "SyncAdapter";
//  private final String URL_HOST = "http://ohoh123.byethost8.com/mytodo/";
  private final String URL_HOST = "http://192.168.1.77/mytodo-service/";
  // JSON Node names
  public final String TAG_ERROR = "error";

  private final String[] TASK_DRAFT_PROJECTION = new String[]{TaskDraftDao.Properties.Id.columnName,
      TaskDraftDao.Properties.TaskId.columnName, TaskDraftDao.Properties.UserName.columnName, TaskDraftDao.Properties.TaskName.columnName,
      TaskDraftDao.Properties.TaskDescription.columnName, TaskDraftDao.Properties.ReminderDate.columnName,
      TaskDraftDao.Properties.CreatedDate.columnName, TaskDraftDao.Properties.UpdatedDate.columnName, TaskDraftDao.Properties.Status.columnName};

  public SyncAdapter(Context context, boolean autoInitialize) {
    super(context, autoInitialize);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void onPerformSync(Account account, Bundle bundle, String authority,
                            ContentProviderClient contentProviderClient, SyncResult syncResult) {
    Log.i(TAG, "starting sync");

    String updatedDate = getUpdatedDate(contentProviderClient);
    syncToLocal(account, contentProviderClient, updatedDate);
    syncToServer(account, contentProviderClient);

    Log.i(TAG, "done sync");

  }

  /**
   * @param contentProviderClient
   * @throws android.os.RemoteException
   */
  public void syncToLocal(Account account, ContentProviderClient contentProviderClient, String updated) {
    Log.i(TAG, "begin sync to local");
    String urlGetAllTask = URL_HOST + "get-all-task.php";

    // Building Parameters
    String[] keys = new String[]{"username", "updatedDate"};
    String[] values = new String[]{account.name, updated};

    try {
      Log.i(TAG, "Last updatedDate : " + updated);
      JSONObject json = NetworkUtils.postJSONObjFromUrl(urlGetAllTask, keys, values);
      if (json != null) {
        // check your log for json response
        Log.d("All task server result", json.toString());

        // json success tag
        boolean error = json.getBoolean(TAG_ERROR);

        if (!error) {

          JSONArray arrTask = json.getJSONArray("task");
          for (int i = 0; i < arrTask.length(); i++) {

            ContentValues contentValues = new ContentValues();
            JSONObject jsonObject = arrTask.getJSONObject(i);

            contentValues.put(TaskDao.Properties.TaskId.columnName, jsonObject.getInt(TaskDao.Properties.TaskId.columnName));
            contentValues.put(TaskDao.Properties.UserName.columnName, account.name);
            contentValues.put(TaskDao.Properties.TaskName.columnName, jsonObject.getString(TaskDao.Properties.TaskName.columnName));
            contentValues.put(TaskDao.Properties.TaskDescription.columnName,
                jsonObject.getString(TaskDao.Properties.TaskDescription.columnName));

            String reminder = jsonObject.getString(TaskDao.Properties.ReminderDate.columnName);
            String createdDate = jsonObject.getString(TaskDao.Properties.CreatedDate.columnName);
            String updatedDate = jsonObject.getString(TaskDao.Properties.UpdatedDate.columnName);

            if (!TextUtils.isEmpty(reminder) && !"0000-00-00 00:00:00".equals(reminder)) {
              contentValues.put(TaskDao.Properties.ReminderDate.columnName, reminder);
            }
            if (!TextUtils.isEmpty(createdDate)) {
              contentValues.put(TaskDao.Properties.CreatedDate.columnName, createdDate);
            }
            if (!TextUtils.isEmpty(updatedDate)) {
              contentValues.put(TaskDao.Properties.UpdatedDate.columnName, updatedDate);
            }

            // Kiểm tra tồn tại task?
            Cursor cursor = contentProviderClient.query(TaskContentProvider.CONTENT_URI, new String[]{TaskDao.Properties.UpdatedDate.columnName},
                TaskDao.Properties.TaskId.columnName + " = ? ", new String[]{jsonObject.getString(TaskDao.Properties.TaskId.columnName)}, "_ID DESC LIMIT(1)");

            if (cursor.getCount() > 0) {
              cursor.moveToFirst();
              Log.i(TAG, "Count Cursor : " + cursor.getCount());
              // Local taskId
              int id = cursor.getInt(cursor.getColumnIndex(TaskDao.Properties.Id.columnName));
              // Thực hiện update
              Uri updateUri = Uri.withAppendedPath(TaskContentProvider.CONTENT_ID_URI, String.valueOf(id));
              Log.i(TAG, updateUri.toString());
              contentProviderClient.update(updateUri, contentValues, null, null);
            } else {
              // Thực hiện insert
              Log.i(TAG, "Inserting task");
              contentProviderClient.insert(TaskContentProvider.CONTENT_URI, contentValues);
              Log.i(TAG, "Inserted task");
            }
          }
        } else {
          Log.i(TAG, json.getString("message"));
        }
      }

    } catch (JSONException e) {
      e.printStackTrace();
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    Log.i(TAG, "end sync to local");
  }

  /**
   * @param contentProviderClient
   * @return
   */
  private String getUpdatedDate(ContentProviderClient contentProviderClient) {
    try {
      Cursor cursor = contentProviderClient.query(TaskContentProvider.CONTENT_URI,
          new String[]{TaskDao.Properties.UpdatedDate.columnName}, TaskDao.Properties.TaskId.columnName + " > ? ",
          new String[]{"0"}, TaskDao.Properties.UpdatedDate.columnName + " DESC LIMIT(1)");
      if (cursor.getCount() > 0) {
        cursor.moveToFirst();

        int colUpdateIndex = cursor.getColumnIndex(TaskDao.Properties.UpdatedDate.columnName);
        return cursor.getString(colUpdateIndex);
      }
    } catch (RemoteException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return "";
  }

  /**
   * @param contentProviderClient
   */
  private void syncToServer(Account account, ContentProviderClient contentProviderClient) {
    Log.i(TAG, "begin sync to server");
    // Building Parameters
    String[] keys = new String[]{"username", "taskId", "name", "description", "reminderDate", "createdDate",
        "updatedDate"};
    boolean noteError;

    try {
      // Uri uri = Uri.withAppendedPath(MyToDo.Tasks.CONTENT_DRAP_URI_BASE, "0");
      Cursor cursor = contentProviderClient.query(TaskDraftContentProvider.TASK_DRAFT_CONTENT_URI, TASK_DRAFT_PROJECTION, TaskDraftDao.Properties.UserName.columnName + " = ?", new String[]{account.name},
          null);

      if (cursor.getCount() > 0) {
        Log.i(TAG, "Count cursor : " + cursor.getCount());
        cursor.moveToFirst();

        int colIdIndex = cursor.getColumnIndex(TaskDraftDao.Properties.Id.columnName);
        int colTaskIdIndex = cursor.getColumnIndex(TaskDraftDao.Properties.TaskId.columnName);
        int colNameIndex = cursor.getColumnIndex(TaskDraftDao.Properties.TaskName.columnName);
        int colDescriptionIndex = cursor.getColumnIndex(TaskDraftDao.Properties.TaskDescription.columnName);
        int colReminderIndex = cursor.getColumnIndex(TaskDraftDao.Properties.ReminderDate.columnName);
        int colCreatedDateIndex = cursor.getColumnIndex(TaskDraftDao.Properties.CreatedDate.columnName);
        int colUpdatedDateIndex = cursor.getColumnIndex(TaskDraftDao.Properties.UpdatedDate.columnName);
        int colStatusIndex = cursor.getColumnIndex(TaskDraftDao.Properties.Status.columnName);

        do {
          Long id = cursor.getLong(colIdIndex);
          Long taskId = cursor.getLong(colTaskIdIndex);
          String name = cursor.getString(colNameIndex);
          String description = cursor.getString(colDescriptionIndex);
          String reminderDate = cursor.getString(colReminderIndex);
          String createdDate = cursor.getString(colCreatedDateIndex);
          String updatedDate = cursor.getString(colUpdatedDateIndex);
          Long status = cursor.getLong(colStatusIndex);

          String[] values = new String[]{account.name, taskId.toString(), name, description, reminderDate,
              createdDate, updatedDate};

          switch (status.intValue()) {

            case Constant.TASK_DRAFT_STATUS_INSERT:

              String urlAddTask = URL_HOST + "add-task.php";

              JSONObject addTaskResult = NetworkUtils.postJSONObjFromUrl(urlAddTask, keys, values);
              // check your log for json response
              Log.d("task add result", addTaskResult.toString());

              // json success tag
              noteError = addTaskResult.getBoolean(TAG_ERROR);

              if (!noteError) {
                JSONArray arrTask = addTaskResult.getJSONArray("task");
                for (int i = 0; i < arrTask.length(); i++) {

                  ContentValues contentValues = new ContentValues();
                  JSONObject jsonObject = arrTask.getJSONObject(i);

                  Log.i(TAG, "Start Update task : " + id);
                  contentValues.put(TaskDao.Properties.Id.columnName, jsonObject.getInt(TaskDao.Properties.Id.columnName));
                  // TODO contentValues.put(MyToDo.Tasks.COLUMN_NAME_IS_DRAFT, 1);
                  Uri uriUpdateTask = Uri.withAppendedPath(TaskContentProvider.CONTENT_ID_URI, String.valueOf(id));
                  contentProviderClient.update(uriUpdateTask, contentValues, null, null);

                  Log.i(TAG, "Start delete task draft : " + id);
                  Uri uriDeleteTaskDraft = Uri
                      .withAppendedPath(TaskDraftContentProvider.TASK_DRAFT_CONTENT_ID_URI, String.valueOf(id));
                  contentProviderClient.delete(uriDeleteTaskDraft, null, null);
                  Log.i(TAG, "End delete task draft");

                  Log.i(TAG, "End Update task");
                }

              } else {
                Log.i(TAG, addTaskResult.getString("message"));
              }
              break;

            case Constant.TASK_DRAFT_STATUS_UPDATE:
              String urlUpdateTask = URL_HOST + "update-task.php";

              JSONObject updateTaskResult = NetworkUtils.postJSONObjFromUrl(urlUpdateTask, keys, values);
              // check your log for json response
              Log.d("task update result", updateTaskResult.toString());

              // json success tag
              noteError = updateTaskResult.getBoolean(TAG_ERROR);

              if (!noteError) {
                JSONArray arrTask = updateTaskResult.getJSONArray("task");
                for (int i = 0; i < arrTask.length(); i++) {
                  Log.i(TAG, "Start delete task draft : " + id);
                  Uri uriDeleteTaskDraft = Uri
                      .withAppendedPath(TaskDraftContentProvider.TASK_DRAFT_CONTENT_ID_URI, String.valueOf(id));
                  contentProviderClient.delete(uriDeleteTaskDraft, null, null);
                  Log.i(TAG, "End delete task draft");
                }

              } else {
                Log.i(TAG, updateTaskResult.getString("message"));
              }
              break;

            case Constant.TASK_DRAFT_STATUS_DELETE:
              String urlDeleteTask = URL_HOST + "delete-task.php";

              JSONObject deleteTaskResult = NetworkUtils.postJSONObjFromUrl(urlDeleteTask, keys, values);
              // check your log for json response
              Log.d("task delete result", deleteTaskResult.toString());

              // json success tag
              noteError = deleteTaskResult.getBoolean(TAG_ERROR);

              if (!noteError) {
                Log.i(TAG, "Start delete task draft");
                Uri uriDeleteTaskDraft = Uri.withAppendedPath(TaskDraftContentProvider.TASK_DRAFT_CONTENT_ID_URI, String.valueOf(id));
                contentProviderClient.delete(uriDeleteTaskDraft, null, null);
                Log.i(TAG, "End delete task draft");
              } else {
                Log.i(TAG, deleteTaskResult.getString("message"));
              }
              break;

            default:
              break;
          }

        } while (cursor.moveToNext());

      }
    } catch (RemoteException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    Log.i(TAG, "end sync to server");
  }

}
