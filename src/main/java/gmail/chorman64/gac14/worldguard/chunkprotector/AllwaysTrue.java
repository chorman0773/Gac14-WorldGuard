package gmail.chorman64.gac14.worldguard.chunkprotector;

import gmail.chorman64.gac14.basic.players.PlayerProfile;
import net.minecraft.nbt.NBTTagCompound;

public class AllwaysTrue extends AccessRequirement {
	public static final AllwaysTrue _true = new AllwaysTrue();
	/* (non-Javadoc)
	 * @see gmail.chorman64.gac14.worldguard.chunkprotector.AccessRequirement#chain(gmail.chorman64.gac14.worldguard.chunkprotector.EnumChainConnectionType, gmail.chorman64.gac14.worldguard.chunkprotector.AccessRequirement)
	 */
	@Override
	public void chain(EnumChainConnectionType connect, AccessRequirement chain) {
		
	}

	/* (non-Javadoc)
	 * @see gmail.chorman64.gac14.worldguard.chunkprotector.AccessRequirement#meetsRequirements(gmail.chorman64.gac14.basic.players.PlayerProfile)
	 */
	@Override
	public boolean meetsRequirements(PlayerProfile prof) {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see gmail.chorman64.gac14.worldguard.chunkprotector.AccessRequirement#serializeNBT()
	 */
	@Override
	public NBTTagCompound serializeNBT() {
		// TODO Auto-generated method stub
		return new NBTTagCompound();
	}
	
	public String toString() {
		return "Any Player";
	}

}
