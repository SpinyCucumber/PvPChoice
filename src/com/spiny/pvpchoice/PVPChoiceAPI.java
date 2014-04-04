package com.spiny.pvpchoice;

import org.bukkit.entity.Player;

public class PVPChoiceAPI {
	
	private static PVPChoice plugin;
	
	public static void setPlugin(PVPChoice newPlugin) {
		plugin = newPlugin;
	}
	
	public static void setPVPEnabled(Player player, boolean enabled) {
		plugin.data(player).pvpEnabled = enabled;
	}
	
	public static boolean getPVPEnabled(Player player) {
		return plugin.data(player).pvpEnabled;
	}
}
