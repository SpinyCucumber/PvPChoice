/**
 * 
 */
/**
 * @author Elijah
 *
 */
package com.spiny.util;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;

public class EntityUtil {
	
	public static Entity projectileCheck(Entity damager) throws ShooterNotEntityException {
		if(damager instanceof Projectile) {
			ProjectileSource shooter = ((Projectile) damager).getShooter();
			if(!(shooter instanceof Entity)) throw new ShooterNotEntityException(shooter);
			damager = (Entity) shooter;
		}
		return damager;
	}
	
	public static class ShooterNotEntityException extends Exception {

		private static final long serialVersionUID = 1L;
		
		private ProjectileSource notEntity;
		
		public ShooterNotEntityException(ProjectileSource notEntity) {
			this.notEntity = notEntity;
		}
		
		public ProjectileSource getNotEntity() {
			return notEntity;
		}
	}
}