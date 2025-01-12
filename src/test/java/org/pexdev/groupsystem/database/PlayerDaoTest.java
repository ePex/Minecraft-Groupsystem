package org.pexdev.groupsystem.database;

import java.sql.SQLException;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.pexdev.groupsystem.Config;
import org.pexdev.groupsystem.database.dao.GroupDao;
import org.pexdev.groupsystem.database.dao.PlayerDao;

class PlayerDaoTest {

  private DatabaseManager databaseManager;
  private GroupDao groupDao;
  private PlayerDao playerDao;

  @BeforeEach
  void setUp() throws SQLException {

    databaseManager = DatabaseManagerSQLite.init("jdbc:sqlite::memory:");
    groupDao = new GroupDao(databaseManager, "foo");
    playerDao =
        new PlayerDao(
            databaseManager,
            groupDao,
            Config.ChatSettings.builder()
                .chatFormat("&7[&f%s&7] &f%s")
                .joinMessage("&7[&f%s&7] &fjoined the server!")
                .defaultPrefix("&7[&f%s&7]")
                .build());
  }

  @AfterEach
  void tearDown() {

    databaseManager.disconnect();
  }

  @Test
  void testAssignPlayerToGroup() {

    groupDao.createGroup("TestGroup", "&f[Test]");
    final var groupId = groupDao.getGroupIdByName("TestGroup");
    Assertions.assertTrue(groupId > 0);

    final var uuid = UUID.randomUUID();
    boolean assigned = playerDao.assignPlayerToGroup(uuid, groupId, 0); // 0 = permanent
    Assertions.assertTrue(assigned);

    final var actualGroupId = playerDao.getPlayerGroupId(uuid);
    Assertions.assertEquals(groupId, actualGroupId);

    boolean expired = playerDao.isExpired(uuid);
    Assertions.assertFalse(expired, "Should not be expired for permanent assignment");
  }

  @Test
  void testTemporaryGroupAssignment() throws InterruptedException {

    groupDao.createGroup("TestGroup", "&f[Test]");
    final var groupId = groupDao.getGroupIdByName("TestGroup");

    final var uuid = UUID.randomUUID();
    final var expiration = System.currentTimeMillis() + 1000;
    final var assigned = playerDao.assignPlayerToGroup(uuid, groupId, expiration);
    Assertions.assertTrue(assigned);

    Assertions.assertFalse(playerDao.isExpired(uuid));
    Thread.sleep(1100);
    Assertions.assertTrue(playerDao.isExpired(uuid), "Group assignment should now be expired");
  }
}
