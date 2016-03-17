package io.hotmail.com.jacob_vejvoda.infernal_mobs;

import java.util.ArrayList;
import java.util.UUID;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

class Mob
{
  public Entity entity;
  public UUID id;
  public World world;
  public boolean infernal;
  public int lives;
  public String effect;
  public ArrayList<String> abilityList = new ArrayList();
  
  Mob(Entity type, UUID i, World w, boolean in, ArrayList<String> l, int li, String e)
  {
    this.entity = type;
    this.id = i;
    this.world = w;
    this.infernal = in;
    this.abilityList = l;
    this.lives = li;
    this.effect = e;
  }
  
  public String toString()
  {
    return "Name: " + this.entity.getType().getName() + " Infernal: " + this.infernal + "Abilities:" + this.abilityList;
  }
  
  public void setLives(int i)
  {
    this.lives = i;
  }
}