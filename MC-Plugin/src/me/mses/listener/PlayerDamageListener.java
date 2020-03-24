package me.mses.listener;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffectType;

import me.mses.commands.ChallengeCommand;
import me.mses.main.SpigotPlugin;
import me.mses.util.config;

public class PlayerDamageListener implements Listener {
	
	@EventHandler
	public void playerDeath(PlayerDeathEvent e) {		
		if(e.getEntityType().equals(EntityType.PLAYER) && SpigotPlugin.getInstance().getRun()) {			
			Player p = e.getEntity();
			String t = e.getDeathMessage();
			e.setDeathMessage("");
			Bukkit.broadcastMessage("[§6Challenge§r] Die Challenge wurde verloren!");
			Bukkit.broadcastMessage("[§6Challenge§r] " + t);
			Bukkit.broadcastMessage("[§6Challenge§r] Ihr habt §l" + (SpigotPlugin.getInstance().getTimer()/60)/60 + "§r Stunden §l"
					+ (SpigotPlugin.getInstance().getTimer()/60)%60 + "§r Minuten §l" 
					+ SpigotPlugin.getInstance().getTimer()%60 + "§r Sekunden gebraucht!");			
			Bukkit.getScheduler().cancelTask(SpigotPlugin.getInstance().getTaskID());
			for(Player i : SpigotPlugin.getInstance().getPlayers()) {
				i.setGameMode(GameMode.SPECTATOR);				
			}
		}
	}
	
	@EventHandler
	public void playerSneak(PlayerToggleSneakEvent e) {		
		if(SpigotPlugin.getInstance().getRun() && !config.isAllowedSneak() && !e.getPlayer().isSneaking()) {
			for(Player p : SpigotPlugin.getInstance().getPlayers()) {
				if(p.getDisplayName().equalsIgnoreCase(e.getPlayer().getDisplayName())) {
					//Bukkit.broadcastMessage("[Challenge] Der Spieler " + e.getPlayer().getDisplayName() + " hat gesneaked!");
				}
			}			
		}
	}
	
	private ArrayList<UUID> prevPlayersOnGround = new ArrayList<>();
    
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (player.getVelocity().getY() > 0) {
            double jumpVelocity = (double) 0.42F;
            if (player.hasPotionEffect(PotionEffectType.JUMP)) {
                jumpVelocity += (double) ((float) (player.getPotionEffect(PotionEffectType.JUMP).getAmplifier() + 1) * 0.1F);
            }
            if (e.getPlayer().getLocation().getBlock().getType() != Material.LADDER && prevPlayersOnGround.contains(player.getUniqueId())) {
                if (!player.isOnGround() && Double.compare(player.getVelocity().getY(), jumpVelocity) == 0) {
                    //player.sendMessage("Jumping!");
                }
            }
        }
        if (player.isOnGround()) {
            prevPlayersOnGround.add(player.getUniqueId());
        } else {
            prevPlayersOnGround.remove(player.getUniqueId());
        }
    }
	/*
	@EventHandler
	public void playerSneak(PlayerMoveEvent e) {		
		if(SpigotPlugin.getInstance().getRun() && config.isAllowedJump() && !e.getPlayer().isOnGround()) {
			for(Player p : SpigotPlugin.getInstance().getPlayers()) {
				if(p.getDisplayName().equalsIgnoreCase(e.getPlayer().getDisplayName())) {
					Bukkit.broadcastMessage("[Challenge] Der Spieler " + e.getPlayer().getDisplayName() + " ist gesprungen!");
				}
			}			
		}
	}
	*/
	
	@EventHandler
	public void playerDamage(EntityDamageEvent e) {
		try {
		if(SpigotPlugin.getInstance().getRun() && e.getEntityType().equals(EntityType.PLAYER) && !e.getCause().equals(DamageCause.CUSTOM)) {			
			Player p = (Player) e.getEntity();			
			Bukkit.broadcastMessage("[§6Challenge§r] §b" + p.getDisplayName() + "§r hat §4" + e.getFinalDamage() + "§r Schaden bekommen! (" + e.getCause().toString() + ")");
			for(Player i : SpigotPlugin.getInstance().getPlayers()) {				
				if(i.getUniqueId().compareTo(p.getUniqueId()) != 0) {
					if(i.isOnline())
						i.damage(e.getFinalDamage());					
				}
			}
			SpigotPlugin.getInstance().setHealth(p.getHealth()-e.getFinalDamage());
		}
		}catch(NullPointerException ex) {
			ex.printStackTrace();
			ex.getMessage();
		}
	}
	
	@EventHandler
	public void joinPlayer(PlayerJoinEvent e) {
		Player p = (Player) e.getPlayer();	
		SpigotPlugin.getInstance().addPlayer(p);
		if(SpigotPlugin.getInstance().getRun())
			p.setHealth(SpigotPlugin.getInstance().getHealth());			
	}
	
	@EventHandler
	public void leavePlayer(PlayerQuitEvent e) {
		Player p = (Player) e.getPlayer();		
		SpigotPlugin.getInstance().removePlayer(p);
	}
	
}
