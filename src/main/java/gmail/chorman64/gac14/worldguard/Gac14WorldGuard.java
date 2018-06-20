package gmail.chorman64.gac14.worldguard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import gmail.chorman64.gac14.basic.Core;
import gmail.chorman64.gac14.basic.EnumFormatting;
import gmail.chorman64.gac14.basic.permission.IPermission;
import gmail.chorman64.gac14.worldguard.chunkprotector.ProtectedChunk;
import gmail.chorman64.gac14.worldguard.commands.CommandWGBase;
import gmail.chorman64.gac14.worldguard.permissions.WGGenericPermission;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

@Mod(modid = Gac14WorldGuard.MODID, version = Gac14WorldGuard.VERSION,dependencies=Core.ID, serverSideOnly=true, acceptableRemoteVersions="*")
public class Gac14WorldGuard
{
    public static final String MODID = "gac14worldguard";
    public static final String VERSION = "1.0.0";
    public static final String GAC14_Core = Core.ID;

    @Instance(GAC14_Core)
    public static Core core;
	private File chunks;

    @EventHandler
    public void serverStarting(FMLServerStartingEvent e) throws FileNotFoundException, IOException {
    	e.registerServerCommand(new CommandWGBase());
    	chunks = new File(core.dataDirectory,"protections.dat");
    	if(!chunks.exists()) {
    		chunks.createNewFile();
    		CompressedStreamTools.writeCompressed(new NBTTagCompound(), new FileOutputStream(chunks));
    	}
    	ProtectedChunk.loadChunks(chunks);
    }

    @EventHandler
    public void serverStopping(FMLServerStoppingEvent e) throws FileNotFoundException, IOException {
    	ProtectedChunk.saveChunks(chunks);
    }

}
