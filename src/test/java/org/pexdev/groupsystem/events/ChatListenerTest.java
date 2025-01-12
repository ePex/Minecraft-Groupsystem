package org.pexdev.groupsystem.events;

import static org.mockito.Mockito.*;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pexdev.groupsystem.Config.ChatSettings;
import org.pexdev.groupsystem.database.dao.PlayerDao;

class ChatListenerTest {

  private final ChatSettings chatSettings = ChatSettings.builder()
          .chatFormat("%prefix% %player%: %message%")
          .joinMessage("Welcome %prefix% %player%!")
          .defaultPrefix("default-prefix")
          .build();
  private final PlayerDao mockPlayerDao = mock(PlayerDao.class);

  private final Player mockPlayer = mock(Player.class);
  private final AsyncPlayerChatEvent mockEvent = mock(AsyncPlayerChatEvent.class);

  private final ChatListener chatListener = new ChatListener(chatSettings, mockPlayerDao);

  @BeforeEach
  void setUp() {

    when(mockPlayer.getName()).thenReturn("PlayerName");
    when(mockPlayerDao.getPrefix(mockPlayer)).thenReturn(Component.text("VIP"));

    when(mockEvent.getPlayer()).thenReturn(mockPlayer);
  }

  @Test
  void testOnPlayerChat_ReplacesPlaceholders() {

    when(mockEvent.getMessage()).thenReturn("Hello World");

    chatListener.onPlayerChat(mockEvent);

    verify(mockEvent).setFormat("VIP PlayerName: Hello World");
  }

  @Test
  void testOnPlayerChat_WithEmptyMessage() {

    when(mockEvent.getMessage()).thenReturn("");

    chatListener.onPlayerChat(mockEvent);

    verify(mockEvent).setFormat("VIP PlayerName: ");
  }
}
