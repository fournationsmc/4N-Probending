# 4N-Probending

A probending plugin for Minecraft servers with ProjectKorra, updated for Paper 1.21.1.

## Description

This plugin adds probending functionality to Minecraft servers running ProjectKorra. Players can create teams, join queues, and participate in probending matches with custom arenas and scoring systems.

## Requirements

- Paper 1.21.1 or higher
- Java 21
- ProjectKorra plugin
- (Optional) Vault plugin for economy features
- (Optional) WorldGuard plugin for arena protection

## Building

To build the plugin, you need Maven installed:

```bash
mvn clean package
```

The compiled JAR will be available in the `target/` directory.

## Installation

1. Download or build the plugin JAR
2. Place it in your server's `plugins/` directory
3. Ensure ProjectKorra is installed
4. Restart your server
5. Configure the plugin using `/pb admin` commands

## Configuration

The plugin will create a `config.yml` file in the plugin directory. You can configure:

- Database settings (SQLite or MySQL)
- Round settings and allowed moves
- Team settings and element restrictions
- Economy integration
- Messages and localization

## Commands

- `/pb` or `/probending` - Main command
- `/pb team create <name>` - Create a team
- `/pb team invite <player>` - Invite a player to your team
- `/pb join` - Join the probending queue
- `/pb info` - View probending information
- `/pb admin` - Admin commands (requires permission)

## Permissions

- `probending.user` - Basic probending commands
- `probending.admin` - Administrative commands

## Changes from Original

This version has been updated from the original 9-year-old codebase:

- Updated to Paper 1.21.1 API
- Removed deprecated API calls
- Updated dependencies to modern versions
- Removed web interface components
- Updated title system to use Adventure API
- Fixed deprecated PlayerAnimationEvent usage
- Updated Maven configuration for modern Java

## Support

This is a community-maintained update of the original Probending plugin. For issues or contributions, please check the project repository.

