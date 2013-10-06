package com.gildorymrp.underdark;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

public class UnderdarkGenerator extends ChunkGenerator {
	
	private Underdark plugin;
	
	public UnderdarkGenerator(Underdark plugin) {
		this.plugin = plugin;
	}
	
	public List<BlockPopulator> getDefaultPopulators(World world) {
		List<BlockPopulator> populators = new ArrayList<BlockPopulator>();
		populators.add(new OrePopulator(world));
		if (plugin.getConfig().getBoolean("worlds." + world.getName() + ".caves-enabled", true)) {
			populators.add(new CavePopulator());
		}
		if (plugin.getConfig().getBoolean("worlds." + world.getName() + ".ridges-enabled", true)) {
			populators.add(new RidgePopulator());
		}
		return populators;
	}
	
	@SuppressWarnings("deprecation") //TODO: Find a way to do this without using deprecated methods
	private void setBlock(byte[][] result, int x, int y, int z, Material material) {
		byte blockId = (byte) material.getId();
		if (result[y >> 4] == null) {
			result[y >> 4] = new byte[4096];
		}
		result[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = blockId;
	}
	
	public byte[][] generateBlockSections(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomeGrid){
		byte[][] result = new byte[world.getMaxHeight() / 16][];
		int x, y, z;
		SimplexOctaveGenerator octaveGenerator = new SimplexOctaveGenerator(world, 4);
		SimplexOctaveGenerator noiseGenerator = new SimplexOctaveGenerator(world, 8);
		octaveGenerator.setScale(1 / 64.0);
		noiseGenerator.setScale(1 / 16.0);
		int highBridgeXOffset = random.nextInt(13) + 3;
		int highBridgeZOffset = random.nextInt(13) + 3;
		int lowBridgeXOffset = random.nextInt(13) + 3;
		int lowBridgeZOffset = random.nextInt(13) + 3;
		for (x = 0; x < 16; x++) {
			for (z = 0; z < 16; z++) {
				this.setBlock(result, x, 0, z, Material.BEDROCK);
				this.setBlock(result, x, world.getMaxHeight() - 1, z, Material.BEDROCK);
				double floorNoise = octaveGenerator.noise(x + chunkX * 16, z + chunkZ * 16, 0.5, 0.5) * 64;
				double ceilingNoise = octaveGenerator.noise(x + chunkX * 16, z + chunkZ * 16, 0.5, 0.5) * 128;
				double bridgeNoise = (noiseGenerator.noise(x + chunkX * 16, z + chunkZ * 16, 0.5, 0.5) * 50) + 50;
				for (y = 1; y < 64; y++) {
					this.setBlock(result, x, y, z, Material.LAVA);
				}
				
				if ((x > highBridgeXOffset - 3 && x <= highBridgeXOffset) || (z > highBridgeZOffset - 3 && z <= highBridgeZOffset)) {
					if (bridgeNoise >= 35) {
						this.setBlock(result, x, 92, z, Material.SMOOTH_BRICK);
					}
				}
				if ((x > highBridgeXOffset - 3 && x <= highBridgeXOffset) && (z > highBridgeZOffset - 3 && z <= highBridgeZOffset)) {
					if (bridgeNoise >= 35) {
						for (y = 1; y < 92; y++) {
							this.setBlock(result, x, y, z, Material.SMOOTH_BRICK);
						}
					}
				}
				
				if ((x > lowBridgeXOffset - 3 && x <= lowBridgeXOffset) || (z > lowBridgeZOffset - 3 && z <= lowBridgeZOffset)) {
					if (bridgeNoise >= 35) {
						this.setBlock(result, x, 84, z, Material.SMOOTH_BRICK);
					}
				}
				if ((x > lowBridgeXOffset - 3 && x <= lowBridgeXOffset) && (z > lowBridgeZOffset - 3 && z <= lowBridgeZOffset)) {
					if (bridgeNoise >= 35) {
						for (y = 1; y < 84; y++) {
							this.setBlock(result, x, y, z, Material.SMOOTH_BRICK);
						}
					}
				}
				for (y = 1; y < 64 + floorNoise; y++) {
					if (y <= 92) {
						if ((x <= 12 && z <= 12) || y <= 84 || y >= 88) {
							this.setBlock(result, x, y, z, Material.STONE);
						}
					}
				}
				for (y = world.getMaxHeight() - 2; y > world.getMaxHeight() - ceilingNoise; y--) {
					this.setBlock(result, x, y, z, Material.STONE);
				}
			}
		}
		return result;
	}

}
