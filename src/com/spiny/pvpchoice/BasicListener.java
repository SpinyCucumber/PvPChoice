package com.spiny.pvpchoice;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class BasicListener<T extends Plugin> implements Listener {
	
	private List<T> plugins = new ArrayList<T>();
	
	public List<T> getPlugins() {
		return plugins;
	}
	
	public void listen(T p) {
		p.getServer().getPluginManager().registerEvents(this, p);
		this.plugin = p;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void newListener(Class<? extends BasicListener> clazz, Plugin p) {
		try {
			BasicListener l = clazz.newInstance();
			l.listen(p);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public T plugin;
}
