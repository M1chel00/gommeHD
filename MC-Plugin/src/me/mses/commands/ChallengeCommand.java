package me.mses.commands;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;

import me.mses.main.SpigotPlugin;

public class ChallengeCommand implements CommandExecutor{	

	//nicht gut
	public static String map_name;
	private World world,nether, end;
	private boolean run = true;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {		
		if(sender instanceof Player && args.length == 1) {
			Player p = (Player) sender;
			
			if(args[0].equalsIgnoreCase("start")) {
				if(!p.hasPermission("mses.challenge.start")) {
					p.sendMessage("[§6Challenge§r] Du verfügst nicht über genügend Rechte um die Challenge zu starten!");
					return true;
				}
				if(SpigotPlugin.getInstance().getRun()) {
					Bukkit.broadcastMessage("[§6Challenge§r] Die Challenge ist bereits am Laufen!");
					return true;
				}
				map_name = "" + System.currentTimeMillis();
								
				//Bukkit.broadcastMessage("Die Challenge startet in Kürze!");
				Bukkit.getWorld("world").setDifficulty(Difficulty.HARD);
				Bukkit.getWorld("world").setGameRule(GameRule.NATURAL_REGENERATION, false);
				
				Bukkit.getWorld("world_nether").setDifficulty(Difficulty.HARD);
				Bukkit.getWorld("world_nether").setGameRule(GameRule.NATURAL_REGENERATION, false);
				
				Bukkit.getWorld("world_the_end").setDifficulty(Difficulty.HARD);
				Bukkit.getWorld("world_the_end").setGameRule(GameRule.NATURAL_REGENERATION, false);
								
				//world = Bukkit.createWorld(new WorldCreator(map_name).environment(Environment.NORMAL));								
				
				SpigotPlugin.getInstance().setRun(true);
				SpigotPlugin.getInstance().setTimer(0);
				/*Bukkit.getWorld(map_name).setDifficulty(Difficulty.HARD);
				Bukkit.getWorld(map_name).setGameRule(GameRule.NATURAL_REGENERATION, false);
				boolean c1 = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv import " + world.getName() + " normal");
				if(c1) {
					Bukkit.broadcastMessage("Welt [1/3] geladen!");
				}
				
				nether = Bukkit.createWorld(new WorldCreator(map_name + "_nether").environment(Environment.NETHER));
				
				Bukkit.getWorld(nether.getName()).setDifficulty(Difficulty.HARD);
				Bukkit.getWorld(nether.getName()).setGameRule(GameRule.NATURAL_REGENERATION, false);
				boolean c2 = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv import " + nether.getName() + " nether");
				if(c1) {
					Bukkit.broadcastMessage("Welt [2/3] geladen!");
				}				
				
				end = Bukkit.createWorld(new WorldCreator(map_name + "_the_end").environment(Environment.THE_END));
				
				Bukkit.getWorld(end.getName()).setDifficulty(Difficulty.HARD);
				Bukkit.getWorld(end.getName()).setGameRule(GameRule.NATURAL_REGENERATION, false);
				boolean c3 = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv import " + end.getName() + " end");
				if(c1) {
					Bukkit.broadcastMessage("Welt [3/3] geladen!");
				}
				
				String s1 = null,s2 = null;
				if(c1 && c2 && c3) {
					s1 = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvnpl nether " + world.getName() + " " + nether.getName()) ? "Das Netherportal wurde gelinkt!" : "Das Netherportal wurde nicht gelinkt";
					s2 = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvnpl end " + world.getName() + " " + end.getName()) ? "Das Endportal wurde gelinkt!" : "Das Endportal wurde nicht gelinkt";
				}
				System.out.println();
				System.out.println(s1);
				System.out.println(s2);
				System.out.println();
				*/
				System.out.println("§6[Challenge§r] Die Challenge wurde gestartet!");
				Bukkit.broadcastMessage("[§6Challenge§r] Die Challenge wurde gestartet!");
				for(Player i : SpigotPlugin.getInstance().getPlayers()) {
					System.out.println(i.getName());
					i.setGameMode(GameMode.SURVIVAL);
					i.getInventory().clear();
					i.setFoodLevel(20);
					i.setExp(0);					
					i.setHealth(20);
					//i.teleport(Bukkit.getWorld(world.getName()).getSpawnLocation());
				}
				int temp = Bukkit.getScheduler().scheduleSyncRepeatingTask(SpigotPlugin.getInstance(), new Runnable() {
					
					@Override
					public void run() {						
						SpigotPlugin.getInstance().addTimer();						
					}
				}, 0, 20 * 1);
				SpigotPlugin.getInstance().setTaskID(temp);
			}else if(args[0].equalsIgnoreCase("stop")) {
				if(!p.hasPermission("mses.challenge.stop")) {
					p.sendMessage("[§6Challenge§r] Du verfügst nicht über genügend Rechte um die Challenge zu stoppen!");
					return true;
				}
				if(!SpigotPlugin.getInstance().getRun()) {
					Bukkit.broadcastMessage("[§6Challenge§r] Die Challenge wurde noch nicht gestartet!");
					return true;
				}
				if(Bukkit.getScheduler().isCurrentlyRunning(SpigotPlugin.getInstance().getTaskID())) {
					Bukkit.getScheduler().cancelTask(SpigotPlugin.getInstance().getTaskID());
				}
				SpigotPlugin.getInstance().setRun(false);				
				for(Player i : SpigotPlugin.getInstance().getPlayers()) {
					i.setGameMode(GameMode.SURVIVAL);
					i.setHealth(20);
					i.setFoodLevel(20);
					//i.teleport(Bukkit.getWorld(SpigotPlugin.getInstance().getMapName()).getSpawnLocation());
				}
				/*
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvdelete " + world.getName());
				String s1 = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvconfirm") ? "World wurde gelöscht!" : "World konnte nicht gelöscht werden!";
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvdelete " + nether.getName());
				String s2 = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvconfirm") ? "Nether wurde gelöscht!" : "Nether konnte nicht gelöscht werden!";				
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvdelete " + end.getName());
				String s3 = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvconfirm") ? "End wurde gelöscht!" : "End konnte nicht gelöscht werden!";				
				
				System.out.println();
				System.out.println(s1);
				System.out.println(s2);
				System.out.println(s3);
				System.out.println();
				*/
				System.out.println("[§6Challenge§r] Die Challenge wurde gestoppt!");				
				Bukkit.broadcastMessage("[§6Challenge§r] Die Challenge wurde gestoppt!");
				Bukkit.broadcastMessage("[§6Challenge§r] Ihr habt §l" + (SpigotPlugin.getInstance().getTimer()/60)/60 + "§r Stunden §l"
						+ (SpigotPlugin.getInstance().getTimer()/60)%60 + "§r Minuten §l" 
						+ SpigotPlugin.getInstance().getTimer()%60 + "§r Sekunden gebraucht!");
			}else {
				return false;
			}
		}
		return true;
	}
}
