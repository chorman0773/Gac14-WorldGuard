package gmail.chorman64.gac14.worldguard.chunkprotector;

import java.util.UUID;

import javax.annotation.Nullable;

import gmail.chorman64.gac14.basic.Core;
import gmail.chorman64.gac14.basic.players.PlayerProfile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;

public class AccessRequirement {
	@Nullable
	private AccessRequirement chain;
	@Nullable
	private EnumChainConnectionType connection;
	private boolean inverted;
	private EnumAccessRequirementType type;
	private String value;
	private String extra;
	
	protected AccessRequirement() {}
	
	public AccessRequirement(EnumAccessRequirementType type,String value) {
		this(type,value,"",false);
	}
	public AccessRequirement(EnumAccessRequirementType type, String value, String extra) {
		// TODO Auto-generated constructor stub
	}
	public AccessRequirement(EnumAccessRequirementType type, String value, String extra, boolean invert) {
		this.type = type;
		this.value = value;
		this.extra = extra;
		this.inverted = invert;
	}
	public void chain(EnumChainConnectionType connect,AccessRequirement chain) {
		this.connection = connect;
		this.chain = chain;
	}
	public static final AccessRequirement ofNBT(NBTTagCompound comp) {
		if(comp.hasKey("Requirements", NBT.TAG_LIST)) {
			return new AccessRequirementList(comp);
		}else if(comp.hasNoTags())
			return AllwaysTrue._true;
		EnumAccessRequirementType type = EnumAccessRequirementType.valueOf(comp.getString("type").toUpperCase());
		String value = comp.getString("value");
		String extra = comp.getString("comp");
		boolean inverted = comp.getBoolean("inverted");
		AccessRequirement ret = new AccessRequirement(type,value,extra,inverted);
		if(comp.hasKey("chain", NBT.TAG_COMPOUND)) {
			NBTTagCompound chain = comp.getCompoundTag("chain");
			NBTTagCompound sub = chain.getCompoundTag("Requirement");
			EnumChainConnectionType con = EnumChainConnectionType.valueOf(chain.getString("connection").toUpperCase());
			ret.chain(con, ofNBT(sub));
		}
		return ret;
	}
	
	public boolean meetsRequirements(PlayerProfile prof) {
		boolean doesMeet = false;
		switch(type) {
		case HAS_PERMISSION:
			doesMeet = prof.hasPermission(Core.instance.server.getPlayerList(), Core.instance.permissionManager.getNode(value));
		break;
		case QUERY:
			doesMeet = prof.query(value)==extra;
		break;
		case WHITELIST:
			String[] entries = value.split(",\\s+");
			for(String id:entries) {
				UUID uid = UUID.fromString(id);
				if(prof.owner().getUniqueID().equals(uid))
					{doesMeet = true;break;}
			}
			break;
	
		}
		if(this.chain!=null) {
			switch(connection) {
			case AND:
				if(!doesMeet)
					return false;
				doesMeet = doesMeet&&chain.meetsRequirements(prof);
				break;
			case OR:
				if(doesMeet)
					return true;
				doesMeet = doesMeet||chain.meetsRequirements(prof);
				break;
			case XOR:
				doesMeet = Boolean.logicalXor(doesMeet, chain.meetsRequirements(prof));
				break;
			default:
				break;
			
			}
		}
		return doesMeet;
	}
	public NBTTagCompound serializeNBT() {
		NBTTagCompound ret = new NBTTagCompound();
		ret.setString("type", type.name());
		ret.setString("value", value);
		ret.setString("extra", extra);
		ret.setBoolean("Inverted", inverted);
		if(this.chain!=null) {
			NBTTagCompound chain = new NBTTagCompound();
			chain.setTag("Requirement", this.chain.serializeNBT());
			chain.setString("connection", connection.name());
			ret.setTag("chain", chain);
		}
		return ret;
	}
	
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append("type:"+type.name().toLowerCase()+System.lineSeparator());
		ret.append("value:"+value+System.lineSeparator());
		if(extra.length()>1)
			ret.append("data:"+extra+System.lineSeparator());
		if(this.chain!=null) {
			ret.append(this.connection.name().toLowerCase()+System.lineSeparator());
			ret.append(this.chain);
		}
		return ret.toString();
	}
}
