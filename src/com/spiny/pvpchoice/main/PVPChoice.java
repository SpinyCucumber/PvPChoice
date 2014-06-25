package com.spiny.pvpchoice.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.spiny.pvpchoice.listeners.BasicListener;
import com.spiny.pvpchoice.listeners.DropListenerHandler;
import com.spiny.pvpchoice.listeners.PVPListenerHandler;
import com.spiny.util.PlayerUtil;

public class PVPChoice extends JavaPlugin {
	
	private Map<Player, PlayerData> data = new HashMap<Player, PlayerData>();
	private PVPChoiceHandler handler;
	
	private static PlayerData defaultData;
	private static final String DEF_DATA_PATH = "defaultmode";
	
	private static final boolean DEBUG = true;
	
	private static PVPChoice recentInstance;
	private static String pluginName;
	
	private void updateInstance() {
		recentInstance = this;
		pluginName = this.getName();
	}
	
	private void printDebug(String debug) {
		if(!DEBUG) return;
		this.getLogger().log(Level.INFO, debug);
	}
	
	public static void updateStaticInstance() {
		recentInstance = (PVPChoice) Bukkit.getPluginManager().getPlugin(pluginName);
	}
	
	public static void setPVPChoiceHandler(PVPChoiceHandler handler) {
		recentInstance.handler = handler;
	}
	
	public static void dropItems(Location l, Player p, ItemStack...items) {
		for(ItemStack item : items) {
			recentInstance.getHandler().tagItem(item, p);
			l.getWorld().dropItem(l, item);
		}
	}
	
	public static void setPVPEnabled(Player player, boolean enabled) {
		recentInstance.data(player).pvpEnabled = enabled;
	}
	
	public static boolean getPVPEnabled(Player player) {
		return recentInstance.data(player).pvpEnabled;
	}
	
	public void onEnable() {
		getDataFolder().mkdir();
		saveDefaultConfig();
		for(Player player : getServer().getOnlinePlayers()) {
			data.put(player, new PlayerData(getConfig().getBoolean("defaultmode")));
		}
		new BukkitRunnable() {
			public void run() {
				for(PlayerData player : data.values()) {
					if(player.cooldown > 0) player.cooldown--;
				}
			}
		}.runTaskTimer(this, 20, 20);
		BasicListener.newListener(PVPListenerHandler.class, this);
		BasicListener.newListener(DropListenerHandler.class, this);
		this.updateInstance();
		defaultData = new PlayerData(this.getConfig().getBoolean(DEF_DATA_PATH));
		PlayerUtil.setServer(this.getServer());
		handler = new DefaultHandler();
	}
	
	public PVPChoiceHandler getHandler() {
		return handler;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!command.getName().equals("pvp")) return true;
			if(args.length == 0) {
				Player player = (Player) sender;
				if(data(player).pvpEnabled && sender.hasPermission("pvpchoice.toggle.off")) {
					if(data(player).cooldown > 0) {
						player.sendMessage(ChatColor.RED + "You must wait " + data(player).cooldown + " seconds after being hit.");
						return true;
					}
					data(player).pvpEnabled = false;
					player.sendMessage(ChatColor.RED + "PvP is now disabled!");
				} else if(sender.hasPermission("pvpchoice.toggle.on")){
					data(player).pvpEnabled = true;
					player.sendMessage(ChatColor.GREEN + "PvP is now enabled!");
				} else {
					player.sendMessage(ChatColor.RED + command.getPermissionMessage());
				}
			} else if(args.length == 1) {
				try {
					Player player = (Player) PlayerUtil.getPlayerFromName(args[0]);
					if(data(player).pvpEnabled && sender.hasPermission("pvpchoice.toggle.off.other")) {
						data(player).pvpEnabled = false;
						sender.sendMessage(ChatColor.RED + "PvP is now disabled for " + player.getName());
						if(getConfig().getBoolean("alwaysnotify")) player.sendMessage(ChatColor.RED + "PvP is now disabled!");
					} else if(sender.hasPermission("pvpchoice.toggle.on.other")){
						data(player).pvpEnabled = true;
						sender.sendMessage(ChatColor.GREEN + "PvP is now enabled for " + player.getName());
						if(getConfig().getBoolean("alwaysnotify")) player.sendMessage(ChatColor.GREEN + "PvP is now enabled!");
					} else {
						sender.sendMessage(ChatColor.RED + command.getPermissionMessage());
					}
				} catch(ClassCastException e) {
					sender.sendMessage(ChatColor.RED + args[0] + " is not currently online.");
				} catch(NullPointerException e) {
					sender.sendMessage(ChatColor.RED + "Could not find the player '" + args[0] + "'.");
				}
			}
		return true;
	}
	
	public PlayerData data(Player p) {
		PlayerData d = data.get(p);
		if(d == null) {
			d = defaultData.clone();
			data.put(p, d);
		}
		return d;
	}
	
	private class DefaultHandler implements PVPChoiceHandler {

		public GetTaggedOwnersEvent getOwners(ItemStack item) {
			if(!item.getItemMeta().hasLore()) return null;
			List<String> players = new ArrayList<String>();
			List<String> dirtyLore = new ArrayList<String>();
		    for(String line : item.getItemMeta().getLore()) {
		    	try {
		    		OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(unInvisify(line)));
		    		if(p == null) continue;
					players.add(p.getUniqueId().toString());
					dirtyLore.add(line);
		    	} catch(IllegalArgumentException e) {
		    		continue;
		    	}
		    }
		    if(players.size() == 0) return null;
		    return new GetTaggedOwnersEvent(players, dirtyLore, item);
		}

		public void handleDeath(Player deceased) {
			data(deceased).cooldown = 0;
			data(deceased).pvpEnabled = getConfig().getBoolean("defaultMode");
		}

		public boolean inquireCancel(Player damaged, Player damager) {
			return !(data(damaged).pvpEnabled) || !(data(damager).pvpEnabled);
		}

		public void tagItem(ItemStack item, Player... players) {
			ItemMeta m = item.getItemMeta();
			List<String> lore = m.getLore();
			if(lore == null) lore = new ArrayList<String>();
			for(Player p : players) {
				lore.add(toInvisible(p.getUniqueId().toString()));
			}
			m.setLore(lore);
			item.setItemMeta(m);
		}

		public void setNonOwner(Item item, Player player) {
			item.setMetadata(player.getUniqueId().toString(), new FixedMetadataValue(PVPChoice.this, null));
		}

		public boolean isNonOwner(Item item, Player player) {
			return item.hasMetadata(player.getUniqueId().toString());
		}

		public void cleanItem(GetTaggedOwnersEvent e) {
			for(String p : e.getPlayers()) {
				PVPChoice.this.printDebug("Cleaning item for " + p);
			}
			ItemMeta m = e.getItem().getItemMeta();
			List<String> lore = m.getLore();
			lore.removeAll(e.getDirtyLore());
			m.setLore(lore);
			e.getItem().setItemMeta(m);
		}
		
	}
	
	private static String unInvisify(String line) {
		return line.replaceAll("�", "");
	}
	
	private static String toInvisible(String s) {
        String hidden = "";
        for (char c : s.toCharArray()) hidden += ChatColor.COLOR_CHAR+""+c;
        return hidden;
    }
	
}
