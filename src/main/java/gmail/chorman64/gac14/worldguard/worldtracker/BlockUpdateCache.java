package gmail.chorman64.gac14.worldguard.worldtracker;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;

import org.apache.commons.lang3.mutable.MutableInt;

import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockUpdateCache {

	public static void clinit() {}

	private SortedSet<BlockUpdate> updates = Sets.newTreeSet();
	public static final BlockUpdateCache instance = new BlockUpdateCache();
	public BlockUpdateCache() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public List<QueryResult> query(final Duration time,final EntityPlayerMP source,final BlockPos center,final int rad){
		QueryResult[] update = updates.stream().filter(u->{return u.isWithinTimeFrame(Instant.now(), time);}).filter(u->u.isLocatedNear(center, rad)).
		filter(u->u.matches(null, source, null)).map(u->new QueryResult(source,u.getPos(),u)).toArray(QueryResult[]::new);
		return Arrays.asList(update);
	}
	public long countDestroyed(final Duration time,final EntityPlayerMP source,final Block b){
		return updates.stream().filter(u->u.isWithinTimeFrame(Instant.now(), time)).filter(u->u.matchesBreak(source, b)).count();
	}
	public int rollback(final Duration time, final BlockPos center, final int rad) {
		final MutableInt counter = new MutableInt();
		updates.stream().filter(u->u.isWithinTimeFrame(Instant.now(), time)).filter(u->u.isLocatedNear(center, rad))
		.forEach(u->{u.rollback();counter.increment();});
		return counter.intValue();
	}

	@SubscribeEvent
	public void blockBroken(BlockEvent.BreakEvent e) {
		World w = e.getWorld();
		IBlockState previous = w.getBlockState(e.getPos());
		IBlockState curr = e.getState();
		updates.add(new BlockUpdate(e.getPos(),previous,w,(EntityPlayerMP)e.getPlayer(),curr,Instant.now()));
	}

	@SubscribeEvent
	public void blockPlaced(BlockEvent.PlaceEvent e) {
		World w = e.getWorld();
		IBlockState previous = w.getBlockState(e.getPos());
		IBlockState curr = e.getState();
		updates.add(new BlockUpdate(e.getPos(),previous,w,(EntityPlayerMP)e.getPlayer(),curr,Instant.now()));
	}

}
