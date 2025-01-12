package org.pexdev.groupsystem.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseManager {

  void connect() throws SQLException;

  void disconnect();

  Connection getConnection();
}
