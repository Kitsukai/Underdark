package com.gildorymrp.underdark;

import java.util.Arrays;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

public class RidgePopulator extends BlockPopulator {

	@Override
	public void populate(World world, Random random, Chunk source) {
		int worldChunkX = source.getX() * 16;
		int worldChunkZ = source.getZ() * 16;
		byte[] stalactiteHeights = new byte[256];
		int x = worldChunkX;
		int y = world.getMaxHeight();
		int z = worldChunkZ;
		int height = 0;
		int i = 0;
		for (x = worldChunkX; x < worldChunkX + 16; x++) {
			for (z = worldChunkZ; z < worldChunkZ + 16; z++) {
				y = world.getMaxHeight();
				height = 0;
				while (world.getBlockAt(x, y, z).getType() != Material.AIR && y > 0) {
					y--;
					height++;
				}
				stalactiteHeights[i] = (byte) height;
				i++;
			}
		}
		int medianHeight = getMedian(stalactiteHeights);
		addEdge(world, x, world.getMaxHeight() - medianHeight, z, worldChunkX, worldChunkZ, 3);
	}
	
	private byte getMedian(byte[] heights) {
		Arrays.sort(heights);
		return heights[heights.length / 2];
	}
	
	private void addEdge(World world, int x, int y, int z, int worldChunkX, int worldChunkZ, int blocks) {
		for (x = worldChunkX; x < worldChunkX + 16; x++) {
			for (z = worldChunkZ; z < worldChunkZ + 16; z++) {
				if (world.getBlockAt(x, y, z).getType() == Material.STONE
						|| world.getBlockAt(x, y, z).getRelative(BlockFace.NORTH).getType() == Material.STONE
						|| world.getBlockAt(x, y, z).getRelative(BlockFace.SOUTH).getType() == Material.STONE
						|| world.getBlockAt(x, y, z).getRelative(BlockFace.WEST).getType() == Material.STONE
						|| world.getBlockAt(x, y, z).getRelative(BlockFace.EAST).getType() == Material.STONE) {
					world.getBlockAt(x, y, z).setType(Material.BEDROCK);
				}
			}
		}
		for (x = worldChunkX; x < worldChunkX + 16; x++) {
			for (z = worldChunkZ; z < worldChunkZ + 16; z++) {
				if (world.getBlockAt(x, y, z).getType() == Material.BEDROCK) {
					world.getBlockAt(x, y, z).setType(Material.STONE);
				}
			}
		}
		if (blocks > 0) {
			addEdge(world, x, y, z, worldChunkX, worldChunkZ, blocks - 1);
		}
	}

}
