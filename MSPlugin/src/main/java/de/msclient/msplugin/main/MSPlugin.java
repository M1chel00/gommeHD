package de.msclient.msplugin.main;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import de.msclient.msplugin.challenge.Challenge;
import de.msclient.msplugin.commands.MSCommandExecuter;
import de.msclient.msplugin.events.EnchantGlow;
import de.msclient.msplugin.events.WorldListener;
import de.msclient.msplugin.guis.GuiInventorys;

public class MSPlugin extends JavaPlugin{

	public static final String prefixChallenge = "[§6Challenge§r] ";
	public static final String prefixSurvival = "[§ASurvival§r]"; 
	public static final String prefixLobby = "[§CLobby§r] "; 
	
	private HashMap<String, Player> playerList = new HashMap<>();
	public static HashMap<String, Challenge> challengeWorldList = new HashMap<>();
	public static HashMap<String, Challenge> survivalWorldList = new HashMap<>();
	private static GuiInventorys guiInv;
	public static MSPlugin plugin;	
	public static File dataFolder;	
	public static boolean isChallengeInCreate = false;	
	
	@Override
	public void onDisable() {
		getLogger().info("MSPlugin was disabled!");		
		saveChallengeWorlds();		
		super.onDisable();
	}

	@Override
	public void onEnable() {
		
		plugin = this;
		dataFolder = this.plugin.getDataFolder();
				
		getLogger().info("MSPlugin was enabled!");
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			playerList.put(player.getName(), player);
		}				
		
		restoreChallengeWorlds();
		World lobby = Bukkit.getServer().createWorld(new WorldCreator("lobby"));
		lobby.setDifficulty(Difficulty.PEACEFUL);
		lobby.setKeepSpawnInMemory(false);
		lobby.setSpawnLocation(0, 100, 0);	
		lobby.getBlockAt(-2, 102, 0).setType(Material.BIRCH_SIGN);
		Block block = lobby.getBlockAt(-2, 102, 0);        		
		Sign sign = (Sign)block.getState();       		
		sign.setLine(1, "[Survival]");
		sign.setLine(2, "world");
		sign.setLine(3, "Click to Join!");
		sign.update();
		
		guiInv = new GuiInventorys();
		registerGlow();
		
		this.getServer().getPluginManager().registerEvents(new WorldListener(this), this);		
		
