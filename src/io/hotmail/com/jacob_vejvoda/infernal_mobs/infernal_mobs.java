package io.hotmail.com.jacob_vejvoda.infernal_mobs;

import io.hotmail.com.jacob_vejvoda.infernal_mobs.versionStuff.ParticleEffects_1_8_6;
import io.hotmail.com.jacob_vejvoda.infernal_mobs.versionStuff.ParticleEffects_1_9_4;
import io.hotmail.com.jacob_vejvoda.infernal_mobs.versionStuff.ParticleEffects_1_10;
import io.hotmail.com.jacob_vejvoda.infernal_mobs.versionStuff.ParticleEffects_1_11;
import io.hotmail.com.jacob_vejvoda.infernal_mobs.versionStuff.ParticleEffects_1_12;
import io.hotmail.com.jacob_vejvoda.WizardlyMagic.WizardlyMagic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.banner.Pattern;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Stray;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.entity.WitherSkull;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

@SuppressWarnings({ "unchecked", "rawtypes", "deprecation"})
public class infernal_mobs extends JavaPlugin implements Listener{
	ArrayList<Mob> infernalList = new ArrayList();
	ArrayList<UUID> dropedLootList = new ArrayList();
	File lootYML = new File(getDataFolder(), "loot.yml");
	File saveYML = new File(getDataFolder(), "save.yml");
	YamlConfiguration lootFile = YamlConfiguration.loadConfiguration(this.lootYML);
	YamlConfiguration mobSaveFile = YamlConfiguration.loadConfiguration(this.saveYML);
	HashMap<Entity, Entity> mountList = new HashMap();
	ArrayList<Player> errorList = new ArrayList();
	ArrayList<Player> levitateList = new ArrayList();
	public GUI gui;
	private EventListener events;
	public long serverTime = 0L;
	public WizardlyMagic wMagic;
	public int loops;
  
	public void onEnable(){
		//Register Events
		getServer().getPluginManager().registerEvents(this, this);
		this.events = new EventListener(this);
		getServer().getPluginManager().registerEvents(this.events, this);
		this.gui = new GUI(this);
		getServer().getPluginManager().registerEvents(this.gui, this);
		this.getLogger().log(Level.INFO, "Registered Events.");
		//Folder 
		File dir = new File(this.getDataFolder().getParentFile().getPath()+File.separator+this.getName());
		if(!dir.exists())
			dir.mkdir();
		//Old config check
		if (new File(getDataFolder(), "config.yml").exists()) {
			if(getConfig().getString("configVersion") == null){
				this.getLogger().log(Level.INFO, "No config version found!");
				getConfig().set("configVersion", Bukkit.getVersion().split(":")[1].replace(")", "").trim());
				saveConfig();
			}
			if(!Bukkit.getVersion().contains(getConfig().getString("configVersion"))){
				this.getLogger().log(Level.INFO, "Old config found, deleting!");
				new File(Bukkit.getServer().getPluginManager().getPlugin(this.getName()).getDataFolder() + File.separator + "config.yml").delete();
			}
		}
		//Register Config
		if (!new File(getDataFolder(), "config.yml").exists()) {
			//saveDefaultConfig();
	  		this.getLogger().log(Level.INFO, "No config.yml found, generating...");
	  		//Generate Config
	  		boolean generatedConfig = false;
	  		for(String version : Arrays.asList("1.12","1.11","1.10","1.9","1.8"))
	  			if(Bukkit.getVersion().contains(version)){
	  	  	        InputStream in = getClass().getResourceAsStream(version.replace(".", "_") + "_config.yml");
	  	  	        isSave(in, "config.yml");
	  	  	        this.getLogger().log(Level.INFO, "Config successfully generated!");
	  	  	        generatedConfig = true;
	  	  	        break;
	  			}
	  		if(!generatedConfig){
	  			this.getLogger().log(Level.SEVERE, "No config available, " + Bukkit.getVersion() + " is not supported!");
	  			Bukkit.getPluginManager().disablePlugin(this);
	  		}
//	  		if(Bukkit.getVersion().contains("1.10")){
//	  	        InputStream in = getClass().getResourceAsStream("1_10_config.yml");
//	  	        isSave(in, "config.yml");
//	  	        this.getLogger().log(Level.INFO, "Config successfully generated!");
//	  		}else if(Bukkit.getVersion().contains("1.9")){
//	  	        InputStream in = getClass().getResourceAsStream("1_9_config.yml");
//	  	        isSave(in, "config.yml");
//	  	        this.getLogger().log(Level.INFO, "Config successfully generated!");
//	  		}else if(Bukkit.getVersion().contains("1.8")){
//	  	        InputStream in = getClass().getResourceAsStream("1_8_config.yml");
//	  	        isSave(in, "config.yml");
//	  	        this.getLogger().log(Level.INFO, "Config successfully generated!");
//	  		}else{
//	  			this.getLogger().log(Level.SEVERE, "No config available, " + Bukkit.getVersion() + " is not supported!");
//	  			Bukkit.getPluginManager().disablePlugin(this);
//	  		}
            reloadConfig();
		}
		//Register Loots
		if (!lootYML.exists()) {
//			try {
//				lootYML.createNewFile();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
	  		this.getLogger().log(Level.INFO, "No loot.yml found, generating...");
	  		//Generate Config
	  		if(Bukkit.getVersion().contains("1.12")){
	  	        InputStream in = getClass().getResourceAsStream("1_12loot.yml");
	  	        isSave(in, "loot.yml");
	  	        this.getLogger().log(Level.INFO, "1.12 Loot successfully generated!");
	  		}else if(Bukkit.getVersion().contains("1.11")){
	  	        InputStream in = getClass().getResourceAsStream("1_11loot.yml");
	  	        isSave(in, "loot.yml");
	  	        this.getLogger().log(Level.INFO, "1.11 Loot successfully generated!");
	  		}else if(Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10")){
	  	        InputStream in = getClass().getResourceAsStream("1_9_10loot.yml");
	  	        isSave(in, "loot.yml");
	  	        this.getLogger().log(Level.INFO, "1.9/1.10 Loot successfully generated!");
	  		}else if(Bukkit.getVersion().contains("1.8")){
	  	        InputStream in = getClass().getResourceAsStream("1_8_loot.yml");
	  	        isSave(in, "loot.yml");
	  	        this.getLogger().log(Level.INFO, "1.8 Loot successfully generated!");
	  		}else{
	  			this.getLogger().log(Level.SEVERE, "No loot available, " + Bukkit.getVersion() + " is not supported!");
	  			Bukkit.getPluginManager().disablePlugin(this);
	  		}
            reloadLoot();
		}
		//Register Save File
		if (!saveYML.exists()) {
			try {
				saveYML.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
  		//Register Metrics
		try {
		    Metrics metrics = new Metrics(this);
		    metrics.start();
		} catch (IOException e) {
		    // Failed to submit the stats :-(
		}
		//Register Wizardly Magic
		try{
			wMagic = (WizardlyMagic) Bukkit.getServer().getPluginManager().getPlugin("WizardlyMagic");
		}catch(Exception e){}
		applyEffect();
		reloadPowers();
		showEffect();
	}
	
//	@EventHandler(priority=EventPriority.HIGH)
//	public void onEntitySpawn(EntitySpawnEvent event){
//		System.out.println("Mob Spawn X");
//	}
	
	  public void isSave(InputStream inputStream, String dFile) {

			OutputStream outputStream = null;

			try {
				// write the inputStream to a FileOutputStream
			    File plugins = Bukkit.getServer().getPluginManager().getPlugin(this.getName()).getDataFolder().getParentFile();
				outputStream = new FileOutputStream(new File(plugins.getPath() + File.separator + this.getName() + File.separator + dFile));

				int read = 0;
				byte[] bytes = new byte[1024];

				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}

				//System.out.println("Done!");

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (outputStream != null) {
					try {
						// outputStream.flush();
						outputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
		  }
  
  public void reloadPowers(){
    ArrayList<World> wList = new ArrayList();
    for (Player p : getServer().getOnlinePlayers()) {
      if (!wList.contains(p.getWorld())) {
        wList.add(p.getWorld());
      }
    }
    for (World world : wList) {
      giveMobsPowers(world);
    }
  }
  
	public void scoreCheck(){
		for (Player p : getServer().getOnlinePlayers())
			GUI.fixBar(p);
    //for (Map.Entry<Entity, Entity> hm : ((HashMap)this.mountList.clone()).entrySet()) {
    HashMap<Entity, Entity> tmp = ( HashMap<Entity, Entity>)mountList.clone();
    for (Map.Entry<Entity, Entity> hm : tmp.entrySet()){
      if ((hm.getKey() != null) && (!((Entity)hm.getKey()).isDead()))
      {
        if ((((Entity)hm.getValue()).isDead()) && 
          ((hm.getKey() instanceof LivingEntity)))
        {
          String fate = getConfig().getString("mountFate");
          if (fate.equals("death"))
          {
            LivingEntity le = (LivingEntity)hm.getKey();
            le.damage(9.99999999E8D);
            this.mountList.remove(hm.getKey());
          }
          else if (fate.equals("removal"))
          {
            ((Entity)hm.getKey()).remove();
            this.getLogger().log(Level.INFO, "Entity remove due to Fate");
            this.mountList.remove(hm.getKey());
          }
        }
      }
      else {
        this.mountList.remove(hm.getKey());
      }
    }
  }
  
  public void giveMobsPowers(World world)
  {
    for (Entity ent : world.getEntities()) {
      if (((ent instanceof LivingEntity)) && (this.mobSaveFile.getString(ent.getUniqueId().toString()) != null)) {
        giveMobPowers(ent);
      }
    }
  }
  
  public void giveMobPowers(Entity ent)
  {
    UUID id = ent.getUniqueId();
    if (idSearch(id) == -1)
    {
      ArrayList<String> aList = null;
      for (MetadataValue v : ent.getMetadata("infernalMetadata")) {
        aList = new ArrayList(Arrays.asList(v.asString().split(",")));
      }
      if (aList == null) {
        if (this.mobSaveFile.getString(ent.getUniqueId().toString()) != null)
        {
          aList = new ArrayList(Arrays.asList(this.mobSaveFile.getString(ent.getUniqueId().toString()).split(",")));
          String list = getPowerString(ent, aList);
          ent.setMetadata("infernalMetadata", new FixedMetadataValue(this, list));
        }
        else
        {
          aList = getAbilitiesAmount(ent);
        }
      }
      Mob newMob = null;
      if (aList.contains("1up")) {
        newMob = new Mob(ent, id, ent.getWorld(), true, aList, 2, getEffect());
      } else {
        newMob = new Mob(ent, id, ent.getWorld(), true, aList, 1, getEffect());
      }
      if (aList.contains("flying")) {
        makeFly(ent);
      }
      this.infernalList.add(newMob);
    }
  }
  
  public void makeInfernal(final Entity e, final boolean fixed) {
	  boolean mobEnabled = true;
		String entName;
		if(is11()){
			entName = e.getType().name();
		}else
			entName = e.getType().getName();
	  if ((!e.hasMetadata("NPC")) && (!e.hasMetadata("shopkeeper"))){
		  if (!fixed) {
			  if (e.getType().equals(EntityType.SKELETON)){
				  Skeleton sk = (Skeleton)e;
				  if (is11() == false && (sk instanceof WitherSkeleton) && (!getConfig().getList("enabledmobs").contains("WitherSkeleton"))) {
					  mobEnabled = false;
				  }else if (is10() && is11() == false && (sk instanceof Stray) && (!getConfig().getList("enabledmobs").contains("Stray"))) {
					  mobEnabled = false;
				  }
			  }else if (e.getType().equals(EntityType.ZOMBIE)){
				  Zombie zo = (Zombie)e;
				  if (is10() && is11() == false && (zo instanceof Husk) && (!getConfig().getList("enabledmobs").contains("Husk"))) {
					  mobEnabled = false;
				  }
			  }else {
          ArrayList<String> babyList = (ArrayList)getConfig().getList("disabledBabyMobs");
          if((e instanceof LivingEntity) && (e instanceof Ageable)) {
        	  Ageable age = (Ageable) e;
        	  boolean baby = !age.isAdult();
        	  if (baby &&  babyList.contains(entName)) {
        		  return;
        	  }
          }
        }
      }
      final UUID id = e.getUniqueId();
      final int chance = getConfig().getInt("chance");
      final boolean mobEnabled2 = mobEnabled;
      final Entity ent = e;
      Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
        public void run(){
        	String entName;
    		if(is11()){
    			entName = e.getType().name();
    		}else
    			entName = e.getType().getName();
          if ((!ent.isDead()) && (ent.isValid()) && (ent != null) && (
            ((getConfig().getList("enabledmobs").contains(entName)) && (mobEnabled2)) || ((fixed) && 
            (idSearch(id) == -1)))){
        	//Default
            int min = 1;
            int max = chance;
            //Pe Mob
            int mc = getConfig().getInt("mobChances." + entName);
            if(mc > 0)
            	max = mc;
            if (fixed)
            	max = 1;
            //int randomNum = new Random().nextInt(max - min + 1) + min;
            int randomNum = rand(min, max);
            if (randomNum == 1){
              ArrayList<String> aList = getAbilitiesAmount(ent);
              if (infernal_mobs.this.getConfig().getString("levelChance." + aList.size()) != null)
              {
                int sc = infernal_mobs.this.getConfig().getInt("levelChance." + aList.size());
                int randomNum2 = new Random().nextInt(sc - min + 1) + min;
                if (randomNum2 != 1) {
                  return;
                }
              }
              Mob newMob = null;
              if (aList.contains("1up")) {
                newMob = new Mob(ent, id, e.getWorld(), true, aList, 2, infernal_mobs.this.getEffect());
              } else {
                newMob = new Mob(ent, id, e.getWorld(), true, aList, 1, infernal_mobs.this.getEffect());
              }
              if (aList.contains("flying")) {
                infernal_mobs.this.makeFly(ent);
              }
              infernal_mobs.this.infernalList.add(newMob);
              infernal_mobs.this.gui.setName(ent);
              infernal_mobs.this.giveMobGear(ent, true);
              infernal_mobs.this.addHealth(ent, aList);
              if (infernal_mobs.this.getConfig().getBoolean("enableSpawnMessages")) {
                if (infernal_mobs.this.getConfig().getList("spawnMessages") != null)
                {
                  ArrayList<String> spawnMessageList = (ArrayList)infernal_mobs.this.getConfig().getList("spawnMessages");
                  Random randomGenerator = new Random();
                  int index = randomGenerator.nextInt(spawnMessageList.size());
                  String spawnMessage = (String)spawnMessageList.get(index);
                  
                  spawnMessage = ChatColor.translateAlternateColorCodes('&', spawnMessage);
                  if (((LivingEntity)ent).getCustomName() != null) {
                    spawnMessage = spawnMessage.replace("mob", ((LivingEntity)ent).getCustomName());
                  } else {
                    spawnMessage = spawnMessage.replace("mob", ent.getType().toString().toLowerCase());
                  }
                  int r = infernal_mobs.this.getConfig().getInt("spawnMessageRadius");
                  if (r == -1) {
                    for (Player p : ent.getWorld().getPlayers()) {
                      p.sendMessage(spawnMessage);
                    }
                  } else if (r == -2) {
                    Bukkit.broadcastMessage(spawnMessage);
                  } else {
                    for (Entity e : ent.getNearbyEntities(r, r, r)) {
                      if ((e instanceof Player))
                      {
                        Player p = (Player)e;
                        p.sendMessage(spawnMessage);
                      }
                    }
                  }
                }
                else
                {
                  System.out.println("No valid spawn messages found!");
                }
              }
            }
          }
        }
      }, 10L);
    }
  }
  
  public void addHealth(Entity ent, ArrayList<String> powerList){
	  double maxHealth = ((Damageable)ent).getHealth();
	  float setHealth;
	  if (getConfig().getBoolean("healthByPower")){
		  int mobIndex = idSearch(ent.getUniqueId());
		  try{
			  Mob m = (Mob)this.infernalList.get(mobIndex);
			  setHealth = (float)(maxHealth * m.abilityList.size());
		  }catch (Exception e){
			  setHealth = (float)(maxHealth * 5.0D);
		  }
	  }else{
		  if (getConfig().getBoolean("healthByDistance")) {
			  Location l = ent.getWorld().getSpawnLocation();
			  int m = (int)l.distance(ent.getLocation()) / getConfig().getInt("addDistance");
			  if (m < 1) {
				  m = 1;
			  }
			  int add = getConfig().getInt("healthToAdd");
			  setHealth = m * add;
		  }else{
			  int healthMultiplier = getConfig().getInt("healthMultiplier");
			  setHealth = (float)(maxHealth * healthMultiplier);
		  }
	  }
	  if (setHealth >= 1.0F) {
		  try{
			  ((LivingEntity)ent).setMaxHealth(setHealth);
			  ((LivingEntity)ent).setHealth(setHealth);
		  }catch (Exception e){
			  System.out.println("addHealth: " + e);
		  }
	  }
	  String list = getPowerString(ent, powerList);
	  ent.setMetadata("infernalMetadata", new FixedMetadataValue(this, list));
	  try{
		  this.mobSaveFile.set(ent.getUniqueId().toString(), list);
		  this.mobSaveFile.save(this.saveYML);
	  }catch (IOException localIOException) {}
  }
  
  public String getPowerString(Entity ent, ArrayList<String> powerList)
  {
    String list = "";
    for (String s : powerList) {
      if (powerList.indexOf(s) != powerList.size() - 1) {
        list = list + s + ",";
      } else {
        list = list + s;
      }
    }
    return list;
  }
  
  public void removeMob(int mobIndex) throws IOException{
    String id = ((Mob)this.infernalList.get(mobIndex)).id.toString();
    this.infernalList.remove(mobIndex);
    this.mobSaveFile.set(id, null);
    this.mobSaveFile.save(this.saveYML);
  }
  
  public void spawnGhost(Location l)
  {
    boolean evil = false;
    if (new Random().nextInt(3) == 1) {
      evil = true;
    }
    Zombie g = (Zombie)l.getWorld().spawnEntity(l, EntityType.ZOMBIE);
    g.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 199999980, 1));
    g.setCanPickupItems(false);
    
    ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
    ItemStack skull;
    if (evil)
    {
      skull = new ItemStack(Material.SKULL_ITEM, 1, (short)1);
      dye(chest, Color.BLACK);
    }
    else
    {
      skull = new ItemStack(Material.SKULL_ITEM, 1);
      dye(chest, Color.WHITE);
    }
    chest.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, new Random().nextInt(10) + 1);
    ItemMeta m = skull.getItemMeta();
    m.setDisplayName("§fGhost Head");
    skull.setItemMeta(m);
    g.getEquipment().setHelmet(skull);
    g.getEquipment().setChestplate(chest);
    g.getEquipment().setHelmetDropChance(0.0F);
    g.getEquipment().setChestplateDropChance(0.0F);
    int min = 1;
    int max = 5;
    int rn = new Random().nextInt(max - min + 1) + min;
    if (rn == 1)
    {
      g.getEquipment().setItemInHand(new ItemStack(Material.STONE_HOE, 1));
      g.getEquipment().setItemInHandDropChance(0.0F);
    }
    ghostMove(g);
    
