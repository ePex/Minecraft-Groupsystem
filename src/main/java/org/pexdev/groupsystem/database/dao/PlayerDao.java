package org.pexdev.groupsystem.database.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.pexdev.groupsystem.Config.ChatSettings;
import org.pexdev.groupsystem.database.DatabaseManager;

@Slf4j
public class PlayerDao implements Dao {

  private final Connection connection;
  private final GroupDao groupDao;
  private final ChatSettings chatSettings;

  public PlayerDao(
      final @NonNull DatabaseManager databaseManager,
      final @NonNull GroupDao groupDao,
      final @NonNull ChatSettings chatSettings) {

    this.connection = databaseManager.getConnection();
    this.groupDao = groupDao;
    this.chatSettings = chatSettings;
  }

  public @NotNull TextComponent getPrefix(final Player player) {

    // Retrieve the player’s group ID
    final var uuid = player.getUniqueId();
    final var groupId = getPlayerGroupId(uuid);

    // No group, use default prefix
    if (groupId == -1) {
      return Component.text(chatSettings.defaultPrefix(), NamedTextColor.GRAY);
    }

    // Lookup the group’s prefix in the database
    var dbPrefix = groupDao.getGroupPrefixById(groupId); // e.g. "[VIP]" or "&6[VIP]"
    if (dbPrefix == null || dbPrefix.isEmpty()) {
      return Component.text(
          chatSettings.defaultPrefix(),
          NamedTextColor.GRAY); // fallback if the group prefix is missing
    }

    return Component.text(dbPrefix, NamedTextColor.GOLD);
  }

  /**
   * Assigns a player to a specific group, with an optional expiration time. expirationMillis == 0
   * or -1 means permanent (no expiry).
   */
  public boolean assignPlayerToGroup(
      final UUID playerUuid, final int groupId, final long expirationMillis) {

    final var sql =
        "INSERT OR REPLACE INTO players (uuid, group_id, expiration_time) VALUES (?, ?, ?)";
    try (final var stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, playerUuid.toString());
      stmt.setInt(2, groupId);
      stmt.setLong(3, expirationMillis);
      final var rowsAffected = stmt.executeUpdate();
      return rowsAffected > 0;
    } catch (final SQLException e) {
      log.error("Failed to assign player to group: {}", playerUuid, e);
      return false;
    }
  }

  /** Retrieves the current group for a given player, or -1 if none assigned. */
  public int getPlayerGroupId(final UUID playerUuid) {

    final var sql = "SELECT group_id FROM players WHERE uuid = ?";
    try (final var stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, playerUuid.toString());
      final var rs = stmt.executeQuery();
      if (rs.next()) {
        return rs.getInt("group_id");
      }
    } catch (final SQLException e) {
      log.error("Failed to retrieve player group ID: {}", playerUuid, e);
    }
    return -1;
  }

  /** Retrieves the expiration time for a player's group. */
  public long getExpirationTime(final UUID playerUuid) {

    final var sql = "SELECT expiration_time FROM players WHERE uuid = ?";
    try (final var stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, playerUuid.toString());
      final var rs = stmt.executeQuery();
      if (rs.next()) {
        return rs.getLong("expiration_time");
      }
    } catch (final SQLException e) {
      log.error("Failed to retrieve player expiration time: {}", playerUuid, e);
    }
    return -1; // Means no expiration
  }

  /** Checks if a player's group assignment is expired. */
  public boolean isExpired(final UUID playerUuid) {

    final var expiration = getExpirationTime(playerUuid);
    if (expiration <= 0) {
      // Means no expiration time set (permanent)
      return false;
    }
    return System.currentTimeMillis() > expiration;
  }
}
