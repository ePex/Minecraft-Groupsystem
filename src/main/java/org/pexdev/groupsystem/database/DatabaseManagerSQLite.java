package org.pexdev.groupsystem.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class DatabaseManagerSQLite implements DatabaseManager {

  @Setter private Connection connection;
  private final String url;

  public static DatabaseManagerSQLite init(final String url) throws SQLException {

    final var dbm = new DatabaseManagerSQLite(url);
    dbm.connect();

    return dbm;
  }

  public DatabaseManagerSQLite(final String url) {

    this.url = url;
  }

  public void connect() throws SQLException {

    connection = DriverManager.getConnection(url);
    createTables();
    log.info("Database connected successfully!");
  }

  public void disconnect() {

    if (connection != null) {
      try {
        connection.close();
      } catch (SQLException e) {
        log.error("Failed to close database connection!", e);
      }
    }
  }

  void createTables() throws SQLException {

    final var groupsTable =
        """
        CREATE TABLE IF NOT EXISTS groups (
          id INTEGER PRIMARY KEY AUTOINCREMENT,
          name TEXT NOT NULL,
          prefix TEXT NOT NULL
        );
      """;

    final var playersTable =
        """
        CREATE TABLE IF NOT EXISTS players (
          id INTEGER PRIMARY KEY AUTOINCREMENT,
          uuid TEXT NOT NULL,
          group_id INTEGER,
          expiration_time INTEGER,
          FOREIGN KEY (group_id) REFERENCES groups(id)
        );
      """;

    try (final var statement1 = connection.prepareStatement(groupsTable);
        final var statement2 = connection.prepareStatement(playersTable)) {
      statement1.execute();
      statement2.execute();
    }
  }
}
