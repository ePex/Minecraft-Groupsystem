package org.pexdev.groupsystem.commands;

import lombok.extern.slf4j.Slf4j;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;

@Slf4j
public class CommandRegistry {

  public static void registerCommand(
      final PluginCommand command, final CommandExecutor commandExecutor) {

    if (command != null) {
      command.setExecutor(commandExecutor);
    } else {
      log.warn("Command not found in plugin.yml!");
    }
  }
}
