package org.pexdev.groupsystem.database;

import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pexdev.groupsystem.database.dao.GroupDao;

class GroupDaoTest {

  private DatabaseManager databaseManager;
  private GroupDao groupDao;

  @BeforeEach
  void setUp() throws SQLException {

    databaseManager = DatabaseManagerSQLite.init("jdbc:sqlite::memory:");
    groupDao = new GroupDao(databaseManager, "foo");
  }

  @AfterEach
  void tearDown() {

    databaseManager.disconnect();
  }

  @Test
  void testCreateAndDeleteGroup() {

    final var created = groupDao.createGroup("TestGroup", "&f[Test]");
    Assertions.assertTrue(created, "Group should be created successfully");

    final var groupId = groupDao.getGroupIdByName("TestGroup");
    Assertions.assertTrue(groupId > 0, "Group ID should be > 0");

    final var deleted = groupDao.deleteGroup("TestGroup");
    Assertions.assertTrue(deleted, "Group should be deleted");

    final var afterDeleteId = groupDao.getGroupIdByName("TestGroup");
    Assertions.assertEquals(-1, afterDeleteId, "Group should no longer exist");
  }

  @Test
  void testGetGroupPrefixById() {

    groupDao.createGroup("VIP", "&6[VIP]");
    final var vipId = groupDao.getGroupIdByName("VIP");

    String prefix = groupDao.getGroupPrefixById(vipId);
    Assertions.assertEquals("&6[VIP]", prefix);
  }

  @Test
  void testGetGroupNameById() {

    groupDao.createGroup("Default", "&7[Default]");
    final var defaultId = groupDao.getGroupIdByName("Default");

    String name = groupDao.getGroupNameById(defaultId);
    Assertions.assertEquals("Default", name);
  }
}
