package gmail.chorman64.gac14.worldguard.chunkprotector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.UUID;

import org.apache.logging.log4j.core.helpers.UUIDUtil;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import akka.japi.Util;
import gmail.chorman64.gac14.basic.Core;
import gmail.chorman64.gac14.basic.players.PlayerProfile;
import gmail.chorman64.gac14.basic.util.MCMathUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@EventBusSubscriber
public class ProtectedChunk {
	private static final Set<ProtectedChunk> chunks = Sets.newHashSet();
	private static final SortedMap<UUID,ProtectedChunk> idMap = Maps.newTreeMap();
	private World w;
	private Chunk c;
	private boolean explosions;
	private boolean damageProtect;
	private boolean envoysEnabled;
	private String name;
	private Map<ChunkPermissionType,AccessRequirement> requirements = Maps.newHashMap();
	private Map<ChunkPermissionType,String> errorMsgs = Maps.newHashMap();
	private UUID id;
	private ProtectedChunk() {
		for(ChunkPermissionType permission:ChunkPermissionType.values()) {
			requirements.put(permission, AllwaysTrue._true);
			errorMsgs.put(permission, defaultFor(permission));
		}
		chunks.add(this);
	}
	public static void loadChunks(File f) throws FileNotFoundException, IOException {
		NBTTagCompound comp = CompressedStreamTools.readCompressed(new FileInputStream(f));
		NBTTagList dim0 = comp.getTagList("DIM0", NBT.TAG_COMPOUND);
		NBTTagList dimN1 = comp.getTagList("DIM-1", NBT.TAG_COMPOUND);
		NBTTagList dim1 = comp.getTagList("DIM1", NBT.TAG_COMPOUND);
		World w = Core.instance.server.worldServerForDimension(0);
		for(int i=0;i<dim0.tagCount();i++) {
			NBTTagCompound tag = dim0.getCompoundTagAt(i);
			new ProtectedChunk(w,tag);
		}
		
		w = Core.instance.server.worldServerForDimension(-1);
		for(int i=0;i<dimN1.tagCount();i++) {
			NBTTagCompound tag = dimN1.getCompoundTagAt(i);
			new ProtectedChunk(w,tag);
		}
		
		w = Core.instance.server.worldServerForDimension(1);
		for(int i=0;i<dim1.tagCount();i++) {
			NBTTagCompound tag = dim1.getCompoundTagAt(i);
			new ProtectedChunk(w,tag);
		}
	}
	
	public static void saveChunks(File f) throws FileNotFoundException, IOException {
		NBTTagCompound comp = new NBTTagCompound();
		NBTTagList dim0 = new NBTTagList();
		World w0 = Core.instance.server.worldServerForDimension(0);
		NBTTagList dimN1 = new NBTTagList();
		World wN1 = Core.instance.server.worldServerForDimension(-1);
		NBTTagList dim1 = new NBTTagList();
		World w1 = Core.instance.server.worldServerForDimension(1);
		for(ProtectedChunk c:chunks) {
			if(c.w==w0)
				dim0.appendTag(c.serializeNBT());
			else if(c.w==wN1)
				dimN1.appendTag(c.serializeNBT());
			else if(c.w==w1)
				dim1.appendTag(c.serializeNBT());
		}
		comp.setTag("DIM0", dim0);
		comp.setTag("DIM-1", dimN1);
		comp.setTag("DIM1", dim1);
		CompressedStreamTools.writeCompressed(comp, new FileOutputStream(f));
	}
	
	private static final String defaultFor(ChunkPermissionType permission) {
		switch(permission) {
		case BreakBlock:
			return "You do not have permission to edit the terrian here";
		case DamageOthers:
			return "You are not allowed to PvP here";
		case OpenAnvil:
		case OpenBeacon:
		case OpenChest:
		case OpenContainer:
		case OpenFurnace:
		case OpenHopper:
		case OpenTrap:
			return "You do not have permission to open this here";
		case PlaceBlock:
			return "You do not have permission to edit the terrian here";
		case UseItem:
			return "You do not have permission to use this here";
		default:
			return "";
		}
	}
	
