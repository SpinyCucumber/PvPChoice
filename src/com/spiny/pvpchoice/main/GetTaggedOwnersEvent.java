package com.spiny.pvpchoice.main;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public class GetTaggedOwnersEvent {
	
	private List<String> players;
	private List<String> dirtyLore;
	private ItemStack item;
	
	public boolean isOwner(String name) {
		return(players.contains(name));
	}
	
	public GetTaggedOwnersEvent(List<String> players, List<String> dirtyLore, ItemStack item) {
		this.players = players;
		this.dirtyLore = dirtyLore;
		this.item = item;
	}

	public List<String> getDirtyLore() {
		return dirtyLore;
	}

	public ItemStack getItem() {
		return item;
	}
	
}
