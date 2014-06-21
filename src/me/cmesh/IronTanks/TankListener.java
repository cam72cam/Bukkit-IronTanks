package me.cmesh.IronTanks;

import java.util.*;

import me.cmesh.MegaBlock.MegaListener;
import me.cmesh.MegaBlock.MegaStructure;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

public class TankListener implements MegaListener {
	//TODO remove some of these needless? helper functions 
	
	
	private boolean isLava(Material m) {
		return stationary(m) == Material.STATIONARY_LAVA;
	}
	
	private boolean isWater(Material m) {
		return stationary(m) == Material.STATIONARY_WATER;
	}
	
	private Material stationary(Material m) {
		switch (m) {
			case STATIONARY_LAVA:
			case LAVA:
				return Material.STATIONARY_LAVA;
			case STATIONARY_WATER:
			case WATER:
				return Material.STATIONARY_WATER;
			default: return null;
		}
	}
	private Material[] allTypes(Material m) {
		switch (m) {
			case STATIONARY_LAVA:
			case LAVA:
				Material [] resl = { Material.STATIONARY_LAVA, Material.LAVA };
				return resl;
			case STATIONARY_WATER:
			case WATER:
				Material [] resw = { Material.STATIONARY_WATER, Material.WATER };
				return resw;
			default: return null;
		}
	}
	
	
	//Should only possibly return null during create (hopefully) 
	private Material getTankLiquid(MegaStructure struct) {
		boolean containsWater = struct.containsType(Material.STATIONARY_WATER);
		boolean containsLava = struct.containsType(Material.STATIONARY_LAVA);
		if (containsWater && containsLava) {
			return null;
		}
		return containsWater ? Material.STATIONARY_WATER :
				containsLava ? Material.STATIONARY_LAVA :
				Material.AIR;
	}
	
	private Material liquidType(Material bucket) {
		return  bucket == Material.WATER_BUCKET ? Material.STATIONARY_WATER :
				bucket == Material.LAVA_BUCKET  ? Material.STATIONARY_LAVA  :
				null;
	}
	private Material bucketType(Material liquid) {
		return  isWater(liquid) ? Material.WATER_BUCKET :
				isLava(liquid) ? Material.LAVA_BUCKET  :
				null;
	}
	
	private void openTankGui(Player p) {
		p.sendMessage("GUI TODO!");
	}
	
	@SuppressWarnings("deprecation")
	private boolean addToTank(MegaStructure struct, Material liquid) {
		List<Material> set = Arrays.asList(allTypes(liquid));
		
		for (List<Block> level : struct.blockLevels()) {
			//Check for empty spaces
			for(Block block : level) {
				if (block.getType() == Material.AIR) {
					block.setType(stationary(liquid));
					block.setData((byte) 7);
					return true;
				}
			}
			//No more empty spaces
			
			//Find liquid blocks
			List<Block> list = new ArrayList<Block>();
			for (Block b : level) {
				if (set.contains(b.getType())) {
					list.add(b);
				}
			}
			
			//emptiest (7) to fullest (0)
			for(byte target = 7; target > 0; target--) {
				for(Block block : list) {
					byte data = block.getData();
					if (data == target){
						//we are not at the hightest level yet
						block.setData((byte) (data - 1));
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	private boolean removeFromTank(MegaStructure struct, Material liquid) {
		List<Material> set = Arrays.asList(allTypes(liquid));
		
		//Check each level from the top down
		for (List<Block> level : Lists.reverse(struct.blockLevels())) {
			//Find liquid blocks
			List<Block> list = new ArrayList<Block>();
			for (Block b : level) {
				if (set.contains(b.getType())) {
					list.add(b);
				}
			}
			
			//Check for reducible blocks
			//fullest (0) to not including emptiest (7)
			for(byte target = 0; target < 7; target ++) {
				for(Block block : list) {
					//Bukkit.broadcastMessage("Check " + target + " at " + block.getLocation());
					byte data = block.getData();
					if (data == target){
						block.setData((byte) (data + 1));
						return true;
					}
				}
			}
			
			//Handle almost gone level (7)
			for(Block block : list) {
				byte data = block.getData();
				if (data == 7){
					block.setType(Material.AIR);
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public void onClick(Event<PlayerInteractEvent> ev) {
		ItemStack bucket = ev.event.getItem();
		Player p = ev.event.getPlayer();
		
		if (p.isSneaking() || ev.event.getAction() == Action.LEFT_CLICK_BLOCK) {
			return;
		}
		
		ev.event.setCancelled(true);
		
		if (ev.event.getAction() == Action.RIGHT_CLICK_BLOCK && ev.event.getItem() != null) {
			Material tankLiquid = getTankLiquid(ev.structure);
			if (liquidType(bucket.getType()) != null) {
				Material bucketLiquid = liquidType(bucket.getType());
				
				if (tankLiquid != bucketLiquid && tankLiquid != Material.AIR) {
					openTankGui(p);
					return;
				}
				
				if (addToTank(ev.structure, bucketLiquid)) {
					if (p.getGameMode() != GameMode.CREATIVE) {
						ev.event.getItem().setType(Material.BUCKET);
					}
				}
			} else {
				//Empty bucket
				
				if (tankLiquid == Material.AIR || tankLiquid == null) {
					p.sendMessage("Tank is empty");
					return;
				}
				
				removeFromTank(ev.structure, tankLiquid);

				if (p.getGameMode() != GameMode.CREATIVE) {
					ev.event.getItem().setType(bucketType(tankLiquid));
				}
			}
		} else {
			openTankGui(p);
		}
	}

	@Override
	public void onCreate(Event<BlockPlaceEvent> ev) {
		ev.event.getPlayer().sendMessage("Tank Created");
	}

	@Override
	public void onBreak(Event<BlockBreakEvent> ev) {
		ev.event.getPlayer().sendMessage("Tank Destroyed");
		for (Block block : ev.structure.blocksByMaterial(getTankLiquid(ev.structure))) {
			block.setType(Material.AIR);
		}
	}
}
