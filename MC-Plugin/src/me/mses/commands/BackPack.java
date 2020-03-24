package me.mses.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.mses.main.SpigotPlugin;

public class BackPack implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			for(Player i : SpigotPlugin.getInstance().getPlayers()) {
				if(i.getName().equalsIgnoreCase(p.getName()) && p.hasPermission("mses.backpack.open")) {
					Inventory inv = SpigotPlugin.getInstance().getBP();					
					p.openInventory(inv);
					return true;
				}
			}
			p.sendMessage("[§6Challenge§r] Du nimmst zur Zeit an keiner Challenge teil!");
			p.sendMessage("[§6Challenge§r] Sobald du an einer teilnimmst, wirst du das BackPack öffnen können!");
			return true;
		}
		return false;
	}

}
