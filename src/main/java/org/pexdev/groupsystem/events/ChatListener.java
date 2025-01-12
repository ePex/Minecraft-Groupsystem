package org.pexdev.groupsystem.events;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.pexdev.groupsystem.Config.ChatSettings;
import org.pexdev.groupsystem.database.dao.PlayerDao;

@Slf4j
public class ChatListener implements Listener {

  private final ChatSettings chatSettings;
  private final PlayerDao playerDao;

  public ChatListener(
      final @NonNull ChatSettings chatSettings, final @NonNull PlayerDao playerDao) {

    this.chatSettings = chatSettings;
    this.playerDao = playerDao;
  }

  @EventHandler
  public void onPlayerChat(final AsyncPlayerChatEvent event) {

    final var player = event.getPlayer();

    event.setFormat(
        chatSettings
            .chatFormat()
            .replace("%prefix%", playerDao.getPrefix(player).content())
            .replace("%player%", player.getName())
            .replace("%message%", event.getMessage()));
  }
}
