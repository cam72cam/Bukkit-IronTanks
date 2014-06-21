package me.cmesh.IronTanks;

import java.util.*;

import me.cmesh.MegaBlock.MegaBlock;
import me.cmesh.MegaBlock.MegaStructureTemplate;

import org.bukkit.plugin.java.JavaPlugin;

public class IronTanks extends JavaPlugin {
	private TankListener listener;
	
	public void onEnable() {
		listener = new TankListener();
		
		List<MegaStructureTemplate> templates = new ArrayList<MegaStructureTemplate>();
		
		for (int i = 4; i < 8; i++) {
			templates.add(TankGenerator.generateTank(3,i));
			templates.add(TankGenerator.generateTank(5,i));
			templates.add(TankGenerator.generateTank(7,i));
		}
		
		new MegaBlock(this, listener, templates);
	}
}
