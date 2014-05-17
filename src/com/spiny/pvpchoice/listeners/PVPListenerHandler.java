package com.spiny.pvpchoice.listeners;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.spiny.pvpchoice.main.PVPChoice;
import com.spiny.util.EntityUtil;
import com.spiny.util.EntityUtil.ShooterNotEntityException;

public class PVPListenerHandler extends BasicListener<PVPChoice> {
	
	private static final ItemStack TEST_ITEM = new ItemStack(Material.APPLE);
	private static final String[] LORE = new String[]{"Hello world!"};
	private static final boolean DEBUG_PC_DL = false;
	
	static {
		ItemMeta m = TEST_ITEM.getItemMeta();
		m.setLore(Arrays.asList(LORE));
		TEST_ITEM.setItemMeta(m);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
		final Entity entity = event.getEntity();
		Entity damager = event.getDamager();
		if(!(entity instanceof Player)) return;
		try {
			damager = EntityUtil.projectileCheck(damager);
		} catch (ShooterNotEntityException e) {
			if(DEBUG_PC_DL) ((Player) entity).getInventory().addItem(TEST_ITEM);
			return;
		}
		if(!(damager instanceof Player)) return;
		if(plugin.getHandler().inquireCancel((Player) entity, (Player) damager)) event.setCancelled(true);
		else if(!((Player) entity).hasPermission("pvpchoice.cooldown.bypass")) plugin.data((Player) entity).cooldown = plugin.getConfig().getInt("cooldown");
    }
	
}
