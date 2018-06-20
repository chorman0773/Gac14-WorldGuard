package gmail.chorman64.gac14.worldguard.commands;

import gmail.chorman64.gac14.basic.Core;
import gmail.chorman64.gac14.worldguard.commands.chunkprotect.CommandChunkProtectBase;
import gmail.chorman64.gac14.worldguard.commands.worldtracker.CommandQuery;
import gmail.chorman64.gac14.worldguard.commands.worldtracker.CommandRollback;
import gmail.chorman64.gac14.worldguard.permissions.WGGenericPermission;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.CommandTreeBase;

public class CommandWGBase extends CommandTreeBase {

	public CommandWGBase() {
		addSubcommand(new CommandChunkProtectBase());
		addSubcommand(new CommandQuery());
		addSubcommand(new CommandRollback());
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "wg";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "/wg <command>";
	}

	/* (non-Javadoc)
	 * @see net.minecraft.command.CommandBase#getRequiredPermissionLevel()
	 */
	@Override
	public int getRequiredPermissionLevel() {
		// TODO Auto-generated method stub
		return 1;
	}

}
