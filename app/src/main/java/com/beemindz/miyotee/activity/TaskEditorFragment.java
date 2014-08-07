package com.beemindz.miyotee.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.beemindz.miyotee.R;
import com.beemindz.miyotee.util.CommonUtils;
import com.beemindz.miyotee.util.Constant;
import com.beemindz.miyotee.dao.Task;
import com.beemindz.miyotee.dao.TaskRepository;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.Calendar;

public class TaskEditorFragment extends Fragment implements View.OnClickListener{
  // For logging and debugging
  private static final String TAG = "TasksEditorActivity";
  final static String ARG_TASK_ID = "TaskId";

  private EditText etName, etDescription;
  private TextView etDate, etTime;
  private RelativeLayout layoutSelectTime, layoutSelectDate;
  private Button btnDelete;
  private ImageButton btnSelectDate, btnSelectTime;
  private CheckBox cbIsComplete;
  private ToggleButton btnReminder;
  /** The view to show the ad. */
  private AdView adView;

  private static Calendar mCalendar;
  private long mTaskId;
  private boolean isReminder;
  private Task task;

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param taskId Id of task
   * @return A new instance of fragment TaskEditorFragment.
   */
  public static TaskEditorFragment newInstance(long taskId) {
    TaskEditorFragment fragment = new TaskEditorFragment();
    Bundle args = new Bundle();
    args.putLong(ARG_TASK_ID, taskId);
    fragment.setArguments(args);
    return fragment;
  }

