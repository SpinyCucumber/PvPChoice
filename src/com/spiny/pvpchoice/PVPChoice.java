package com.spiny.pvpchoice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PVPChoice extends JavaPlugin implements Listener {
	
	private Map<Player, PlayerData> data = new HashMap<Player, PlayerData>();
	private PVPChoiceHandler handler;
	
	public void onEnable() {
		getDataFolder().mkdir();
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(this, this);
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
		PVPChoiceAPI.setPlugin(this);
		handler = new DefaultHandler();
	}
	
	public PVPChoiceHandler getHandler() {
		return handler;
	}
	
	public void setHandler(PVPChoiceHandler handler) {
		this.handler = handler;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		data.put(event.getPlayer(), new PlayerData(getConfig().getBoolean("defaultmode")));
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
				Player player = getServer().getPlayer(args[0]);
				if(player == null) {
					sender.sendMessage(ChatColor.RED + "Could not find the player '" + args[0] + "'.");
				}
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
			}
		return true;
	}
	
	public PlayerData data(Player p) {
		return data.get(p);
	}
	
	private static String toInvisible(String s) {
        String hidden = "";
        for (char c : s.toCharArray()) hidden += ChatColor.COLOR_CHAR+""+c;
        return hidden;
    }
	
	public interface PVPChoiceHandler {

		void tagItems(List<ItemStack> items, Player... owners);
		GetTaggedOwnersEvent getOwners(ItemStack item);
		
		boolean inquireCancel(Player damaged, Player damager);
		void handleDeath(Player deceased);
	}
	
	public class DefaultHandler implements PVPChoiceHandler {

		public GetTaggedOwnersEvent getOwners(ItemStack item) {
			if(!item.getItemMeta().hasLore()) return null;
			List<String> players = new ArrayList<String>();
			List<String> dirtyLore = new ArrayList<String>();
		    for(String line : item.getItemMeta().getLore()) {
			   Player p = Bukkit.getPlayer(unInvisify(line));
			   if(p != null) {
				   players.add(p.getName());
				   dirtyLore.add(line);
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

		public void tagItems(List<ItemStack> items, Player... players) {
			for(ItemStack item : items) {
				ItemMeta m = item.getItemMeta();
				List<String> lore = m.getLore();
				if(lore == null) lore = new ArrayList<String>();
				for(Player p : players) {
					lore.add(toInvisible(p.getName()));
				}
				m.setLore(lore);
				item.setItemMeta(m);
			}
		}
	}
	
	private static String unInvisify(String line) {
		return line.replaceAll("§", "");
	}
}
