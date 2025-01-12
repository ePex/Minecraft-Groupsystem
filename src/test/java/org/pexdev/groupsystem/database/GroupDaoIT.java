package org.pexdev.groupsystem.database;

import java.sql.SQLException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.pexdev.groupsystem.Config;
import org.pexdev.groupsystem.database.dao.GroupDao;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class GroupDaoIT {

  @Container
  private static final PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:15.1")
          .withDatabaseName("testdb")
          .withUsername("testuser")
          .withPassword("testpass");

  private static DatabaseManagerPostgreSQL databaseManager;
  private static GroupDao groupDao;

  @BeforeAll
  static void setUp() throws SQLException {
    // This will automatically start the container
    postgres.start();

    final var databaseSettings =
        Config.DatabaseSettings.builder()
            .url(postgres.getJdbcUrl())
            .user(postgres.getUsername())
            .password(postgres.getPassword())
            .build();

    databaseManager = DatabaseManagerPostgreSQL.init(databaseSettings);

    groupDao = new GroupDao(databaseManager, "foo");
  }

  @AfterAll
  static void tearDown() {

    databaseManager.disconnect();
    postgres.stop();
  }

  @Test
  void testCreateGroup() {

    boolean created = groupDao.createGroup("VIP", "&6[VIP]");
    Assertions.assertTrue(created, "Group should be created successfully");

    int vipId = groupDao.getGroupIdByName("VIP");
    Assertions.assertTrue(vipId > 0, "Group ID should be > 0 after creation");
  }

  @Test
  void testDeleteGroup() {

    groupDao.createGroup("TestGroup", "&7[Test]");
    int groupId = groupDao.getGroupIdByName("TestGroup");
    Assertions.assertTrue(groupId > 0);

    boolean deleted = groupDao.deleteGroup("TestGroup");
    Assertions.assertTrue(deleted, "Group should be deleted");

    int afterDelete = groupDao.getGroupIdByName("TestGroup");
    Assertions.assertEquals(-1, afterDelete, "Group ID should be -1 if not found");
  }
}
