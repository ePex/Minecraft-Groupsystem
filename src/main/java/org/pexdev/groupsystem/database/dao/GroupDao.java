package org.pexdev.groupsystem.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.pexdev.groupsystem.database.DatabaseManager;

@Slf4j
public class GroupDao implements Dao {

  private final Connection connection;
  private final String defaultGroupName;

  public GroupDao(
      final @NonNull DatabaseManager databaseManager, final @NonNull String defaultGroupName) {

    this.connection = databaseManager.getConnection();
    this.defaultGroupName = defaultGroupName;
  }

  /**
   * Creates a group in the database.
   *
   * @param groupName Name of the group
   * @param prefix Prefix for the group
   * @return true if the group was successfully created, false if it already exists or on error
   */
  public boolean createGroup(final String groupName, final String prefix) {

    final var sql = "INSERT INTO groups (name, prefix) VALUES (?, ?)";
    try (final PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, groupName);
      stmt.setString(2, prefix);
      final var rowsAffected = stmt.executeUpdate();
      return rowsAffected > 0; // If rowsAffected == 0, insertion failed (e.g., group exists)
    } catch (final SQLException e) {
      log.error("Failed to create group: {}", groupName, e);
      return false;
    }
  }

  /**
   * Deletes a group by name.
   *
   * @param groupName Name of the group
   * @return true if a group was deleted, false if the group did not exist
   */
  public boolean deleteGroup(final String groupName) {

    final var sql = "DELETE FROM groups WHERE name = ?";
    try (final var stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, groupName);
      final var rowsAffected = stmt.executeUpdate();
      return rowsAffected > 0; // If rowsAffected == 0, no group was found/deleted
    } catch (final SQLException e) {
      log.error("Failed to delete group: {}", groupName, e);
      return false;
    }
  }

  /** Checks if a group exists by name. */
  public boolean groupExists(final String groupName) {

    final var sql = "SELECT id FROM groups WHERE name = ?";
    try (final var stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, groupName);
      final var rs = stmt.executeQuery();
      return rs.next(); // True if we have at least one row
    } catch (final SQLException e) {
      log.error("Failed to check if group exists: {}", groupName, e);
      return false;
    }
  }

  /** Retrieves the unique ID for a group (by name). Returns -1 if not found. */
  public int getGroupIdByName(final String groupName) {

    final var sql = "SELECT id FROM groups WHERE name = ?";
    try (final var stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, groupName);
      final var rs = stmt.executeQuery();
      if (rs.next()) {
        return rs.getInt("id");
      }
    } catch (final SQLException e) {
      log.error("Failed to retrieve group ID: {}", groupName, e);
    }
    return -1; // not found
  }

  /**
   * Retrieves the group prefix for a specific group ID. Returns null if the group doesn't exist or
   * on error.
   */
  public String getGroupPrefixById(final int groupId) {

    final var sql = "SELECT prefix FROM groups WHERE id = ?";
    try (final var stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, groupId);
      final var rs = stmt.executeQuery();
      if (rs.next()) {
        return rs.getString("prefix");
      }
    } catch (final SQLException e) {
      log.error("Failed to retrieve group prefix: {}", groupId, e);
    }
    return null;
  }

  public String getGroupNameById(final int groupId) {

    final var sql = "SELECT name FROM groups WHERE id = ?";
    try (final var stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, groupId);
      final var rs = stmt.executeQuery();
      if (rs.next()) {
        return rs.getString("name");
      }
    } catch (final SQLException e) {
      log.error("Failed to retrieve group name: {}", groupId, e);
    }
    return null;
  }

  public int getDefaultGroupId() {

    return getGroupIdByName(defaultGroupName);
  }
}
