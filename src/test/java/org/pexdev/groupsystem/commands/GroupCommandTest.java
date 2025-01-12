package org.pexdev.groupsystem.commands;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;
import org.pexdev.groupsystem.database.dao.GroupDao;
import org.pexdev.groupsystem.database.dao.PlayerDao;

class GroupCommandTest {

  private final GroupDao mockGroupDao = mock(GroupDao.class);
  private final PlayerDao mockPlayerDao = mock(PlayerDao.class);
  private final CommandSender mockSender = mock(CommandSender.class);
  private final Command command = mock(Command.class);

  private final GroupCommand groupCommand = new GroupCommand(mockGroupDao, mockPlayerDao);
  private String groupName = "testGroup";
  private String groupPrefix = "testPrefix";

  @Test
  void testOnCommand_NoArguments_UsageMessageDisplayed() {

    boolean result = groupCommand.onCommand(mockSender, command, "group", new String[] {});

    assertTrue(result);
    verify(mockSender)
        .sendMessage(
            Component.text("Usage: /group <create|delete|assign|info>", NamedTextColor.RED));
    verifyNoInteractions(mockGroupDao, mockPlayerDao);
  }

  @Test
  void testOnCommand_UnknownSubcommand_UnknownSubcommandMessageDisplayed() {

    boolean result =
        groupCommand.onCommand(mockSender, command, "group", new String[] {"invalidSubcommand"});

    assertTrue(result);
    verify(mockSender)
        .sendMessage(
            Component.text(
                "Unknown subcommand. Usage: /group <create|delete|assign|info>",
                NamedTextColor.RED));
    verifyNoInteractions(mockGroupDao, mockPlayerDao);
  }

  @Test
  void testHandleCreate_ValidGroup_SuccessMessageDisplayed() {

    when(mockGroupDao.createGroup(groupName, groupPrefix)).thenReturn(true);

    boolean result =
        groupCommand.onCommand(
            mockSender, command, "group", new String[] {"create", groupName, groupPrefix});

    assertTrue(result);
    verify(mockSender)
        .sendMessage(
            Component.text(
                String.format("Group '%s' created with prefix '%s'.", groupName, groupPrefix),
                NamedTextColor.GREEN));
    verify(mockGroupDao).createGroup(groupName, groupPrefix);
    verifyNoInteractions(mockPlayerDao);
  }

  @Test
  void testHandleCreate_GroupAlreadyExists_ErrorMessageDisplayed() {

    when(mockGroupDao.createGroup(groupName, groupPrefix)).thenReturn(false);

    boolean result =
        groupCommand.onCommand(
            mockSender, command, "group", new String[] {"create", groupName, groupPrefix});

    assertTrue(result);
    verify(mockSender)
        .sendMessage(
            Component.text(
                String.format("Failed to create group '%s'. It may already exist.", groupName), NamedTextColor.RED));
    verify(mockGroupDao).createGroup(groupName, groupPrefix);
    verifyNoInteractions(mockPlayerDao);
  }

  @Test
  void testHandleDelete_GroupExists_SuccessMessageDisplayed() {

    when(mockGroupDao.deleteGroup(groupName)).thenReturn(true);

    boolean result =
        groupCommand.onCommand(mockSender, command, "group", new String[] {"delete", groupName});

    assertTrue(result);
    verify(mockSender)
        .sendMessage(Component.text(String.format("Group '%s' deleted.", groupName), NamedTextColor.GREEN));
    verify(mockGroupDao).deleteGroup(groupName);
    verifyNoInteractions(mockPlayerDao);
  }

  @Test
  void testHandleDelete_GroupDoesNotExist_ErrorMessageDisplayed() {

    when(mockGroupDao.deleteGroup(groupName)).thenReturn(false);

    boolean result =
        groupCommand.onCommand(mockSender, command, "group", new String[] {"delete", groupName});

    assertTrue(result);
    verify(mockSender)
        .sendMessage(
            Component.text(
                String.format("Group '%s' does not exist or could not be deleted.", groupName), NamedTextColor.RED));
    verify(mockGroupDao).deleteGroup(groupName);
    verifyNoInteractions(mockPlayerDao);
  }
}
