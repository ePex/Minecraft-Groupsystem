# GroupSystem Plugin

A lightweight group/authorization system for Paper (Minecraft 1.21.3+) that supports:
- Creating and managing groups in-game
- Assigning players to groups (permanently or temporarily)
- Displaying group prefixes in chat and on join
- Customizable messages via configuration
- Relational database storage (SQLite or PostgreSQL)
- Display Player Name and Group on Signs with `[RankSign]` on the first line.

## Features

- **Easy Group Management**: `/group create <name> <prefix>` / `/group delete <name>`.
- **Player Assignments**: Permanent or temporary (e.g., `4d7m23s`).
- **Chat & Join Prefix**: Automatically shows the group prefix in chat and when players join.
- **Configurable Messages**: All messages can be customized in `config.yml`.
- **Async I/O**: Database operations can be run asynchronously for large servers.
- **Unit Tested**: Key DAO and utility methods covered by JUnit tests.

## Requirements

- **Paper 1.21.3** (or a compatible Paper-based fork).
- **Java 21**.
- SQLite driver or PostgreSQL driver and docker if you prefer external DB.

## Installation

1. Ensure that you have the correct java version active
2. Run `mvn clean package`
3. Copy the jar from the `target` folder in your server's `plugins` folder.
4. Start (or restart) your server. The plugin will generate a default `config.yml` in `plugins/GroupSystem`.
5. Customize `config.yml` to your liking.
   1. If you want to use an external postgresql db, make sure that the container is running.  
   You can use the `infrastructure/docker-compose.yml`.   
   Just cd into the folder and run `docker-compose up -d` and make sure the ports are equal with the ones
   in the `config.yml` file and the database-provider property is set to postgresql

## Configuration

```yaml
# Default config.yml
database-provider: sqlite

database:
  url: jdbc:postgresql://localhost:5438/groupsystem
  user: admin
  password: secret

messages:
  chatFormat: "[%prefix%] %player%: %message%"
  joinMessage: "[%prefix%] %player% joined the server!"
  defaultPrefix: "&7[Default] "

default-group-name: default
