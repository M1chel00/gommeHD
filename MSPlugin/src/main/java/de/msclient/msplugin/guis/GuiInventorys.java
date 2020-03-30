package de.msclient.msplugin.guis;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.msclient.msplugin.events.EnchantGlow;

public class GuiInventorys {

	private Inventory challengeInv = Bukkit.createInventory(null, 9, "Customize your Challenge!");
	private Inventory difInv = Bukkit.createInventory(null, 9, "Set the difficulty!");
	private Inventory regInv = Bukkit.createInventory(null, 9, "Set the regeneration!");	
	private Inventory lifeInv = Bukkit.createInventory(null, 9, "Set the life settings!");
	
	public GuiInventorys() {
		initializeItems();
	}
	
	private void initializeItems() {
		challengeInv.addItem(createGuiItem(Material.DIAMOND_SWORD, "Difficulty", "Easy", "Normal", "Hardcore"));
		challengeInv.addItem(createGuiItem(Material.GOLDEN_APPLE, "Regeneration", "On", "Off"));
		challengeInv.addItem(createGuiItem(Material.DIAMOND_BOOTS, "Team Life", "On", "Off"));
		challengeInv.setItem(8, createGuiItem(Material.GREEN_BANNER, "CONFIRM", "Click to start the challenge!"));
		
		difInv.addItem(createGuiItem(Material.WOODEN_SWORD, "Easy", ""));
		difInv.addItem(createGuiItem(Material.IRON_SWORD, "Normal", ""));
		difInv.addItem(createGuiItem(Material.DIAMOND_SWORD, "Hardcore", ""));
		difInv.setItem(8, createGuiItem(Material.GREEN_BANNER, "CONFIRM", "Click to safe the settings!"));
		
		regInv.addItem(createGuiItem(Material.RED_DYE, "OFF", "Disable the natural regeneration!"));		
		regInv.addItem(createGuiItem(Material.GREEN_DYE, "ON", "Enable the natural regeneration!"));
		regInv.setItem(8, createGuiItem(Material.GREEN_BANNER, "CONFIRM", "Click to safe the settings!"));
		
		lifeInv.addItem(createGuiItem(Material.RED_DYE, "OFF", "Everyone has there own life!"));
		lifeInv.addItem(createGuiItem(Material.GREEN_DYE, "ON", "Everyone has the same life!"));
		lifeInv.setItem(8, createGuiItem(Material.GREEN_BANNER, "CONFIRM", "Click to safe the settings!"));
	}
	
	private ItemStack createGuiItem(Material material, String name, String...lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);        
        ArrayList<String> metaLore = new ArrayList<String>();

        for(String loreComments : lore) {
            metaLore.add(loreComments);
        }

        meta.setLore(metaLore);
        item.setItemMeta(meta);
        return item;
    }
	

	public Inventory getChallengeInv() {
		return challengeInv;
	}

	public Inventory getDifInv() {
		return difInv;
	}

	public Inventory getRegInv() {
		return regInv;
	}

	public Inventory getLifeInv() {
		return lifeInv;
	}

	public void setChallengeInv(Inventory challengeInv) {
		this.challengeInv = challengeInv;
	}

	public void setDifInv(Inventory difInv) {
		this.difInv = difInv;
	}

	public void setRegInv(Inventory regInv) {
		this.regInv = regInv;
	}

	public void setLifeInv(Inventory lifeInv) {
		this.lifeInv = lifeInv;
	}
	
}
