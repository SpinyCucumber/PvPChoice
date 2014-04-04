package com.spiny.pvpchoice;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class PVPListenerHandler extends BasicListener<PVPChoice> {
	
	@EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
		final Entity entity = event.getEntity();
		Entity damager = event.getDamager();
		if(damager instanceof Projectile) damager = ((Projectile) (damager)).getShooter();
		if(!(entity instanceof Player) || !(damager instanceof Player)) return;
		if(plugin.getHandler().inquireCancel((Player) entity, (Player) damager)) event.setCancelled(true);
		else if(!((Player) entity).hasPermission("pvpchoice.cooldown.bypass")) plugin.data((Player) entity).cooldown = plugin.getConfig().getInt("cooldown");
    }
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if(!(entity instanceof Player)) return;
		
	}
}
