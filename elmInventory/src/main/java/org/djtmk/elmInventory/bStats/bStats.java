package org.djtmk.elmInventory.bStats;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public class bStats extends JavaPlugin {

    @Override
    public void onEnable() {
        // All you have to do is adding the following two lines in your onEnable method.
        // You can find the plugin ids of your plugins on the page https://bstats.org/what-is-my-plugin-id
        int pluginId = 24134; // <-- Replace with the id of your plugin!
        Metrics metrics = new Metrics(this, pluginId);

        // Optional: Add custom charts
        //metrics.addCustomChart(new Metrics.SimplePie("chart_id", () -> "My value")); <--Get this to work
    }
//Need to update version in prom.xml to 3.1.0
}