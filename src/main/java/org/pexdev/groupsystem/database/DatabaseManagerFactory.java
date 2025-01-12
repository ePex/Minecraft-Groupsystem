package org.pexdev.groupsystem.database;

import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.pexdev.groupsystem.Config;

@Slf4j
public class DatabaseManagerFactory {

  private DatabaseManagerFactory() {

    log.error("DatabaseManagerFactory class should be used in a static manner!");
  }

  public static DatabaseManager init(
          final Config.DatabaseSettings databaseSettings, final DatabaseProvider databaseProvider)
      throws SQLException {

    log.info("Using {} database provider", databaseProvider);

    switch (databaseProvider) {
      case POSTGRESQL -> {
        return DatabaseManagerPostgreSQL.init(databaseSettings);
      }
      case SQLITE -> {
        return DatabaseManagerSQLite.init("jdbc:sqlite:plugins/GroupSystem/groupsystem.db");
      }
      default -> throw new SQLException("Unsupported database provider!");
    }
  }
}