    ArrayList<String> aList = new ArrayList();
    aList.add("ender");
    if (evil)
    {
      aList.add("necromancer");
      aList.add("withering");
      aList.add("blinding");
    }
    else
    {
      aList.add("ghastly");
      aList.add("sapper");
      aList.add("confusing");
    }
    Mob newMob;
    if (evil) {
      newMob = new Mob(g, g.getUniqueId(), g.getWorld(), false, aList, 1, "smoke:2:12");
    } else {
      newMob = new Mob(g, g.getUniqueId(), g.getWorld(), false, aList, 1, "cloud:0:8");
    }
    this.infernalList.add(newMob);
  }
  
  public void ghostMove(final Entity g)
  {
    if (g.isDead()) {
      return;
    }
    Vector v = g.getLocation().getDirection().multiply(0.3D);
    g.setVelocity(v);
    
    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
    {
      public void run()
      {
        try
        {
          infernal_mobs.this.ghostMove(g);
        }
        catch (Exception localException) {}
      }
    }, 2L);
  }
  
  public void dye(ItemStack item, Color color)
  {
    try
    {
      LeatherArmorMeta meta = (LeatherArmorMeta)item.getItemMeta();
      meta.setColor(color);
      item.setItemMeta(meta);
    }
    catch (Exception localException) {}
  }
  
  public void keepAlive(Item item)
  {
    final UUID id = item.getUniqueId();
    this.dropedLootList.add(id);
    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
    {
      public void run()
      {
        infernal_mobs.this.dropedLootList.remove(id);
      }
    }, 300L);
  }
  
  	public boolean mobPowerLevelFine(int lootId, int mobPowers){
  		int min = 0;
  		int max = 99;
  		if (lootFile.getString("loot." + lootId + ".powersMin") != null){
  			min = lootFile.getInt("loot." + lootId + ".powersMin");
  		}
  		if(lootFile.getString("loot." + lootId + ".powersMax") != null)
  			max = lootFile.getInt("loot." + lootId + ".powersMax");
  		if(getConfig().getBoolean("debug"))
  			this.getLogger().log(Level.INFO, "Loot " + lootId + " min = " + min + " and max = " + max);
		if ((mobPowers >= min) && (mobPowers <= max)) {
			return true;
		}else
			return false;
  	}
  
  public ItemStack getRandomLoot(Player player, String mob, int powers){
	  ArrayList<Integer> lootList = new ArrayList();
	  //for (int i = 0; i <= 512; i++) {
	  for (String i : lootFile.getConfigurationSection("loot").getKeys(false)) {
		  if ((lootFile.getString("loot." + i) != null) && 
		     ((lootFile.getList("loot." + i + ".mobs") == null) || 
			 (this.lootFile.getList("loot." + i + ".mobs").contains(mob))) &&
			 (lootFile.getString("loot." + i + ".chancePercentage") == null || 
			 rand(1,100) <= lootFile.getInt("loot." + i + ".chancePercentage"))) {
			  if (mobPowerLevelFine(Integer.parseInt(i), powers)) {
				  lootList.add(Integer.valueOf(i));
			  }
		  }
    }
    try{
    	//System.out.println("LL: " + lootList.toString());
  		if(getConfig().getBoolean("debug"))
  			this.getLogger().log(Level.INFO, "Loot List " +  lootList.toString());
    	if(!lootList.isEmpty()){
    		return getLoot(player, lootList.get(rand(1,lootList.size())-1));
    	}else
    		 return null;
    }catch (Exception e){
    	System.out.println("Error in get random loot ");e.printStackTrace();
    	System.out.println("Error: No valid drops found!");
    }
    return null;
  }
  
  public ItemStack getLoot(Player player, int loot){
    if (this.lootFile.getList("loot." + loot + ".commands") != null){
      ArrayList<String> commandList = (ArrayList)this.lootFile.getList("loot." + loot + ".commands");
      for (String command : commandList) {
        command = ChatColor.translateAlternateColorCodes('&', command);
        command = command.replace("player", player.getName());
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
      }
    }
    if (this.lootFile.getString("loot." + loot + ".staff.id") != null)
    {
      int id = this.lootFile.getInt("loot." + loot + ".staff.id");
      ArrayList<String> spells = new ArrayList();
      if (this.lootFile.getList("loot." + loot + ".staff.spells") != null) {
        spells = (ArrayList)this.lootFile.getList("loot." + loot + ".staff.spells");
      }
      return this.wMagic.getStaffWithSpells(id, spells);
    }
    return getItem(loot);
  }
  
  public ItemStack getItem(int loot){
	//System.out.println("Get Loot: " + loot);
    try {
      int setItem = this.lootFile.getInt("loot." + loot + ".item");
      
      String setAmountString = this.lootFile.getString("loot." + loot + ".amount");
      int setAmount;
      if(setAmountString != null){
    	  setAmount = getIntFromString(setAmountString);
      }else
    	  setAmount = 1;
      ItemStack stack = new ItemStack(setItem, setAmount);
      if (this.lootFile.getString("loot." + loot + ".durability") != null)
      {
        String durabilityString = this.lootFile.getString("loot." + loot + ".durability");
        int durability = getIntFromString(durabilityString);
        stack.setDurability((short)durability);
      }
      String name = null;
      if(lootFile.getString("loot." + loot + ".name") != null && lootFile.isString("loot." + loot + ".name")){
    	name = lootFile.getString("loot." + loot + ".name") ;
        name = prosessLootName(name, stack);
      }else if(lootFile.isList("loot." + loot + ".name")){
    	  ArrayList<String> names = (ArrayList<String>) lootFile.getList("loot." + loot + ".name");
    	  if(names != null){
    		  name = names.get(rand(1, names.size())-1);
    		  name = prosessLootName(name, stack);
    	  }
      }
      //Lore
      ArrayList<String> loreList = new ArrayList();
      for (int i = 0; i <= 32; i++) {
    	  if (this.lootFile.getString("loot." + loot + ".lore" + i) != null){
    		  String lore = this.lootFile.getString("loot." + loot + ".lore" + i);
    		  lore = ChatColor.translateAlternateColorCodes('&', lore);
    		  loreList.add(lore);
    	  }
      }
	  if (lootFile.getList("loot." + loot + ".lore") != null){
		  ArrayList<String> lb = (ArrayList<String>)lootFile.getList("loot." + loot + ".lore");
		  ArrayList<String> l = (ArrayList<String>) lb.clone();
		  int min = l.size();
		  if (lootFile.getString("loot." + loot + ".minLore") != null)
			  min = lootFile.getInt("loot." + loot + ".minLore");
		  int max = l.size();
		  if (lootFile.getString("loot." + loot + ".maxLore") != null)
			  max = lootFile.getInt("loot." + loot + ".maxLore");
		  if(!l.isEmpty())
			  for(int i = 0; i < rand(min,max); i++){
				  String lore = l.get(rand(1,l.size())-1);
				  l.remove(lore);
				  loreList.add(prosessLootName(lore, stack));
			  }
	  }
      ItemMeta meta = stack.getItemMeta();
      if (name != null) {
        meta.setDisplayName(name);
      }
      if (!loreList.isEmpty()) {
        meta.setLore(loreList);
      }
      if (meta != null) {
        stack.setItemMeta(meta);
      }
      //Colour
      if (this.lootFile.getString("loot." + loot + ".colour") != null && stack.getType().toString().toLowerCase().contains("leather")){
    	  String c = this.lootFile.getString("loot." + loot + ".colour");
    	  String[] split = c.split(",");
    	  Color colour = Color.fromRGB(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    	  dye(stack, colour);
      }
      //Book
      if ((stack.getType().equals(Material.WRITTEN_BOOK)) || (stack.getType().equals(Material.BOOK_AND_QUILL))){
        BookMeta bMeta = (BookMeta)stack.getItemMeta();
        if (this.lootFile.getString("loot." + loot + ".author") != null)
        {
          String author = this.lootFile.getString("loot." + loot + ".author");
          author = ChatColor.translateAlternateColorCodes('&', author);
          bMeta.setAuthor(author);
        }
        if (this.lootFile.getString("loot." + loot + ".title") != null)
        {
          String title = this.lootFile.getString("loot." + loot + ".title");
          title = ChatColor.translateAlternateColorCodes('&', title);
          bMeta.setTitle(title);
        }
        if (this.lootFile.getString("loot." + loot + ".pages") != null) {
          for (String i : this.lootFile.getConfigurationSection("loot." + loot + ".pages").getKeys(false))
          {
            String page = this.lootFile.getString("loot." + loot + ".pages." + i);
            page = ChatColor.translateAlternateColorCodes('&', page);
            bMeta.addPage(new String[] { page });
          }
        }
        stack.setItemMeta(bMeta);
      }
		//Banners
		if(stack.getType().equals(Material.BANNER)){
			BannerMeta b = (BannerMeta) stack.getItemMeta();
			List<Pattern> patList = (List<Pattern>) lootFile.getList("loot." + loot + ".patterns");
			if(patList != null && (!patList.isEmpty()))
				b.setPatterns(patList);
			stack.setItemMeta(b);
		}
	    //Shield
		if(is9())
			if(stack.getType().equals(Material.SHIELD)){
				ItemMeta im = stack.getItemMeta();
				BlockStateMeta bmeta = (BlockStateMeta) im;
	
				Banner b = (Banner) bmeta.getBlockState();
				List<Pattern> patList = (List<Pattern>) lootFile.getList("loot." + loot + ".patterns");
				b.setBaseColor(DyeColor.valueOf(lootFile.getString("loot." + loot + ".colour")));
				b.setPatterns(patList);
				b.update();
				bmeta.setBlockState(b);
				stack.setItemMeta(bmeta);
			}
		//Owner
		if ((stack.getType().equals(Material.SKULL_ITEM)) && (stack.getDurability() == 3)){
			String owner = this.lootFile.getString("loot." + loot + ".owner");
			SkullMeta sm = (SkullMeta)stack.getItemMeta();
			sm.setOwner(owner);
			stack.setItemMeta(sm);
		}
		//Potions
		if(is9())
			if(lootFile.getString("loot." + loot + ".potion") != null)
				if(stack.getType().equals(Material.POTION) || stack.getType().equals(Material.SPLASH_POTION) || stack.getType().equals(Material.LINGERING_POTION)){
					PotionMeta pMeta = (PotionMeta) stack.getItemMeta();
					String pn = lootFile.getString("loot." + loot + ".potion");
					pMeta.setBasePotionData(new PotionData(PotionType.getByEffect(PotionEffectType.getByName(pn)), false, false));
					stack.setItemMeta(pMeta);
				}
      int enchAmount = 0;
      for (int e = 0; e <= 10; e++) {
        if (this.lootFile.getString("loot." + loot + ".enchantments." + e) != null) {
          enchAmount++;
        }
      }
      if (enchAmount > 0)
      {
        int enMin = enchAmount;
        int enMax = enchAmount;
        if ((this.lootFile.getString("loot." + loot + ".minEnchantments") != null) && (this.lootFile.getString("loot." + loot + ".maxEnchantments") != null))
        {
          enMin = this.lootFile.getInt("loot." + loot + ".minEnchantments");
          enMax = this.lootFile.getInt("loot." + loot + ".maxEnchantments");
        }
        int enchNeeded = new Random().nextInt(enMax + 1 - enMin) + enMin;
        if (enchNeeded > enMax) {
          enchNeeded = enMax;
        }
        ArrayList<LevelledEnchantment> enchList = new ArrayList();
        int safety = 0;
        int j = 0;
        int chance;
        do
        {
          if (this.lootFile.getString("loot." + loot + ".enchantments." + j) != null)
          {
            int enChance = 1;
            if (this.lootFile.getString("loot." + loot + ".enchantments." + j + ".chance") != null) {
              enChance = this.lootFile.getInt("loot." + loot + ".enchantments." + j + ".chance");
            }
            chance = new Random().nextInt(enChance - 1 + 1) + 1;
            if (chance == 1)
            {
              String enchantment = this.lootFile.getString("loot." + loot + ".enchantments." + j + ".enchantment");
              
              String levelString = this.lootFile.getString("loot." + loot + ".enchantments." + j + ".level");
              int level = getIntFromString(levelString);
              if (Enchantment.getByName(enchantment) != null)
              {
                if (level < 1) {
                  level = 1;
                }
                LevelledEnchantment le = new LevelledEnchantment(Enchantment.getByName(enchantment), level);
                
                boolean con = false;
                for (LevelledEnchantment testE : enchList) {
                  if (testE.getEnchantment.equals(le.getEnchantment)) {
                    con = true;
                  }
                }
                if (!con) {
                  enchList.add(le);
                }
              }
              else
              {
                System.out.println("Error: No valid drops found!");
                System.out.println("Error: " + enchantment + " is not a valid enchantment!");
                return null;
              }
            }
          }
          j++;
          if (j > enchAmount)
          {
            j = 0;
            safety++;
          }
          if (safety >= enchAmount * 100)
          {
            System.out.println("Error: No valid drops found!");
            System.out.println("Error: Please increase chance for enchantments on item " + loot);
            return null;
          }
        } while (enchList.size() != enchNeeded);
        for (LevelledEnchantment le : enchList) {
          if (stack.getType().equals(Material.ENCHANTED_BOOK))
          {
            EnchantmentStorageMeta enchantMeta = (EnchantmentStorageMeta)stack.getItemMeta();
            enchantMeta.addStoredEnchant(le.getEnchantment, le.getLevel, true);
            stack.setItemMeta(enchantMeta);
          }
          else
          {
            stack.addUnsafeEnchantment(le.getEnchantment, le.getLevel);
          }
        }
      }
      return stack;
	}catch(Exception e){this.getLogger().log(Level.SEVERE, e.getMessage(), true); e.printStackTrace();}
    return null;
  }
  
  
  private void setItem(ItemStack s, String path, FileConfiguration fc){
    if (s != null){
      fc.set(path + ".item", Integer.valueOf(s.getTypeId()));
      fc.set(path + ".amount", Integer.valueOf(s.getAmount()));
      fc.set(path + ".durability", Short.valueOf(s.getDurability()));
      if (s.getItemMeta() != null){
        if (s.getItemMeta().getDisplayName() != null) {
          fc.set(path + ".name", s.getItemMeta().getDisplayName());
        }
        if (s.getItemMeta().getLore() != null) {
          for (int l = 0; l < s.getItemMeta().getLore().size(); l++) {
            if (s.getItemMeta().getLore().get(l) != null) {
              fc.set(path + ".lore" + l, s.getItemMeta().getLore().get(l));
            }
          }
        }
      }
      Enchantment e;
      if (s.getEnchantments() != null) {
        for (Map.Entry<Enchantment, Integer> hm : s.getEnchantments().entrySet()){
          e = (Enchantment)hm.getKey();
          int level = ((Integer)hm.getValue()).intValue();
          for (int ei = 0; ei < 13; ei++) {
            if (fc.getString(path + ".enchantments." + ei) == null){
              fc.set(path + ".enchantments." + ei + ".enchantment", e.getName());
              fc.set(path + ".enchantments." + ei + ".level", Integer.valueOf(level));
              break;
            }
          }
        }
      }
      if (s.getType().equals(Material.ENCHANTED_BOOK)) {
        EnchantmentStorageMeta em = (EnchantmentStorageMeta)s.getItemMeta();
        if (em.getStoredEnchants() != null) {
          for (Object hm : em.getStoredEnchants().entrySet()){
            e = (Enchantment)((Map.Entry)hm).getKey();
            int level = ((Integer)((Map.Entry)hm).getValue()).intValue();
            for (int ei = 0; ei < 13; ei++) {
              if (fc.getString(path + ".enchantments." + ei) == null){
                fc.set(path + ".enchantments." + ei + ".enchantment", e.getName());
                fc.set(path + ".enchantments." + ei + ".level", Integer.valueOf(level));
                break;
              }
            }
          }
        }
      }
      if ((s.getType().equals(Material.WRITTEN_BOOK)) || (s.getType().equals(Material.BOOK_AND_QUILL)))
      {
        BookMeta meta = (BookMeta)s.getItemMeta();
        if (meta.getAuthor() != null) {
          fc.set(path + ".author", meta.getAuthor());
        }
        if (meta.getTitle() != null) {
          fc.set(path + ".title", meta.getTitle());
        }
        int i = 0;
        if (meta.getPages() != null) {
          for (String p : meta.getPages())
          {
            fc.set(path + ".pages." + i, p);
            i++;
          }
        }
      }
      //Banner
      if (s.getType().equals(Material.BANNER)){
    	  BannerMeta b = (BannerMeta)s.getItemMeta();
    	  if(b != null){
	    	  List patList = b.getPatterns();
	    	  if(patList != null && (!patList.isEmpty()))
	    		  fc.set(path + ".patterns", patList);
    	  }
      }
      //Shield
      if(is9())
	      if (s.getType().equals(Material.SHIELD)){
	    	  ItemMeta im = s.getItemMeta();
	    	  BlockStateMeta bmeta = (BlockStateMeta) im;
	    	  Banner b = (Banner) bmeta.getBlockState();
	    	  if(b != null){
	    		  fc.set(path + ".colour", b.getBaseColor().toString());
		    	  List patList = b.getPatterns();
		    	  if(patList != null && (!patList.isEmpty()))
		    		  fc.set(path + ".patterns", patList);
	    	  }
	      }
      //Potions
      if(is9())
	      if(s.getType().equals(Material.POTION) || s.getType().equals(Material.SPLASH_POTION) || s.getType().equals(Material.LINGERING_POTION)){
				PotionMeta pMeta = (PotionMeta) s.getItemMeta();
				org.bukkit.potion.PotionData pd = pMeta.getBasePotionData();
				//System.out.println("CP: " + pMeta.getCustomEffects());
				//String ps = pd.getType().getEffectType().getName()+":"+pd.getType().get;
				fc.set(path+".potion", pd.getType().getEffectType().getName());
	      }
      if ((s.getType().equals(Material.LEATHER_BOOTS)) || (s.getType().equals(Material.LEATHER_CHESTPLATE)) || (s.getType().equals(Material.LEATHER_HELMET)) || (s.getType().equals(Material.LEATHER_LEGGINGS))){
    	  LeatherArmorMeta l = (LeatherArmorMeta)s.getItemMeta();
    	  Color c = l.getColor();
    	  String color = c.getRed() + "," + c.getGreen() + "," + c.getBlue();
    	  fc.set(path + ".colour", color);
      }
      if ((s.getType().equals(Material.SKULL_ITEM)) && (s.getDurability() == 3)) {
    	  SkullMeta sm = (SkullMeta)s.getItemMeta();
//    	  fc.set(path + ".flags", sm.getItemFlags());
//    	  for(ItemFlag f : s)
    	  fc.set(path + ".owner", sm.getOwner());
    	  //fc.set(path + ".SkullOwner", getSkullOwner(s));
      }
      //Flags
     // System.out.println("FLAGS: " + s.getItemMeta().getItemFlags());
      if(s.getItemMeta().getItemFlags() != null){
    	  ArrayList<String> flags = new ArrayList<String>();
    	  for(ItemFlag f : s.getItemMeta().getItemFlags())
    		  if(f != null)
    			  flags.add(f.name());
    	  //System.out.println("FLAGS2: " + flags);
    	  if(!flags.isEmpty())
    		  fc.set(path + ".flags", flags);
      }
    }else{
    	System.out.println("Item is null!");
    }
    try {
      this.lootFile.save(this.lootYML);
    }catch (IOException localIOException) {}
    saveConfig();
  }
  
  private String prosessLootName(String name, ItemStack stack){
      name = ChatColor.translateAlternateColorCodes('&', name);
      String itemName = stack.getType().name();
      itemName = itemName.replace("_", " ");
      itemName = itemName.toLowerCase();
      name = name.replace("<itemName>", itemName);
      return name;
  }
  
  public int getIntFromString(String setAmountString){
    int setAmount = 1;
    if (setAmountString.contains("-"))
    {
      String[] split = setAmountString.split("-");
      try
      {
        Integer minSetAmount = Integer.valueOf(Integer.parseInt(split[0]));
        Integer maxSetAmount = Integer.valueOf(Integer.parseInt(split[1]));
        setAmount = new Random().nextInt(maxSetAmount.intValue() - minSetAmount.intValue() + 1) + minSetAmount.intValue();
      }
      catch (Exception e)
      {
        System.out.println("getIntFromString: " + e);
      }
    }
    else
    {
      setAmount = Integer.parseInt(setAmountString);
    }
    return setAmount;
  }
  
  public boolean isBaby(Entity mob)
  {
    if (mob.getType().equals(EntityType.ZOMBIE))
    {
      Zombie zombie = (Zombie)mob;
      if (zombie.isBaby()) {
        return true;
      }
    }
    else if (mob.getType().equals(EntityType.PIG_ZOMBIE))
    {
      PigZombie pigzombie = (PigZombie)mob;
      if (pigzombie.isBaby()) {
        return true;
      }
    }
    return false;
  }
  
	public String getEffect(){
		String effect = "mobSpawnerFire";
		try{
			//Get Enabled Particles
			ArrayList<String> partTypes = (ArrayList<String>) getConfig().getStringList("mobParticles");
			//Get Random Particle
			effect = partTypes.get(new Random().nextInt(partTypes.size()));
		}catch(Exception e){System.out.println("Error: " + e);}
		return effect;
	}
  
	@SuppressWarnings("unused")
	public void displayEffect(Location l, String effect){
		if(effect == null){
			try{
				//Get Particles
				effect = getEffect();
			}catch(Exception e){effect = "mobSpawnerFire";}
		}
		//Get Effect and Datas
		String[] split = effect.split(":");
    	effect = split[0];
    	int data1 = Integer.parseInt(split[1]);
    	int data2 = Integer.parseInt(split[2]);
		//Players
//    	int radius = 25;
//    	Entity e = l.getWorld().spawnEntity(l, EntityType.EXPERIENCE_ORB);
//    	ArrayList<Player> pList = new ArrayList<Player>();
//    			for(Entity ent : e.getNearbyEntities(radius, radius, radius))
//    				if(ent instanceof Player)
//    					pList.add((Player)ent);
//    	e.remove();
    	try{
    		String f = "FLAME";
			if(effect.equals("potionBrake")){
				f = "SWIRL";
			}else if(effect.equals("smoke")){
				f = "SMOKE";
			}else if(effect.equals("blockBrake")){
				f = "FOOTSTEP";
			}else if(effect.equals("hugeExplode")){
				f = "BIG_EXPLODE";
			}else if(effect.equals("angryVillager")){
				f = "THUNDERCLOUD";
			}else if(effect.equals("cloud")){
				f = "CLOUD";
			}else if(effect.equals("criticalHit")){
				f = "CRITICALS";
			}else if(effect.equals("mobSpell")){
				f = "INVIS_SWIRL";
			}else if(effect.equals("enchantmentTable")){
				//ParticleEffect.ENCHANTMENT_TABLE.display((float)0, (float)0, (float)0, (float)data1, data2, l, radius)";
				//return";
				f = "ENCHANTS";
			}else if(effect.equals("ender")){
				f = "ENDER";
			}else if(effect.equals("explode")){
				f = "EXPLODE";
			}else if(effect.equals("greenSparkle")){
				f = "HAPPY";
			}else if(effect.equals("heart")){
				f = "HEARTS";
			}else if(effect.equals("largeExplode")){
				f = "LARGE_SMOKE";
			}else if(effect.equals("splash")){
				f = "SPLASH";
			}else if(effect.equals("largeSmoke")){
				f = "LARGE_SMOKE";
			}else if(effect.equals("lavaSpark")){
				f = "LAVA_SPARK";
			}else if(effect.equals("magicCriticalHit")){
				f = "ENCHANT_CRITS";
			}else if(effect.equals("noteBlock")){
				f = "NOTES";
			}else if(effect.equals("tileDust")){
				f = "ITEM_CRACK";
			}else if(effect.equals("colouredDust")){
				f = "REDSTONE_DUST";
			}else if(effect.equals("flame")){
				f = "FLAME";
			}else if(effect.equals("witchMagic")){
				f = "WITCH_MAGIC";
			}else if(effect != null)
				f = effect;
			if(f != null){
				displayParticle(f, l, 1.0, data1, data2);
				//ParticleEffects_1_9_4.sendToLocation(f, l, 0, 0, 0, data1, data2);
				//p.spigot().playEffect(l, f, data1, data2, 0, 0, 0, data1, data2, radius);
			}else
				l.getWorld().playEffect(l, Effect.MOBSPAWNER_FLAMES, data2);					
    	}catch(Exception x){x.printStackTrace();}
	}
  
	public void showEffect(){
		try{
			//GUI Bars And Stuff
			scoreCheck();
			//Mob Stuff
			ArrayList<Mob> tmp = (ArrayList<Mob>) infernalList.clone();
			for(Mob m : tmp){
				final Entity mob = m.entity;
				UUID id = mob.getUniqueId();
				int index = idSearch(id);
				if (mob.isValid() && (!mob.isDead()) && (mob.getLocation() != null) && (index != -1) && (mob.getLocation().getChunk().isLoaded())){
					//System.out.println("PE2");
					Location feet = mob.getLocation();
					Location head = mob.getLocation();
					head.setY(head.getY() +1);
					if (getConfig().getBoolean("enableParticles") == true){
						displayEffect(feet, m.effect);
						//mob.getWorld().playEffect(feet, Effect.ENDER_SIGNAL, 1);
						if (isSmall(mob) == false){
							displayEffect(head, m.effect);
							//mob.getWorld().playEffect(head, Effect.ENDER_SIGNAL, 1);
						}
						if ((mob.getType().equals(EntityType.ENDERMAN)) || (mob.getType().equals(EntityType.IRON_GOLEM))){
							head.setY(head.getY() +1);
							displayEffect(head, m.effect);
							//mob.getWorld().playEffect(head, Effect.ENDER_SIGNAL, 1);
						}
					}
					//Ability's
					ArrayList<String> abilityList = findMobAbilities(id);
					//System.out.println("PE1");
					if (!mob.isDead()){
						for (String ability : abilityList){
							Random rand = new Random();
							int min = 1;
							int max = 10;
							int randomNum = rand.nextInt(max - min + 1) + min;
							//System.out.println("PE: " + ability);
							if (ability.equals("cloaked")){
								((LivingEntity) mob).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 40, 1), true);
							}else if (ability.equals("armoured")){
								if ((!(mob instanceof Skeleton)) && (!(mob instanceof Zombie)) && (!(mob instanceof PigZombie))){
									((LivingEntity) mob).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 1), true);
								}
							}else if (ability.equals("1up")){
								if (((Damageable) mob).getHealth() <= 5){
									Mob oneUpper = (Mob) infernalList.get(index);
									if (oneUpper.lives > 1) {
										//System.out.print("1");//-------------------------------Debug
										((Damageable) mob).setHealth(((Damageable) mob).getMaxHealth());
										//System.out.print("UP!");//-------------------------------Debug
										//Mob newMob = new Mob(mob, id, mob.getWorld(), oneUpper.infernal, abilityList, 1, getEffect());
										//infernalList.set(index, newMob);
										oneUpper.setLives(oneUpper.lives - 1);
									}
								}
							}else if (ability.equals("sprint")){
								((LivingEntity) mob).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 1), true);
							}else if (ability.equals("molten")){
								((LivingEntity) mob).addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 40, 1), true);
							}else if (ability.equals("tosser")){
								if (randomNum < 6){
						    		double radius = 6D;
						    		ArrayList<Player> near = (ArrayList<Player>) mob.getWorld().getPlayers();
								    for(Player player : near) {
								    	if(player.getLocation().distance(mob.getLocation()) <= radius) {
								    		if ((!player.isSneaking()) && (!player.getGameMode().equals(GameMode.CREATIVE))){
								    			player.setVelocity(mob.getLocation().toVector().subtract(player.getLocation().toVector()));
								    		}
								    	}
								    }
								}
							}else if (ability.equals("gravity")){
								if (randomNum >= 9){
						    		double radius = 10D;
						    		ArrayList<Player> near = (ArrayList<Player>) mob.getWorld().getPlayers();
								    	for(Player player : near) {
								    		if(player.getLocation().distance(mob.getLocation()) <= radius) {
								    			Location feetBlock = player.getLocation();
								    			feetBlock.setY(feetBlock.getY() - 2);
								    			Block block = feetBlock.getWorld().getBlockAt(feetBlock);
								    			if ((!block.getType().equals(Material.AIR)) && (!player.getGameMode().equals(GameMode.CREATIVE))){
								    				int amount = 6;
								    				if (getConfig().getString("gravityLevitateLength") != null){
								    					amount = getConfig().getInt("gravityLevitateLength");
								    				}
								    				levitate(player, amount);
								    			}
								    		}
										}
								}
							}else if ((ability.equals("ghastly")) || (ability.equals("necromancer"))){
								if ((randomNum == 6)  && (!mob.isDead())){
						    		double radius = 20D;
						    		ArrayList<Player> near = (ArrayList<Player>) mob.getWorld().getPlayers();
							    	for(Player player : near) {
							    		if((player.getLocation().distance(mob.getLocation()) <= radius) && (!player.getGameMode().equals(GameMode.CREATIVE))) {
							    			Fireball fb = null;
							    			if (ability.equals("ghastly")){
							    				fb = ((LivingEntity) mob).launchProjectile(Fireball.class);
							    				if(is9()){
							    					player.getWorld().playSound(player.getLocation(), Sound.AMBIENT_CAVE, 5, 1);
							    				}else
							    					player.getWorld().playSound(player.getLocation(), Sound.valueOf("AMBIENCE_CAVE"), 5, 1);
							    			}else{
												fb = ((LivingEntity) mob).launchProjectile(WitherSkull.class);
							    			}
											//Location loc1 = player.getEyeLocation();
											//Location loc2 = mob.getLocation();
											//int arrowSpeed = 1;
											//loc2.setY(loc2.getBlockY()+2);
											//loc2.setX(loc2.getBlockX()+0.5);
											//loc2.setZ(loc2.getBlockZ()+0.5);
											//Arrow ar = mob.getWorld().spawnArrow(loc2, new Vector(loc1.getX()-loc2.getX(), loc1.getY()-loc2.getY(), loc1.getZ()-loc2.getZ()), arrowSpeed, 12);
											//Vector vel = ar.getVelocity();
											//fb.setVelocity(vel);
											//ar.remove();
							    			moveToward(fb, player.getLocation(), 0.6);
							    		}
							    	}
							    }
							}
						}
					}
				}
			}
		}catch(Exception x){x.printStackTrace();}
    	serverTime = serverTime + 1;
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
		     public void run() {
		    	showEffect();
			 }
		}, (1 * 20));
	}
  
  public boolean isSmall(Entity mob)
  {
    if ((isBaby(mob)) && (mob.getType().equals(EntityType.BAT)) && (mob.getType().equals(EntityType.CAVE_SPIDER)) && (mob.getType().equals(EntityType.CHICKEN)) && (mob.getType().equals(EntityType.COW)) && (mob.getType().equals(EntityType.MUSHROOM_COW)) && (mob.getType().equals(EntityType.PIG)) && (mob.getType().equals(EntityType.OCELOT)) && (mob.getType().equals(EntityType.SHEEP)) && (mob.getType().equals(EntityType.SILVERFISH)) && (mob.getType().equals(EntityType.SPIDER)) && (mob.getType().equals(EntityType.WOLF))) {
      return true;
    }
    return false;
  }
  
  public void moveToward(final Entity e, final Location to, final double speed)
  {
    if (e.isDead()) {
      return;
    }
    Vector direction = to.toVector().subtract(e.getLocation().toVector()).normalize();
    e.setVelocity(direction.multiply(speed));
    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
    {
      public void run()
      {
        try
        {
          infernal_mobs.this.moveToward(e, to, speed);
        }
        catch (Exception localException) {}
      }
    }, 1L);
  }
  
	public void applyEffect(){
		//Check Players
		for(Player p : this.getServer().getOnlinePlayers()){
			World world = p.getWorld();
			if((getConfig().getList("effectworlds").contains(world.getName())) || (getConfig().getList("effectworlds").contains("<all>"))){
				//Get Player Items
				//ArrayList<ItemStack> items = new ArrayList<ItemStack>();
				HashMap<Integer, ItemStack> itemMap = new HashMap<Integer, ItemStack>();
				//for(int i = 0; i < 9; i++){
				for(int i : (ArrayList<Integer>)getConfig().getList("enabledCharmSlots")){
				    ItemStack in;
				    if(i == 36 && is9()){
				    	in = p.getInventory().getItemInOffHand();
				    }else
				    	in = p.getInventory().getItem(i);
				    //System.out.println(i + " in: " + in);
					if(in != null)
						itemMap.put(i, in);
				}
				int ai = 100;
				for(ItemStack ar : p.getInventory().getArmorContents())
					if(ar != null){
						itemMap.put(ai, ar);
						ai = ai + 1;
					}
				//for(int i = 0; i < 256; i++){
				if(lootFile.getString("potionEffects") != null)
					for (String id : lootFile.getConfigurationSection("potionEffects").getKeys(false))
						if((lootFile.getString("potionEffects." + id) != null) && (lootFile.getString("potionEffects." + id + ".attackEffect") == null) && (lootFile.getString("potionEffects." + id + ".attackHelpEffect") == null)){
							ArrayList<ItemStack> itemsPlayerHas = new ArrayList<ItemStack>();
							for(int neededItemIndex : lootFile.getIntegerList("potionEffects." + id + ".requiredItems"))	{
								ItemStack neededItem = getItem(neededItemIndex);
								//for(ItemStack check : items){
								for (Map.Entry<Integer, ItemStack> hm : itemMap.entrySet()){
									ItemStack check = hm.getValue();
									try{
										//System.out.println(neededItem.toString() + " =? " + check.toString());
		    							if((neededItem.getItemMeta() == null) || (neededItem.getItemMeta().getDisplayName() == null) || (check.getItemMeta().getDisplayName().equals(neededItem.getItemMeta().getDisplayName()))){
		    								if(check.getTypeId() == neededItem.getTypeId()){
		    									if((neededItem.getType().getMaxDurability() > 0) || (check.getDurability() == (neededItem.getDurability()))){
				    								//if(!itemsPlayerHas.contains(neededItem)){
		    										if(isArmor(neededItem) == false || hm.getKey() >= 100)
				    									itemsPlayerHas.add(neededItem);
				    								//}
		    									}
			    							}
		    							}
	  							}catch(Exception e){/**System.out.println("Error: " + e);**/}
	  						}
	  					}
  					
  					if(itemsPlayerHas.size() >= lootFile.getIntegerList("potionEffects." + id + ".requiredItems").size()){
  						applyEffects(p, Integer.parseInt(id));
  					}
				}
			}
		}
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
		     public void run() {
		    	 applyEffect();
			 }
		}, (10 * 20));
	}
	
	private boolean isArmor(ItemStack s){
		String t = s.getType().toString().toLowerCase();
		if(t.contains("helm") || t.contains("plate") || t.contains("leg") || t.contains("boot"))
			return true;
		return false;
	}
  
  public void applyEffects(LivingEntity e, int effectID){
    int level = this.lootFile.getInt("potionEffects." + effectID + ".level");
    String name = this.lootFile.getString("potionEffects." + effectID + ".potion");
    if ((PotionEffectType.getByName(name).equals(PotionEffectType.HARM)) || (PotionEffectType.getByName(name).equals(PotionEffectType.HEAL))) {
      e.addPotionEffect(new PotionEffect(PotionEffectType.getByName(name), 1, level - 1), true);
    } else {
      e.addPotionEffect(new PotionEffect(PotionEffectType.getByName(name), 400, level - 1), true);
    }
    if (this.lootFile.getString("potionEffects." + effectID + ".particleEffect") != null)
    {
      String effect = this.lootFile.getString("potionEffects." + effectID + ".particleEffect");
      showEffectParticles(e, effect, 15);
    }
  }
  
  private void showEffectParticles(final Entity p, final String e, int time)
  {
    displayEffect(p.getLocation(), e);
    final int nt = time - 1;
    if (time > 0) {
      Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
      {
        public void run()
        {
          infernal_mobs.this.showEffectParticles(p, e, nt);
        }
      }, 20L);
    }
  }
  
  private void levitate(final Entity e, final int time){
	  if(is9() && (e instanceof LivingEntity)){
		  ((LivingEntity)e).addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, time*20, 0));
	  }else{
		  if (((e instanceof Player)) && (((Player)e).getAllowFlight()) == false) {
		    	((Player)e).setAllowFlight(true);
		    	levitateList.add(((Player)e));
		  }
		  for (int i = 0; i < 40; i++) {
			  Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
				  public void run(){
					  Vector vec = e.getVelocity();
					  vec.add(new Vector(0.0D, 0.1D, 0.0D));
					  e.setVelocity(vec);
				  }
			  }, i);
		  }
		  Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
			  public void run(){
				  infernal_mobs.this.airHold(e, time - 2);
			  }
		  }, 20L);
	  }
  }
  
  public void airHold(final Entity e, int time){
	  for (int i = 0; i < time * 20; i++){
		  Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
			  public void run(){
				  Vector vec = e.getVelocity();
				  vec.setY(0.01D);
				  e.setVelocity(vec);
			  }
		  }, i);
		  i++;
	  }
	  Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
		  public void run(){
			  if ((e instanceof Player) && levitateList.contains(((Player)e))) {
				  ((Player)e).setAllowFlight(false);
				  levitateList.remove(((Player)e));
			  }
		  }
	  }, 20 * time);
  }
  
	public boolean doEffect(Player player, final Entity mob, boolean playerIsVictom) throws Exception {
		//Do Player Loot Effects
		if(playerIsVictom == false){
			//Get Player Item In Hand
			ItemStack itemUsed = player.getItemInHand();
			//Get Player Items
			ArrayList<ItemStack> items = new ArrayList<ItemStack>();
			for(int i = 0; i < 9; i++){
			    ItemStack in = player.getInventory().getItem(i);
				if(in != null)
					items.add(in);
			}
			for(ItemStack ar : player.getInventory().getArmorContents())
				if(ar != null)
					items.add(ar);
			for(int i = 0; i < 256; i++){
				if(lootFile.getString("potionEffects." + i) != null){
					if(lootFile.getString("potionEffects." + i + ".attackEffect") != null){
						boolean effectsPlayer = true;
						if(lootFile.getString("potionEffects." + i + ".attackEffect").equals("target"))
							effectsPlayer = false;
						for(int neededItemIndex : lootFile.getIntegerList("potionEffects." + i + ".requiredItems"))	{
	  						ItemStack neededItem = getItem(neededItemIndex);
							try{
								if((neededItem.getItemMeta() == null) || (neededItem.getItemMeta().getDisplayName() == null) || (itemUsed.getItemMeta().getDisplayName().equals(neededItem.getItemMeta().getDisplayName()))){
	  								if(itemUsed.getTypeId() == neededItem.getTypeId()){
	  									if((neededItem.getType().getMaxDurability() > 0) || (itemUsed.getDurability() == (neededItem.getDurability()))){
	  										//Player Using Item
	  										if(effectsPlayer == true){
	  											applyEffects(player, i);
	  										}else{
	  											if(mob instanceof LivingEntity)
	  												applyEffects((LivingEntity)mob, i);
	  										}
	  									}
		    						}
	  							}
							}catch(Exception e){/**System.out.println("Error: " + e);**/}
	  					}
					}else if(lootFile.getString("potionEffects." + i + ".attackHelpEffect") != null){
						boolean effectsPlayer = true;
						if(lootFile.getString("potionEffects." + i + ".attackHelpEffect").equals("target"))
							effectsPlayer = false;
						ArrayList<ItemStack> itemsPlayerHas = new ArrayList<ItemStack>();
	  					for(int neededItemIndex : lootFile.getIntegerList("potionEffects." + i + ".requiredItems"))	{
	  						ItemStack neededItem = getItem(neededItemIndex);
	  						for(ItemStack check : items){
	  							try{
		    							if((neededItem.getItemMeta() == null) || (neededItem.getItemMeta().getDisplayName() == null) || (check.getItemMeta().getDisplayName().equals(neededItem.getItemMeta().getDisplayName()))){
		    								if(check.getTypeId() == neededItem.getTypeId()){
		    									if((neededItem.getType().getMaxDurability() > 0) || (check.getDurability() == (neededItem.getDurability()))){
				    								if(!itemsPlayerHas.contains(neededItem)){
				    									itemsPlayerHas.add(neededItem);
				    								}
		    									}
			    							}
		    							}
	  							}catch(Exception e){/**System.out.println("Error: " + e);**/}
	  						}
	  					}
	  					if(itemsPlayerHas.size() >= lootFile.getIntegerList("potionEffects." + i + ".requiredItems").size()){
								//Player Using Item
								if(effectsPlayer == true){
									applyEffects(player, i);
								}else{
									if(mob instanceof LivingEntity)
										applyEffects((LivingEntity)mob, i);
								}
	  					}
					}
				}
			}
		}
		//Do Mob Effects
		try{
			UUID id = mob.getUniqueId();
			if (idSearch(id) != -1) {
				//fixBar(player);
				ArrayList<String> abilityList = findMobAbilities(id);
				if ((!player.isDead()) && (!mob.isDead())){
					for (String ability : abilityList)
						doMagic(player, mob, playerIsVictom, ability, id);
				}else{
					return false;
				}
				return true;
			}else{
				return false;
			}
		}catch(Exception e){/**System.out.println("Do Effect Error: " + e);**/}
		return false;
	}
  
	public void doMagic(Entity vic, Entity atc, boolean playerIsVictom, String ability, UUID id){
		//System.out.println("Do Magic: "+ability);
		int min = 1;
		int max = 10;
		int randomNum = new Random().nextInt(max - min + 1) + min;
		if ((atc instanceof Player)) {
			randomNum = 1;
		}
		try{
			if ((atc instanceof Player)) {
				if (ability.equals("tosser")){
					if ((!(vic instanceof Player)) || ((!((Player)vic).isSneaking()) && (!((Player)vic).getGameMode().equals(GameMode.CREATIVE)))) {
						vic.setVelocity(atc.getLocation().toVector().subtract(vic.getLocation().toVector()));
					}
				}else if (ability.equals("gravity")){
					if ((!(vic instanceof Player)) || ((!((Player)vic).isSneaking()) && (!((Player)vic).getGameMode().equals(GameMode.CREATIVE)))){
						Location feetBlock = vic.getLocation();
						feetBlock.setY(feetBlock.getY() - 2.0D);
						Block block = feetBlock.getWorld().getBlockAt(feetBlock);
						if (!block.getType().equals(Material.AIR)){
							int amount = 6;
							if (getConfig().getString("gravityLevitateLength") != null) {
								amount = getConfig().getInt("gravityLevitateLength");
							}
							levitate(vic, amount);
						}
					}
				}else if ((ability.equals("ghastly")) || (ability.equals("necromancer"))){
					//System.out.println("ghastly");
					if ((!vic.isDead()) && ((!(vic instanceof Player)) || ((!((Player)vic).isSneaking()) && (!((Player)vic).getGameMode().equals(GameMode.CREATIVE))))){
						Fireball fb = null;
						if (ability.equals("ghastly")) {
							fb = (Fireball)((LivingEntity)atc).launchProjectile(Fireball.class);
						} else {
							fb = (Fireball)((LivingEntity)atc).launchProjectile(WitherSkull.class);
						}
						moveToward(fb, vic.getLocation(), 0.6D);
					}
				}
			}
			if (ability.equals("ender")) {
				atc.teleport(vic.getLocation());
			}else if ((ability.equals("poisonous")) && (isLegitVictim(atc, playerIsVictom, ability))){
				((LivingEntity)vic).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 1), true);
			}else if ((ability.equals("morph")) && (isLegitVictim(atc, playerIsVictom, ability))) {
				try{
					Entity newEnt;
					int mc = new Random().nextInt(25) + 1;
					if (mc != 20) {
						return;
					}
					Location l = atc.getLocation().clone();
					double h = ((Damageable)atc).getHealth();
					ArrayList<String> aList = ((Mob)this.infernalList.get(idSearch(id))).abilityList;
					//Remove old
					double dis = 46.0D;
					for(Entity e : atc.getNearbyEntities(dis, dis, dis))
						if(e instanceof Player)
							GUI.fixBar(((Player)e));
					atc.teleport(new Location(atc.getWorld(), l.getX(), 0.0D, l.getZ()));
					atc.remove();
					this.getLogger().log(Level.INFO, "Entity remove due to Morph");
					ArrayList<String> mList = (ArrayList)getConfig().getList("enabledmobs");
					int index = new Random().nextInt(mList.size());
					String mobName = (String)mList.get(index);
        
					newEnt = null;
					EntityType[] arrayOfEntityType;
					int j = (arrayOfEntityType = EntityType.values()).length;
					for (int i = 0; i < j; i++){
						EntityType e = arrayOfEntityType[i];
						try{
							if ((e.getName() != null) && (e.getName().equalsIgnoreCase(mobName))) {
								newEnt = vic.getWorld().spawnEntity(l, e);
							}
						}catch (Exception localException1) {}
					}
//					if (mobName.equals("WitherSkeleton")){
//						newEnt = vic.getWorld().spawnEntity(l, EntityType.SKELETON);
//						((Skeleton)newEnt).setSkeletonType(Skeleton.SkeletonType.WITHER);
//					}
					if (newEnt == null){
						System.out.println("Infernal Mobs can't find mob type: " + mobName + "!");
						return;
					}
					Mob newMob = null;
					if (aList.contains("1up")) {
						newMob = new Mob(newEnt, newEnt.getUniqueId(), vic.getWorld(), true, aList, 2, getEffect());
					} else {
						newMob = new Mob(newEnt, newEnt.getUniqueId(), vic.getWorld(), true, aList, 1, getEffect());
					}
					if (aList.contains("flying")) {
						makeFly(newEnt);
					}
					this.infernalList.set(idSearch(id), newMob);
					this.gui.setName(newEnt);
					
					giveMobGear(newEnt, true);
					
					addHealth(newEnt, aList);
					if (h >= ((Damageable)newEnt).getMaxHealth()) {
						return;
					}
					((Damageable)newEnt).setHealth(h);
				}catch (Exception ex){System.out.print("Morph Error: ");ex.printStackTrace();}
			}
			if ((ability.equals("molten")) && (isLegitVictim(atc, playerIsVictom, ability))){
				int amount;
				if (getConfig().getString("moltenBurnLength") != null) {
					amount = getConfig().getInt("moltenBurnLength");
				} else {
					amount = 5;
				}
				vic.setFireTicks(amount * 20);
			}else if ((ability.equals("blinding")) && (isLegitVictim(atc, playerIsVictom, ability))){
				((LivingEntity)vic).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1), true);
			}else if ((ability.equals("confusing")) && (isLegitVictim(atc, playerIsVictom, ability))){
				((LivingEntity)vic).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 80, 2), true);
			}else if ((ability.equals("withering")) && (isLegitVictim(atc, playerIsVictom, ability))) {
				((LivingEntity)vic).addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 180, 1), true);
			}else if ((ability.equals("thief")) && (isLegitVictim(atc, playerIsVictom, ability))) {
				if ((vic instanceof Player)){
					if ((((Player)vic).getInventory().getItemInHand() != null) && (!((Player)vic).getInventory().getItemInHand().getType().equals(Material.AIR)) && ((randomNum <= 1) || (randomNum == 1))){
						vic.getWorld().dropItemNaturally(atc.getLocation(), ((Player)vic).getInventory().getItemInHand());
						int slot = ((Player)vic).getInventory().getHeldItemSlot();
						((Player)vic).getInventory().setItem(slot, null);
					}
				}else if (((vic instanceof PigZombie)) || ((vic instanceof Zombie)) || ((vic instanceof Skeleton))) {
					EntityEquipment eq = ((LivingEntity)vic).getEquipment();
					if (eq.getItemInHand() != null){
						vic.getWorld().dropItemNaturally(atc.getLocation(), eq.getItemInHand());
						eq.setItemInHand(null);
					}
				}
			}else if ((ability.equals("quicksand")) && (isLegitVictim(atc, playerIsVictom, ability))){
				((LivingEntity)vic).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 180, 1), true);
			}
        else if ((ability.equals("bullwark")) && (isLegitVictim(atc, playerIsVictom, ability)))
        {
          ((LivingEntity)atc).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 500, 2), true);
        }
        else if ((ability.equals("rust")) && (isLegitVictim(atc, playerIsVictom, ability)))
        {
          if (((Player)vic).getInventory().getItemInHand() != null)
          {
            ItemStack damItem = ((Player)vic).getInventory().getItemInHand();
            if (((randomNum <= 3) || (randomNum == 1)) && (damItem.getMaxStackSize() == 1))
            {
              int cDur = damItem.getDurability();
              damItem.setDurability((short)(cDur + 20));
            }
          }
        }
        else if ((ability.equals("sapper")) && (isLegitVictim(atc, playerIsVictom, ability)))
        {
          ((LivingEntity)vic).addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 500, 1), true);
        }
        else if ((!ability.equals("1up")) || (!isLegitVictim(atc, playerIsVictom, ability)))
        {
          Location needAir2;
          if ((ability.equals("ender")) && (isLegitVictim(atc, playerIsVictom, ability)))
          {
            Location targetLocation = vic.getLocation();
            if (randomNum >= 8)
            {
              Random rand2 = new Random();
              int min2 = 1;
              int max2 = 4;
              int randomNum2 = rand2.nextInt(max2 - min2 + 1) + min2;
              if (randomNum2 == 1) {
                targetLocation.setZ(targetLocation.getZ() + 6.0D);
              } else if (randomNum == 2) {
                targetLocation.setZ(targetLocation.getZ() - 5.0D);
              } else if (randomNum == 3) {
                targetLocation.setX(targetLocation.getX() + 8.0D);
              } else if (randomNum == 4) {
                targetLocation.setX(targetLocation.getX() - 10.0D);
              }
              Location needAir1 = targetLocation;
              needAir2 = targetLocation;
              Location needAir3 = targetLocation;
              needAir2.setY(needAir2.getY() + 1.0D);
              needAir3.setY(needAir3.getY() + 2.0D);
              if (((needAir1.getBlock().getType().equals(Material.AIR)) || (needAir1.getBlock().getType().equals(Material.TORCH))) && 
                ((needAir2.getBlock().getType().equals(Material.AIR)) || (needAir2.getBlock().getType().equals(Material.TORCH))) && (
                (needAir3.getBlock().getType().equals(Material.AIR)) || (needAir3.getBlock().getType().equals(Material.TORCH)))) {
                atc.teleport(targetLocation);
              }
            }
          }
          else if ((ability.equals("lifesteal")) && (isLegitVictim(atc, playerIsVictom, ability)))
          {
            ((LivingEntity)atc).addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 1), true);
          }
          else if ((!ability.equals("cloaked")) || (!isLegitVictim(atc, playerIsVictom, ability)))
          {
            if ((ability.equals("storm")) && (isLegitVictim(atc, playerIsVictom, ability)))
            {
              if (((randomNum <= 2) || (randomNum == 1)) && (!atc.isDead())) {
                vic.getWorld().strikeLightning(vic.getLocation());
              }
            }
            else if ((!ability.equals("sprint")) || (!isLegitVictim(atc, playerIsVictom, ability))) {
              if ((ability.equals("webber")) && (isLegitVictim(atc, playerIsVictom, ability)))
              {
                if ((randomNum >= 8) || (randomNum == 1))
                {
                  Location feet = vic.getLocation();
                  feet.getBlock().setType(Material.WEB);
                  setAir(feet, 60);
                  
                  int rNum = new Random().nextInt(max - min + 1) + min;
                  if ((rNum == 5) && (
                    (atc.getType().equals(EntityType.SPIDER)) || (atc.getType().equals(EntityType.CAVE_SPIDER))))
                  {
                    Location l = atc.getLocation();
                    Block b = l.getBlock();
                    List<Block> blocks = getSphere(b, 4);
                    for (Block bl : blocks) {
                      if (bl.getType().equals(Material.AIR))
                      {
                        bl.setType(Material.WEB);
                        setAir(bl.getLocation(), 30);
                      }
                    }
                  }
                }
              }
              else if ((ability.equals("vengeance")) && (isLegitVictim(atc, playerIsVictom, ability)))
              {
                if ((randomNum >= 5) || (randomNum == 1))
                {
                  int amount;
                  if (getConfig().getString("vengeanceDamage") != null) {
                    amount = getConfig().getInt("vengeanceDamage");
                  } else {
                    amount = 6;
                  }
                  if ((vic instanceof LivingEntity)) {
                    ((LivingEntity)vic).damage((int)Math.round(2.0D * amount));
                  }
                }
              }
              else if ((ability.equals("weakness")) && (isLegitVictim(atc, playerIsVictom, ability)))
              {
                ((LivingEntity)vic).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 500, 1), true);
              }
              else if ((ability.equals("berserk")) && (isLegitVictim(atc, playerIsVictom, ability)))
              {
                if ((randomNum >= 5) && (!atc.isDead()))
                {
                  double health = ((Damageable)atc).getHealth();
                  ((Damageable)atc).setHealth(health - 1.0D);
                  int amount;
                  if (getConfig().getString("berserkDamage") != null) {
                    amount = getConfig().getInt("berserkDamage");
                  } else {
                    amount = 3;
                  }
                  if ((vic instanceof LivingEntity)) {
                    ((LivingEntity)vic).damage((int)Math.round(2.0D * amount));
                  }
                }
              }
              else if ((ability.equals("potions")) && (isLegitVictim(atc, playerIsVictom, ability))){
//                Potion potion = null;
//                if (randomNum == 5) {
//                  potion = new Potion(PotionType.INSTANT_DAMAGE, 2);
//                } else if (randomNum == 6) {
//                  potion = new Potion(PotionType.INSTANT_DAMAGE, 1);
//                } else if (randomNum == 7) {
//                  potion = new Potion(PotionType.WEAKNESS, 2);
//                } else if (randomNum == 8) {
//                  potion = new Potion(PotionType.POISON, 2);
//                } else if (randomNum == 9) {
//                  potion = new Potion(PotionType.SLOWNESS, 2);
//                }
//                if (potion != null){
              //potion.setSplash(true);
              //potion.apply(iStack);
                  ItemStack iStack = new ItemStack(Material.POTION);
                  PotionMeta potion = (PotionMeta) iStack.getItemMeta();
            	  switch(randomNum) {
            	  	case 5 :
            	  		potion.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 1, 2), true);
            	  	case 6 :
            	  		potion.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 1, 1), true);
            	  	case 7 :
            	  		potion.addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, (20*15), 2), true);
            	  	case 8 :
            	  		potion.addCustomEffect(new PotionEffect(PotionEffectType.POISON, (20*5), 2), true);
            	  	case 9 :
            	  		potion.addCustomEffect(new PotionEffect(PotionEffectType.SLOW, (20*10), 2), true);
            	  }
            	  iStack.setItemMeta(potion);
                  Location sploc = atc.getLocation();
                  sploc.setY(sploc.getY() + 3.0D);
                  ThrownPotion thrownPotion = (ThrownPotion)vic.getWorld().spawnEntity(sploc, EntityType.SPLASH_POTION);
                  thrownPotion.setItem(iStack);
                  Vector direction = atc.getLocation().getDirection();
                  direction.normalize();
                  direction.add(new Vector(0.0D, 0.2D, 0.0D));
                  
                  double dist = atc.getLocation().distance(vic.getLocation());
                  
                  dist /= 15.0D;
                  direction.multiply(dist);
                  thrownPotion.setVelocity(direction);
