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
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class CommandChunkProtect extends PermissibleCommandBase {




	public CommandChunkProtect() {
		super(WGGenericPermission.WG_PROTECT, "protect", "/wg chunk protect <tag>");
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
		ProtectedChunk pc = new ProtectedChunk(w,c,comp);
		CommandBase.notifyCommandListener(sender, this, "Successfuly protected the chunk at("+c.xPosition+","+c.zPosition+")");
		}catch(NBTException e) {
			throw new CommandException("NBTException: "+e.getMessage());
		}

	}

}
