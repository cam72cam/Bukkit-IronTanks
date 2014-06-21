package me.cmesh.IronTanks;

import me.cmesh.MegaBlock.MegaStructureTemplate;

import org.bukkit.Material;

public class TankGenerator {

	private static Material[] casing = { Material.IRON_BLOCK };
	private static Material[] casingorwindow = { Material.IRON_BLOCK, Material.IRON_FENCE, Material.GLASS };
	private static Material[] air = { Material.AIR };
	
	private static Material[][][] generateTop(int size) {
		Material [][][] res = new Material [size][size][];
		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				res[i][j] = casingorwindow;
			}
		}
		
		for (int i = 0; i < size; i++) {
			res[i][0] = casing;
			res[i][size-1] = casing;
			res[0][i] = casing;
			res[size-1][i] = casing;
		}
		
		return res;
	}
	
	private static Material[][][] generateWall(int size) {
		Material [][][] res = new Material [size][size][];
		
		//Fill Air
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				res[i][j] = air;
			}
		}
		
		//Walls:
		for (int i = 0; i < size; i++) {
			res[i][0] = casingorwindow;
			res[i][size-1] = casingorwindow;
			res[0][i] = casingorwindow;
			res[size-1][i] = casingorwindow;
		}
		
		//four corners
		res[0][0] = casing;
		res[0][size-1] = casing;
		res[size-1][0] = casing;
		res[size-1][size-1] = casing;
		
		return res;
	}
	
	public  static MegaStructureTemplate generateTank(int width, int height) {
		Material [][][][] struct = new Material[height][width][width][];
		
		Material[][][] top = generateTop(width);
		Material[][][] wall = generateWall(width);
		
		struct[0] = top;
		
		for(int i = 1; i + 1 < height; i++) {
			struct[i] = wall;
		}
		
		struct[height - 1] = top;
		
		return new MegaStructureTemplate(struct);
	}
}
