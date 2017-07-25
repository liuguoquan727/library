package com.mdroid.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ReflectUtils {

  private ReflectUtils() {

  }

  /**
   * @see #getFieldValue(Object, Class, String)
   */
  public static <T> T getFieldValue(Object obj, String propertyName)
      throws NoSuchFieldException, IllegalAccessException {
    return getFieldValue(obj, obj.getClass(), propertyName);
  }

  /**
   * 暴力反射获取字段值
   *
   * @param propertyName 属性名
   * @param target 目标类型
   * @param obj 实例对象
   * @return 属性值
   */
  public static <T> T getFieldValue(Object obj, Class target, String propertyName)
      throws NoSuchFieldException, IllegalAccessException {
    Class temp = obj.getClass();
    while (temp != null && !temp.equals(target)) {
      temp = temp.getSuperclass();
    }

    if (temp == null) {
      return null;
    }
    Field field = temp.getDeclaredField(propertyName);
    field.setAccessible(true);
    return (T) field.get(obj);
  }

  /**
   * 暴力反射设置字段值
   *
   * @param propertyName 字段名
   * @param obj 实例对象
   * @param value 新的字段值
   */
  public static void setFieldValue(Object obj, String propertyName, Object value)
      throws NoSuchFieldException, IllegalAccessException {
    Field field = obj.getClass().getDeclaredField(propertyName);
    field.setAccessible(true);
    field.set(obj, value);
  }

  /**
   * 通过构造函数实例化对象
   *
   * @param className 类的全路径名称
   * @param parameterTypes 参数类型
   * @param initargs 参数值
   * @return 返回对象实例
   */
  public static <T> T constructorNewInstance(String className, Class[] parameterTypes,
      Object[] initargs)
      throws ClassNotFoundException, IllegalAccessException, InvocationTargetException,
      InstantiationException, NoSuchMethodException {
    Constructor<?> constructor = Class.forName(className).getDeclaredConstructor(parameterTypes);
    //暴力反射
    constructor.setAccessible(true);
    return (T) constructor.newInstance(initargs);
  }

  /**
   * @see #methodInvoke(Object, Class, String, Class[], Object[])
   */
  public static <T> T methodInvoke(Object obj, String name, Class[] parameterTypes, Object[] args)
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    return methodInvoke(obj, obj.getClass(), name, parameterTypes, args);
  }

  /**
   * 暴力反射执行方法
   *
   * @param obj 实例对象
   * @param target 目标类型
   * @param name 方法名
   * @param parameterTypes 参数类型
   * @param args 参数
   * @param <T> 返回类型
   * @return 执行方法返回的值
   */
  public static <T> T methodInvoke(Object obj, Class target, String name, Class[] parameterTypes,
      Object[] args)
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Class temp = obj.getClass();
    while (temp != null && !temp.equals(target)) {
      temp = temp.getSuperclass();
    }

    if (temp == null) {
      throw new IllegalArgumentException("The target is not a super class of obj");
    }

    Method method = temp.getDeclaredMethod(name, parameterTypes);
    method.setAccessible(true);
    return (T) method.invoke(obj, args);
  }
}