package gmail.chorman64.gac14.worldguard.worldtracker;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;

public class QueryResult {

	private EntityPlayerMP source;
	private BlockPos pos;
	private BlockUpdate update;

	public QueryResult(EntityPlayerMP source,BlockPos pos,BlockUpdate update) {
		this.source = source;
		this.pos = pos;
		this.update = update;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder("\u00a7aWG Result\u00a78>>\u00a7r");
		if(source!=null) {
			builder.append(source.getName());
			builder.append('(').append(source.getCachedUniqueIdString()).append(')');
		}else
			builder.append("Unknown Source");
		builder.append("@").append(toString(pos));
		builder.append(":").append(update);
		
		return builder.toString();
		
	}
	
	public static final String toString(BlockPos pos) {
		return "("+pos.getX()+","+pos.getY()+","+pos.getZ()+")";
	}
	
	
}
