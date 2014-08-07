package com.beemindz.miyotee;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * Created by Sony on 7/31/2014.
 */
public class MiyoteeGenerator {
  public static void main(String args[]) throws Exception {

    Schema schema = new Schema(1, "com.beemindz.miyotee.dao");

    Entity task = schema.addEntity("Task");
    task.addIdProperty();
    task.addIntProperty("taskId");
    task.addStringProperty("userName");
    task.addStringProperty("taskName");
    task.addStringProperty("taskDescription");
    task.addDateProperty("reminderDate");
    task.addBooleanProperty("isReminder");
    task.addBooleanProperty("isComplete");
    task.addDateProperty("createdDate");
    task.addDateProperty("updatedDate");
    task.addContentProvider();

    Entity taskDraft = schema.addEntity("TaskDraft");
    taskDraft.addIdProperty();
    taskDraft.addIntProperty("taskId");
    taskDraft.addStringProperty("userName");
    taskDraft.addStringProperty("taskName");
    taskDraft.addStringProperty("taskDescription");
    taskDraft.addDateProperty("reminderDate");
    taskDraft.addBooleanProperty("isReminder");
    taskDraft.addBooleanProperty("isComplete");
    taskDraft.addDateProperty("createdDate");
    taskDraft.addDateProperty("updatedDate");
    taskDraft.addIntProperty("status");
    taskDraft.addContentProvider();


    new DaoGenerator().generateAll(schema, args[0]);
  }
}
