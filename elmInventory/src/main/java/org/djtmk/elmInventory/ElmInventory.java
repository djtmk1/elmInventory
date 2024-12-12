package org.djtmk.elmInventory;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ElmInventory extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Save default configuration if it doesn't exist
        saveDefaultConfig();

        // Register event listener
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("InventoryManager enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("InventoryManager disabled!");
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        String fromWorld = event.getFrom().getName();
        String toWorld = event.getPlayer().getWorld().getName();
        FileConfiguration config = getConfig();

        if (toWorld != null && isWorldConfigured(toWorld, config)) {
            handleWorldEntry(event, toWorld, config);
        }

        if (fromWorld != null && isWorldConfigured(fromWorld, config)) {
            handleWorldExit(event, fromWorld, config);
        }
    }

    // Helper method to handle world entry logic
    private void handleWorldEntry(PlayerChangedWorldEvent event, String toWorld, FileConfiguration config) {
        if (config.getBoolean("worlds." + toWorld + ".clear_on_enter", false)) {
            clearPlayerInventory(event, "messages.inventory_cleared", config);
        }

        if (config.getBoolean("worlds." + toWorld + ".set_gamemode", false)) {
            String gamemode = config.getString("worlds." + toWorld + ".gamemode", "SURVIVAL");
            try {
                event.getPlayer().setGameMode(GameMode.valueOf(gamemode));
            } catch (IllegalArgumentException e) {
                getLogger().warning("Invalid gamemode '" + gamemode + "' for world '" + toWorld + "'. Using default SURVIVAL.");
                event.getPlayer().setGameMode(GameMode.SURVIVAL);
            }
        }
    }

    // Helper method to handle world exit logic
    private void handleWorldExit(PlayerChangedWorldEvent event, String fromWorld, FileConfiguration config) {
        // No need to clear inventory on exit to avoid duplicate message
        getLogger().info("Player is leaving world: " + fromWorld); // Optional logging for debugging
    }

    // Clears inventory and sends custom-defined messages
    private void clearPlayerInventory(PlayerChangedWorldEvent event, String messageKey, FileConfiguration config) {
        Player player = event.getPlayer();

        // Clear the inventory
        player.getInventory().clear();

        // Send message to the player
        String message = getMessage(messageKey, "Your inventory has been cleared");
        if (!message.isEmpty()) {
            player.sendMessage(message);
        }
    }

    // Verify if a world is configured
    private boolean isWorldConfigured(String worldName, FileConfiguration config) {
        return config.getBoolean("worlds." + worldName + ".enabled", false);
    }

    // Retrieve a message from configuration with a default fallback
    public String getMessage(String messageKey, String defaultMessage) {
        // Retrieve message from the configuration
        String message = getConfig().getString(messageKey, defaultMessage);

        // Apply color codes if necessary (e.g., '&' to '§')
        return colorize(message);
    }

    // Utility to colorize strings (replace & with § for Minecraft color codes)
    public String colorize(String message) {
        return message.replace("&", "§");
    }
}