package com.liuguoquan.study.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Description:
 *
 * Created by liuguoquan on 2017/11/9 11:49.
 */

@Entity public class User {

  @PrimaryKey(autoGenerate = true) public int id;

  @ColumnInfo public String userId;

  @ColumnInfo public String name;

  @ColumnInfo public int age;

  @Embedded public Address address;

  @Ignore public String desc;

  @Override public String toString() {
    return "User{"
        + "id="
        + id
        + ", userId='"
        + userId
        + '\''
        + ", name='"
        + name
        + '\''
        + ", age="
        + age
        + '}';
  }
}
