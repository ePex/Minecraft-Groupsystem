package org.pexdev.groupsystem.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pexdev.groupsystem.Config.DatabaseSettings;

@Slf4j
@Getter
public class DatabaseManagerPostgreSQL implements DatabaseManager {

  @Setter private Connection connection;
  private final String url;
  private final String username;
  private final String password;

  public static DatabaseManagerPostgreSQL init(final DatabaseSettings databaseSettings)
      throws SQLException {

    final var dbm = new DatabaseManagerPostgreSQL(databaseSettings);
    dbm.connect();

    return dbm;
  }

  public DatabaseManagerPostgreSQL(final DatabaseSettings databaseSettings) {

    this.url = databaseSettings.url();
    this.username = databaseSettings.user();
    this.password = databaseSettings.password();
  }

  public void connect() throws SQLException {

    // need to load the class, otherwise a "no driver" exception is thrown
    try {
      Class.forName("org.postgresql.Driver");
    } catch (final ClassNotFoundException e) {
      log.error("Failed to load PostgreSQL driver!", e);
    }

    connection = DriverManager.getConnection(url, username, password);
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
          id SERIAL PRIMARY KEY,
          name VARCHAR(255) NOT NULL UNIQUE,
          prefix VARCHAR(255) NOT NULL
        );
      """;

    final var playersTable =
        """
        CREATE TABLE IF NOT EXISTS players (
          id SERIAL PRIMARY KEY,
          uuid VARCHAR(36) NOT NULL UNIQUE,
          group_id INT,
          expiration_time BIGINT,
          FOREIGN KEY (group_id) REFERENCES groups (id)
        );
      """;

    try (final var statement1 = connection.prepareStatement(groupsTable);
        final var statement2 = connection.prepareStatement(playersTable)) {
      statement1.execute();
      statement2.execute();
    }
  }
}
