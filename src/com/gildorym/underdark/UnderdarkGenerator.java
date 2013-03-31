package com.gildorym.underdark;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

public class UnderdarkGenerator extends ChunkGenerator {
	
	public List<BlockPopulator> getDefaultPopulators(World world) {
		List<BlockPopulator> populators = new ArrayList<BlockPopulator>();
		populators.add(new OrePopulator(world));
		return populators;
	}
	
	private void setBlock(byte[][] result, int x, int y, int z, byte blockid) {
		if (result[y >> 4] == null) {
			result[y >> 4] = new byte[4096];
		}
		result[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = blockid;
	}
	
	public byte[][] generateBlockSections(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomeGrid){
		byte[][] result = new byte[world.getMaxHeight() / 16][];
		int x, y, z;
		SimplexOctaveGenerator octaveGenerator = new SimplexOctaveGenerator(world, 4);
		SimplexOctaveGenerator noiseGenerator = new SimplexOctaveGenerator(world, 8);
		octaveGenerator.setScale(1 / 64.0);
		noiseGenerator.setScale(1 / 16.0);
		for (x = 0; x < 16; x++) {
			for (z = 0; z < 16; z++) {
				this.setBlock(result, x, 0, z, (byte) Material.BEDROCK.getId());
				this.setBlock(result, x, world.getMaxHeight() - 1, z, (byte) Material.BEDROCK.getId());
				double floorNoise = octaveGenerator.noise(x + chunkX * 16, z + chunkZ * 16, 0.5, 0.5) * 64;
				double ceilingNoise = octaveGenerator.noise(x + chunkX * 16, z + chunkZ * 16, 0.5, 0.5) * 128;
				double bridgeNoise = (noiseGenerator.noise(x + chunkX * 16, z + chunkZ * 16, 0.5, 0.5) * 50) + 50;
				for (y = 1; y < 64; y++) {
					this.setBlock(result, x, y, z, (byte) Material.LAVA.getId());
				}
				if (x < 3 || z < 3) {
					if (bridgeNoise >= 35) {
						this.setBlock(result, x, 92, z, (byte) Material.SMOOTH_BRICK.getId());
					}
				}
				if (x < 3 && z < 3) {
					if (bridgeNoise >= 35) {
						for (y = 1; y < 92; y++) {
							this.setBlock(result, x, y, z, (byte) Material.SMOOTH_BRICK.getId());
						}
					}
				}
				if (x > 12 || z > 12) {
					if (bridgeNoise >= 35) {
						this.setBlock(result, x, 84, z, (byte) Material.SMOOTH_BRICK.getId());
					}
				}
				if (x > 12 && z > 12) {
					if (bridgeNoise >= 35) {
						for (y = 1; y < 84; y++) {
							this.setBlock(result, x, y, z, (byte) Material.SMOOTH_BRICK.getId());
						}
					}
				}
				for (y = 1; y < 64 + floorNoise; y++) {
					if (y <= 92) {
						if ((x <= 12 && z <= 12) || y <= 84 || y >= 88) {
							this.setBlock(result, x, y, z, (byte) Material.STONE.getId());
						}
					}
				}
				for (y = world.getMaxHeight() - 2; y > world.getMaxHeight() - ceilingNoise; y--) {
					this.setBlock(result, x, y, z, (byte) Material.STONE.getId());
				}
			}
		}
		return result;
	}

}
