package de.msclient.msplugin.challenge;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.material.Directional;

import de.msclient.msplugin.main.MSPlugin;

import org.bukkit.World.Environment;

public class Challenge {
	
	private long timer;	
	private int taskID;
	private final String id;
	private boolean isRunning;
	private HashMap<String, Player> playerList = new HashMap<>();
	private boolean teamDamage;
	private boolean natReg;
	private Difficulty diff;	
	private double healthTeam;

	public Challenge(String id, Difficulty diff, boolean natReg, boolean teamLife, long timer) {
		this.id = id;
		this.diff = diff;
		this.teamDamage = teamLife;
		this.natReg = natReg;
		this.timer = timer;
		if(this.teamDamage)
			this.healthTeam = 20;
		else
			this.healthTeam = -1;			
		Challenge c = this;
		Bukkit.getScheduler().runTask(MSPlugin.plugin, new Runnable() {
            @Override
            public void run() {
            	MSPlugin.isChallengeInCreate = true;            	
            	Bukkit.broadcastMessage(MSPlugin.prefixChallenge + "Eine Challenge wird erstellt! Hierbei kann es zu kleinen Lags kommen. [0/3]");
            	World world = Bukkit.getServer().createWorld(new WorldCreator(id + ""));
        		world.setDifficulty(diff);
        		world.setGameRule(GameRule.NATURAL_REGENERATION, natReg);        		
        		Bukkit.broadcastMessage(MSPlugin.prefixChallenge + "Eine Challenge wird erstellt! Hierbei kann es zu kleinen Lags kommen. [1/3]");
        		World nether = Bukkit.getServer().createWorld(new WorldCreator(id + "_nether").environment(Environment.NETHER));
        		nether.setDifficulty(diff);
        		nether.setGameRule(GameRule.NATURAL_REGENERATION, natReg);
        		Bukkit.broadcastMessage(MSPlugin.prefixChallenge + "Eine Challenge wird erstellt! Hierbei kann es zu kleinen Lags kommen. [2/3]");
        		World end = Bukkit.getServer().createWorld(new WorldCreator(id + "_the_end").environment(Environment.THE_END));
        		end.setDifficulty(diff);
        		end.setGameRule(GameRule.NATURAL_REGENERATION, natReg);
        		Bukkit.broadcastMessage(MSPlugin.prefixChallenge + "Die Challenge wurde erstellt! [3/3]");
        		
        		MSPlugin.challengeWorldList.put(id + "", c);
        		int size = MSPlugin.challengeWorldList.size()-1;
        		Bukkit.getWorld("lobby").getBlockAt(size, 102, 0).setType(Material.BIRCH_SIGN);
        		Block block = Bukkit.getWorld("lobby").getBlockAt(size, 102, 0);        		
        		Sign sign = (Sign)block.getState();       		
        		sign.setLine(1, "[Challenge]");
        		sign.setLine(2, "" + id);
        		sign.setLine(3, "Click to Join!");
        		sign.update();
        		Bukkit.broadcastMessage(MSPlugin.prefixChallenge + "Ein Schild wurde bei " + size + " 102 0 mit der id: " + id + " zum Joinen erstellt!");
        		
        		MSPlugin.isChallengeInCreate = false;
            }
		});				
	}
	
	public Challenge(String id, Difficulty diff, boolean natReg, boolean teamLife, long timer, double teamHealth) {
		this(id, diff, natReg, teamLife, timer);
		this.healthTeam = teamHealth;
	}
	
	/**
	 * This function is only for restoring a challenge.
	 * Don't use this to create a new challenge!!!
	 * @param id
	 * @param timer
	 */
	public Challenge(String id, long timer) {		
		this.timer = timer;
		this.id = id;
		Bukkit.getScheduler().runTask(MSPlugin.plugin, new Runnable() {
            @Override
            public void run() {
            	Bukkit.getServer().createWorld(new WorldCreator(id+""));
            	Bukkit.getServer().createWorld(new WorldCreator(id+"_nether").environment(Environment.NETHER));
            	Bukkit.getServer().createWorld(new WorldCreator(id+"_the_end").environment(Environment.THE_END));
            	System.out.println(MSPlugin.prefixChallenge + "Eine Challenge mit id: " + id + " und timer: " + timer + " wurde geladen!");
            }
		});
		MSPlugin.challengeWorldList.put(id, this);
	}
	
	public void startTimer() {
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(MSPlugin.plugin, new Runnable() {
			
			@Override
			public void run() {					
				timer++;				
			}
		}, 0, 20 * 1);
		this.setRunning(true);
		for(Player p : playerList.values()) {
			p.sendMessage("The timer was started!");
		}
	}
	
	public void stopTimer() {
		Bukkit.getScheduler().cancelTask(taskID);
		for(Player p : playerList.values()) {
			p.sendMessage("The timer was stopped!");
		}
		this.setRunning(false);
	}
	
	public boolean isRunning() {
		return this.isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public long getTimer() {
		return this.timer;
	}
	
	public String getID() {
		return this.id;
	}
	
	public boolean getTeamDamage() {
		return this.teamDamage;
	}

	public boolean getNatReg() {
		return this.natReg;
	}
	
	public Difficulty getDifficulty() {
		return this.diff;
	}
	
	public double getTeamHealth() {
		return this.healthTeam;
	}
	
	public void damageTeam(double d) {
		this.healthTeam -= d;
	}
}
