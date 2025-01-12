package org.pexdev.groupsystem.commands;

import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.pexdev.groupsystem.database.dao.GroupDao;
import org.pexdev.groupsystem.database.dao.PlayerDao;
import org.pexdev.groupsystem.util.TimeUtil;

public record GroupCommand(@NonNull GroupDao groupDao, @NonNull PlayerDao playerDao)
    implements CommandExecutor {

  @Override
  public boolean onCommand(
      final @NonNull CommandSender sender,
      final @NonNull Command command,
      final @NonNull String label,
      final @NonNull String[] args) {

    if (args.length == 0) {
      sender.sendMessage(
          Component.text("Usage: /group <create|delete|assign|info>", NamedTextColor.RED));
      return true;
    }

    final var subcommand = args[0].toLowerCase();
    switch (subcommand) {
      case "create" -> handleCreate(sender, args);
      case "delete" -> handleDelete(sender, args);
      case "assign" -> handleAssign(sender, args);
      case "info" -> handleInfo(sender, args);
      default ->
          sender.sendMessage(
              Component.text(
                  "Unknown subcommand. Usage: /group <create|delete|assign|info>",
                  NamedTextColor.RED));
    }

    return true;
  }

  private void handleCreate(final @NonNull CommandSender sender, final @NonNull String[] args) {

    if (args.length < 3) {
      sender.sendMessage(
          Component.text("Usage: /group create <name> <prefix>", NamedTextColor.RED));
      return;
    }

    final var groupName = args[1];
    final var prefix = args[2];
    if (groupDao.createGroup(groupName, prefix)) {
      sender.sendMessage(
          Component.text(
              String.format("Group '%s' created with prefix '%s'.", groupName, prefix),
              NamedTextColor.GREEN));
    } else {
      sender.sendMessage(
          Component.text(
              String.format("Failed to create group '%s'. It may already exist.", groupName),
              NamedTextColor.RED));
    }
  }

  private void handleDelete(final @NonNull CommandSender sender, final @NonNull String[] args) {

    if (args.length < 2) {
      sender.sendMessage(Component.text("Usage: /group delete <name>", NamedTextColor.RED));
      return;
    }

    final var groupName = args[1];
    if (groupDao.deleteGroup(groupName)) {
      sender.sendMessage(
          Component.text(String.format("Group '%s' deleted.", groupName), NamedTextColor.GREEN));
    } else {
      sender.sendMessage(
          Component.text(
              String.format("Group '%s' does not exist or could not be deleted.", groupName),
              NamedTextColor.RED));
    }
  }

  private void handleAssign(final @NonNull CommandSender sender, final @NonNull String[] args) {

    // /group assign <player> <group> [time]
    if (args.length < 3) {
      sender.sendMessage(
          Component.text("Usage: /group assign <player> <group> [time]", NamedTextColor.RED));
      return;
    }

    final var playerName = args[1];
    final var groupName = args[2];
    final var timeArg = args.length > 3 ? args[3] : "";

    final var target = Bukkit.getPlayerExact(playerName);
    if (target == null) {
      sender.sendMessage(Component.text("Player not found or not online.", NamedTextColor.RED));
      return;
    }

    // Find the group ID from the group name (GroupDao)
    final var  groupId = groupDao.getGroupIdByName(groupName); // you'd need to implement getGroupIdByName
    if (groupId == -1) {
      sender.sendMessage(
          Component.text(
              String.format("Group '%s' doesn't exist.", groupName), NamedTextColor.RED));
      return;
    }

    // Parse time
    final var  duration = TimeUtil.toMilliseconds(timeArg); // 0 => permanent
    long expiration = 0;
    if (duration > 0) {
      expiration = System.currentTimeMillis() + duration;
    }

    // Store in the DB
    if (playerDao.assignPlayerToGroup(target.getUniqueId(), groupId, expiration)) {
      if (duration > 0) {
        sender.sendMessage(
            Component.text(
                String.format("Assigned %s to %s for %s.", playerName, groupName, timeArg),
                NamedTextColor.GREEN));
      } else {
        sender.sendMessage(
            Component.text(
                String.format("Assigned %s to %s permanently.", playerName, groupName),
                NamedTextColor.GREEN));
      }

      // TODO update playername

    } else {
      sender.sendMessage(
          Component.text(
              String.format("Failed to assign %s to %s.", playerName, groupName),
              NamedTextColor.RED));
    }
  }

  private void handleInfo(final @NonNull CommandSender sender, final @NonNull String[] args) {

    if (args.length < 2) {
      sender.sendMessage(Component.text("Usage: /group info <player>", NamedTextColor.RED));
      return;
    }

    final var playerName = args[1];
    final var target = Bukkit.getPlayerExact(playerName);
    if (target == null) {
      sender.sendMessage(Component.text("Player not found or not online.", NamedTextColor.RED));
      return;
    }

    final var groupId = playerDao.getPlayerGroupId(target.getUniqueId());
    if (groupId == -1) {
      sender.sendMessage(
          Component.text(
              String.format("%s has no group assigned.", playerName), NamedTextColor.YELLOW));
      return;
    }

    // Retrieve group name from groupId
    final var groupName = groupDao.getGroupNameById(groupId);
    // Or store it in memory if you prefer

    // Check expiration
    final var expirationTime = playerDao.getExpirationTime(target.getUniqueId());
    if (expirationTime <= 0) {
      // Permanent
      sender.sendMessage(
          Component.text(
              String.format("%s is in group %s permanently.", playerName, groupName),
              NamedTextColor.GREEN));
    } else {
      final var remaining = expirationTime - System.currentTimeMillis();
      if (remaining <= 0) {
        sender.sendMessage(
            Component.text(
                String.format("%s is in group %s but it seems expired.", playerName, groupName),
                NamedTextColor.RED));
      } else {
        // Convert remaining ms to a user-friendly string
        final var readable = TimeUtil.toTimeString(remaining);
        sender.sendMessage(
            Component.text(
                String.format("%s is in group %s for %s longer.", playerName, groupName, readable),
                NamedTextColor.GREEN));
      }
    }
  }
}
