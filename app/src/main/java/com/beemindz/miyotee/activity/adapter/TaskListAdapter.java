package com.beemindz.miyotee.activity.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.beemindz.miyotee.R;
import com.beemindz.miyotee.dao.Task;

public class TaskListAdapter extends ArrayAdapter<Task> {

  //private static final String TAG = "TaskListAdapter";

  private List<Task> tasks;
  private Context context;
  private ArrayList<Integer> colors;

  public TaskListAdapter(Context context, List<Task> tasks) {
    super(context, R.layout.task_list_item, tasks);
    this.context = context;
    this.tasks = tasks;
    addBackgroundColor();
  }

  /**
   * Backgroup color item list.
   */
  private void addBackgroundColor() {
    colors = new ArrayList<Integer>();
    colors.add(R.color.GREEN_07ac4d);
    colors.add(R.color.GREEN_56b847);
    colors.add(R.color.GREEN_82c341);
    colors.add(R.color.GREEN_a6ce39);
    colors.add(R.color.GREEN_c5d92c);
    colors.add(R.color.RED_feb616);
    colors.add(R.color.RED_f9961e);
    colors.add(R.color.RED_f37521);
    colors.add(R.color.RED_f05222);
    colors.add(R.color.RED_ed1b24);
    colors.add(R.color.RED_ec0a70);
    colors.add(R.color.RED_ed008c);
    colors.add(R.color.RED_b91c8d);
    colors.add(R.color.RED_912690);
    colors.add(R.color.BLUE_732b91);
    colors.add(R.color.BLUE_5d2d91);
    colors.add(R.color.BLUE_472f91);
    colors.add(R.color.BLUE_2e3192);
    colors.add(R.color.BLUE_22409a);
    colors.add(R.color.BLUE_014fa2);
    colors.add(R.color.BLUE_015eac);
    colors.add(R.color.BLUE_0072bb);
    colors.add(R.color.BLUE_0094da);
    colors.add(R.color.BLUE_00adef);
    colors.add(R.color.BLUE_00abbf);
    colors.add(R.color.GREEN_01a89e);
    colors.add(R.color.GREEN_00a86d);
    colors.add(R.color.GREEN_00a65c);
  }

  /** View lookup cache */
  private static class ViewHolder {
    TextView tvTaskName;
  }

  @SuppressLint("DefaultLocale")
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    // color background.
    int sizeColor = colors.size();
    int colorPos = position % sizeColor;

    // get data item task for this position.
    Task task = tasks.get(position);
    if (task != null) {
      // Check if an existing view is being reused, otherwise inflate the

      // View lookup cache stored in tag
      ViewHolder viewHolder;
      if (convertView == null) {
        viewHolder = new ViewHolder();
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.task_list_item, parent, false);
        viewHolder.tvTaskName = (TextView) convertView.findViewById(R.id.tvTaskName);
        convertView.setTag(viewHolder);
      } else {
        viewHolder = (ViewHolder) convertView.getTag();
      }
      
      int width = parent.getWidth();
      
      Log.d("===width list view====", "" + width + "; length:"+task.getTaskName().trim().length());
      
      viewHolder.tvTaskName.setIncludeFontPadding(false);
      viewHolder.tvTaskName.setText(cutStr(width, task.getTaskName().trim().toUpperCase()));

      // set color background item.
      convertView.setBackgroundResource(colors.get(colorPos));
      float alpha = 1;
      if (task.getIsComplete()) {
        alpha= 0.45f;
        viewHolder.tvTaskName.setPaintFlags(viewHolder.tvTaskName.getPaintFlags() | (Paint.STRIKE_THRU_TEXT_FLAG));
      } else if ((viewHolder.tvTaskName.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0) {
        viewHolder.tvTaskName.setPaintFlags(viewHolder.tvTaskName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
      }
      
      AlphaAnimation alphaUp = new AlphaAnimation(alpha, alpha);
      alphaUp.setFillAfter(true);
      viewHolder.tvTaskName.startAnimation(alphaUp);
    }

    return convertView;
  }
  
  private String cutStr(int width, String name) {
    if (width == 320 && name.length() > 19) {
      return String.format("%s ...", name.substring(0, 19));
    }
    
    if (width == 480 && name.length() > 29) {
      return String.format("%s ...", name.substring(0, 29));
    }
    
    if ((width == 720 || width == 768) && name.length() > 39) {
      return String.format("%s ...", name.substring(0, 39));
    }
    
    if (width >= 800 && name.length() > 49) {
      return String.format("%s ...", name.substring(0, 49));
    }
    
    return name;
  }
}
