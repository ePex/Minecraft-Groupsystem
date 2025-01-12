package org.pexdev.groupsystem.events;

import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.pexdev.groupsystem.database.dao.PlayerDao;

@Slf4j
public class SignListener implements Listener {

  private final PlayerDao playerDao;

  public SignListener(final PlayerDao playerDao) {

    this.playerDao = playerDao;
  }

  @EventHandler
  public void onSignChange(final SignChangeEvent event) {

    // Ensure the block is actually a Sign (not strictly necessary in modern APIs)
    if (!(event.getBlock().getState() instanceof org.bukkit.block.Sign)) {
      return;
    }

    final var player = event.getPlayer();
    final var lines = event.lines();

    if (!lines.isEmpty() && ((TextComponent) lines.getFirst()).content().equalsIgnoreCase("[RankSign]")) {
      final var playerName = player.getName();

      final var prefix = playerDao.getPrefix(player).content();

      event.line(0, Component.text(playerName, NamedTextColor.GREEN));
      event.line(1, Component.text(String.format("Rank: %s", prefix), NamedTextColor.YELLOW));

      player.sendMessage(
          Component.text(
              String.format("Created a rank sign for %s!", playerName), NamedTextColor.GREEN));
    }
  }
}
