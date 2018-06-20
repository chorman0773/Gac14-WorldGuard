package gmail.chorman64.gac14.worldguard.commands.worldtracker;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

import gmail.chorman64.gac14.basic.commands.PermissibleCommandBase;
import gmail.chorman64.gac14.basic.permission.IPermission;
import gmail.chorman64.gac14.worldguard.permissions.WGGenericPermission;
import gmail.chorman64.gac14.worldguard.worldtracker.BlockUpdateCache;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

public class CommandRollback extends PermissibleCommandBase {


	public CommandRollback() {
		super(WGGenericPermission.WG_ROLLBACK, "rollback", "/wg rollback <time> [radius=World] - Note: a radius of -1 means the entire world.");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args.length==0)
			throw new WrongUsageException(getUsage(sender));
		int radius;
		if(args.length==1)
			radius=-1;
		else
			radius = CommandBase.parseInt(args[1],-1);
		long seconds = CommandBase.parseInt(args[1],10);
		Duration d = Duration.of(seconds,ChronoUnit.SECONDS);
		int blocks = BlockUpdateCache.instance.rollback(d, sender.getPosition(), -1);
		String result = String.format("WorldTracker Rollback: %s changed %d blocks", String.join(" ", args),blocks);
		CommandBase.notifyCommandListener(sender,this,result);
	}

}
