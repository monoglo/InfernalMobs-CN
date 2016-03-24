package io.hotmail.com.jacob_vejvoda.infernal_mobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener{
  static infernal_mobs plugin;
  HashMap<String, Long> spawnerMap = new HashMap<String, Long>();
  
  public EventListener(infernal_mobs instance)
  {
    plugin = instance;
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void onPlayerInteractEntity(PlayerInteractEntityEvent e)
  {
    Player p = e.getPlayer();
    Entity ent = e.getRightClicked();
    if (plugin.errorList.contains(p))
    {
      plugin.errorList.remove(p);
      p.sendMessage("§6Error report:");
      
      String name = "";
      try
      {
        name = ent.getCustomName();
      }
      catch (Exception localException) {}
      p.sendMessage("§eName: §f" + name);
      p.sendMessage("§eSaved: §f" + plugin.mobSaveFile.getString(ent.getUniqueId().toString()));
      p.sendMessage("§eHealth: §f" + ((Damageable)ent).getMaxHealth());
      p.sendMessage("§eInfernal: §f" + plugin.idSearch(ent.getUniqueId()));
    }
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void onEnitityDamaged(EntityDamageEvent e)
  {
    Entity mob = e.getEntity();
    if (plugin.idSearch(mob.getUniqueId()) != -1) {
      for (Entity entity : mob.getNearbyEntities(64.0D, 64.0D, 64.0D)) {
        if ((entity instanceof Player)) {
          plugin.gui.fixBar((Player)entity);
        }
      }
    }
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void onLightningStrike(LightningStrikeEvent e)
  {
    for (Entity m : e.getLightning().getNearbyEntities(6.0D, 6.0D, 6.0D)) {
      if (plugin.idSearch(m.getUniqueId()) != -1)
      {
        e.setCancelled(true);
        
        break;
      }
    }
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void onPlayerTeleport(PlayerTeleportEvent event)
  {
    World world = event.getPlayer().getWorld();
    plugin.giveMobsPowers(world);
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void onPlayerChangedWorld(PlayerChangedWorldEvent event)
  {
    World world = event.getPlayer().getWorld();
    plugin.giveMobsPowers(world);
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void onChunkLoad(ChunkLoadEvent e)
    throws Exception
  {
    Entity[] arrayOfEntity;
    int j = (arrayOfEntity = e.getChunk().getEntities()).length;
    for (int i = 0; i < j; i++)
    {
      Entity ent = arrayOfEntity[i];
      if (((ent instanceof LivingEntity)) && (((LivingEntity)ent).getCustomName() != null) && 
        (plugin.mobSaveFile.getString(ent.getUniqueId().toString()) != null)) {
        plugin.giveMobPowers(ent);
      }
    }
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void onEntityAttack(EntityDamageByEntityEvent event)
    throws Exception
  {
    try
    {
      Entity attacker = event.getDamager();
      Entity victim = event.getEntity();
      Entity mob;
      if ((attacker instanceof Arrow))
      {
        Arrow arrow = (Arrow)event.getDamager();
        if (((arrow.getShooter() instanceof Player)) && (!(victim instanceof Player)))
        {
          mob = victim;
          Player player = (Player)arrow.getShooter();
          plugin.doEffect(player, mob, false);
        }
        else if ((!(arrow.getShooter() instanceof Player)) && ((victim instanceof Player)))
        {
          mob = (Entity)arrow.getShooter();
          Player player = (Player)victim;
          plugin.doEffect(player, mob, true);
        }
      }
      else if ((attacker instanceof Snowball))
      {
        Snowball snowBall = (Snowball)event.getDamager();
        if (snowBall.getShooter() != null) {
          if (((snowBall.getShooter() instanceof Player)) && (!(victim instanceof Player)))
          {
            mob = victim;
            Player player = (Player)snowBall.getShooter();
            plugin.doEffect(player, mob, false);
          }
          else if ((!(snowBall.getShooter() instanceof Player)) && ((victim instanceof Player)))
          {
            mob = (Entity)snowBall.getShooter();
            Player player = (Player)victim;
            plugin.doEffect(player, mob, true);
          }
        }
      }
      else if (((attacker instanceof Player)) && (!(victim instanceof Player)))
      {
        Player player = (Player)attacker;
        mob = victim;
        plugin.doEffect(player, mob, false);
      }
      else if ((!(attacker instanceof Player)) && ((victim instanceof Player)))
      {
        Player player = (Player)victim;
        mob = attacker;
        plugin.doEffect(player, mob, true);
      }
      if (plugin.idSearch(victim.getUniqueId()) != -1) {
        for (Entity entity : victim.getNearbyEntities(64.0D, 64.0D, 64.0D)) {
          if ((entity instanceof Player)) {
            plugin.gui.fixBar((Player)entity);
          }
        }
      }
    }
    catch (Exception e)
    {
      System.out.println("Ent Attack Ent Errror: " + e);
    }
  }
  
  @SuppressWarnings("deprecation")
@EventHandler(priority=EventPriority.HIGH)
  public void onMobSpawn(CreatureSpawnEvent event)
  {
    World world = event.getEntity().getWorld();
    if ((!event.getEntity().hasMetadata("NPC")) && (!event.getEntity().hasMetadata("shopkeeper")))
    {
      if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER))
      {
        Block spawner = plugin.blockNear(event.getEntity().getLocation(), Material.MOB_SPAWNER, 10);
        if (spawner != null)
        {
          String name = plugin.getLocationName(spawner.getLocation());
          if (plugin.mobSaveFile.getString("infernalSpanwers." + name) != null)
          {
            if (this.spawnerMap.get(name) == null)
            {
              plugin.makeInfernal(event.getEntity(), true);
              this.spawnerMap.put(name, Long.valueOf(plugin.serverTime));
            }
            else
            {
              long startTime = ((Long)this.spawnerMap.get(name)).longValue();
              long endTime = plugin.serverTime;
              long timePassed = endTime - startTime;
              int delay = plugin.mobSaveFile.getInt("infernalSpanwers." + name);
              if (timePassed >= delay)
              {
                plugin.makeInfernal(event.getEntity(), true);
                this.spawnerMap.put(name, Long.valueOf(plugin.serverTime));
              }
              else
              {
                event.setCancelled(true);
              }
            }
          }
          else if (!plugin.getConfig().getBoolean("enableFarmingDrops")) {
            return;
          }
        }
      }
      if ((event.getEntity().hasMetadata("NPC")) || (event.getEntity().hasMetadata("shopkeeper"))) {
        return;
      }
      if (((plugin.getConfig().getList("enabledworlds").contains(world.getName())) || (plugin.getConfig().getList("enabledworlds").contains("<all>"))) && 
        (plugin.getConfig().getList("enabledmobs").contains(event.getEntity().getType().getName())) && 
        (plugin.getConfig().getInt("naturalSpawnHeight") < event.getEntity().getLocation().getY())) {
        plugin.makeInfernal(event.getEntity(), false);
      }
    }
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void onBlockBreak(BlockBreakEvent e)
    throws IOException
  {
    if (e.getBlock().getType().equals(Material.MOB_SPAWNER))
    {
      String name = plugin.getLocationName(e.getBlock().getLocation());
      if (plugin.mobSaveFile.getString("infernalSpanwers." + name) != null)
      {
        plugin.mobSaveFile.set("infernalSpanwers." + name, null);
        plugin.mobSaveFile.save(plugin.saveYML);
        if (e.getPlayer().isOp()) {
          e.getPlayer().sendMessage("§cYou broke an infernal mob spawner!");
        }
      }
    }
  }
  
  @SuppressWarnings({ "unchecked", "deprecation", "rawtypes" })
@EventHandler(priority=EventPriority.HIGH)
  public void onEntityDeath(EntityDeathEvent event)
  {
    try
    {
      UUID id = event.getEntity().getUniqueId();
      int mobIndex = plugin.idSearch(id);
      if (mobIndex != -1)
      {
        ArrayList<String> aList;
        if (plugin.findMobAbilities(id) != null) {
          aList = plugin.findMobAbilities(id);
        } else {
          return;
        }
        //ArrayList<String> aList;
        if (aList.contains("explode"))
        {
          TNTPrimed tnt = (TNTPrimed)event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.PRIMED_TNT);
          tnt.setFuseTicks(1);
        }
        boolean isGhost = false;
        try
        {
          if (((Zombie)event.getEntity()).getEquipment().getHelmet().getItemMeta().getDisplayName().equals("§fGhost Head")) {
            isGhost = true;
          }
        }
        catch (Exception localException1) {}
        if (aList.contains("ghost")) {
          plugin.spawnGhost(event.getEntity().getLocation());
        }
        Location dropSpot = null;
        if (aList.contains("molten"))
        {
          Location lavaSpot = event.getEntity().getLocation();
          dropSpot = lavaSpot;
          dropSpot.setX(dropSpot.getX() - 2.0D);
        }
        else
        {
          dropSpot = event.getEntity().getLocation();
        }
        if ((plugin.getConfig().getBoolean("enableDeathMessages")) && ((event.getEntity().getKiller() instanceof Player)) && (!isGhost))
        {
          Player player = event.getEntity().getKiller();
          if (plugin.getConfig().getList("deathMessages") != null)
          {
            ArrayList<String> deathMessagesList = (ArrayList)plugin.getConfig().getList("deathMessages");
            Random randomGenerator = new Random();
            int index = randomGenerator.nextInt(deathMessagesList.size());
            String deathMessage = (String)deathMessagesList.get(index);
            String tittle = plugin.gui.getMobNameTag(event.getEntity());
            deathMessage = ChatColor.translateAlternateColorCodes('&', deathMessage);
            deathMessage = deathMessage.replace("player", player.getName());
            if ((player.getItemInHand() != null) && (!player.getItemInHand().getType().equals(Material.AIR)))
            {
              if (player.getItemInHand().getItemMeta().getDisplayName() != null) {
                deathMessage = deathMessage.replace("weapon", player.getItemInHand().getItemMeta().getDisplayName());
              } else {
                deathMessage = deathMessage.replace("weapon", player.getItemInHand().getType().name().replace("_", " ").toLowerCase());
              }
            }
            else {
              deathMessage = deathMessage.replace("weapon", "fist");
            }
            if (event.getEntity().getCustomName() != null) {
              deathMessage = deathMessage.replace("mob", event.getEntity().getCustomName());
            } else {
              deathMessage = deathMessage.replace("mob", tittle);
            }
            Bukkit.broadcastMessage(deathMessage);
          }
          else
          {
            System.out.println("No valid death messages found!");
          }
        }
        if ((plugin.getConfig().getBoolean("enableDrops")) && 
          ((plugin.getConfig().getBoolean("enableFarmingDrops")) || (event.getEntity().getKiller() != null)) && (
          (plugin.getConfig().getBoolean("enableFarmingDrops")) || ((event.getEntity().getKiller() instanceof Player))))
        {
          Player player = null;
          if ((event.getEntity().getKiller() instanceof Player)) {
            player = event.getEntity().getKiller();
          }
          if ((player != null) && (player.getGameMode().equals(GameMode.CREATIVE)) && (plugin.getConfig().getBoolean("noCreativeDrops"))) {
            return;
          }
          ItemStack drop = plugin.getRandomLoot(player, event.getEntity().getType().getName(), aList.size());
          if (drop != null)
          {
            int min = 1;
            int max = plugin.getConfig().getInt("dropChance");
            int randomNum = new Random().nextInt(max - min + 1) + min;
            if ((dropSpot != null) && (randomNum == 1))
            {
              Item dropedItem = event.getEntity().getWorld().dropItemNaturally(dropSpot, drop);
              plugin.keepAlive(dropedItem);
            }
            int xpm = plugin.getConfig().getInt("xpMultiplier");
            int xp = event.getDroppedExp() * xpm;
            event.setDroppedExp(xp);
          }
        }
        try
        {
          plugin.removeMob(mobIndex);
        }
        catch (Exception e)
        {
          System.out.println("Error: " + e);
        }
      }
      return;
    }
    catch (Exception e)
    {
      System.out.println("EntityDeathEvent: " + e);
    }
  }
}
