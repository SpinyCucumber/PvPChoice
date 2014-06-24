package com.spiny.pvpchoice.main;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface PVPChoiceHandler {

	void tagItem(ItemStack item, Player... owners);
	void cleanItem(GetTaggedOwnersEvent e);
	void setNonOwner(Item item, Player player);
	boolean isNonOwner(Item item, Player player);
	GetTaggedOwnersEvent getOwners(ItemStack item);
	
	boolean inquireCancel(Player damaged, Player damager);
	void handleDeath(Player deceased);
	
}