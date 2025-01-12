package org.pexdev.groupsystem.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.pexdev.groupsystem.Config.ChatSettings;
import org.pexdev.groupsystem.database.dao.GroupDao;
import org.pexdev.groupsystem.database.dao.PlayerDao;

class JoinListenerTest {

  private final ChatSettings chatSettings = ChatSettings.builder()
          .chatFormat("%prefix% %player%: %message%")
          .joinMessage("Welcome %prefix% %player%!")
          .defaultPrefix("default-prefix")
          .build();

  private final Player player = mock(Player.class);
  private final UUID playerUuid = mock(UUID.class);
  private final String playername = "Player";

  private final GroupDao groupDao = mock(GroupDao.class);
  private final int defaultGroupId = 1;
  private final PlayerDao playerDao = mock(PlayerDao.class);
  private final String defaultPrefix = "default-prefix";

  private final PlayerJoinEvent event = mock(PlayerJoinEvent.class);

  private final JoinListener joinListener = new JoinListener(chatSettings, groupDao, playerDao);

  private final ArgumentCaptor<Component> playerListNameCaptor = ArgumentCaptor.forClass(Component.class);
  private final ArgumentCaptor<Component> joinMessageCaptor = ArgumentCaptor.forClass(Component.class);

  @BeforeEach
  void setUp() {

    when(player.getUniqueId()).thenReturn(playerUuid);
    when(player.getName()).thenReturn(playername);

    when(groupDao.getDefaultGroupId()).thenReturn(defaultGroupId);
    when(playerDao.getPrefix(player)).thenReturn(Component.text(defaultPrefix));

    when(event.getPlayer()).thenReturn(player);
  }

  @Test
  void testOnPlayerJoin_PlayerAssignmentExpired() {

    when(playerDao.isExpired(playerUuid)).thenReturn(true);

    joinListener.onPlayerJoin(event);

    verify(playerDao).assignPlayerToGroup(playerUuid, defaultGroupId, 0);

    verifyPlayerListNameAndJoinMessage(defaultPrefix);
  }

  @Test
  void testOnPlayerJoin_PlayerAssignmentNotExpired() {

    when(playerDao.isExpired(playerUuid)).thenReturn(false);
    final var playerGroupPrefix = "active-prefix";
    when(playerDao.getPrefix(player)).thenReturn(Component.text(playerGroupPrefix));

    joinListener.onPlayerJoin(event);

    verify(playerDao, never()).assignPlayerToGroup(playerUuid, defaultGroupId, 0);

    verifyPlayerListNameAndJoinMessage(playerGroupPrefix);
  }

  private void verifyPlayerListNameAndJoinMessage(final String defaultPrefix) {

    verify(player).playerListName(playerListNameCaptor.capture());
    final var expectedPlayerListName =
            Component.text(String.format("[%s] ", defaultPrefix), NamedTextColor.GOLD)
                    .append(Component.text(playername, NamedTextColor.WHITE));
    assertEquals(expectedPlayerListName, playerListNameCaptor.getValue());

    verify(event).joinMessage(joinMessageCaptor.capture());
    final var expectedJoinMessage = Component.text(String.format("Welcome %s %s!", defaultPrefix, playername));
    assertEquals(expectedJoinMessage, joinMessageCaptor.getValue());
  }
}
