package gmail.chorman64.gac14.worldguard.commands.worldtracker;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

import gmail.chorman64.gac14.basic.commands.PermissibleCommandBase;
import gmail.chorman64.gac14.basic.permission.IPermission;
import gmail.chorman64.gac14.worldguard.permissions.WGGenericPermission;
import gmail.chorman64.gac14.worldguard.worldtracker.BlockUpdateCache;
import gmail.chorman64.gac14.worldguard.worldtracker.QueryResult;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandQuery extends PermissibleCommandBase {



	public CommandQuery() {
		super(WGGenericPermission.WG_QUERY, "query", "/wg query <time> [radius=-1] [player=*]");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args.length==0)
			throw new WrongUsageException(getUsage(sender));
		long time = CommandBase.parseLong(args[0], 0,Long.MAX_VALUE);
		int radius;
		if(args.length==1)
			radius = -1;
		else
			radius = CommandBase.parseInt(args[1]);
		EntityPlayerMP player;
		if(args.length<3)
			player = null;
		else
			player = CommandBase.getPlayer(server, sender, args[2]);
		List<QueryResult> result = BlockUpdateCache.instance.query(Duration.of(time, ChronoUnit.SECONDS), player, sender.getPosition(), radius);
		if(result.isEmpty())
			CommandBase.notifyCommandListener(sender, this, String.format("WorldTracker Query: %s failed to match any results", String.join(" ", args)));
		else for(QueryResult q:result)
			CommandBase.notifyCommandListener(sender,this,String.format("WorldTracker Query: %s matched %s", String.join(" ", args),q));
	}

}