		this.getCommand("msclient").setExecutor(new MSCommandExecuter(this));
		this.getCommand("heal").setExecutor(new MSCommandExecuter(this));
		this.getCommand("challenge").setExecutor(new MSCommandExecuter(this));
		this.getCommand("name").setExecutor(new MSCommandExecuter(this));
		this.getCommand("lobby").setExecutor(new MSCommandExecuter(this));
		this.getCommand("tpw").setExecutor(new MSCommandExecuter(this));
		this.getCommand("timer").setExecutor(new MSCommandExecuter(this));
		this.getCommand("loadWorld").setExecutor(new MSCommandExecuter(this));
		super.onEnable();
	}		
	
	/**
	 * 
	 * @param p Der zu speichernde Spieler
	 * @param w Die Welt auf dem der Spieler sich befand/befindet, aber als Overworld version.
	 * z.B. Spieler war auf world_nether, w = world
	 * @throws IOException
	 */
	public static void saveConfig(Player p, World w) throws IOException {
		YamlConfiguration c = new YamlConfiguration();
		c.set("inventory.armor", p.getInventory().getArmorContents());
		c.set("inventory.content", p.getInventory().getContents());
		c.set("ec.content", p.getEnderChest().getContents());
		c.set("player.life", p.getHealth());
		c.set("player.food", p.getFoodLevel());
		c.set("player.xp", (double)p.getExp());
		c.set("player.level", p.getLevel());
		if(p.getWorld().getName().contains("_"))
			c.set("world.type", p.getWorld().getName().split("_", 2)[1]);
		else
			c.set("world.type", "world");

		if (p.getBedSpawnLocation() == null) {
			c.set("world.spawnLoc.x", w.getSpawnLocation().getX());
			c.set("world.spawnLoc.y", w.getSpawnLocation().getY());
			c.set("world.spawnLoc.z", w.getSpawnLocation().getZ());
		} else {
			c.set("world.spawnLoc.x", p.getBedSpawnLocation().getX());
			c.set("world.spawnLoc.y", p.getBedSpawnLocation().getY());
			c.set("world.spawnLoc.z", p.getBedSpawnLocation().getZ());
		}

		c.set("position.x", p.getLocation().getX());
		c.set("position.y", p.getLocation().getY());
		c.set("position.z", p.getLocation().getZ());		//TODO evtl noch facing		
		c.save(new File(dataFolder, p.getName() + "_" + w.getName() + ".yml"));
	}
	
    public static void restore(Player p, World w) throws IOException {
		File f = new File(dataFolder, p.getName() + "_" + w.getName() + ".yml");
		if (f.exists()) {
			YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
			ItemStack[] content = ((List<ItemStack>) c.get("inventory.armor")).toArray(new ItemStack[0]);
			p.getInventory().setArmorContents(content);
			content = ((List<ItemStack>) c.get("inventory.content")).toArray(new ItemStack[0]);
			p.getInventory().setContents(content);
			content = ((List<ItemStack>) c.get("ec.content")).toArray(new ItemStack[0]);
			p.getEnderChest().setContents(content);
			double life = c.getDouble("player.life");
			p.setHealth(life);
			int food = c.getInt("player.food");
			p.setFoodLevel(food);
			float xp = (float) c.getDouble("player.xp");
			p.setExp(xp);
			int level = c.getInt("player.level");
			p.setLevel(level);

			String world = c.getString("world.type").equalsIgnoreCase("world") ? w.getName()
					: w.getName() + "_" + c.getString("world.type");

			p.setBedSpawnLocation(new Location(Bukkit.getWorld(world), c.getDouble("world.spawnLoc.x"),
					c.getDouble("world.spawnLoc.y"), c.getDouble("world.spawnLoc.z")), true);

			Location l = new Location(Bukkit.getWorld(world), c.getDouble("position.x"), c.getDouble("position.y"),
					c.getDouble("position.z"));
			p.teleport(l);
		}else {
			System.out.println("Es wurde keine Config für den Spieler auf der Map gefunden!");
			p.setBedSpawnLocation(w.getSpawnLocation(), true);
			p.setHealth(20);
			p.setFoodLevel(20);
			p.setLevel(0);
			p.setExp(0);
			p.getEnderChest().clear();
			p.getInventory().clear();
		}
		Challenge c = MSPlugin.challengeWorldList.get(w.getName());
		if(c != null && c.getTeamDamage()) {
			p.setHealth(c.getTeamHealth());
		}
		
    }	
    
    public static void saveChallengeWorlds() {
		YamlConfiguration c = new YamlConfiguration();
		Iterator it = challengeWorldList.entrySet().iterator();		
		while(it.hasNext()) {
			Map.Entry m = (Map.Entry) it.next();	
			c.set("worlds." + m.getKey() + ".map", (String) m.getKey());
			c.set("worlds." + m.getKey() + ".timer", (long)((Challenge)m.getValue()).getTimer());
			c.set("worlds." + m.getKey() + ".damage", ((Challenge)m.getValue()).getTeamDamage());
			c.set("worlds." + m.getKey() + ".reg", ((Challenge)m.getValue()).getNatReg());
			c.set("worlds." + m.getKey() + ".diff", ((Challenge)m.getValue()).getDifficulty().name());
			c.set("worlds." + m.getKey() + ".life", (double)((Challenge)m.getValue()).getTeamHealth());
		}		
		try {			
			c.save(new File(dataFolder, "worlds.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void restoreChallengeWorlds() {
		try {
			YamlConfiguration c = YamlConfiguration.loadConfiguration(new File(dataFolder, "worlds.yml"));
			Map<String, Object> map = c.getValues(true);			
			for(String s : map.keySet()) {				
				if(s.endsWith("map")) {		//TODO stellt Schilder mit gleichen ids auf			
					String str = c.getString(s);														
					long timer = c.getLong("worlds." + str + ".timer"); 
					boolean damage = c.getBoolean("worlds." + str + ".damage");
					boolean reg = c.getBoolean("worlds." + str + ".reg");
					String diff = (String) c.get("worlds." + str + ".diff");
					Difficulty d;
					if(diff.contains("PEACE"))
						d = Difficulty.PEACEFUL;
					else if(diff.contains("EASY"))
						d = Difficulty.EASY;
					else if(diff.contains("NORMAL"))
						d = Difficulty.NORMAL;
					else 
						d = Difficulty.HARD;
					double life = c.getDouble("worlds." + str + ".life");					
					new Challenge(str, d, reg, damage, timer, life);				
				}		
				System.out.println("String: " + s);
			}
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("SHIT");
		}
	}    
	
	public void registerGlow() {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            EnchantGlow glow = new EnchantGlow(NamespacedKey.minecraft("70"));
            Enchantment.registerEnchantment(glow);
        }
        catch (IllegalArgumentException e){
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
	
	public static GuiInventorys getGuiInv() {
		return guiInv;
	}
}