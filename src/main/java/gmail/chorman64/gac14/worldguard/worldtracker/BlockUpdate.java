package gmail.chorman64.gac14.worldguard.worldtracker;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockUpdate implements Comparable<BlockUpdate> {

	private IBlockState old;
	private World access;
	private EntityPlayerMP source;
	private IBlockState update;
	private Instant time;
	private BlockPos pos;

	public BlockUpdate(BlockPos pos,IBlockState old,World access, EntityPlayerMP source,IBlockState update,Instant time) {
		this.old = old;
		this.access = access;
		this.source = source;
		this.update = update;
		this.time = time;
		this.pos = pos;
	}

	public boolean matches(@Nullable IBlockState old,@Nullable EntityPlayerMP source, @Nullable IBlockState update) {
		if(old==null&&source==null&&update==null)
			throw new NullPointerException("Must provide at least one filter set");
		if(old!=null) {
			if(!Block.isEqualTo(old.getBlock(), this.old.getBlock()))
				return false;
			else if(!isCollectionEqual(old.getPropertyKeys(),this.old.getPropertyKeys()))
				return false;
			else if(!isMapEqual(old.getProperties(),this.old.getProperties()))
				return false;
		}
		if(source!=null) {
			if(!source.getGameProfile().equals(this.source.getGameProfile()))
				return false;
		}
		if(update!=null) {
			if(!Block.isEqualTo(update.getBlock(), this.update.getBlock()))
				return false;
			else if(!isCollectionEqual(update.getPropertyKeys(),this.update.getPropertyKeys()))
				return false;
			else if(!isMapEqual(update.getProperties(),this.update.getProperties()))
				return false;
		}

		return true;

	}
	public boolean matchesBreak(@Nullable EntityPlayerMP p, Block src) {
		if(!(old.getBlock()==src&&update.getBlock()==Blocks.AIR))
			return false;
		if(p!=null&&!this.source.getGameProfile().equals(p.getGameProfile()))
			return false;
		return true;
	}

	public boolean isWithinTimeFrame(Instant from, Duration back) {
		if(from.isBefore(this.time))
			return false;
		return back.toMillis()+this.time.toEpochMilli()>=from.toEpochMilli();
	}

	/**
	 * Sets the state of the block to its state prior to this;
	 */
	public void rollback() {
		access.setBlockState(getPos(), old);
	}

	private static <T> boolean isCollectionEqual(Collection<T> a, Collection<T> b) {
		return a.isEmpty()?b.isEmpty():(a.size()==b.size()&&a.containsAll(b)&&b.containsAll(a));
	}
	private static<K,V>  boolean isMapEqual(Map<K,V> a,Map<K,V> b) {
		return a.isEmpty()?b.isEmpty():isCollectionEqual(a.entrySet(),b.entrySet());
	}

	@Override
	public int compareTo(BlockUpdate arg0) {
		// TODO Auto-generated method stub
		return time.compareTo(arg0.time);
	}

	public String toString() {
		return time.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)+" "+toString(old)+"->"+toString(update);
	}

	public boolean isLocatedNear(BlockPos pos, int radius) {
		if(radius==-1)
			return true;
		return pos.distanceSq(this.getPos())<=radius;
	}

	public static String toString(IBlockState state) {
		StringBuilder builder = new StringBuilder();
		builder.append(state.getBlock().getRegistryName());
		if(!state.getProperties().isEmpty()) {
			builder.append("@");
			for(IProperty<?> property:state.getPropertyKeys()) {
				builder.append(property.getName());
				builder.append("=");
				builder.append(state.getValue(property));
				builder.append(",");
			}
			builder.deleteCharAt(builder.length());
		}
		return builder.toString();
	}

	/**
	 * @return the pos
	 */
	public BlockPos getPos() {
		return pos;
	}


}
