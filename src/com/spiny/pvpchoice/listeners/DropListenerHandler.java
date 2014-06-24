package com.spiny.pvpchoice.listeners;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import com.spiny.pvpchoice.main.GetTaggedOwnersEvent;
import com.spiny.pvpchoice.main.PVPChoice;
import com.spiny.pvpchoice.main.PlayerData;

public class DropListenerHandler extends BasicListener<PVPChoice> {
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if(!(event.getEntity() instanceof Player)) return;
		Player p = (Player) event.getEntity();
		PlayerData d = plugin.data(p);
		List<ItemStack> drops = event.getDrops();
		if(!d.pvpEnabled) tagItems(drops, p);
		else if(p.getKiller() != null) tagItems(drops, p, p.getKiller());
	}
	
	private void tagItems(List<ItemStack> drops, Player...players) {
		for(ItemStack item : drops) {
			plugin.getHandler().tagItem(item, players);
		}
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if(plugin.getHandler().isNonOwner(event.getItem(), event.getPlayer())) {
			event.setCancelled(true);
			return;
		}
		GetTaggedOwnersEvent owners = plugin.getHandler().getOwners(event.getItem().getItemStack());
		if(owners == null) return;
		if(!owners.isOwner(event.getPlayer().getUniqueId().toString())) {
			plugin.getHandler().setNonOwner(event.getItem(), event.getPlayer());
			event.setCancelled(true);
			return;
		}
		plugin.getHandler().cleanItem(owners);
	}
	
}
