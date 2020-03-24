package me.mses.main;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import me.mses.commands.BackPack;
import me.mses.commands.ChallengeCommand;
import me.mses.commands.Health;
import me.mses.commands.Timer;
import me.mses.commands.addPlayerLate;
import me.mses.commands.callPos;
import me.mses.listener.PlayerDamageListener;

public class SpigotPlugin extends JavaPlugin{

	private static SpigotPlugin instance;
	private final String NAME = "1.14 Survival Plugin";
	private final String MAP_NAME = "world";	
	private final String NETHER_NAME = "world_nether";	
	private final String END_NAME = "world_the_end";
	private int timer = 0;
	private int taskID;
	private boolean run = false;
	private static double health = 20; 
	private ArrayList<Player> players = new ArrayList<>();
	private Inventory bp = Bukkit.createInventory(null,  27, "BackPack");
	Scoreboard sc;
	
	@Override
	public void onDisable() {		
	}

	@Override
	public void onEnable() {
		
		sc = Bukkit.getScoreboardManager().getNewScoreboard();
		sc.registerNewTeam("1");
		sc.registerNewTeam("2");
		sc.getTeam("1").setAllowFriendlyFire(false);
		sc.getTeam("1").setColor(ChatColor.BLUE);
		sc.getTeam("1").setDisplayName(ChatColor.BLUE + "");
		sc.getTeam("1").setPrefix("[Test] ");
		for(Player p : Bukkit.getOnlinePlayers()) {
			sc.getTeam("1").addPlayer(p);		
		}
		
		System.out.println(NAME + " wurde aktiviert!");		
		init();
	}
	
	private void init(){							
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new PlayerDamageListener(), this);				
		
		this.getCommand("challenge").setExecutor(new ChallengeCommand());
		this.getCommand("bp").setExecutor(new BackPack());
		this.getCommand("timer").setExecutor(new Timer());
		this.getCommand("pos").setExecutor(new callPos());
		this.getCommand("addP").setExecutor(new addPlayerLate());
		this.getCommand("heal").setExecutor(new Health());
		instance = this;
	}
	
	public double getHealth() {
		return this.health;
	}
	
	public void setHealth(double d) {
		this.health = d;
	}
	
	public int getTaskID() {
		return this.taskID;
	}
	
	public void setTaskID(int i) {
		this.taskID = i;
	}
	
	public Inventory getBP() {
		return this.bp;
	}
	
	public void setBP(Inventory i) {
		this.bp = i;
	}
	
	public String getNetherName() {
		return this.NETHER_NAME;
	}
	
	public String getEndName() {
		return this.END_NAME;
	}
	
	public String getMapName() {
		return this.MAP_NAME;
	}
	
	public ArrayList<Player> getPlayers(){
		return this.players;
	}

	public void addPlayer(Player p) {
		this.players.add(p);
	}
	
	public void removePlayer(Player p) {
		players.remove(p);
	}
	
	public void setRun(boolean b) {
		this.run = b;
	}
	
	public boolean getRun() {
		return this.run;
	}
	
	public void addTimer() {
		this.timer++;
	}
	
	public int getTimer() {
		return this.timer;
	}
	
	public void setTimer(int l) {
		this.timer = l;
	}
	
	public static SpigotPlugin getInstance() {
		return instance;
	}
	
}
