package io.hotmail.com.jacob_vejvoda.infernal_mobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class GUI implements Listener{
	static infernal_mobs plugin;
	HashMap<String, Scoreboard> playerScoreBoard = new  HashMap<String, Scoreboard>();
	HashMap<Entity, BossBar> bossBars = new  HashMap<Entity, BossBar>();
  
	public GUI(infernal_mobs instance){
		plugin = instance;
	}
  
	public void fixBar(Player p){
		//System.out.println("fixBar");
		double dis = 26.0D;
		Entity b = null;
		for (Mob m : plugin.infernalList) {
			if (m.entity.getWorld().equals(p.getWorld())){
				Entity boss = m.entity;
				if (p.getLocation().distance(boss.getLocation()) < dis){
					dis = p.getLocation().distance(boss.getLocation());
					b = boss;
				}
			}
		}
		if (b != null){
			//System.out.println("Dead: " + b.isDead());
			//System.out.println("HP: " + ((Damageable)b).getHealth());
			if(b.isDead() || ((Damageable)b).getHealth() <= 0){
				if (plugin.getConfig().getBoolean("enableBossBar")){
					try{
						for(Player p2 : bossBars.get(b).getPlayers())
							bossBars.get(b).removePlayer(p2);
						bossBars.remove(b);
					}catch(Exception x){}
				}
			    int mobIndex = plugin.idSearch(b.getUniqueId());
				try{
					if (mobIndex != -1)
						plugin.removeMob(mobIndex);
				}catch (IOException e) {}
				clearInfo(p);
			}else{
				if (plugin.getConfig().getBoolean("enableBossBar")) {
					showBossBar(p, b);
				}
				if (plugin.getConfig().getBoolean("enableScoreBoard")) {
					fixScoreboard(p, b, plugin.findMobAbilities(b.getUniqueId()));
				}
			}
		}else
			clearInfo(p);
	}
  
	@SuppressWarnings("deprecation")
	public void showBossBar(Player p, Entity e){
		ArrayList<String> oldMobAbilityList = plugin.findMobAbilities(e.getUniqueId());
		String tittle;
		if (plugin.getConfig().getString("bossBarsName") != null) {
			tittle = plugin.getConfig().getString("bossBarsName");
		}else
			tittle = "&fLevel <powers> &fInfernal <mobName>";
		String mobName = e.getType().getName();
		if (e.getType().equals(EntityType.SKELETON)) {
			Skeleton sk = (Skeleton)e;
			if (sk.getSkeletonType().equals(Skeleton.SkeletonType.WITHER)) {
				mobName = "WitherSkeleton";
			}
		}else if (e.getType().equals(EntityType.HORSE)){
			mobName = "Horse";
		}
		String prefix = plugin.getConfig().getString("namePrefix");
		if (plugin.getConfig().getString("levelPrefixs." + oldMobAbilityList.size()) != null) {
			prefix = plugin.getConfig().getString("levelPrefixs." + oldMobAbilityList.size());
		}
		tittle = tittle.replace("<prefix>", prefix);
		tittle = tittle.replace("<mobName>", mobName);
		tittle = tittle.replace("<mobLevel>", oldMobAbilityList.size()+"");
		String abilities = plugin.generateString(5, oldMobAbilityList);
		int count = 4;
		try{
			do{
				abilities = plugin.generateString(count, oldMobAbilityList);
				count--;
				if (count <= 0) {
					break;
				}
			} while (tittle.length() + abilities.length() + mobName.length() > 64);
		}catch (Exception x){System.out.println("showBossBar error: ");x.printStackTrace();}
		tittle = tittle.replace("<abilities>", abilities);
		tittle = ChatColor.translateAlternateColorCodes('&', tittle);
		
		//float health = (float)((Damageable)e).getHealth();
		//float maxHealth = (float)((Damageable)e).getMaxHealth();
		//float setHealth = health * 100.0F / maxHealth;
		//BossBarAPI.setMessage(p, tittle, setHealth);
		if(!bossBars.containsKey(e)){
			BarColor bc = BarColor.valueOf(plugin.getConfig().getString("bossBarSettings.defaultColor"));
			BarStyle bs = BarStyle.valueOf(plugin.getConfig().getString("bossBarSettings.defaultStyle"));
			//Per Level Setings
			String lc = plugin.getConfig().getString("bossBarSettings.perLevel."+oldMobAbilityList.size()+".color");
			if(lc != null)
				bc = BarColor.valueOf(lc);
			String ls = plugin.getConfig().getString("bossBarSettings.perLevel."+oldMobAbilityList.size()+".style");
			if(ls != null)
				bs = BarStyle.valueOf(ls);
			//Per Mob Setings
			String mc = plugin.getConfig().getString("bossBarSettings.perMob."+e.getType().getName()+".color");
			if(mc != null)
				bc = BarColor.valueOf(mc);
			String ms = plugin.getConfig().getString("bossBarSettings.perMob."+e.getType().getName()+".style");
			if(ms != null)
				bs = BarStyle.valueOf(ms);
			BossBar bar = Bukkit.createBossBar(tittle, bc, bs, BarFlag.CREATE_FOG);
			bar.setVisible(true);
			bossBars.put(e, bar);
		}
		if(!bossBars.get(e).getPlayers().contains(p))
			bossBars.get(e).addPlayer(p);
    	float health = (float) ((Damageable) e).getHealth();
    	float maxHealth = (float) ((Damageable) e).getMaxHealth();
    	float setHealth = (health * 100.0f) / maxHealth;
    	bossBars.get(e).setProgress(setHealth/100.0f);
	}
  
	@SuppressWarnings("deprecation")
	public void clearInfo(Player player){
		if (plugin.getConfig().getBoolean("enableBossBar")) {
			//BossBarAPI.removeBar(player);
			for (Map.Entry<Entity, BossBar> hm : bossBars.entrySet())
				if(hm.getValue().getPlayers().contains(player))
					hm.getValue().removePlayer(player);
		}
		if (plugin.getConfig().getBoolean("enableScoreBoard")) {
			try{
				player.getScoreboard().resetScores(player);
				player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).unregister();
			}
			catch (Exception localException1) {}
		}
	}
  
	@SuppressWarnings("deprecation")
	public void fixScoreboard(Player player, Entity e, ArrayList<String> abilityList){
		if((plugin.getConfig().getBoolean("enableScoreBoard") == true) && (e instanceof Damageable)){
			//String name = getMobNameTag(e);
			//Get Display
			//System.out.println("sb1");
			if(playerScoreBoard.get(player.getName()) == null){
				//System.out.println("Creating ScoreBoard");
	            ScoreboardManager manager = Bukkit.getScoreboardManager();
	            Scoreboard board = manager.getNewScoreboard();
	            playerScoreBoard.put(player.getName(), board);
			}
			//System.out.println("sb2");
			Objective o;
          Scoreboard board = playerScoreBoard.get(player.getName());
          //System.out.println("Board = " + board);
          if(board.getObjective(DisplaySlot.SIDEBAR) == null){
          	o = board.registerNewObjective(player.getName(), "dummy");
				o.setDisplaySlot(DisplaySlot.SIDEBAR);
          }else{
          	o = board.getObjective(DisplaySlot.SIDEBAR);
          }
         //System.out.println("sb3");
			//Name
          //System.out.println("Name: " + e.getType().getName());
          //System.out.println("OBJ = " + o);
			o.setDisplayName(e.getType().getName());
			//System.out.println("Set ScoreBoard Name");
            //Remove Old
			//for(OfflinePlayer p : board.getPlayers())
			//    board.resetScores(p);
			for (String s : board.getEntries())
				board.resetScores(s);
			//Power Display
			int score = 1;
			//System.out.println("sb4");
			for(String ability : abilityList){
				//Score pointsDisplayScore = o.getScore(Bukkit.getOfflinePlayer("§r" + ability));
				//pointsDisplayScore.setScore(score);
				o.getScore("§r" + ability).setScore(score);
				score = score + 1;
			}
			//Score abDisplayScore = o.getScore(Bukkit.getOfflinePlayer("§e§lAbilities:"));
			//abDisplayScore.setScore(score);
			o.getScore("§e§lAbilities:").setScore(score);
			//Health
			//System.out.println("sb5");
			if(plugin.getConfig().getBoolean("showHealthOnScoreBoard") == true){
				//System.out.println("shosb");
				//Display HP
				score = score + 1;
				float health = (float) ((Damageable) e).getHealth();
				float maxHealth = (float) ((Damageable) e).getMaxHealth();
          		double roundOff = Math.round(health * 100.0) / 100.0;
				//Score hDisplayScore = o.getScore(Bukkit.getOfflinePlayer(roundOff + "/" + maxHealth));
				//hDisplayScore.setScore(score);
          		o.getScore(roundOff + "/" + maxHealth).setScore(score);
				score = score + 1;
				//Score htDisplayScore = o.getScore(Bukkit.getOfflinePlayer("§e§lHealth:"));
				//htDisplayScore.setScore(score);
				o.getScore("§e§lHealth:").setScore(score);
			}
			//System.out.println("sb6");
			//Display
			if((player.getScoreboard() == null) || (player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) == null) || (player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getName() == null) || (!player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getName().equals(board.getObjective(DisplaySlot.SIDEBAR).getName()))){
				//System.out.println("Set SB");
				player.setScoreboard(board);
			}
			//System.out.println("sb7");
		}
	}
  
	public void setName(Entity ent){
		try{
			//System.out.println("SN1 " + ent);
			if (plugin.getConfig().getInt("nameTagsLevel") != 0){
				String tittle = getMobNameTag(ent);
				((LivingEntity) ent).setCustomName(tittle);
				if (plugin.getConfig().getInt("nameTagsLevel") == 2){
					((LivingEntity) ent).setCustomNameVisible(true);
				}
			}
		}catch(Exception x){System.out.println("Error in setName: "); x.printStackTrace();}
	}
  
	@SuppressWarnings("deprecation")
	public String getMobNameTag(Entity entity){
		ArrayList<String> oldMobAbilityList = plugin.findMobAbilities(entity.getUniqueId());
		//System.out.println("OMAL: " + oldMobAbilityList);
		String tittle;
		if (plugin.getConfig().getString("nameTagsName") != null){
			tittle = plugin.getConfig().getString("nameTagsName");
		}else{
			tittle = "&fInfernal <mobName>";
		}
		String mobName = entity.getType().getName();
		if (entity.getType().equals(EntityType.SKELETON)){
			Skeleton sk = (Skeleton) entity;
			if(sk.getSkeletonType().equals(SkeletonType.WITHER)){
				mobName = "WitherSkeleton";
			}
		}else if (entity.getType().equals(EntityType.HORSE)){
				mobName = "Horse";
		}
		tittle = tittle.replace("<mobName>", mobName);	
		tittle = tittle.replace("<mobLevel>", ""+oldMobAbilityList.size());	
		String abilities = plugin.generateString(5, oldMobAbilityList);
		int count = 4;
		do{
			abilities = plugin.generateString(count, oldMobAbilityList);
			count--;
		}while((tittle.length() + abilities.length() + mobName.length()) > 64);
		tittle = tittle.replace("<abilities>", abilities);
		//Prefix
		String prefix = plugin.getConfig().getString("namePrefix");
		if(plugin.getConfig().getString("levelPrefixs." + oldMobAbilityList.size()) != null)
			prefix = plugin.getConfig().getString("levelPrefixs." + oldMobAbilityList.size());
		tittle = tittle.replace("<prefix>", prefix);	
		tittle = ChatColor.translateAlternateColorCodes('&', tittle);	
		return tittle;
	}
}