package org.pexdev.groupsystem.events;

import static org.mockito.Mockito.*;

import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pexdev.groupsystem.database.dao.PlayerDao;

class SignListenerTest {

  private final PlayerDao mockPlayerDao = mock(PlayerDao.class);
  private final Player mockPlayer = mock(Player.class);
  private final Sign mockSign = mock(Sign.class);
  private final Block mockBlock = mock(Block.class);
  private final SignChangeEvent mockEvent = mock(SignChangeEvent.class);

  private final SignListener signListener = new SignListener(mockPlayerDao);

  @BeforeEach
  void setUp() {

    when(mockEvent.getPlayer()).thenReturn(mockPlayer);
    when(mockEvent.getBlock()).thenReturn(mockBlock);
    when(mockBlock.getState()).thenReturn(mockSign);

    when(mockPlayer.getName()).thenReturn("Player");
    when(mockPlayerDao.getPrefix(mockPlayer)).thenReturn(Component.text("VIP"));
    }

  @Test
  void testOnSignChange_NotSignBlock() {

    when(mockEvent.getBlock()).thenReturn(mockBlock);
    when(mockBlock.getState()).thenReturn(mock(BlockState.class)); // Not a Sign instance

    signListener.onSignChange(mockEvent);

    verify(mockEvent, never()).line(anyInt(), any());
  }

  @Test
  void testOnSignChange_NoRankSignHeader() {

    when(mockEvent.lines()).thenReturn(List.of(Component.text("SomeOtherText")));

    signListener.onSignChange(mockEvent);

    verify(mockEvent, never()).line(anyInt(), any());
    verify(mockPlayer, never()).sendMessage(any(Component.class));
  }

  @Test
  void testOnSignChange_WithRankSignHeader() {

    when(mockEvent.lines()).thenReturn(List.of(Component.text("[RankSign]")));

    signListener.onSignChange(mockEvent);

    verify(mockEvent).line(0, Component.text("Player", NamedTextColor.GREEN));
    verify(mockEvent).line(1, Component.text("Rank: VIP", NamedTextColor.YELLOW));
    verify(mockPlayer)
        .sendMessage(Component.text("Created a rank sign for Player!", NamedTextColor.GREEN));
  }
}
