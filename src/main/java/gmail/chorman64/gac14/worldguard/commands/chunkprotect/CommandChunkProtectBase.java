package gmail.chorman64.gac14.worldguard.commands.chunkprotect;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;

public class CommandChunkProtectBase extends CommandTreeBase {

	public CommandChunkProtectBase() {
		addSubcommand(new CommandChunkGetInfo());
		addSubcommand(new CommandChunkLoadInfo());
		addSubcommand(new CommandChunkProtect());
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "chunk";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "/wg chunk <subcommand>";
	}

}