	public ProtectedChunk(World w,Chunk c) {
		this();
		this.w = w;
		this.c = c;
		this.name = "WorldGuard Protected Chunk ("+c.xPosition+","+c.zPosition+")";
		this.id = UUIDUtil.getTimeBasedUUID();
	}
	public ProtectedChunk(World w,Chunk c,String name) {
		this();
		this.w = w;
		this.c = c;
		this.name = name;
		this.id = UUIDUtil.getTimeBasedUUID();
	}
	public ProtectedChunk(World w,Chunk c,NBTTagCompound comp) {
		this();
		this.w = w;
		this.c = c;
		this.explosions = comp.getBoolean("AllowsExplosions");
		this.damageProtect = comp.getBoolean("DamageProtection");
		if(comp.hasKey("Name", NBT.TAG_STRING))
			this.name = comp.getString("Name");
		NBTTagCompound reqs = comp.getCompoundTag("Requirements");
		for(String s:reqs.getKeySet()) {
			ChunkPermissionType t = ChunkPermissionType.valueOf(s);
			NBTTagCompound req = comp.getCompoundTag(s);
			setRequirement(t,AccessRequirement.ofNBT(req));
		}
		NBTTagCompound errors = comp.getCompoundTag("Errors");
		for(String s:errors.getKeySet()) {
			ChunkPermissionType t = ChunkPermissionType.valueOf(s);
			setErrorMsg(t,errors.getString(s));
		}
		this.id = UUIDUtil.getTimeBasedUUID();
	}
	public ProtectedChunk(World w,NBTTagCompound comp) {
		this();
		this.w = w;
		int x = comp.getInteger("x");
		int z = comp.getInteger("z");
		this.c = w.getChunkFromChunkCoords(x, z);
		this.explosions = comp.getBoolean("AllowsExplosions");
		this.damageProtect = comp.getBoolean("DamageProtection");
		this.name = comp.getString("Name");
		this.id = comp.getUniqueId("UUID");
		NBTTagCompound reqs = comp.getCompoundTag("Requirements");
		for(String s:reqs.getKeySet()) {
			ChunkPermissionType t = ChunkPermissionType.valueOf(s);
			NBTTagCompound req = comp.getCompoundTag(s);
			setRequirement(t,AccessRequirement.ofNBT(req));
		}
		NBTTagCompound errors = comp.getCompoundTag("Errors");
		for(String s:errors.getKeySet()) {
			ChunkPermissionType t = ChunkPermissionType.valueOf(s);
			setErrorMsg(t,errors.getString(s));
		}
	}

