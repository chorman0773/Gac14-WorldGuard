package gmail.chorman64.gac14.worldguard.commands.chunkprotect;

import java.util.List;

import gmail.chorman64.gac14.basic.commands.PermissibleCommandBase;
import gmail.chorman64.gac14.basic.permission.IPermission;
import gmail.chorman64.gac14.worldguard.chunkprotector.ProtectedChunk;
import gmail.chorman64.gac14.worldguard.permissions.WGGenericPermission;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class CommandChunkGetInfo extends PermissibleCommandBase {





	public CommandChunkGetInfo() {
		super(WGGenericPermission.WG_QUERY, "info", "/wg chunk info");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		World w = sender.getEntityWorld();
		Chunk c = w.getChunkFromBlockCoords(sender.getPosition());
		ProtectedChunk pc = ProtectedChunk.get(w, c);
		if(pc==null)
			CommandBase.notifyCommandListener(sender, this, "The chunk at ("+c.xPosition+","+c.zPosition+") is not protected by worldguard");
		else {
			String[] vals = pc.toString().split("\n");
			for(String val:vals) {
				val = String.format("WorldGuard Response: %s", val);
				CommandBase.notifyCommandListener(sender, this, val);
			}
		}
	}

}
