package com.gildorym.underdark;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public class Underdark extends JavaPlugin {
	
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id){
		return new UnderdarkGenerator();
	}

}
