package io.hotmail.com.jacob_vejvoda.infernal_mobs.versionStuff;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.server.v1_9_R2.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public enum ParticleEffects_1_9_4 {
	 
    BIG_EXPLODE("EXPLOSION_HUGE", 0),
    BLOCK_CRACK("BLOCK_CRACK", 0),
    BLOCK_DUST("BLOCK_DUST", 0),
    BUBBLES("WATER_BUBBLE", 0),
    CLOUD("CLOUD", 0),
    CRITICALS("CRIT", 0),
    ENCHANT_CRITS("CRIT_MAGIC", 0),
    ENCHANTS("ENCHANTMENT_TABLE", 0),
    ENDER("PORTAL", 0),
    EXPLODE("EXPLOSION_NORMAL", 0),
    FLAME("FLAME", 0),
    FIREWORKS("FIREWORKS_SPARK", 0),
    FOOTSTEP("FOOTSTEP", 0),
    HAPPY("VILLAGER_HAPPY", 0),
    HEARTS("HEART", 0),
    ITEM_CRACK("ITEM_CRACK", 0),
    ITEM_TAKE("ITEM_TAKE", 0),
    INVIS_SWIRL("SPELL_MOB_AMBIENT", 0),
    LARGE_EXPLODE("EXPLOSION_LARGE", 0),
    LARGE_SMOKE("SMOKE_LARGE", 0),
    LAVA_SPARK("LAVA", 0),
    LAVA("DRIP_LAVA", 0),
    MOB_APPEARANCE("MOB_APPEARANCE", 0),
    NOTES("NOTE", 5),
    REDSTONE_DUST("REDSTONE", 0),
    SLIME("SLIME", 0),
    SMOKE("SMOKE_NORMAL", 0),
    SNOW("SNOW_SHOVEL", 0),
    SNOWBALL("SNOWBALL", 0),
    SPLASH("SPELL_INSTANT", 0),
    SUSPEND("SUSPENDED", 0),
    SWIRL("SPELL_MOB", 0),
    WHITE_SWIRL("SPELL", 1),
    THUNDERCLOUD("VILLAGER_ANGRY", 0),
    TOWN_AURA("TOWN_AURA", 0),
    VOID("SUSPENDED_DEPTH", 0),
    WATER_SPLASH("WATER_SPLASH", 0),
    WATER("DRIP_WATER", 0),
    WATER_DROP("WATER_DROP", 0),
    WATER_WAKE("WATER_WAKE", 0),
    WITCH_MAGIC("SPELL_WITCH", 0);
 
	private String name;
	private int id;
 
	ParticleEffects_1_9_4(String name, int id) {
		this.name = name;
		this.id = id;
	}
 
	/**
	 * Gets the name of the Particle Effect
	 * 
	 * @return The particle effect name
	 */
	String getName() {
		return name;
	}
 
	/**
	 * Gets the id of the Particle Effect
	 * 
	 * @return The id of the Particle Effect
	 */
	int getId() {
		return id;
	}
 
	/**
	 * Send a particle effect to a player
	 * 
	 * @param effect
	 *            The particle effect to send
	 * @param player
	 *            The player to send the effect to
	 * @param location
	 *            The location to send the effect to
	 * @param offsetX
	 *            The x range of the particle effect
	 * @param offsetY
	 *            The y range of the particle effect
	 * @param offsetZ
	 *            The z range of the particle effect
	 * @param speed
	 *            The speed (or color depending on the effect) of the particle
	 *            effect
	 * @param count
	 *            The count of effects
	 */
	public static void sendToPlayer(ParticleEffects_1_9_4 effect, Player player, Location location, float offsetX, float offsetY,
			float offsetZ, float speed, int count) {
		try {
			Object packet = createPacket(effect, location, offsetX, offsetY, offsetZ, speed, count);
			sendPacket(player, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
 
	}
 
	/**
	 * Send a particle effect to all players
	 * 
	 * @param effect
	 *            The particle effect to send
	 * @param location
	 *            The location to send the effect to
	 * @param offsetX
	 *            The x range of the particle effect
	 * @param offsetY
	 *            The y range of the particle effect
	 * @param offsetZ
	 *            The z range of the particle effect
	 * @param speed
	 *            The speed (or color depending on the effect) of the particle
	 *            effect
	 * @param count
	 *            The count of effects
	 */
	public static void sendToLocation(ParticleEffects_1_9_4 effect, Location location, float offsetX, float offsetY, float offsetZ, float speed, int count) {
		try {
			Object packet = createPacket(effect, location, offsetX, offsetY, offsetZ, speed, count);
			for (Player player : Bukkit.getOnlinePlayers()) {
				sendPacket(player, packet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
 
	private static Object createPacket(ParticleEffects_1_9_4 effect, Location location, float offsetX, float offsetY,
			float offsetZ, float speed, int count) throws Exception {
		if (count <= 0) {
			count = 1;
		}
		Class<?> packetClass = getCraftClass("PacketPlayOutWorldParticles");
		Object packet = packetClass.getConstructor(EnumParticle.class, boolean.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class, int.class, int[].class).newInstance(EnumParticle.valueOf(effect.getName()), true, (float) location.getX(), (float) location.getY(), (float) location.getZ(), offsetX, offsetY, offsetZ, speed, count, null);
//		Object packet = packetClass.getConstructor(String.class, float.class, float.class, float.class, float.class,
//				float.class, float.class, float.class, int.class).newInstance(effect.name, (float) location.getX(),
//				(float) location.getY(), (float) location.getZ(), offsetX, offsetY, offsetZ, speed, count);
		return packet;
	}
 
	private static void sendPacket(Player p, Object packet) throws Exception {
		Object eplayer = getHandle(p);
		Field playerConnectionField = eplayer.getClass().getField("playerConnection");
		Object playerConnection = playerConnectionField.get(eplayer);
		for (Method m : playerConnection.getClass().getMethods()) {
			if (m.getName().equalsIgnoreCase("sendPacket")) {
				m.invoke(playerConnection, packet);
				return;
			}
		}
	}
 
	private static Object getHandle(Entity entity) {
		try {
			Method entity_getHandle = entity.getClass().getMethod("getHandle");
			Object nms_entity = entity_getHandle.invoke(entity);
			return nms_entity;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
 
	private static Class<?> getCraftClass(String name) {
		String version = getVersion() + ".";
		String className = "net.minecraft.server." + version + name;
		Class<?> clazz = null;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return clazz;
	}
 
	private static String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	}
 
}