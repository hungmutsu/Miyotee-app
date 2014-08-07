package com.beemindz.miyotee;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.beemindz.miyotee.dao.DaoMaster;
import com.beemindz.miyotee.dao.DaoSession;

import java.io.Console;

/**
 * Created by Sony on 7/31/2014.
 */
public class MiyoteeApplication extends Application {
  public DaoSession daoSession;

  @Override
  public void onCreate() {
    super.onCreate();
    setupDatabase();
  }

  private void setupDatabase() {
    DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "miyotee-db", null);
    SQLiteDatabase db = helper.getWritableDatabase();
    DaoMaster daoMaster = new DaoMaster(db);
    daoSession = daoMaster.newSession();
  }

  public DaoSession getDaoSession() {
    return daoSession;
  }

  public void testGit() {
    System.out.print("");
  }
}
