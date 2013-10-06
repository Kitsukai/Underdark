package com.gildorymrp.underdark;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

public class CavePopulator extends BlockPopulator {

	@Override
	public void populate(World world, Random random, Chunk source) {
		int worldChunkX = source.getX() * 16;
		int worldChunkZ = source.getZ() * 16;
		int x, y, z;
		SimplexOctaveGenerator noiseGenerator = new SimplexOctaveGenerator(world, 8);
		noiseGenerator.setScale(1 / 32.0);
		for (x = worldChunkX; x < worldChunkX + 16; x++) {
			for (z = worldChunkZ; z < worldChunkZ + 16; z++) {
				for (y = world.getMaxHeight() - 32; y > world.getMaxHeight() - (32 + (noiseGenerator.noise(x, z, 0.5, 0.5) * 4)); y--) {
					if (world.getBlockAt(x, y, z).getType() != Material.AIR) {
						world.getBlockAt(x, y, z).setType(Material.AIR);
					}
				}
				for (y = world.getMaxHeight() - 32; y < world.getMaxHeight() - (32 - (noiseGenerator.noise(x, z, 0.5, 0.5) * 4)); y++) {
					if (world.getBlockAt(x, y, z).getType() != Material.AIR) {
						world.getBlockAt(x, y, z).setType(Material.AIR);
					}
				}
			}
		}
	}

}
