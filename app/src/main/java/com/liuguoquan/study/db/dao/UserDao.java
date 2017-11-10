package com.liuguoquan.study.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import com.liuguoquan.study.db.entity.User;
import io.reactivex.Flowable;
import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Description:
 * <p>
 * Created by liuguoquan on 2017/11/9 11:51.
 */

@Dao public interface UserDao {

  @Query("select * from user") Flowable<List<User>> getAll();

  @Query("select * from user where userId = :userId") User getUserById(String userId);

  @Query("select * from user where userId = :userId") Flowable<User> getObservableUserById(
      String userId);

  @Insert(onConflict = REPLACE) long insertUser(User user);

  @Update int updateUser(User user);

  @Delete void delete(User user);
}
