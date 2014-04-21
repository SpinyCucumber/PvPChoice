package com.spiny.pvpchoice.listeners;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
		if(!d.pvpEnabled) plugin.getHandler().tagItems(drops, p);
		else {
			if(p.getKiller() != null) plugin.getHandler().tagItems(drops, p, p.getKiller());
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		GetTaggedOwnersEvent owners = plugin.getHandler().getOwners(event.getItem().getItemStack());
		if(owners == null) return;
		if(!owners.isOwner(event.getPlayer())) event.setCancelled(true);
		else owners.cleanItem();
	}
}