//                }
              }
              else if ((ability.equals("mama")) && (isLegitVictim(atc, playerIsVictom, ability)))
              {
                if (randomNum == 1)
                {
                  int amount;
                  if (getConfig().getString("mamaSpawnAmount") != null) {
                    amount = getConfig().getInt("mamaSpawnAmount");
                  } else {
                    amount = 3;
                  }
                  if (atc.getType().equals(EntityType.MUSHROOM_COW)) {
                    for (int i = 0; i < amount; i++)
                    {
                      MushroomCow minion = (MushroomCow)atc.getWorld().spawnEntity(atc.getLocation(), EntityType.MUSHROOM_COW);
                      minion.setBaby();
                    }
                  } else if (atc.getType().equals(EntityType.COW)) {
                    for (int i = 0; i < amount; i++)
                    {
                      Cow minion = (Cow)atc.getWorld().spawnEntity(atc.getLocation(), EntityType.COW);
                      minion.setBaby();
                    }
                  } else if (atc.getType().equals(EntityType.SHEEP)) {
                    for (int i = 0; i < amount; i++)
                    {
                      Sheep minion = (Sheep)atc.getWorld().spawnEntity(atc.getLocation(), EntityType.SHEEP);
                      minion.setBaby();
                    }
                  } else if (atc.getType().equals(EntityType.PIG)) {
                    for (int i = 0; i < amount; i++)
                    {
                      Pig minion = (Pig)atc.getWorld().spawnEntity(atc.getLocation(), EntityType.PIG);
                      minion.setBaby();
                    }
                  } else if (atc.getType().equals(EntityType.CHICKEN)) {
                    for (int i = 0; i < amount; i++)
                    {
                      Chicken minion = (Chicken)atc.getWorld().spawnEntity(atc.getLocation(), EntityType.CHICKEN);
                      minion.setBaby();
                    }
                  } else if (atc.getType().equals(EntityType.WOLF)) {
                    for (int i = 0; i < amount; i++)
                    {
                      Wolf minion = (Wolf)atc.getWorld().spawnEntity(atc.getLocation(), EntityType.WOLF);
                      minion.setBaby();
                    }
                  } else if (atc.getType().equals(EntityType.ZOMBIE)) {
                    for (int i = 0; i < amount; i++)
                    {
                      Zombie minion = (Zombie)atc.getWorld().spawnEntity(atc.getLocation(), EntityType.ZOMBIE);
                      minion.setBaby(true);
                    }
                  } else if (atc.getType().equals(EntityType.PIG_ZOMBIE)) {
                    for (int i = 0; i < amount; i++)
                    {
                      PigZombie minion = (PigZombie)atc.getWorld().spawnEntity(atc.getLocation(), EntityType.PIG_ZOMBIE);
                      minion.setBaby(true);
                    }
                  } else if (atc.getType().equals(EntityType.OCELOT)) {
                    for (int i = 0; i < amount; i++)
                    {
                      Ocelot minion = (Ocelot)atc.getWorld().spawnEntity(atc.getLocation(), EntityType.OCELOT);
                      minion.setBaby();
                    }
                  } else if (atc.getType().equals(EntityType.HORSE)) {
                    for (int i = 0; i < amount; i++)
                    {
                      Horse minion = (Horse)atc.getWorld().spawnEntity(atc.getLocation(), EntityType.HORSE);
                      minion.setBaby();
                    }
                  } else if (atc.getType().equals(EntityType.VILLAGER)) {
                    for (int i = 0; i < amount; i++)
                    {
                      Villager minion = (Villager)atc.getWorld().spawnEntity(atc.getLocation(), EntityType.VILLAGER);
                      minion.setBaby();
                    }
                  } else {
                    for (int i = 0; i < amount; i++) {
                      atc.getWorld().spawnEntity(atc.getLocation(), atc.getType());
                    }
                  }
                }
              }
              else if ((ability.equals("archer")) && (isLegitVictim(atc, playerIsVictom, ability)))
              {
                if ((randomNum > 7) || (randomNum == 1))
                {
                  ArrayList<Arrow> arrowList = new ArrayList();
                  Location loc1 = vic.getLocation();
                  Location loc2 = atc.getLocation();
                  if (!isSmall(atc)) {
                    loc2.setY(loc2.getY() + 1.0D);
                  }
                  Arrow a = ((LivingEntity)atc).launchProjectile(Arrow.class);
                  int arrowSpeed = 1;
                  loc2.setY(loc2.getBlockY() + 2);
                  loc2.setX(loc2.getBlockX() + 0.5D);
                  loc2.setZ(loc2.getBlockZ() + 0.5D);
                  Arrow a2 = a.getWorld().spawnArrow(loc2, new Vector(loc1.getX() - loc2.getX(), loc1.getY() - loc2.getY(), loc1.getZ() - loc2.getZ()), arrowSpeed, 12.0F);
                  a2.setShooter((LivingEntity)atc);
                  loc2.setY(loc2.getBlockY() + 2);
                  loc2.setX(loc2.getBlockX() - 1);
                  loc2.setZ(loc2.getBlockZ() - 1);
                  Arrow a3 = a.getWorld().spawnArrow(loc2, new Vector(loc1.getX() - loc2.getX(), loc1.getY() - loc2.getY(), loc1.getZ() - loc2.getZ()), arrowSpeed, 12.0F);
                  a3.setShooter((LivingEntity)atc);
                  arrowList.add(a);
                  arrowList.add(a2);
                  arrowList.add(a3);
                  for (Arrow ar : arrowList)
                  {
                    double minAngle = 6.283185307179586D;
                    Entity minEntity = null;
                    for (Entity entity : atc.getNearbyEntities(64.0D, 64.0D, 64.0D)) {
                      if ((((LivingEntity)atc).hasLineOfSight(entity)) && ((entity instanceof LivingEntity)) && (!entity.isDead()))
                      {
                        Vector toTarget = entity.getLocation().toVector().clone().subtract(atc.getLocation().toVector());
                        double angle = ar.getVelocity().angle(toTarget);
                        if (angle < minAngle)
                        {
                          minAngle = angle;
                          minEntity = entity;
                        }
                      }
                    }
                    if (minEntity != null) {
                      new ArrowHomingTask(ar, (LivingEntity)minEntity, this);
                    }
                  }
                }
              }
              else if ((ability.equals("firework")) && (isLegitVictim(atc, playerIsVictom, ability)))
              {
                int red = getConfig().getInt("fireworkColour.red");
                int green = getConfig().getInt("fireworkColour.green");
                int blue = getConfig().getInt("fireworkColour.blue");
                ItemStack tmpCol = new ItemStack(Material.LEATHER_HELMET, 1);
                LeatherArmorMeta tmpCol2 = (LeatherArmorMeta)tmpCol.getItemMeta();
                tmpCol2.setColor(Color.fromRGB(red, green, blue));
                
                Color col = tmpCol2.getColor();
                launchFirework(atc.getLocation(), col, 1);
              }
            }
          }
        }
    }
    catch (Exception localException2) {}
  }
  
  public void launchFirework(Location l, Color c, int speed)
  {
    Firework fw = (Firework)l.getWorld().spawn(l, Firework.class);
    FireworkMeta meta = fw.getFireworkMeta();
    meta.addEffect(FireworkEffect.builder().withColor(c).with(FireworkEffect.Type.BALL_LARGE).build());
    fw.setFireworkMeta(meta);
    fw.setVelocity(l.getDirection().multiply(speed));
    detonate(fw);
  }
  
  public void detonate(final Firework fw)
  {
    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
    {
      public void run()
      {
        try
        {
          fw.detonate();
        }
        catch (Exception localException) {}
      }
    }, 2L);
  }
  
  public boolean isLegitVictim(Entity e, boolean playerIsVictom, String ability)
  {
    if ((e instanceof Player)) {
      return true;
    }
    if (getConfig().getBoolean("effectAllPlayerAttacks")) {
      return true;
    }
    ArrayList<String> attackAbilityList = new ArrayList();
    attackAbilityList.add("poisonous");
    attackAbilityList.add("blinding");
    attackAbilityList.add("withering");
    attackAbilityList.add("thief");
    attackAbilityList.add("sapper");
    attackAbilityList.add("lifesteal");
    attackAbilityList.add("storm");
    attackAbilityList.add("webber");
    attackAbilityList.add("weakness");
    attackAbilityList.add("berserk");
    attackAbilityList.add("potions");
    attackAbilityList.add("archer");
    attackAbilityList.add("confusing");
    if ((playerIsVictom) && (attackAbilityList.contains(ability))) {
      return true;
    }
    ArrayList<String> defendAbilityList = new ArrayList();
    defendAbilityList.add("thief");
    defendAbilityList.add("storm");
    defendAbilityList.add("webber");
    defendAbilityList.add("weakness");
    defendAbilityList.add("potions");
    defendAbilityList.add("archer");
    defendAbilityList.add("quicksand");
    defendAbilityList.add("bullwark");
    defendAbilityList.add("rust");
    defendAbilityList.add("ender");
    defendAbilityList.add("vengeance");
    defendAbilityList.add("mama");
    defendAbilityList.add("firework");
    defendAbilityList.add("morph");
    if ((!playerIsVictom) && (defendAbilityList.contains(ability))) {
      return true;
    }
    return false;
  }
  
  public static List<Block> getSphere(Block block1, int radius)
  {
    List<Block> blocks = new LinkedList();
    double xi = block1.getLocation().getX() + 0.5D;
    double yi = block1.getLocation().getY() + 0.5D;
    double zi = block1.getLocation().getZ() + 0.5D;
    for (int v1 = 0; v1 <= 90; v1++)
    {
      double y = Math.sin(0.017453292519943295D * v1) * radius;
      double r = Math.cos(0.017453292519943295D * v1) * radius;
      if (v1 == 90) {
        r = 0.0D;
      }
      for (int v2 = 0; v2 <= 90; v2++)
      {
        double x = Math.sin(0.017453292519943295D * v2) * r;
        double z = Math.cos(0.017453292519943295D * v2) * r;
        if (v2 == 90) {
          z = 0.0D;
        }
        if (!blocks.contains(block1.getWorld().getBlockAt((int)(xi + x), (int)(yi + y), (int)(zi + z)))) {
          blocks.add(block1.getWorld().getBlockAt((int)(xi + x), (int)(yi + y), (int)(zi + z)));
        }
        if (!blocks.contains(block1.getWorld().getBlockAt((int)(xi - x), (int)(yi + y), (int)(zi + z)))) {
          blocks.add(block1.getWorld().getBlockAt((int)(xi - x), (int)(yi + y), (int)(zi + z)));
        }
        if (!blocks.contains(block1.getWorld().getBlockAt((int)(xi + x), (int)(yi - y), (int)(zi + z)))) {
          blocks.add(block1.getWorld().getBlockAt((int)(xi + x), (int)(yi - y), (int)(zi + z)));
        }
        if (!blocks.contains(block1.getWorld().getBlockAt((int)(xi + x), (int)(yi + y), (int)(zi - z)))) {
          blocks.add(block1.getWorld().getBlockAt((int)(xi + x), (int)(yi + y), (int)(zi - z)));
        }
        if (!blocks.contains(block1.getWorld().getBlockAt((int)(xi - x), (int)(yi - y), (int)(zi - z)))) {
          blocks.add(block1.getWorld().getBlockAt((int)(xi - x), (int)(yi - y), (int)(zi - z)));
        }
        if (!blocks.contains(block1.getWorld().getBlockAt((int)(xi + x), (int)(yi - y), (int)(zi - z)))) {
          blocks.add(block1.getWorld().getBlockAt((int)(xi + x), (int)(yi - y), (int)(zi - z)));
        }
        if (!blocks.contains(block1.getWorld().getBlockAt((int)(xi - x), (int)(yi + y), (int)(zi - z)))) {
          blocks.add(block1.getWorld().getBlockAt((int)(xi - x), (int)(yi + y), (int)(zi - z)));
        }
        if (!blocks.contains(block1.getWorld().getBlockAt((int)(xi - x), (int)(yi - y), (int)(zi + z)))) {
          blocks.add(block1.getWorld().getBlockAt((int)(xi - x), (int)(yi - y), (int)(zi + z)));
        }
      }
    }
    return blocks;
  }
  
  public void setAir(final Location block, int time)
  {
    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
    {
      public void run()
      {
        if (block.getBlock().getType().equals(Material.WEB)) {
          block.getBlock().setType(Material.AIR);
        }
      }
    }, time * 20);
  }
  
  public ArrayList<String> getAbilitiesAmount(Entity e){
	//this.getLogger().log(Level.INFO, "Getting Abilities");
    int power;
    if (getConfig().getBoolean("powerByDistance")){
      Location l = e.getWorld().getSpawnLocation();
      int m = (int)l.distance(e.getLocation()) / getConfig().getInt("addDistance");
      if (m < 1) {
        m = 1;
      }
      int add = getConfig().getInt("powerToAdd");
      power = m * add;
    }else{
      //this.getLogger().log(Level.INFO, "Default");
      int min = getConfig().getInt("minpowers");
      int max = getConfig().getInt("maxpowers");
      power = rand(min,  max);
    }
    //this.getLogger().log(Level.INFO, "Powers: " + power);
    return getAbilities(power);
  }
  
  	public ArrayList<String> getAbilities(int amount){
  		ArrayList<String> abilityList = new ArrayList();
  		ArrayList<String> allAbilitiesList = new ArrayList(Arrays.asList(new String[] { "confusing", "ghost", "morph", "mounted", "flying", "gravity", "firework", "necromancer", "archer", "molten", "mama", "potions", "explode", "berserk", "weakness", "vengeance", "webber", "storm", "sprint", "lifesteal", "ghastly", "ender", "cloaked", "1up", "sapper", "rust", "bullwark", "quicksand", "thief", "tosser", "withering", "blinding", "armoured", "poisonous" }));
  		int min = 1;
  		for (int i = 0; i < amount; i++){
  			int max = allAbilitiesList.size();
  			int randomNum = new Random().nextInt(max - min + 1) + min - 1;
      
  			String ab = (String)allAbilitiesList.get(randomNum);
  			//this.getLogger().log(Level.INFO, "Found: " + ab);
  			if(getConfig().getString(ab) != null){
				if ((getConfig().getString(ab).equals("always")) || (getConfig().getBoolean(ab))){
					abilityList.add(ab);
					allAbilitiesList.remove(randomNum);
					//this.getLogger().log(Level.INFO, "was added");
				}else{
					allAbilitiesList.remove(randomNum);
					i = i - 1;
				}
  			}else
  				this.getLogger().log(Level.WARNING, "Ability: " + ab + " is not set!");
  		}
  		return abilityList;
  	}
  
  public int idSearch(UUID id)
  {
    Mob idMob = null;
    for (Mob mob : this.infernalList) {
      if (mob.id.equals(id)) {
        idMob = mob;
      }
    }
    if (idMob != null) {
      return this.infernalList.indexOf(idMob);
    }
    return -1;
  }
  
  public ArrayList<String> findMobAbilities(UUID id)
  {
    for (Mob mob : this.infernalList) {
      if (mob.id.equals(id))
      {
        ArrayList<String> abilityList = mob.abilityList;
        return abilityList;
      }
    }
    return null;
  }
  
	public Entity getTarget(final Player player) {
		 
        BlockIterator iterator = new BlockIterator(player.getWorld(), player
                .getLocation().toVector(), player.getEyeLocation()
                .getDirection(), 0, 100);
        Entity target = null;
        while (iterator.hasNext()) {
            Block item = iterator.next();
            for (Entity entity : player.getNearbyEntities(100, 100, 100)) {
                int acc = 2;
                for (int x = -acc; x < acc; x++)
                    for (int z = -acc; z < acc; z++)
                        for (int y = -acc; y < acc; y++)
                            if (entity.getLocation().getBlock()
                                    .getRelative(x, y, z).equals(item)) {
                                return target = entity;
                            }
            }
        }
        return target;
    }
  
  public void makeFly(Entity ent)
  {
    Entity bat = ent.getWorld().spawnEntity(ent.getLocation(), EntityType.BAT);
    bat.setVelocity(new Vector(0, 1, 0));
    bat.setPassenger(ent);
    ((LivingEntity)bat).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 1), true);
  }
  
  public void giveMobGear(Entity mob, boolean naturalSpawn)
  {
    UUID mobId = mob.getUniqueId();
    ArrayList<String> mobAbilityList = null;
    boolean armoured = false;
    if (idSearch(mobId) != -1)
    {
      mobAbilityList = findMobAbilities(mobId);
      if (mobAbilityList.contains("armoured"))
      {
        armoured = true;
        ((LivingEntity)mob).setCanPickupItems(false);
      }
    }
    ItemStack helm = new ItemStack(Material.DIAMOND_HELMET, 1);
    ItemStack chest = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
    ItemStack pants = new ItemStack(Material.DIAMOND_LEGGINGS, 1);
    ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS, 1);
    ItemStack sword = new ItemStack(Material.DIAMOND_SWORD, 1);
    sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 4);
    EntityEquipment ee = ((LivingEntity)mob).getEquipment();
    if ((mob instanceof Skeleton))
    {
      Skeleton sk = (Skeleton)mob;
      if (sk.getSkeletonType().equals(Skeleton.SkeletonType.WITHER))
      {
        if (armoured)
        {
          ee.setHelmetDropChance(0.0F);
          ee.setChestplateDropChance(0.0F);
          ee.setLeggingsDropChance(0.0F);
          ee.setBootsDropChance(0.0F);
          ee.setItemInHandDropChance(0.0F);
          ee.setHelmet(helm);
          ee.setChestplate(chest);
          ee.setLeggings(pants);
          ee.setBoots(boots);
          ee.setItemInHand(sword);
        }
      }
      else
      {
        ItemStack bow = new ItemStack(Material.BOW, 1);
        ee.setItemInHand(bow);
        if (armoured)
        {
          ee.setHelmetDropChance(0.0F);
          ee.setChestplateDropChance(0.0F);
          ee.setHelmet(helm);
          ee.setChestplate(chest);
          if (!mobAbilityList.contains("cloaked"))
          {
            ee.setLeggingsDropChance(0.0F);
            ee.setBootsDropChance(0.0F);
            ee.setLeggings(pants);
            ee.setBoots(boots);
          }
          ee.setItemInHandDropChance(0.0F);
          ee.setItemInHand(sword);
        }
        else if (mobAbilityList.contains("cloaked"))
        {
          ItemStack skull = new ItemStack(Material.GLASS_BOTTLE, 1);
          ee.setHelmet(skull);
        }
      }
    }
    else if (((mob instanceof Zombie)) || ((mob instanceof PigZombie)))
    {
      if (armoured)
      {
        ee.setHelmetDropChance(0.0F);
        ee.setChestplateDropChance(0.0F);
        ee.setHelmet(helm);
        ee.setChestplate(chest);
        if (!mobAbilityList.contains("cloaked"))
        {
          ee.setLeggings(pants);
          ee.setBoots(boots);
        }
        ee.setLeggingsDropChance(0.0F);
        ee.setBootsDropChance(0.0F);
        ee.setItemInHandDropChance(0.0F);
        ee.setItemInHand(sword);
      }
      else if (((mob instanceof Zombie)) && (mobAbilityList.contains("cloaked")))
      {
        ItemStack skull = new ItemStack(Material.GLASS_BOTTLE, 1, (short)2);
        ee.setHelmet(skull);
      }
    }
    if (((mobAbilityList.contains("mounted")) && (getConfig().getList("enabledRiders").contains(mob.getType().getName()))) || ((!naturalSpawn) && (mobAbilityList.contains("mounted")))) {
      ArrayList<String> mounts = new ArrayList();
      
      mounts = (ArrayList)getConfig().getList("enabledMounts");
      
      Random randomGenerator = new Random();
      int index = randomGenerator.nextInt(mounts.size());
      String mount = (String)mounts.get(index);
      //Type
      String type = null;
      if(mount.contains(":")){
    	  String[] s = mount.split(":");
    	  mount = s[0];
    	  type = s[1];
      }
      if (EntityType.fromName(mount) != null){
        Entity liveMount = mob.getWorld().spawnEntity(mob.getLocation(), EntityType.fromName(mount));
        
        this.mountList.put(liveMount, mob);
        liveMount.setPassenger(mob);
        if (liveMount.getType().equals(EntityType.HORSE)){
          Horse hm = (Horse)liveMount;
          if(type != null){
        	  hm.setVariant(Horse.Variant.valueOf(type));
          }else{
        	  int randomNum2 = rand(1,7);
	          if (randomNum2 <= 3) {
	            hm.setVariant(Horse.Variant.HORSE);
	          } else if (randomNum2 == 4) {
	            hm.setVariant(Horse.Variant.DONKEY);
	          } else if (randomNum2 == 5) {
	            hm.setVariant(Horse.Variant.MULE);
	          } else if (randomNum2 == 6) {
	            hm.setVariant(Horse.Variant.SKELETON_HORSE);
	          } else {
	            hm.setVariant(Horse.Variant.UNDEAD_HORSE);
	          }
          }
          if (getConfig().getBoolean("horseMountsHaveSaddles")){
        	  ItemStack saddle = new ItemStack(Material.SADDLE);
        	  hm.getInventory().setSaddle(saddle);
          }
          hm.setTamed(true);
          if (hm.getVariant().equals(Horse.Variant.HORSE)){
            int randomNum3 = rand(1,7);
            if (randomNum3 == 1) {
              hm.setColor(Horse.Color.BLACK);
            } else if (randomNum3 == 2) {
              hm.setColor(Horse.Color.BROWN);
            } else if (randomNum3 == 3) {
              hm.setColor(Horse.Color.CHESTNUT);
            } else if (randomNum3 == 4) {
              hm.setColor(Horse.Color.CREAMY);
            } else if (randomNum3 == 5) {
              hm.setColor(Horse.Color.DARK_BROWN);
            } else if (randomNum3 == 6) {
              hm.setColor(Horse.Color.GRAY);
            } else {
              hm.setColor(Horse.Color.WHITE);
            }
            if ((armoured) && (getConfig().getBoolean("armouredMountsHaveArmour"))){
            	ItemStack armour = new ItemStack(419);
            	hm.getInventory().setArmor(armour);
            }
          }
        }else if (liveMount.getType().equals(EntityType.SHEEP)){
        	  Sheep sh = (Sheep)liveMount;
        	  if(type != null){
        		  //String[] s = type.split(",");
        		  //ItemStack is = new ItemStack(Material.LEATHER_BOOTS);
        		  //dye(is, Color.fromRGB(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2])));
        		  sh.setColor(DyeColor.valueOf(type));
        	  }
        }
      }
      else
      {
        System.out.println("Can't spawn mount!");
        System.out.println(mount + " is not a valid Entity!");
      }
    }
  }
  
  public void changeIntoWither(Skeleton skeleton){
    try{
    	if(Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12")){
    		skeleton.setSkeletonType(Skeleton.SkeletonType.WITHER);
    	}else if(Bukkit.getVersion().contains("1.10")){
	        net.minecraft.server.v1_10_R1.EntitySkeleton ent = ((org.bukkit.craftbukkit.v1_10_R1.entity.CraftSkeleton)skeleton).getHandle();
	        ent.setSkeletonType(net.minecraft.server.v1_10_R1.EnumSkeletonType.WITHER);
	        Field selector = net.minecraft.server.v1_10_R1.EntityInsentient.class.getDeclaredField("goalSelector");
	        selector.setAccessible(true);
    	}else if(Bukkit.getVersion().contains("1.9.4")){
	        net.minecraft.server.v1_9_R2.EntitySkeleton ent = ((org.bukkit.craftbukkit.v1_9_R2.entity.CraftSkeleton)skeleton).getHandle();
	        ent.setSkeletonType(1);
	        Field selector = net.minecraft.server.v1_9_R2.EntityInsentient.class.getDeclaredField("goalSelector");
	        selector.setAccessible(true);
    	}/*else if(Bukkit.getVersion().contains("1.9")){
	        net.minecraft.server.v1_9_R1.EntitySkeleton ent = ((org.bukkit.craftbukkit.v1_9_R1.entity.CraftSkeleton)skeleton).getHandle();
	        ent.setSkeletonType(1);
	        Field selector = net.minecraft.server.v1_9_R1.EntityInsentient.class.getDeclaredField("goalSelector");
	        selector.setAccessible(true);
    	}else if(getServer().getVersion().contains("1.8.3")){
	        net.minecraft.server.v1_8_R2.EntitySkeleton ent = ((org.bukkit.craftbukkit.v1_8_R2.entity.CraftSkeleton)skeleton).getHandle();
	        ent.setSkeletonType(1);
	        Field selector = net.minecraft.server.v1_8_R2.EntityInsentient.class.getDeclaredField("goalSelector");
	        selector.setAccessible(true);
    	}*/else if(getServer().getVersion().contains("1.8.6") || getServer().getVersion().contains("1.8.7") || getServer().getVersion().contains("1.8.8") || getServer().getVersion().contains("1.8.9")){
	        net.minecraft.server.v1_8_R3.EntitySkeleton ent = ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftSkeleton)skeleton).getHandle();
	        ent.setSkeletonType(1);
	        Field selector = net.minecraft.server.v1_8_R3.EntityInsentient.class.getDeclaredField("goalSelector");
	        selector.setAccessible(true);
    	}/*else if(getServer().getVersion().contains("1.8")){
	        net.minecraft.server.v1_8_R1.EntitySkeleton ent = ((org.bukkit.craftbukkit.v1_8_R1.entity.CraftSkeleton)skeleton).getHandle();
	        ent.setSkeletonType(1);
	        Field selector = net.minecraft.server.v1_8_R1.EntityInsentient.class.getDeclaredField("goalSelector");
	        selector.setAccessible(true);
    	}*/
        EntityEquipment ee = skeleton.getEquipment();
        if (ee.getItemInHand().equals(null))
        {
          ItemStack sword = new ItemStack(Material.STONE_SWORD, 1);
          ee.setItemInHand(sword);
        }
      }
    catch (Throwable e)
    {
      e.printStackTrace();
    }
  }
  
  boolean is9(){
	  if(Bukkit.getVersion().contains("1.9") || is10())
		  return true;
	  return false;
  }
  
  boolean is10(){
	  if(Bukkit.getVersion().contains("1.10") || is11())
		  return true;
	  return false;
  }
  
  boolean is11(){
	  if(Bukkit.getVersion().contains("1.11") || is12())
		  return true;
	  return false;
  }
  
  boolean is12(){
	  if(Bukkit.getVersion().contains("1.12"))
		  return true;
	  return false;
  }
  
  public void changeIntoElder(Guardian g){
	  if(Bukkit.getVersion().contains("1.12")){
		  net.minecraft.server.v1_12_R1.EntityGuardian ent = (net.minecraft.server.v1_12_R1.EntityGuardian)((org.bukkit.craftbukkit.v1_12_R1.entity.CraftGuardian)g).getHandle();
		  ((Guardian)ent).setElder(true);
	  }else if(Bukkit.getVersion().contains("1.11")){
		  net.minecraft.server.v1_11_R1.EntityGuardian ent = (net.minecraft.server.v1_11_R1.EntityGuardian)((org.bukkit.craftbukkit.v1_11_R1.entity.CraftGuardian)g).getHandle();
		  ((Guardian)ent).setElder(true);
	  }else if(Bukkit.getVersion().contains("1.10")){
		  net.minecraft.server.v1_10_R1.EntityGuardian ent = (net.minecraft.server.v1_10_R1.EntityGuardian)((org.bukkit.craftbukkit.v1_10_R1.entity.CraftGuardian)g).getHandle();
		  ((Guardian)ent).setElder(true);
	  }else if(Bukkit.getVersion().contains("1.9.4")){
		  net.minecraft.server.v1_9_R2.EntityGuardian ent = (net.minecraft.server.v1_9_R2.EntityGuardian)((org.bukkit.craftbukkit.v1_9_R2.entity.CraftGuardian)g).getHandle();
		  ((Guardian)ent).setElder(true);
	  }/*else if(Bukkit.getVersion().contains("1.9")){
		  net.minecraft.server.v1_9_R1.EntityGuardian ent = (net.minecraft.server.v1_9_R1.EntityGuardian)((org.bukkit.craftbukkit.v1_9_R1.entity.CraftGuardian)g).getHandle();
		  ((Guardian)ent).setElder(true);
	  }else if(getServer().getVersion().contains("1.8.3")){
		  net.minecraft.server.v1_8_R2.EntityGuardian ent = (net.minecraft.server.v1_8_R2.EntityGuardian)((org.bukkit.craftbukkit.v1_8_R2.entity.CraftGuardian)g).getHandle();
		  ((Guardian)ent).setElder(true);
	  }*/else if(getServer().getVersion().contains("1.8.6") || getServer().getVersion().contains("1.8.7") || getServer().getVersion().contains("1.8.8") || getServer().getVersion().contains("1.8.9")){
		  net.minecraft.server.v1_8_R3.EntityGuardian ent = (net.minecraft.server.v1_8_R3.EntityGuardian)((org.bukkit.craftbukkit.v1_8_R3.entity.CraftGuardian)g).getHandle();
		  ((Guardian)ent).setElder(true);
	  }/*else if(getServer().getVersion().contains("1.8")){
		  net.minecraft.server.v1_8_R1.EntityGuardian ent = (net.minecraft.server.v1_8_R1.EntityGuardian)((org.bukkit.craftbukkit.v1_8_R1.entity.CraftGuardian)g).getHandle();
		  ((Guardian)ent).setElder(true);
	  }*/
  }
  
  public void displayParticle(String effect, Location l, double radius, int speed, int amount){
	  	displayParticle(effect, l.getWorld(), l.getX(), l.getY(), l.getZ(), radius, speed, amount);
  }
  
  private void displayParticle(String effect, World w, double x, double y, double z, double radius, int speed, int amount){
		Location l = new Location(w, x, y, z);
		try{
			if(Bukkit.getVersion().contains("1.12")){
				if(radius == 0){
					ParticleEffects_1_12.sendToLocation(ParticleEffects_1_12.valueOf(effect), l, 0, 0, 0, speed, amount);
				}else{
					ArrayList<Location> ll = getArea(l, radius, 0.2);
					for(int i = 0; i < amount; i++){
				        int index = new Random().nextInt(ll.size());
				        ParticleEffects_1_12.sendToLocation(ParticleEffects_1_12.valueOf(effect), ll.get(index), 0, 0, 0, speed, 1);
				        ll.remove(index);
					}
				}
			}else if(Bukkit.getVersion().contains("1.11")){
				if(radius == 0){
					ParticleEffects_1_11.sendToLocation(ParticleEffects_1_11.valueOf(effect), l, 0, 0, 0, speed, amount);
				}else{
					ArrayList<Location> ll = getArea(l, radius, 0.2);
					for(int i = 0; i < amount; i++){
				        int index = new Random().nextInt(ll.size());
				        ParticleEffects_1_11.sendToLocation(ParticleEffects_1_11.valueOf(effect), ll.get(index), 0, 0, 0, speed, 1);
				        ll.remove(index);
					}
				}
			}else if(Bukkit.getVersion().contains("1.10")){
				if(radius == 0){
					ParticleEffects_1_10.sendToLocation(ParticleEffects_1_10.valueOf(effect), l, 0, 0, 0, speed, amount);
				}else{
					ArrayList<Location> ll = getArea(l, radius, 0.2);
					for(int i = 0; i < amount; i++){
				        int index = new Random().nextInt(ll.size());
				        ParticleEffects_1_10.sendToLocation(ParticleEffects_1_10.valueOf(effect), ll.get(index), 0, 0, 0, speed, 1);
				        ll.remove(index);
					}
				}
			}else if(Bukkit.getVersion().contains("1.9.3") || Bukkit.getVersion().contains("1.9.4") || Bukkit.getVersion().contains("1.9.5")){
				if(radius == 0){
					ParticleEffects_1_9_4.sendToLocation(ParticleEffects_1_9_4.valueOf(effect), l, 0, 0, 0, speed, amount);
				}else{
					ArrayList<Location> ll = getArea(l, radius, 0.2);
					for(int i = 0; i < amount; i++){
				        int index = new Random().nextInt(ll.size());
				        ParticleEffects_1_9_4.sendToLocation(ParticleEffects_1_9_4.valueOf(effect), ll.get(index), 0, 0, 0, speed, 1);
				        ll.remove(index);
					}
				}
			}/*else if(Bukkit.getVersion().contains("1.9")){
				if(radius == 0){
					ParticleEffects_1_9.sendToLocation(ParticleEffects_1_9.valueOf(effect), l, 0, 0, 0, speed, amount);
				}else{
					ArrayList<Location> ll = getArea(l, radius, 0.2);
					for(int i = 0; i < amount; i++){
				        int index = new Random().nextInt(ll.size());
				        ParticleEffects_1_9.sendToLocation(ParticleEffects_1_9.valueOf(effect), ll.get(index), 0, 0, 0, speed, 1);
				        ll.remove(index);
					}
				}
			}else if(getServer().getVersion().contains("1.8.3")){
				if(radius == 0){
					ParticleEffects_1_8_3.sendToLocation(ParticleEffects_1_8_3.valueOf(effect), l, 0, 0, 0, speed, amount);
				}else{
					ArrayList<Location> ll = getArea(l, radius, 0.2);
					for(int i = 0; i < amount; i++){
				        int index = new Random().nextInt(ll.size());
				        ParticleEffects_1_8_3.sendToLocation(ParticleEffects_1_8_3.valueOf(effect), ll.get(index), 0, 0, 0, speed, 1);
				        ll.remove(index);
					}
				}
			}*/else if(getServer().getVersion().contains("1.8.6") || getServer().getVersion().contains("1.8.7") || getServer().getVersion().contains("1.8.8") || getServer().getVersion().contains("1.8.9")){
				if(radius == 0){
					ParticleEffects_1_8_6.sendToLocation(ParticleEffects_1_8_6.valueOf(effect), l, 0, 0, 0, speed, amount);
				}else{
					ArrayList<Location> ll = getArea(l, radius, 0.2);
					for(int i = 0; i < amount; i++){
				        int index = new Random().nextInt(ll.size());
				        ParticleEffects_1_8_6.sendToLocation(ParticleEffects_1_8_6.valueOf(effect), ll.get(index), 0, 0, 0, speed, 1);
				        ll.remove(index);
					}
				}
			}/*else if(getServer().getVersion().contains("1.8")){
				if(radius == 0){
					ParticleEffects_1_8.sendToLocation(ParticleEffects_1_8.valueOf(effect), l, 0, 0, 0, speed, amount);
				}else{
					ArrayList<Location> ll = getArea(l, radius, 0.2);
					for(int i = 0; i < amount; i++){
				        int index = new Random().nextInt(ll.size());
				        ParticleEffects_1_8.sendToLocation(ParticleEffects_1_8.valueOf(effect), ll.get(index), 0, 0, 0, speed, 1);
				        ll.remove(index);
					}
				}
			}*/
		}catch(Exception ex){System.out.println("V: " + getServer().getVersion());ex.printStackTrace();}
  }
  
  private ArrayList<Location> getArea(Location l, double r, double t)
  {
    ArrayList<Location> ll = new ArrayList();
    for (double x = l.getX() - r; x < l.getX() + r; x += t) {
      for (double y = l.getY() - r; y < l.getY() + r; y += t) {
        for (double z = l.getZ() - r; z < l.getZ() + r; z += t) {
          ll.add(new Location(l.getWorld(), x, y, z));
        }
      }
    }
    return ll;
  }
  
  public String getRandomMob()
  {
    ArrayList<String> mobList = (ArrayList)getConfig().getList("enabledmobs");
    if (mobList.isEmpty()) {
      return "Zombie";
    }
    String mob = (String)mobList.get(rand(1,mobList.size())-1);
    if (mob != null) {
      return mob;
    }
    return "Zombie";
  }
  
  public String generateString(int maxNames, ArrayList<String> names)
  {
    String namesString = "";
    if (maxNames > names.size()) {
      maxNames = names.size();
    }
    for (int i = 0; i < maxNames; i++) {
      namesString = namesString + (String)names.get(i) + " ";
    }
    if (names.size() > maxNames) {
      namesString = namesString + "... ";
    }
    return namesString;
  }
  
  public void reloadLoot(){
	  if (this.lootYML == null) {
		  this.lootYML = new File(getDataFolder(), "loot.yml");
	  }
	  this.lootFile = YamlConfiguration.loadConfiguration(this.lootYML);
	  
	  YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(lootYML);
	  this.lootFile.setDefaults(defConfig);
	  
//	  InputStream defConfigStream = getResource("loot.yml");
//	  if (defConfigStream != null){
//		  YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
//		  this.lootFile.setDefaults(defConfig);
//	  }
  }
  
  public String getLocationName(Location l){
	  return (l.getX() + "." + l.getY() + "." + l.getZ() + l.getWorld().getName()).replace(".", "");
  }
  
  public Block blockNear(Location l, Material mat, int radius)
  {
    double xTmp = l.getX();
    double yTmp = l.getY();
    double zTmp = l.getZ();
    int finalX = (int)Math.round(xTmp);
    int finalY = (int)Math.round(yTmp);
    int finalZ = (int)Math.round(zTmp);
    for (int x = finalX - radius; x <= finalX + radius; x++) {
      for (int y = finalY - radius; y <= finalY + radius; y++) {
        for (int z = finalZ - radius; z <= finalZ + radius; z++)
        {
          Location loc = new Location(l.getWorld(), x, y, z);
          Block block = loc.getBlock();
          if (block.getType().equals(mat)) {
            return block;
          }
        }
      }
    }
    return null;
  }
  
  private boolean cSpawn(CommandSender sender, String mob, Location l, ArrayList<String> abList){
	  //cspawn <mob> <world> <x> <y> <z> <ability> <ability>
	  if ((EntityType.fromName(mob) != null)/* || (mob.equalsIgnoreCase("WitherSkeleton")*/){
//		  boolean isWither = false;
//          if (mob.equalsIgnoreCase("WitherSkeleton")){
//        	  mob = "Skeleton";
//        	  isWither = true;
//          }
          Entity ent = l.getWorld().spawnEntity(l, EntityType.fromName(mob));
//          if (isWither){
//        	  Skeleton skel = (Skeleton)ent;
//        	  changeIntoWither(skel);
//        	  mob = "WitherSkeleton";
//          }
          Mob newMob = null;
          UUID id = ent.getUniqueId();
          if (abList.contains("1up")) {
        	  newMob = new Mob(ent, id, l.getWorld(), true, abList, 2, getEffect());
          } else {
        	  newMob = new Mob(ent, id, l.getWorld(), true, abList, 1, getEffect());
          }
          if (abList.contains("flying")) {
        	  makeFly(ent);
          }
          this.infernalList.add(newMob);
          this.gui.setName(ent);
          
          giveMobGear(ent, false);
          addHealth(ent, abList);
          return true;
        }else{
          sender.sendMessage("Can't spawn a " + mob + "!");
          return false;
        }
  }
  
  private int rand(int min, int max){
    int r = min + (int)(Math.random() * (1 + max - min));
    return r;
  }
  
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    if ((cmd.getName().equals("infernalmobs")) || (cmd.getName().equals("im"))) {
      try{
        Player player = null;
        boolean plyr = true;
        if (!(sender instanceof Player)){
          if (args != null && args.length > 0 && (!args[0].equals("cspawn")) && (!args[0].equals("giveloot")) && (!args[0].equals("reload")) && (!args[0].equals("killall"))) {
            sender.sendMessage("This command can only be run by a player!");
            return true;
          }
          plyr = false;
        }else{
          player = (Player)sender;
        }
        if ((!plyr) || (player.hasPermission("infernal_mobs.commands")))
        {
          if (args.length == 0)
          {
            throwError(sender);
            return true;
          }
          if ((args.length == 1) && (args[0].equals("reload"))){
            reloadConfig();
            reloadLoot();
            sender.sendMessage("§eConfig reloaded!");
          }
          else if (args[0].equals("mobList")){
              sender.sendMessage("§6Mob List:");
              for(EntityType et : EntityType.values())
            	  if(et != null && et.getName() != null)
            		  sender.sendMessage("§e" + et.getName());
              return true;
          }
          else if ((args.length == 1) && (args[0].equals("error")))
          {
            this.errorList.add(player);
            sender.sendMessage("§eClick on a mob to send an error report about it.");
          }
          else if ((args.length == 1) && (args[0].equals("info")))
          {
            sender.sendMessage("§eMounts: " + this.mountList.size());
            sender.sendMessage("§eLoops: " + this.loops);
            sender.sendMessage("§eInfernals: " + this.infernalList.size());
          }
          else if ((args.length == 1) && (args[0].equals("worldInfo")))
          {
            ArrayList<String> enWorldList = (ArrayList)getConfig().getList("mobworlds");
            World world = player.getWorld();
            String enabled = "is not";
            if ((enWorldList.contains(world.getName())) || (enWorldList.contains("<all>"))) {
              enabled = "is";
            }
            sender.sendMessage("The world you are currently in, " + world + " " + enabled + " enabled.");
            sender.sendMessage("All the worlds that are enabled are: " + enWorldList.toString());
          }
          else if ((args.length == 1) && (args[0].equals("help")))
          {
            throwError(sender);
          } else if ((args.length == 1) && (args[0].equals("getloot"))) {
            int min = getConfig().getInt("minpowers");
            int max = getConfig().getInt("maxpowers");
            int powers = rand(min, max);
            ItemStack gottenLoot = getRandomLoot(player, getRandomMob(), powers);
            if (gottenLoot != null) {
              player.getInventory().addItem(gottenLoot);
            }
            sender.sendMessage("§eGave you some random loot!");
          }
          else if ((args.length == 2) && (args[0].equals("getloot"))){
        	  try{
        		  int index = Integer.parseInt(args[1]);
        		  ItemStack i = getLoot(player, index);
        		  if(i != null){
	        		  player.getInventory().addItem(i);
	        		  sender.sendMessage("§eGave you the loot at index §9" + index);
	            	  return true;
        		  }
        	  }catch (Exception e){}
        	  sender.sendMessage("§cUnable to get that loot!");
          }else if ((args.length == 3) && (args[0].equals("giveloot"))){
        	  try{
        		  Player p = getServer().getPlayer(args[1]);
        		  if(p != null) {
	        		  int index = Integer.parseInt(args[2]);
	        		  ItemStack i = getLoot(p, index);
	        		  if(i != null){
		        		  p.getInventory().addItem(i);
		        		  sender.sendMessage("§eGave the player the loot at index §9" + index);
		            	  return true;
	        		  }
        		  }else{
        			  sender.sendMessage("§cPlayer not found!!");
        			  return true;
        		  }
        	  }catch (Exception e){}
        	  sender.sendMessage("§cUnable to get that loot!");
          }else if (((args.length == 2) && (args[0].equals("spawn"))) || ((args[0].equals("cspawn")) && (args.length == 6))){
            if ((EntityType.fromName(args[1]) != null)/* || (args[1].equalsIgnoreCase("WitherSkeleton"))*/)
            {
//              boolean isWither = false;
//              if (args[1].equalsIgnoreCase("WitherSkeleton"))
//              {
//                args[1] = "Skeleton";
//                isWither = true;
//              }
              boolean exmsg;
              World world;
              Entity ent;
              if ((args[0].equals("cspawn")) && (args[2] != null) && (args[3] != null) && (args[4] != null) && (args[5] != null))
              {
                if (Bukkit.getServer().getWorld(args[2]) == null)
                {
                  sender.sendMessage(args[2] + " dose not exist!");
                  return true;
                }
                world = Bukkit.getServer().getWorld(args[2]);
                Location spoint = new Location(Bukkit.getServer().getWorld(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]));
                ent = world.spawnEntity(spoint, EntityType.fromName(args[1]));
                exmsg = true;
              } else{
                world = player.getWorld();
                Location farSpawnLoc = player.getTargetBlock((HashSet<Material>)null, 200).getLocation();
                farSpawnLoc.setY(farSpawnLoc.getY() + 1.0D);
                ent = player.getWorld().spawnEntity(farSpawnLoc, EntityType.fromName(args[1]));
                exmsg = false;
              }
//              if (isWither)
//              {
//                Skeleton skel = (Skeleton)ent;
//                changeIntoWither(skel);
//                args[1] = "WitherSkeleton";
//              }
              ArrayList<String> abList = getAbilitiesAmount(ent);
              Mob newMob = null;
              UUID id = ent.getUniqueId();
              if (abList.contains("1up")) {
                newMob = new Mob(ent, id, world, true, abList, 2, getEffect());
              } else {
                newMob = new Mob(ent, id, world, true, abList, 1, getEffect());
              }
              if (abList.contains("flying")) {
                makeFly(ent);
              }
              this.infernalList.add(newMob);
              this.gui.setName(ent);
              
              giveMobGear(ent, false);
              addHealth(ent, abList);
              if (!exmsg) {
                sender.sendMessage("Spawned a " + args[1]);
              } else if ((exmsg) && ((sender instanceof Player))) {
                sender.sendMessage("Spawned a " + args[1] + " in " + args[2] + " at " + args[3] + ", " + args[4] + ", " + args[5]);
              }
            }
            else
            {
              sender.sendMessage("Can't spawn a " + args[1] + "!");
              return true;
            }
          }
          else if (((args.length >= 3) && (args[0].equals("spawn"))) || ((args[0].equals("cspawn")) && (args.length >= 6)) || ((args[0].equals("pspawn")) && (args.length >= 3)))
          {
            if (args[0].equals("spawn")){
              World world = player.getWorld();
              if ((EntityType.fromName(args[1]) != null)/* || (args[1].equalsIgnoreCase("WitherSkeleton"))*/)
              {
//                boolean isWither = false;
//                if (args[1].equalsIgnoreCase("WitherSkeleton"))
//                {
//                  args[1] = "Skeleton";
//                  isWither = true;
//                }
            	Location farSpawnLoc = player.getTargetBlock((HashSet<Material>)null, 200).getLocation();
                farSpawnLoc.setY(farSpawnLoc.getY() + 1.0D);
                Entity ent = player.getWorld().spawnEntity(farSpawnLoc, EntityType.fromName(args[1]));
//                if (isWither)
//                {
//                  Skeleton skel = (Skeleton)ent;
//                  changeIntoWither(skel);
//                  args[1] = "WitherSkeleton";
//                }
                ArrayList<String> spesificAbList = new ArrayList();
                for (int i = 0; i <= args.length - 3; i++) {
                  if (getConfig().getString(args[(i + 2)]) != null)
                  {
                    spesificAbList.add(args[(i + 2)]);
                  }
                  else
                  {
                    sender.sendMessage(args[(i + 2)] + " is not a valid ability!");
                    return true;
                  }
                }
                Mob newMob = null;
                UUID id = ent.getUniqueId();
                if (spesificAbList.contains("1up")) {
                  newMob = new Mob(ent, id, world, true, spesificAbList, 2, getEffect());
                } else {
                  newMob = new Mob(ent, id, world, true, spesificAbList, 1, getEffect());
                }
                if (spesificAbList.contains("flying")) {
                  makeFly(ent);
                }
                this.infernalList.add(newMob);
                this.gui.setName(ent);
                giveMobGear(ent, false);
                
                addHealth(ent, spesificAbList);
                
                sender.sendMessage("Spawned a " + args[1] + " with the abilities:");
                sender.sendMessage(spesificAbList.toString());
              }
              else
              {
                sender.sendMessage("Can't spawn a " + args[1] + "!");
              }
            }else if (args[0].equals("cspawn")) {
            	//cspawn <mob> <world> <x> <y> <z> <ability> <ability>
                if (Bukkit.getServer().getWorld(args[2]) == null){
              	  sender.sendMessage(args[2] + " dose not exist!");
              	  return true;
                }
                World world = Bukkit.getServer().getWorld(args[2]);
                Location spoint = new Location(world, Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]));
                ArrayList<String> abList = new ArrayList();
                for (int i = 0; i <= args.length - 7; i++) {
                	abList.add(args[(i + 6)]);
                }
                if(cSpawn(sender, args[1], spoint, abList)){
                    sender.sendMessage("Spawned a " + args[1] + " in " + args[2] + " at " + args[3] + ", " + args[4] + ", " + args[5] + " with the abilities:");
                    sender.sendMessage(abList.toString());
                }
            }else if(args[0].equals("pspawn")){
            	//pspawn <mob> <player> <ability> <ability>
            	Player p = getServer().getPlayer(args[2]);
                if (p == null){
                	sender.sendMessage(args[2] + " is not online!");
                	return true;
                }
                ArrayList<String> abList = new ArrayList();
                for (int i = 3; i < args.length; i++) {
                	abList.add(args[(i)]);
                }
                if(cSpawn(sender, args[1], p.getLocation(), abList)){
                	sender.sendMessage("Spawned a " + args[1] + " at " + p.getName() + " with the abilities:");
                	sender.sendMessage(abList.toString());
                }
            }else{
              sender.sendMessage("Can't spawn a " + args[1] + "!");
            }
          }
          else if ((args.length == 1) && (args[0].equals("abilities")))
          {
            sender.sendMessage("--Infernal Mobs Abilities--");
            sender.sendMessage("mama, molten, weakness, vengeance, webber, storm, sprint, lifesteal, ghastly, ender, cloaked, berserk, 1up, sapper, rust, bullwark, quicksand, thief, tosser, withering, blinding, armoured, poisonous, potions, explode, gravity, archer, necromancer, firework, flying, mounted, morph, ghost, confusing");
          }
          else
          {
            ArrayList<String> oldMobAbilityList;
            if ((args.length == 1) && (args[0].equals("showAbilities")))
            {
              if (getTarget(player) != null)
              {
                Entity targeted = getTarget(player);
                UUID mobId = targeted.getUniqueId();
                if (idSearch(mobId) != -1)
                {
                  oldMobAbilityList = findMobAbilities(mobId);
                  if (!targeted.isDead())
                  {
                    sender.sendMessage("--Targeted Mob's Abilities--");
                    sender.sendMessage(oldMobAbilityList.toString());
                  }
                }
                else
                {
                  sender.sendMessage("§cThis " + targeted.getType().getName() + " §cis not an infernal mob!");
                }
              }
              else
              {
                sender.sendMessage("§cUnable to find mob!");
              }
            }
            else if ((args[0].equals("setInfernal")) && (args.length == 2))
            {
            	if (player.getTargetBlock((HashSet<Material>)null, 25).getType().equals(Material.MOB_SPAWNER)) {
                int delay = Integer.parseInt(args[1]);
                
                String name = getLocationName(player.getTargetBlock((HashSet<Material>)null, 25).getLocation());
                
                this.mobSaveFile.set("infernalSpanwers." + name, Integer.valueOf(delay));
                this.mobSaveFile.save(this.saveYML);
                sender.sendMessage("§cSpawner set to infernal with a " + delay + " second delay!");
              }
              else
              {
                sender.sendMessage("§cYou must be looking a spawner to make it infernal!");
              }
            }else if ((args[0].equals("kill")) && (args.length == 2)){
            	int size = Integer.parseInt(args[1]);
            	for (Entity e : player.getNearbyEntities(size, size, size)) {
            		int id = idSearch(e.getUniqueId());
            		if (id != -1) {
            			removeMob(id);
            			e.remove();
            			this.getLogger().log(Level.INFO, "Entity remove due to /kill");
            		}
            	}
            	sender.sendMessage("§eKilled all infernal mobs near you!");
            }else if ((args[0].equals("killall")) && (args.length == 2)){
            	World w = getServer().getWorld(args[1]);
            	if (w != null){
            		for (Entity e : w.getEntities()) {
                		int id = idSearch(e.getUniqueId());
                		if (id != -1) {
                			removeMob(id);
                			this.getLogger().log(Level.INFO, "Entity remove due to /killall");
                			e.remove();
                		}
            		}
            		sender.sendMessage("§eKilled all loaded infernal mobs in that world!");
            	}else{
            		sender.sendMessage("§cWorld not found!");
            	}
            }else if (args[0].equals("mobs")){
            	sender.sendMessage("§6List of Mobs:");
            	for(EntityType e : EntityType.values())
            		if(e != null)
            			sender.sendMessage(e.toString());
            }else if (args[0].equals("setloot")) {            	
            	setItem(player.getInventory().getItemInHand(),"loot."+args[1],lootFile);       	
            	sender.sendMessage("§eSet loot at index "+args[1]+" §eto item in hand.");
            }else{
            	throwError(sender);
            }
          }
        }
        else
        {
          sender.sendMessage("§cYou don't have permission to use this command!");
        }
      }catch (Exception x){
        throwError(sender);
        x.printStackTrace();
      }
    }
    return true;
  }
  
  public void throwError(CommandSender sender)
  {
    sender.sendMessage("--Infernal Mobs v" + Bukkit.getServer().getPluginManager().getPlugin("InfernalMobs").getDescription().getVersion() + "--");
    sender.sendMessage("Usage: /im reload");
    sender.sendMessage("Usage: /im worldInfo");
    sender.sendMessage("Usage: /im error");
    sender.sendMessage("Usage: /im getloot <index>");
    sender.sendMessage("Usage: /im setloot <index>");    
    sender.sendMessage("Usage: /im giveloot <player> <index>");
    sender.sendMessage("Usage: /im abilities");
    sender.sendMessage("Usage: /im showAbilities");
    sender.sendMessage("Usage: /im setInfernal <time delay>");
    sender.sendMessage("Usage: /im spawn <mob> <ability> <ability>");
    sender.sendMessage("Usage: /im cspawn <mob> <world> <x> <y> <z> <ability> <ability>");
    sender.sendMessage("Usage: /im pspawn <mob> <player> <ability> <ability>");
    sender.sendMessage("Usage: /im kill <size>");
    sender.sendMessage("Usage: /im killall <world>");
  }
}