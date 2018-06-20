package gmail.chorman64.gac14.worldguard.chunkprotector;

import java.util.List;

import com.google.common.collect.Lists;

import gmail.chorman64.gac14.basic.players.PlayerProfile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

public class AccessRequirementList extends AccessRequirement {
	private List<AccessRequirement> access = Lists.newArrayList();
	private EnumChainConnectionType relationship;
	public AccessRequirementList(EnumChainConnectionType type) {
		this.relationship = type;
	}
	public AccessRequirementList(NBTTagCompound access) {
		NBTTagList requirements = access.getTagList("Requirements", NBT.TAG_COMPOUND);
		this.relationship = EnumChainConnectionType.valueOf(access.getString("Connection").toUpperCase());
		for(int i=0;i<requirements.tagCount();i++) {
			NBTTagCompound req = requirements.getCompoundTagAt(i);
			chain(null,ofNBT(req));
		}
		
	}
	/* (non-Javadoc)
	 * @see gmail.chorman64.gac14.worldguard.chunkprotector.AccessRequirement#chain(gmail.chorman64.gac14.worldguard.chunkprotector.EnumChainConnectionType, gmail.chorman64.gac14.worldguard.chunkprotector.AccessRequirement)
	 */
	@Override
	public void chain(EnumChainConnectionType connect, AccessRequirement chain) {
		access.add(chain);
	}
	/* (non-Javadoc)
	 * @see gmail.chorman64.gac14.worldguard.chunkprotector.AccessRequirement#meetsRequirements(gmail.chorman64.gac14.basic.players.PlayerProfile)
	 */
	@Override
	public boolean meetsRequirements(PlayerProfile prof) {
		boolean doesMeet = false;
		boolean hasChecked = false;
		for(AccessRequirement req:access) {
			if(hasChecked) {
				if(this.relationship==EnumChainConnectionType.AND&&!doesMeet)
					return false;
				else if(this.relationship==EnumChainConnectionType.OR&&doesMeet)
					return true;
				switch(this.relationship) {
				case AND:
					doesMeet = doesMeet&&req.meetsRequirements(prof);
					break;
				case OR:
					doesMeet = doesMeet||req.meetsRequirements(prof);
					break;
				case XOR:
					doesMeet = Boolean.logicalXor(doesMeet, req.meetsRequirements(prof));
					break;
				default:
					break;
				
				}
			}else {
				hasChecked = true;
				doesMeet = req.meetsRequirements(prof);
			}
			
		}
		return doesMeet;
	}
	/* (non-Javadoc)
	 * @see gmail.chorman64.gac14.worldguard.chunkprotector.AccessRequirement#serializeNBT()
	 */
	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound ret = new NBTTagCompound();
		NBTTagList reqs = new NBTTagList();
		for(AccessRequirement r:access)
			reqs.appendTag(r.serializeNBT());
		ret.setTag("Requirements", reqs);
		ret.setString("Connection", relationship.name());
		return ret;
	}
	
	public String toString() {
		String[] strs = new String[access.size()];
		for(int i=0;i<access.size();i++) {
			strs[i] = access.get(i).toString();
		}
		return String.join(relationship.name().toLowerCase()+System.lineSeparator(), strs);
	}
	
	
	
	

}
