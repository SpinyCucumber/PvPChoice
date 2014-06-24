package com.spiny.pvpchoice.main;

public class PlayerData implements Cloneable {
	
	public boolean pvpEnabled;
	public int cooldown;
	
	public PlayerData(boolean pvpEnabled) {
		this.pvpEnabled = pvpEnabled;
	}
	
	@Override
	public PlayerData clone() {
		PlayerData d = new PlayerData(pvpEnabled);
		d.cooldown = cooldown;
		return d;
	}
	
}