	public void setRequirement(ChunkPermissionType type, AccessRequirement req) {
		requirements.put(type, req);
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public void setExplosionsEnabled(boolean explosions) {
		this.explosions = explosions;
	}
	public void setDamageProtection(boolean damageProtect) {
		this.damageProtect = damageProtect;
	}
	public void setEnvoysEnabled(boolean envoys) {
		this.envoysEnabled = envoys;
	}
	
	public void setErrorMsg(ChunkPermissionType type,String msg) {
		errorMsgs.put(type, msg);
	}
	/**
	 * Loads all values present in the compound into the table
	 * @param comp
	 */
	public void load(NBTTagCompound comp) {
		NBTTagCompound reqs = comp.getCompoundTag("Requirements");
		for(String s:reqs.getKeySet()) {
			ChunkPermissionType t = ChunkPermissionType.valueOf(s);
			NBTTagCompound req = comp.getCompoundTag(s);
			setRequirement(t,AccessRequirement.ofNBT(req));
		}
		NBTTagCompound errors = comp.getCompoundTag("Errors");
		for(String s:errors.getKeySet()) {
			ChunkPermissionType t = ChunkPermissionType.valueOf(s);
			setErrorMsg(t,errors.getString(s));
		}
		if(comp.hasKey("AllowExplosions",NBT.TAG_BYTE))
			this.explosions = comp.getBoolean("AllowsExplosions");
		if(comp.hasKey("DamageProtection",NBT.TAG_BYTE))
			this.damageProtect = comp.getBoolean("DamageProtection");
		if(comp.hasKey("Name",NBT.TAG_STRING))
			this.name = comp.getString("Name");
	}
	
	public NBTTagCompound serializeNBT() {
		NBTTagCompound ret = new NBTTagCompound();
		ret.setInteger("x", c.xPosition);
		ret.setInteger("z", c.zPosition);
		ret.setBoolean("AllowsExplosions", this.explosions);
		ret.setBoolean("DamageProtection", this.damageProtect);
		NBTTagCompound reqs = new NBTTagCompound();
		NBTTagCompound errors = new NBTTagCompound();
		for(ChunkPermissionType c:ChunkPermissionType.values()) {
			reqs.setTag(c.name(), requirements.get(c).serializeNBT());
			errors.setString(c.name(), errorMsgs.get(c));
		}
		ret.setTag("Requirements", reqs);
		ret.setTag("Errors", errors);
		return ret;
	}
	
	public static final ProtectedChunk get(World w,Chunk c) {
		for(ProtectedChunk ch:chunks) {
			if(ch.w==w&&ch.c==c)
				return ch;
		}
		return null;
	}
	
	public boolean areEnvoysEnabled() {
		return envoysEnabled;
	}
	
	@SubscribeEvent
	public static void explosionStart(ExplosionEvent.Start e) {
		World w = e.getWorld();
		Explosion ex = e.getExplosion();
		BlockPos pos = MCMathUtil.ofVec(ex.getPosition());
		Chunk c = w.getChunkFromBlockCoords(pos);
		ProtectedChunk pc = get(w,c);
		if(pc!=null) {
			if(!pc.explosions)
				e.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void explosionDetonate(ExplosionEvent.Detonate e) {
		World w = e.getWorld();
		List<BlockPos> toRemove = Lists.newArrayList();
		for(BlockPos pos:e.getAffectedBlocks()) {
			Chunk c = w.getChunkFromBlockCoords(pos);
			ProtectedChunk pc = get(w,c);
			if(pc!=null)
				if(!pc.explosions)
					toRemove.add(pos);
			}
		e.getAffectedBlocks().removeAll(toRemove);
	}
	
	@SubscribeEvent
	public static void entityHurt(LivingHurtEvent e) {
		if(e.getSource().canHarmInCreative())return;
		if(e.getEntityLiving() instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) e.getEntityLiving();
			World w = player.getEntityWorld();
			Chunk tChunk = w.getChunkFromBlockCoords(player.getPosition());
			ProtectedChunk tPC = get(w,tChunk);
			if(tPC!=null)
				if(tPC.damageProtect) {
					e.setCanceled(true);
					return;
				}
			Entity source = e.getSource().getEntity();
			if(source!=null&&(source instanceof EntityPlayerMP)) {
				EntityPlayerMP pSource = (EntityPlayerMP)source;
				PlayerProfile prof = PlayerProfile.get(pSource);
				Chunk sChunk = w.getChunkFromBlockCoords(pSource.getPosition());
				ProtectedChunk sPC = get(w,sChunk);
				if(tPC!=null)
					if(!tPC.allowAction(ChunkPermissionType.DamageOthers,prof))
					{
						pSource.sendMessage(new TextComponentString("\u00a74"+tPC.errorMsgs.get(ChunkPermissionType.DamageOthers)+"\u00a7r"));
						e.setCanceled(true);
					}
				if(sPC!=null) {
					if(!sPC.allowAction(ChunkPermissionType.DamageOthers,prof))
					{
						pSource.sendMessage(new TextComponentString("\u00a74"+sPC.errorMsgs.get(ChunkPermissionType.DamageOthers)+"\u00a7r"));
						e.setCanceled(true);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void blockBroken(BlockEvent.BreakEvent b) {
		EntityPlayerMP player = (EntityPlayerMP)b.getPlayer();
		World w = b.getWorld();
		Chunk c = w.getChunkFromBlockCoords(b.getPos());
		ProtectedChunk PC = get(w,c);
		if(PC!=null)
			if(!PC.allowAction(ChunkPermissionType.BreakBlock, PlayerProfile.get(player)))
			{
				player.sendMessage(new TextComponentString("\u00a74"+PC.errorMsgs.get(ChunkPermissionType.BreakBlock)+"\u00a7r"));
				b.setCanceled(true);
			}
	}
	@SubscribeEvent
	public static void blockPlaced(BlockEvent.PlaceEvent b) {
		EntityPlayerMP player = (EntityPlayerMP)b.getPlayer();
		World w = b.getWorld();
		Chunk c = w.getChunkFromBlockCoords(b.getPos());
		ProtectedChunk PC = get(w,c);
		if(PC!=null)
			if(!PC.allowAction(ChunkPermissionType.PlaceBlock, PlayerProfile.get(player)))
			{
				player.sendMessage(new TextComponentString("\u00a74"+PC.errorMsgs.get(ChunkPermissionType.PlaceBlock)+"\u00a7r"));
				b.setCanceled(true);
			}
	}
	
	private boolean allowAction(ChunkPermissionType permission, PlayerProfile prof) {
		return requirements.get(permission).meetsRequirements(prof);
	}
	
	public String getRequirementString(ChunkPermissionType permission) {
		return requirements.get(permission).toString();
	}
	public String toString() {
		ToStringHelper ret = Objects.toStringHelper("WorldGuardProtectedChunk").add("location", "("+c.xPosition+","+c.zPosition+")")
				.add(System.lineSeparator()+"ExplosionsEnabled",explosions).add("DamageProtectionEnabled", damageProtect);
		ToStringHelper permissions = Objects.toStringHelper(System.lineSeparator());
		for(ChunkPermissionType c:ChunkPermissionType.values()) {
			permissions.add(c.name(), requirements.get(c)+System.lineSeparator());
		}
		ret.add("AccessRequirements", permissions);
		return ret.toString();
	}
	
	public String getName() {
		return name;
	}
	public UUID getId() {
		return id;
	}
	
	public static boolean checkAction(ChunkPermissionType perm,PlayerProfile prof) {
		World w = prof.getWorld();
		ProtectedChunk pc = get(w,w.getChunkFromBlockCoords(prof.owner().getPosition()));
		return pc.allowAction(perm, prof);
	}

}
