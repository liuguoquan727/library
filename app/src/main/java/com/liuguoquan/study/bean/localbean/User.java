package com.liuguoquan.study.bean.localbean;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import com.liuguoquan.study.BR;

/**
 * Description:
 *
 * Created by liuguoquan on 2017/11/21 10:03.
 */

public class User extends BaseObservable {
  public String name;
  public String sex;
  public String age;
  public String address;

  @Bindable public String getName() {
    return name;
  }

  @Bindable public String getSex() {
    return sex;
  }

  @Bindable public String getAge() {
    return age;
  }

  @Bindable public String getAddress() {
    return address;
  }

  public void setName(String name) {
    this.name = name;
    notifyPropertyChanged(BR.name);
  }

  public void setSex(String sex) {
    this.sex = sex;
    notifyPropertyChanged(BR.sex);
  }

  public void setAge(String age) {
    this.age = age;
    notifyPropertyChanged(BR.age);
  }

  public void setAddress(String address) {
    this.address = address;
    notifyPropertyChanged(BR.address);
  }
}