  public TaskEditorFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getArguments() != null) {
      mTaskId = getArguments().getLong(ARG_TASK_ID);
    }
    Log.d(TAG, "TaskId" + mTaskId);
    mCalendar = Calendar.getInstance();
    task = new Task();
    customActionBar();

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {


    View view = inflater.inflate(R.layout.fragment_task_editor, container, false);
    // Gets a handle to the EditText in the the layout.
    etName = (EditText) view.findViewById(R.id.etName);
    etDescription = (EditText) view.findViewById(R.id.etDescription);
    etDate = (TextView) view.findViewById(R.id.etDate);
    etTime = (TextView) view.findViewById(R.id.etTime);
    layoutSelectDate = (RelativeLayout) view.findViewById(R.id.layoutSelectDate);
    layoutSelectTime = (RelativeLayout) view.findViewById(R.id.layoutSelectTime);
    btnDelete = (Button) view.findViewById(R.id.btnDeleteTask);
    cbIsComplete = (CheckBox) view.findViewById(R.id.cbComplete);
    btnSelectDate = (ImageButton) view.findViewById(R.id.btnSelectDate);
    btnSelectTime = (ImageButton) view.findViewById(R.id.btnSelectTime);
    btnReminder = (ToggleButton) view.findViewById(R.id.tgbSetReminder);

    btnSelectDate.setOnClickListener(this);
    btnSelectTime.setOnClickListener(this);
    btnReminder.setOnClickListener(this);
    btnDelete.setOnClickListener(this);

    updateTaskView(mTaskId);
    initAdModule(view);

    return view;
  }

  @Override
  public void onStart() {
    super.onStart();
    EasyTracker.getInstance(getActivity()).activityStart(getActivity());
  }

  @Override
  public void onResume() {
    super.onResume();
    if (adView != null) {
      adView.resume();
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    EasyTracker.getInstance(getActivity()).activityStop(getActivity());
  }

  @Override
  public void onPause() {
    // Destroy the AdView.
    if (adView != null) {
      adView.pause();
    }
    super.onPause();
    Log.d(TAG, "On Pause");
  }

  @Override
  public void onDestroy() {
    // Destroy the AdView.
    if (adView != null) {
      adView.destroy();
    }
    super.onDestroy();
  }

  public void updateTaskView(long taskId) {
    task = TaskRepository.getTaskForId(getActivity(), taskId);
    if (task != null) {
      etName.setTextKeepState(task.getTaskName().trim());
      etName.setSelection(task.getTaskName().trim().length());
      getActivity().getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
      if(!TextUtils.isEmpty(task.getTaskDescription())) {
        etDescription.setTextKeepState(task.getTaskDescription().trim());
      }
      isReminder = !task.getIsReminder();
      btnReminder.setChecked(!task.getIsReminder());
      isShowReminder();

      mCalendar.setTime(task.getReminderDate());
      etTime.setText(CommonUtils.getStringDate(task.getReminderDate(), Constant.TIME_FORMAT));
      etDate.setText(CommonUtils.getStringDate(task.getReminderDate(), CommonUtils.getDateFormatSystem(getActivity().getApplicationContext())));

      cbIsComplete.setChecked(task.getIsComplete());
    }
  }

  private ActionBar getActionBar() {
    return ((ActionBarActivity) getActivity()).getSupportActionBar();
  }

  /**
   * Custom action bar.
   */
  public void customActionBar() {
    ActionBar actionBar = getActionBar();
    actionBar.setDisplayShowCustomEnabled(true);

    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    actionBar.setCustomView(R.layout.editor_task_action_bar);

    Button btnCancel = (Button) getActivity().findViewById(R.id.btnCancel);
    Button btnOk = (Button) getActivity().findViewById(R.id.btnOK);

    btnCancel.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View arg0) {
        getActivity().getSupportFragmentManager().popBackStack();
      }
    });

    btnOk.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        updateTask(task);
        getActivity().getSupportFragmentManager().popBackStack();
      }
    });
  }

 /*--- EVENT SET REMINDER ON/OFF ---*/
  private void isShowReminder() {
    if (isReminder) {
      // show set date/time.
      layoutSelectTime.setVisibility(View.GONE);
      layoutSelectDate.setVisibility(View.GONE);
    } else {
      // hide set date/time.
      layoutSelectTime.setVisibility(View.VISIBLE);
      layoutSelectDate.setVisibility(View.VISIBLE);

      etDate.setTextKeepState(CommonUtils.getStringDate(mCalendar, CommonUtils.getDateFormatSystem(getActivity())));
      etTime.setTextKeepState(CommonUtils.getStringDate(mCalendar, Constant.TIME_FORMAT));
    }
  }

  @Override
  public void onClick(View view) {
    Log.d(TAG, "onclick" + view.getId());
    switch (view.getId()) {
      case R.id.tgbSetReminder :
        isReminder = ((ToggleButton) view).isChecked();
        isShowReminder();
        break;
      case R.id.btnSelectDate:
        Log.d(TAG, "btnSelectDate clicked");
        DialogFragment dateFragment = new SelectDateFragment();
        dateFragment.show(getActivity().getSupportFragmentManager(), "DatePicker");
        break;
      case R.id.btnSelectTime:
        DialogFragment timeFragment = new SelectTimeFragment();
        timeFragment.show(getActivity().getSupportFragmentManager(), "TimePicker");
        break;
      case R.id.btnDeleteTask:
        confirmDelete().show();
        break;
    }
  }

  private void initAdModule(View view) {
    // Create an ad.
    adView = new AdView(getActivity());

    adView.setAdSize(AdSize.BANNER);
    adView.setAdUnitId(Constant.ADMOD_UNIT_ID);

    // Add the AdView to the view hierarchy. The view will have no size
    // until the ad is loaded.
    LinearLayout layout = (LinearLayout) view.findViewById(R.id.linearEditor);
    layout.addView(adView);

    // Create an ad request. Check logcat output for the hashed device ID to
    // get test ads on a physical device.
    final AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        .addTestDevice("INSERT_YOUR_HASHED_DEVICE_ID_HERE").build();

    // Start loading the ad in the background.
    adView.loadAd(adRequest);
  }

  private void updateTask(Task task) {
      // validate input.
      int valid = validateInputName(etName.getText().toString());
      // case: input not correct.
      if (valid != 0) {
        return;
      } else {
        if (task != null) {
          task.setTaskName(etName.getText().toString());
          task.setTaskDescription(etDescription.getText().toString());
          task.setIsReminder(btnReminder.isChecked());
          task.setIsComplete(cbIsComplete.isChecked());
          task.setReminderDate(mCalendar.getTime());
          task.setUpdatedDate(Calendar.getInstance().getTime());

          TaskRepository.insertOrUpdate(getActivity(), task);
        }
      }

//      if (!isReminder && !cbIsComplete.isChecked() && reminderCal.getTimeInMillis() > System.currentTimeMillis()) {
//        new ReminderManager().setReminder(this, mCursor.getLong(mCursor.getColumnIndex(MyToDo.Tasks._ID)), mCalendar,
//            name, description);
//      }

  }

  /**
   * Validate input task. + name: required. + description: required. + date &
   * time: required.
   */
  private int validateInputName(String name) {
    // case: name null.
    if (TextUtils.isEmpty(name)) {
      return R.string.toast_err_task_name_required;
    }

    return 0;
  }

  /*--Confirm dialog delete--*/
  private AlertDialog confirmDelete() {
    return CommonUtils.confirmDelete(getActivity(), new android.content.DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {
        // your deleting code
        TaskRepository.deleteTaskWithId(getActivity().getApplicationContext(),mTaskId);
        dialog.dismiss();
        getActivity().getSupportFragmentManager().popBackStack();
      }
    }, new android.content.DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
  }

  /**
   * Open date picker
   */
  public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      int yy = mCalendar.get(Calendar.YEAR);
      int mm = mCalendar.get(Calendar.MONTH);
      int dd = mCalendar.get(Calendar.DAY_OF_MONTH);
      return new DatePickerDialog(getActivity(), this, yy, mm, dd);
    }

    @Override
    public void onDateSet(DatePicker view, int yy, int mm, int dd) {
      mCalendar.set(yy, mm, dd);
      Log.i(TAG, "Select Date : " + mCalendar.getTime().toString());

      TextView tvDate = (TextView)getActivity().findViewById(R.id.etDate);
      tvDate.setText(CommonUtils.getStringDate(mCalendar, CommonUtils.getDateFormatSystem(getActivity().getApplicationContext())));
    }

  }

  /**
   * Open time picker
   */
  public static class SelectTimeFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
      int minute = mCalendar.get(Calendar.MINUTE);

      return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
      // TODO Auto-generated method stub

      mCalendar.set(Calendar.HOUR_OF_DAY, hour);
      mCalendar.set(Calendar.MINUTE, minute);

      Log.i(TAG, "Select Time : " + mCalendar.getTime().toString());
      TextView tvTime = (TextView)getActivity().findViewById(R.id.etTime);
      tvTime.setText(CommonUtils.getStringDate(mCalendar, Constant.TIME_FORMAT));
    }
  }

}

