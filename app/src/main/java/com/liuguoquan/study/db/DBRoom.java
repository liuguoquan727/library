package com.liuguoquan.study.db;

import android.arch.persistence.room.Room;
import android.content.Context;
import com.liuguoquan.study.base.App;
import com.liuguoquan.study.db.dao.UserDao;

/**
 * Description:
 *
 * Created by liuguoquan on 2017/11/9 16:20.
 */

public class DBRoom {

  private static DBRoom sInstance = null;
  private AppDatabase mDatabase;

  private DBRoom(Context context) {
    mDatabase = Room.databaseBuilder(context, AppDatabase.class, AppDatabase.DB_NAME).build();
  }

  public static DBRoom getInstance() {
    synchronized (DBRoom.class) {
      if (sInstance == null) {
        sInstance = new DBRoom(App.getInstance());
      }
    }
    return sInstance;
  }

  public UserDao getUserDao() {
    return mDatabase.userDao();
  }
}
