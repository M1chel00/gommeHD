package me.mses.util;

import org.bukkit.Difficulty;
import org.bukkit.GameRule;

public class config {

	private static Difficulty difficulty = Difficulty.NORMAL;
	private static boolean isAllowedSneak = true;
	private static boolean isAllowedJump = true;
	
	public static Difficulty getDifficulty() {
		return difficulty;
	}
	public static void setDifficulty(Difficulty difficulty) {
		config.difficulty = difficulty;
	}
	public static boolean isAllowedSneak() {
		return isAllowedSneak;
	}
	public static void setAllowedSneak(boolean isAllowedSneak) {
		config.isAllowedSneak = isAllowedSneak;
	}
	public static boolean isAllowedJump() {
		return isAllowedJump;
	}
	public static void setAllowedJump(boolean isAllowedJump) {
		config.isAllowedJump = isAllowedJump;
	}
	
}
