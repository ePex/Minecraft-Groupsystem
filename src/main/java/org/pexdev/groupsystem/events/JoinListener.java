package org.pexdev.groupsystem.events;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.pexdev.groupsystem.Config;
import org.pexdev.groupsystem.database.dao.GroupDao;
import org.pexdev.groupsystem.database.dao.PlayerDao;

@Slf4j
public class JoinListener implements Listener {

  private final Config.ChatSettings chatSettings;
  private final GroupDao groupDao;
  private final PlayerDao playerDao;

  public JoinListener(
      final @NonNull Config.ChatSettings chatSettings,
      final @NonNull GroupDao groupDao,
      final @NonNull PlayerDao playerDao) {

    this.chatSettings = chatSettings;
    this.groupDao = groupDao;
    this.playerDao = playerDao;
  }

  @EventHandler
  public void onPlayerJoin(final PlayerJoinEvent event) {

    final var player = event.getPlayer();
    final var uuid = player.getUniqueId();

    // If expired, reassign to default group
    if (playerDao.isExpired(uuid)) {
      // Possibly store ID of 'default group'
      log.info("player assignment has expired, assigning player to default group");
      playerDao.assignPlayerToGroup(uuid, groupDao.getDefaultGroupId(), 0);
    }

    final var dbPrefix = playerDao.getPrefix(player);

    player.playerListName(
        Component.text("[" + dbPrefix.content() + "] ", NamedTextColor.GOLD)
            .append(Component.text(player.getName(), NamedTextColor.WHITE)));

    event.joinMessage(Component.text(chatSettings
            .joinMessage()
            .replace("%prefix%", dbPrefix.content())
            .replace("%player%", event.getPlayer().getName()))
    );
  }
}
