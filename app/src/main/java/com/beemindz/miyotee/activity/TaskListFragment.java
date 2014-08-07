package com.beemindz.miyotee.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.beemindz.miyotee.R;
import com.beemindz.miyotee.activity.adapter.TaskListAdapter;
import com.beemindz.miyotee.dao.Task;
import com.beemindz.miyotee.dao.TaskRepository;
import com.google.analytics.tracking.android.EasyTracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TaskListFragment extends ListFragment {

  private final String TAG = "TaskListFragment";
  private EditText etTitle;
  private ImageButton btnAddTask;
  private ListView listView;
  private List<Task> tasks;

  private OnTaskSelectedListener mListener;

  public interface OnTaskSelectedListener {
    public void onTaskSelected(long taskId);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    tasks = new ArrayList<Task>();

  }

  /**
   * Custom action bar.
   */
  @SuppressLint("InflateParams")
  public void customActionBar() {
    ActionBar actionBar = getActionBar();
    actionBar.setDisplayShowCustomEnabled(true);
    actionBar.setDisplayShowTitleEnabled(false);
    actionBar.setDisplayUseLogoEnabled(true);
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setHomeButtonEnabled(true);
    actionBar.setDisplayShowHomeEnabled(true);

    LayoutInflater inflator = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View v = inflator.inflate(R.layout.add_task_list_action_bar, null);

    btnAddTask = (ImageButton) v.findViewById(R.id.btnAddTask);
    etTitle = (EditText) v.findViewById(R.id.etTitle);

    btnAddTask = (ImageButton) v.findViewById(R.id.btnAddTask);
    etTitle = (EditText) v.findViewById(R.id.etTitle);

    btnAddTask.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View arg0) {
        Log.d(TAG, "btnAddTask onclick");
        String title = etTitle.getText().toString().trim();
        if (!TextUtils.isEmpty(title)) {
          Task task = new Task();
          task.setTaskName(title);
          task.setIsReminder(false);
          task.setIsComplete(false);
          task.setReminderDate(Calendar.getInstance().getTime());
          task.setUpdatedDate(Calendar.getInstance().getTime());
          task.setCreatedDate(Calendar.getInstance().getTime());

          TaskRepository.insertOrUpdate(getActivity().getApplicationContext(), task);
          etTitle.setText("");
          updateAdapter();
        }
      }
    });

    actionBar.setCustomView(v);
  }

  private ActionBar getActionBar() {
    return ((ActionBarActivity) getActivity()).getSupportActionBar();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_task_list, container, false);
    listView = (ListView) rootView.findViewById(android.R.id.list);
    customActionBar();
    updateAdapter();
    return rootView;
  }

  @Override
  public void onStart() {
    super.onStart();
    EasyTracker.getInstance(getActivity()).activityStart(getActivity());

  }

  @Override
  public void onStop() {
    super.onStop();
    EasyTracker.getInstance(getActivity()).activityStart(getActivity());

  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    try {
      mListener = (OnTaskSelectedListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString()
          + " must implement OnTaskSelectedListener");
    }
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {

    if(tasks.size() > 0) {
      mListener.onTaskSelected(tasks.get(position).getId());
    }
    getListView().setItemChecked(position, true);
  }

  /**
   * Update list view adapter
   */
  public void updateAdapter() {
    this.tasks = TaskRepository.getAllTasks(getActivity());
    TaskListAdapter adapter = new TaskListAdapter(getActivity(), tasks);
    listView.setAdapter(adapter);

  }
}
