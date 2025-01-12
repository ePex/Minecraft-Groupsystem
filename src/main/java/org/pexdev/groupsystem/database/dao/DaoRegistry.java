package org.pexdev.groupsystem.database.dao;

import java.util.HashMap;
import java.util.Map;

public class DaoRegistry {

  public static final Map<Class<?>, Dao> REGISTERED_DAOS = new HashMap();

  public static void registerDao(final Dao dao) {

    REGISTERED_DAOS.put(dao.getClass(), dao);
  }

  public static <T> T getDao(final Class<?> daoClass) {

    return (T) REGISTERED_DAOS.get(daoClass);
  }
}
