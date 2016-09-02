package com.monitorjbl.timberflow.reflection;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import static java.util.stream.Collectors.joining;

public class ObjectCreator {
  public static <E> E newInstance(Class<E> cls, Object[] args) {
    Constructor c = null;
    try {
      c = findConstructor(cls, args);
      return (E) c.newInstance(args);
    } catch(Exception e) {
      throw new ConstructorInstantiationException("Could not instantiate " + cls.getCanonicalName(), e);
    }
  }

  public static Constructor findConstructor(Class cls, Object[] objects) {
    Object[] args = objects == null ? new Object[0] : objects;
    Class[] classes = (Class[]) Arrays.stream(args)
        .map(a -> a == null ? null : a.getClass())
        .toArray(size -> new Class[size]);
    return Arrays.stream(cls.getConstructors())
        .filter(c -> argsMatch(classes, args))
        .findFirst()
        .orElseThrow(() -> new ConstructorLookupException("No constructor found for class " + cls.getCanonicalName() + " using args " + toString(classes)));
  }

  private static boolean argsMatch(Class[] classes, Object[] args) {
    if(classes.length != args.length) {
      return false;
    } else {
      boolean matches = true;
      for(int i = 0; i < classes.length; i++) {
        matches &= classes[i].equals(args[i].getClass());
      }
      return matches;
    }
  }

  private static String toString(Class[] classes) {
    return Arrays.stream(classes)
        .map(Class::getCanonicalName)
        .collect(joining(", "));
  }
}
