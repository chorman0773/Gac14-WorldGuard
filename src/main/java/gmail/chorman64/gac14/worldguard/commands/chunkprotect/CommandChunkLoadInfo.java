package gmail.chorman64.gac14.worldguard.commands.chunkprotect;

import java.util.List;

import gmail.chorman64.gac14.basic.commands.PermissibleCommandBase;
import gmail.chorman64.gac14.basic.permission.IPermission;
import gmail.chorman64.gac14.worldguard.chunkprotector.ProtectedChunk;
import gmail.chorman64.gac14.worldguard.permissions.WGGenericPermission;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class CommandChunkLoadInfo extends PermissibleCommandBase {





	public CommandChunkLoadInfo() {
		super(WGGenericPermission.WG_SETREQUIREMENTS, "setrequirements", "/wg chunk setrequirements <tag>");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args.length==0)
			throw new WrongUsageException(getUsage(sender));
		String tag = args[0];
		try {
		NBTTagCompound comp = JsonToNBT.getTagFromJson(tag);
		World w = sender.getEntityWorld();
		Chunk c = w.getChunkFromBlockCoords(sender.getPosition());
		ProtectedChunk pc = ProtectedChunk.get(w, c);
		if(pc==null)
			CommandBase.notifyCommandListener(sender, this, "Your current chunk is not protected by WorldGuard. You can protect this chunk using /wg chunk protect <tag>");
		else {
			pc.load(comp);
		}
		}catch(NBTException e) {
			throw new CommandException("NBTException :"+e.getMessage());
		}
	}

}
