package me.mses.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.mses.main.SpigotPlugin;

public class callPos implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(!SpigotPlugin.getInstance().getRun()) {
				p.sendMessage("[§6Challenge§r] Die Challenge wurde noch nicht gestartet!");
				return true;
			}
			if(!p.hasPermission("mses.challenge.pos")) {
				p.sendMessage("[§6Challenge§r] Du verfügst nicht über genügend Rechte um die aktuelle Position anzuzeigen!");
				return true;
			}
			Bukkit.broadcastMessage("[§6Challenge§r] Spieler §b" + p.getDisplayName() + "§r befindet sich bei §4X: §2" + (int)p.getLocation().getX() + " §4Y: §2" + (int)p.getLocation().getY() + " §4Z: §2" + (int)p.getLocation().getZ() + ".");
			return true;
		}
		return false;
	}

}
