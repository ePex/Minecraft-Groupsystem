package org.pexdev.groupsystem;

import lombok.Builder;
import lombok.NonNull;
import org.bukkit.configuration.file.FileConfiguration;

@Builder
public record Config(
    @NonNull ChatSettings chatSettings,
    @NonNull DatabaseSettings databaseSettings,
    @NonNull String defaultGroupName) {

  @Builder
  public record ChatSettings(
      @NonNull String chatFormat, @NonNull String joinMessage, @NonNull String defaultPrefix) {

    public static ChatSettings init(final @NonNull FileConfiguration config) {

      return ChatSettings.builder()
          .chatFormat(
              config.getString("messages.chatFormat", "&f[%prefix%] %player% &7>> &r%message%"))
          .joinMessage(
              config.getString(
                  "messages.joinMessage", "&f[%prefix%] &a%player% joined the server!"))
          .defaultPrefix(config.getString("messages.defaultPrefix", "&7[Default] "))
          .build();
    }
  }

  @Builder
  public record DatabaseSettings(
      @NonNull String url, @NonNull String user, @NonNull String password) {

    public static DatabaseSettings init(@NonNull final FileConfiguration config) {

      return DatabaseSettings.builder()
          .url(config.getString("database.url", "jdbc:postgresql://localhost:5438/groupsystem"))
          .user(config.getString("database.user", "admin"))
          .password(config.getString("database.password", "secret"))
          .build();
    }
  }

  public static Config init(final GroupSystemPlugin plugin) {

    // Load pluginConfig if it doesn't exist
    plugin.saveDefaultConfig();
    // Read values from yaml
    final var pluginConfig = plugin.getConfig();

    return Config.builder()
        .chatSettings(ChatSettings.init(pluginConfig))
        .databaseSettings(DatabaseSettings.init(pluginConfig))
        .defaultGroupName(pluginConfig.getString("default-group-name", "default"))
        .build();
  }
}
