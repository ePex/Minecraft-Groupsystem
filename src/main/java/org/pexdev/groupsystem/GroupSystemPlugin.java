package org.pexdev.groupsystem;

import java.sql.SQLException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.plugin.java.JavaPlugin;
import org.pexdev.groupsystem.Config.ChatSettings;
import org.pexdev.groupsystem.commands.CommandRegistry;
import org.pexdev.groupsystem.commands.GroupCommand;
import org.pexdev.groupsystem.database.DatabaseManager;
import org.pexdev.groupsystem.database.DatabaseManagerFactory;
import org.pexdev.groupsystem.database.DatabaseProvider;
import org.pexdev.groupsystem.database.dao.DaoRegistry;
import org.pexdev.groupsystem.database.dao.GroupDao;
import org.pexdev.groupsystem.database.dao.PlayerDao;
import org.pexdev.groupsystem.events.ChatListener;
import org.pexdev.groupsystem.events.JoinListener;
import org.pexdev.groupsystem.events.SignListener;

@Getter
@Slf4j
public final class GroupSystemPlugin extends JavaPlugin {

  private DatabaseManager databaseManager;
  private Config customConfig;

  @Override
  public void onEnable() {

    log.info("GroupSystem Plugin Enabled!");

    customConfig = Config.init(this);
    log.info("Custom config loaded! {}", customConfig.toString());
    initDatabase();
    registerCommands();
    registerEvents(customConfig.chatSettings());
  }

  @Override
  public void onDisable() {

    if (databaseManager != null) {
      databaseManager.disconnect();
    }
    log.info("GroupSystem Plugin Disabled!");
  }

  private void initDatabase() {

    try {
      databaseManager =
          DatabaseManagerFactory.init(
              customConfig.databaseSettings(),
              DatabaseProvider.valueOf(
                  getConfig().getString("database-provider", "INMEMORY").toUpperCase()));

      DaoRegistry.registerDao(new GroupDao(databaseManager, customConfig.defaultGroupName()));
      DaoRegistry.registerDao(
          new PlayerDao(
              databaseManager, DaoRegistry.getDao(GroupDao.class), customConfig.chatSettings()));
    } catch (SQLException e) {
      log.error("Failed to connect to the database: {}", e.getMessage());
      getServer().getPluginManager().disablePlugin(this);
    }
  }

  private void registerCommands() {

    CommandRegistry.registerCommand(
        getCommand("group"),
        new GroupCommand(DaoRegistry.getDao(GroupDao.class), DaoRegistry.getDao(PlayerDao.class)));
  }

  private void registerEvents(final ChatSettings chatSettings) {

    final var pluginManager = getServer().getPluginManager();
    pluginManager.registerEvents(
        new ChatListener(chatSettings, DaoRegistry.getDao(PlayerDao.class)), this);
    pluginManager.registerEvents(
        new JoinListener(
            customConfig.chatSettings(),
            DaoRegistry.getDao(GroupDao.class),
            DaoRegistry.getDao(PlayerDao.class)),
        this);
    pluginManager.registerEvents(new SignListener(DaoRegistry.getDao(PlayerDao.class)), this);
  }
}
