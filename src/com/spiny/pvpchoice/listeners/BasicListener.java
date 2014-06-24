package com.spiny.pvpchoice.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class BasicListener<T extends Plugin> implements Listener {
	
	private List<T> plugins = new ArrayList<T>();
	protected T plugin;
	
	public List<T> getPlugins() {
		return plugins;
	}
	
	public void listen(T p) {
		p.getServer().getPluginManager().registerEvents(this, p);
		plugin = p;
	}

	@SafeVarargs
	public static <T extends Plugin> void newListener(Class<? extends BasicListener<T>> clazz, T... ps) {
		try {
			BasicListener<T> l = clazz.newInstance();
			for(T p : ps) l.listen(p);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
