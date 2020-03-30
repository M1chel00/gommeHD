package de.msclient.msplugin.events;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.block.EnderChest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.msclient.msplugin.challenge.Challenge;
import de.msclient.msplugin.commands.MSCommandExecuter;import de.msclient.msplugin.guis.GuiInventorys;
import de.msclient.msplugin.main.MSPlugin;

public class WorldListener implements Listener{
	
	private MSPlugin plugin;
	private Difficulty diff = Difficulty.NORMAL;
	private boolean natReg = true;
	private boolean teamLife = false;
	
	
	public WorldListener(MSPlugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void netherPortal(PlayerPortalEvent e) {
		World world = e.getFrom().getWorld();
		if(!world.getName().contains("world")) {
			if(world.getEnvironment().equals(Environment.NETHER) && e.getCause().equals(TeleportCause.NETHER_PORTAL)) {
				e.setTo(new Location(Bukkit.getWorld(world.getName().split("_")[0]), e.getPlayer().getLocation().getX()*8,e.getPlayer().getLocation().getY(),e.getPlayer().getLocation().getZ()*8));
			}else if(world.getEnvironment().equals(Environment.NORMAL) && e.getCause().equals(TeleportCause.NETHER_PORTAL)){
				e.setTo(new Location(Bukkit.getWorld(world.getName().concat("_Nether")), e.getPlayer().getLocation().getX()/8,e.getPlayer().getLocation().getY(),e.getPlayer().getLocation().getZ()/8));
			}else if(world.getEnvironment().equals(Environment.NORMAL) && e.getCause().equals(TeleportCause.END_PORTAL)) {				
				e.setTo(Bukkit.getWorld(world.getName().concat("_End")).getSpawnLocation());
			}
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		ItemStack clickedItem = e.getCurrentItem();
		GuiInventorys gui = MSPlugin.getGuiInv();
		EnchantGlow glow = new EnchantGlow(NamespacedKey.minecraft("70"));
		Inventory inv;
		ItemMeta meta1;
		ItemMeta meta2;
		ItemMeta meta3;
		if (e.getInventory().equals(gui.getChallengeInv())) {
			e.setCancelled(true);			
			if (clickedItem == null || clickedItem.getType() == Material.AIR)
				return;
			if (clickedItem.getType() == Material.DIAMOND_SWORD) {
				player.openInventory(gui.getDifInv());
			} else if (clickedItem.getType() == Material.GOLDEN_APPLE) {
				player.openInventory(gui.getRegInv());
			} else if (clickedItem.getType() == Material.DIAMOND_BOOTS) {
				player.openInventory(gui.getLifeInv());
			} else if (clickedItem.getType() == Material.GREEN_BANNER) {				
				if(!MSPlugin.isChallengeInCreate) {
					player.closeInventory();					
					new Challenge(System.currentTimeMillis()+"", diff, natReg, teamLife, 0);
				}else {
					player.sendMessage(MSPlugin.prefixChallenge + "There is a challenge currently in create!");
				}
			}
		}else if(e.getInventory().equals(gui.getDifInv())) {
			e.setCancelled(true);	
			inv = gui.getDifInv();			
			if (clickedItem == null || clickedItem.getType() == Material.AIR)
				return;
			if (clickedItem.getType() == Material.WOODEN_SWORD) {
				diff = Difficulty.EASY;				
				meta1 = inv.getItem(0).getItemMeta();
				meta2 = inv.getItem(1).getItemMeta();
				meta3 = inv.getItem(2).getItemMeta();
				meta1.addEnchant(glow, 1, true);
				meta2.removeEnchant(glow);
				meta3.removeEnchant(glow);
				inv.getItem(0).setItemMeta(meta1);
				inv.getItem(1).setItemMeta(meta2);
				inv.getItem(2).setItemMeta(meta3);
			} else if (clickedItem.getType() == Material.IRON_SWORD) {
				diff = Difficulty.NORMAL;
				meta1 = inv.getItem(0).getItemMeta();
				meta2 = inv.getItem(1).getItemMeta();
				meta3 = inv.getItem(2).getItemMeta();
				meta2.addEnchant(glow, 1, true);
				meta1.removeEnchant(glow);
				meta3.removeEnchant(glow);
				inv.getItem(0).setItemMeta(meta1);
				inv.getItem(1).setItemMeta(meta2);
				inv.getItem(2).setItemMeta(meta3);
			} else if (clickedItem.getType() == Material.DIAMOND_SWORD) {
				diff = Difficulty.HARD;
				meta1 = inv.getItem(0).getItemMeta();
				meta2 = inv.getItem(1).getItemMeta();
				meta3 = inv.getItem(2).getItemMeta();
				meta3.addEnchant(glow, 1, true);
				meta2.removeEnchant(glow);
				meta1.removeEnchant(glow);
				inv.getItem(0).setItemMeta(meta1);
				inv.getItem(1).setItemMeta(meta2);
				inv.getItem(2).setItemMeta(meta3);
			} else if (clickedItem.getType() == Material.GREEN_BANNER) {
				player.openInventory(gui.getChallengeInv());
			}			
			gui.setDifInv(inv);
		}else if(e.getInventory().equals(gui.getRegInv())) {
			e.setCancelled(true);
			inv = gui.getRegInv();
			if (clickedItem == null || clickedItem.getType() == Material.AIR)
				return;
			if (clickedItem.getType() == Material.GREEN_DYE) {
				if(teamLife) {
					player.sendMessage(MSPlugin.prefixChallenge + "Es ist nicht möglich sowohl gemeinsames Leben als auch natürliche Regeneration zu aktivieren!");
					natReg = false;
					meta1 = inv.getItem(0).getItemMeta();
					meta2 = inv.getItem(1).getItemMeta();
					meta2.removeEnchant(glow);
					meta1.addEnchant(glow, 1, true);;
					inv.getItem(0).setItemMeta(meta1);
					inv.getItem(1).setItemMeta(meta2);
					gui.setRegInv(inv);
					return;
				}
				natReg = true;
				meta1 = inv.getItem(0).getItemMeta();
				meta2 = inv.getItem(1).getItemMeta();								
				meta2.addEnchant(glow, 1, true);
				meta1.removeEnchant(glow);
				inv.getItem(0).setItemMeta(meta1);
				inv.getItem(1).setItemMeta(meta2);
			} else if (clickedItem.getType() == Material.RED_DYE) {
				natReg = false;
				meta1 = inv.getItem(0).getItemMeta();
				meta2 = inv.getItem(1).getItemMeta();
				meta2.removeEnchant(glow);
				meta1.addEnchant(glow, 1, true);;
				inv.getItem(0).setItemMeta(meta1);
				inv.getItem(1).setItemMeta(meta2);		
			} else if (clickedItem.getType() == Material.GREEN_BANNER) {
				player.openInventory(gui.getChallengeInv());
			}
			gui.setRegInv(inv);
		}else if(e.getInventory().equals(gui.getLifeInv())) {
			e.setCancelled(true);
			inv = gui.getLifeInv();
			if (clickedItem == null || clickedItem.getType() == Material.AIR)
				return;
			if (clickedItem.getType() == Material.GREEN_DYE) {
				if(natReg) {
					player.sendMessage(MSPlugin.prefixChallenge + "Es ist nicht möglich sowohl gemeinsames Leben als auch natürliche Regeneration zu aktivieren!");
					teamLife = false;
					meta1 = inv.getItem(0).getItemMeta();
					meta2 = inv.getItem(1).getItemMeta();								
					meta1.addEnchant(glow, 1, true);
					meta2.removeEnchant(glow);
					inv.getItem(0).setItemMeta(meta1);
					inv.getItem(1).setItemMeta(meta2);
					gui.setLifeInv(inv);
					return;
				}
				teamLife = true;
				meta1 = inv.getItem(0).getItemMeta();
				meta2 = inv.getItem(1).getItemMeta();								
				meta2.addEnchant(glow, 1, true);
				meta1.removeEnchant(glow);
				inv.getItem(0).setItemMeta(meta1);
				inv.getItem(1).setItemMeta(meta2);
			} else if (clickedItem.getType() == Material.RED_DYE) {
				teamLife = false;
				meta1 = inv.getItem(0).getItemMeta();
				meta2 = inv.getItem(1).getItemMeta();								
				meta1.addEnchant(glow, 1, true);
				meta2.removeEnchant(glow);
				inv.getItem(0).setItemMeta(meta1);
				inv.getItem(1).setItemMeta(meta2);
			} else if (clickedItem.getType() == Material.GREEN_BANNER) {
				player.openInventory(gui.getChallengeInv());
			}
			gui.setLifeInv(inv);
		}
	}	
	
	@EventHandler
	public void signChange(PlayerInteractEvent e) {
		if (e.getClickedBlock() != null && e.getClickedBlock().getState() != null) {
			if (e.getClickedBlock().getState() instanceof Sign && e.getPlayer() instanceof Player) {
				Sign sign = (Sign) e.getClickedBlock().getState();
				Player player = (Player) e.getPlayer();
				if (sign.getLine(1).contains("[")) {
					String s = sign.getLine(2);
					if(Bukkit.getWorld(s) != null) {
						if (sign.getLine(1).contains("Challenge")) {
							player.setPlayerListName(MSPlugin.prefixChallenge + player.getName());
							player.setDisplayName(MSPlugin.prefixChallenge + player.getName());
						} else if (sign.getLine(1).contains("Survival")) {
							player.setPlayerListName(MSPlugin.prefixSurvival + player.getName());
							player.setDisplayName(MSPlugin.prefixSurvival + player.getName());
						}

						try {
							MSPlugin.saveConfig(player, Bukkit.getWorld(player.getWorld().getName().split("_")[0]));
							player.teleport(Bukkit.getWorld(s).getSpawnLocation());
							e.setCancelled(true);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}else {
						player.sendMessage("[§cError§r] Die Welt konnte nicht gefunden werden!");
						e.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void changeWorld(PlayerChangedWorldEvent e) {
		World from = e.getFrom();		
		Player player = e.getPlayer();
		World to = player.getWorld();
		//checkt, ob man eine Map gewechselt hat und nicht nur den Typ(z.B Nether to End)
		if(!from.getName().split("_")[0].equalsIgnoreCase(to.getName().split("_")[0])) {
			try {
				//MSPlugin.saveConfig(player, from);
				MSPlugin.saveChallengeWorlds();				
				if(to.getName().equalsIgnoreCase("lobby")) {
					resetPlayer(player);
				}else {
					player.setGameMode(GameMode.SURVIVAL);
					MSPlugin.restore(player, to);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}			
		}							
	}	
	
	private void resetPlayer(Player player) {
		player.getInventory().clear();
		player.getEnderChest().clear();
		player.setGameMode(GameMode.ADVENTURE);
		player.setFoodLevel(999999);
		player.setHealth(20);
		player.setExp(0);
		player.setLevel(0);
	}
	
	@EventHandler
	public void quitServer(PlayerQuitEvent e) {		
		Player player = e.getPlayer();
		World w = player.getWorld();
		try {
			MSPlugin.saveConfig(player, Bukkit.getWorld(w.getName().split("_")[0]));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}	
	
	@EventHandler
	public void joinServer(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.teleport(Bukkit.getWorld("lobby").getSpawnLocation());
		p.setPlayerListName(MSPlugin.prefixLobby + p.getName());
		p.setDisplayName(MSPlugin.prefixLobby + p.getName());
		try {
			//MSPlugin.restore(p, p.getWorld());
			resetPlayer(p);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	@EventHandler
	public void damagePlayer(EntityDamageEvent e) {
		if (e.getEntityType().equals(EntityType.PLAYER) && !e.getCause().equals(DamageCause.CUSTOM)) {
			Player player = (Player) e.getEntity();
			String w = player.getWorld().getName().split("_")[0];
			double damage = e.getFinalDamage();
			DamageCause cause = e.getCause();
			Challenge c = MSPlugin.challengeWorldList.get(w);
			if (c == null && w.equalsIgnoreCase("lobby")) {
				e.setCancelled(true);
			} else if (c != null && c.getTeamDamage()) {
				List<Player> playerList = new ArrayList<Player>();
				for (Player p : Bukkit.getWorld(w).getPlayers()) {
					playerList.add(p);
				}
				for (Player p : Bukkit.getWorld(w + "_nether").getPlayers()) {
					playerList.add(p);
				}
				for (Player p : Bukkit.getWorld(w + "_the_end").getPlayers()) {
					playerList.add(p);
				}
				for (Player p : playerList) {
					if(!p.equals(player)) {						
						p.damage(damage);						
					}
					p.sendMessage(MSPlugin.prefixChallenge + player.getName() + " hat " + (int)damage + " Herzen Schaden bekommen! [" + cause + "]");
				}
				c.damageTeam(damage);
				MSPlugin.saveChallengeWorlds();
			} else {
				return;
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void worldInit(WorldInitEvent e) {		
		e.getWorld().setKeepSpawnInMemory(false);
	}	
}
