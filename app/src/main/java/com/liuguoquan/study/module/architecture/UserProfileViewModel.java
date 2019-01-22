package com.liuguoquan.study.module.architecture;

import androidx.lifecycle.ViewModel;

/**
 * Description:
 *
 * Created by liuguoquan on 2018/1/18 10:45.
 */

public class UserProfileViewModel extends ViewModel {
  public String sex;
  public String age;
  public String address;

  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

  public String getAge() {
    return age;
  }

  public void setAge(String age) {
    this.age = age;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }
}
