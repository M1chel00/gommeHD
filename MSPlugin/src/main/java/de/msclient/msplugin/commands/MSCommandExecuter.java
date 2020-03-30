package de.msclient.msplugin.commands;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.msclient.msplugin.challenge.Challenge;
import de.msclient.msplugin.guis.GuiInventorys;
import de.msclient.msplugin.main.MSPlugin;

public class MSCommandExecuter implements CommandExecutor{

	private final MSPlugin plugin;
	
	public MSCommandExecuter(MSPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("msclient")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
			}else {
				sender.sendMessage("This command can only be run by a player.");
			}
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("heal")) {
			if(args.length != 2) {
				return false;
			}
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if(!player.hasPermission("msclient.heal"))
					return false;
			}
			Player target = (Bukkit.getServer().getPlayer(args[0]));
			if (target == null) {
				sender.sendMessage(args[0] + " is not online!");
				return false;
			}
			try {				
				target.setHealth(Double.valueOf(args[1]));
			}catch(Exception e) {
				this.plugin.getLogger().warning(e.getMessage());
				sender.sendMessage("You're second argument needs to be an Integer!");
				return false;
			}
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("challenge")) {
			if(sender instanceof Player) {				
				Player player = (Player) sender;
				if(!player.hasPermission("challenge.create"))
					return false;
				if(MSPlugin.isChallengeInCreate) {
					player.sendMessage(MSPlugin.prefixChallenge + "One Challenge is currently in create!");
					return true;
				}					
				player.openInventory(MSPlugin.getGuiInv().getChallengeInv());	
				
				return true;
			}
			return false;
		}
		if(cmd.getName().equalsIgnoreCase("name")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if(!player.hasPermission("msclient.name"))
					return false;
				player.sendMessage(player.getLocation().getWorld().getName());				
				return true;
			}
			return false;
		}
		if(cmd.getName().equalsIgnoreCase("lobby")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				player.setPlayerListName(MSPlugin.prefixLobby + player.getName());
				player.setDisplayName(MSPlugin.prefixLobby + player.getName());
				try {
					MSPlugin.saveConfig(player, Bukkit.getWorld(player.getWorld().getName().split("_")[0]));
				} catch (IOException e) {
					e.printStackTrace();
				}
				player.teleport(Bukkit.getWorld("lobby").getSpawnLocation());																	
				return true;
			}
			return false;
		}
		if(cmd.getName().equalsIgnoreCase("tpw")) {
			if(sender instanceof Player && args.length == 1) {
				Player player = (Player) sender;
				if(player.hasPermission("msclient.tpw")) {
					try{
						MSPlugin.saveConfig(player, Bukkit.getWorld(player.getWorld().getName().split("_")[0]));
						player.teleport(Bukkit.getWorld(args[0]).getSpawnLocation());
					}catch(Exception e) {
						player.sendMessage("This world does not exists!");
					}
					return true;
				}					
			}
			return false;
		}
		if(cmd.getName().equalsIgnoreCase("timer")) {	//TODO automatisch Timer stoppen, wenn der letzte die welt verlässt
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if(!player.hasPermission("challenge.timer"))
					return false;
																		
				String world = player.getWorld().getName().split("_")[0];
				if(MSPlugin.challengeWorldList.containsKey(world)) {
					Challenge ch = MSPlugin.challengeWorldList.get(world);
					if(args.length == 0) {
						player.sendMessage(MSPlugin.prefixChallenge + "Seit §l" + (ch.getTimer()/60)/60 + "§r Stunden §l"
								+ (ch.getTimer()/60)%60 + "§r Minuten §l" 
								+ ch.getTimer()%60 + "§r Sekunden in der Challenge!");
					}else if(args.length == 1 && ch.isRunning() && args[0].equalsIgnoreCase("stop")) {
						ch.stopTimer();
						player.sendMessage(MSPlugin.prefixChallenge + "Der Timer wurde bei " + (ch.getTimer()/60)/60 + "§r Stunden §l"
								+ (ch.getTimer()/60)%60 + "§r Minuten §l" 
								+ ch.getTimer()%60 + "§r Sekunden gestoppt!");
					}else if(args.length == 1 && !ch.isRunning() && args[0].equalsIgnoreCase("start")) {
						ch.startTimer();
						player.sendMessage(MSPlugin.prefixChallenge + "Der Timer wurde gestartet!");
					}else {						
						return false;
					}
					return true;
				}
				player.sendMessage(MSPlugin.prefixChallenge + "Du befindest dich in keiner Challenge!");
				return true;
			}
			return false;
		}
		if(cmd.getName().equalsIgnoreCase("loadWorld")) {			
			if(args.length == 1) {
				if (sender instanceof Player) {
					Player p = (Player) sender;
					if (!p.hasPermission("msclient.load"))
						return false;			
					try {
						Bukkit.getServer().createWorld(new WorldCreator(args[0] + ""));	
						p.sendMessage(MSPlugin.prefixChallenge + "Die Map "+ args[0] + " wurde erfolgreich geladen!");						
					} catch (Exception e) {
						p.sendMessage(MSPlugin.prefixChallenge + "Beim Laden der Map "+ args[0] + " ist ein Fehler aufgetreten!");
						e.printStackTrace();						
					}
					return true;
				}
				try {
					Bukkit.getServer().createWorld(new WorldCreator(args[0] + ""));		
					System.out.println(MSPlugin.prefixChallenge + "Die Map "+ args[0] + " wurde erfolgreich geladen!");
				} catch (Exception e) {
					System.out.println(MSPlugin.prefixChallenge + "Beim Laden der Map "+ args[0] + " ist ein Fehler aufgetreten!");
					e.printStackTrace();
				}
				return true;
			}
			return false;
		}
		return false;
	}		
	
}
