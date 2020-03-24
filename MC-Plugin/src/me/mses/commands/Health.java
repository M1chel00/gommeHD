package me.mses.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.mses.main.SpigotPlugin;

public class Health implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 1) {
			double t = Double.parseDouble(args[0]);
			for(Player p : SpigotPlugin.getInstance().getPlayers()) {
				p.setHealth(t);
				SpigotPlugin.getInstance().setHealth(t);
				p.sendMessage("[§6Challenge§r] Deine Herzen wurden auf §4" + t + "§r gesetzt!");
			}
			return true;
		}
		return false;
	}

}
