package com.liuguoquan.study.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.liuguoquan.study.db.dao.UserDao;
import com.liuguoquan.study.db.entity.User;

/**
 * Description:
 *
 * Created by liuguoquan on 2017/11/9 11:57.
 */

@Database(entities = { User.class }, version = 1) public abstract class AppDatabase
    extends RoomDatabase {

  public static final String DB_NAME = "user";

  public abstract UserDao userDao();
}
