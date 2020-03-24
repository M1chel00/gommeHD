package me.mses.commands;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

import me.mses.main.SpigotPlugin;

public class Timer implements CommandExecutor{

	private boolean run = true;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) { 
			Player p = (Player) sender;
			if(!SpigotPlugin.getInstance().getRun()) {
				p.sendMessage("[§6Challenge§r] Die Challenge wurde noch nicht gestartet!");
				return true;
			}			
			if(args.length == 0) {
				p.sendMessage("[§6Challenge§r] Seit §l" + (SpigotPlugin.getInstance().getTimer()/60)/60 + "§r Stunden §l"
						+ (SpigotPlugin.getInstance().getTimer()/60)%60 + "§r Minuten §l" 
						+ SpigotPlugin.getInstance().getTimer()%60 + "§r Sekunden in der Challenge!");
			}else if(args.length == 1 && args[0].equalsIgnoreCase("pause") && p.hasPermission("mses.challenge.timer.pause")) {
				if(!this.run) {
					p.sendMessage("[§6Challenge§r] Der Timer ist bereits pausiert!");
					return true;
				}
				this.run = false;
				Bukkit.broadcastMessage("[§6Challenge§r] Der Timer wurde angehalten!");
				Bukkit.getScheduler().cancelTask(SpigotPlugin.getInstance().getTaskID());
			}else if(args.length == 1 && args[0].equalsIgnoreCase("resume") && p.hasPermission("mses.challenge.timer.resume")) {
				if(this.run) {
					p.sendMessage("[§6Challenge§r] Der Timer läuft bereits!");
					return true;
				}
				this.run = true;
				int temp = Bukkit.getScheduler().scheduleSyncRepeatingTask(SpigotPlugin.getInstance(), new Runnable() {					
					@Override
					public void run() {						
						SpigotPlugin.getInstance().addTimer();						
					}
				}, 0, 20 * 1);
				SpigotPlugin.getInstance().setTaskID(temp);
				Bukkit.broadcastMessage("[§6Challenge§r] Der Timer läuft nun weiter!");
			}else {
				return false;
			}
		}
		return true;
	}

}
