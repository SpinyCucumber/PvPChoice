package com.spiny.pvpchoice.main;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GetTaggedOwnersEvent {
	
	private List<String> players;
	private List<String> dirtyLore;
	private ItemStack item;
	
	public boolean isOwner(Player player) {
		return(players.contains(player.getName()));
	}
	
	public void cleanItem() {
		ItemMeta m = item.getItemMeta();
		List<String> lore = m.getLore();
		lore.removeAll(dirtyLore);
		m.setLore(lore);
		item.setItemMeta(m);
	}
	
	public GetTaggedOwnersEvent(List<String> players, List<String> dirtyLore, ItemStack item) {
		this.players = players;
		this.dirtyLore = dirtyLore;
		this.item = item;
	}
}
