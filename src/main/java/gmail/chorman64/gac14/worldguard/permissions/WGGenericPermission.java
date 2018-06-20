package gmail.chorman64.gac14.worldguard.permissions;

import gmail.chorman64.gac14.basic.permission.IPermission;
import gmail.chorman64.gac14.basic.permission.PermissionBase;
import gmail.chorman64.gac14.basic.permission.RootPermission;
import gmail.chorman64.gac14.worldguard.Gac14WorldGuard;
import net.minecraft.server.management.PlayerList;

public class WGGenericPermission extends PermissionBase {
	/*WG_PROTECT("commands.protect",4,"Allows users to protect chunks from modification"),
	WG_SETREQUIREMENTS("command.requirements",4,"Allows users to modify requirements for a chunk."),
	WG_USECOMMAND("commands.permissions",4,"Allows users to change other users WorldGuard permissions"),
	WG_QUERY("commands.query",4,"Allows users to read edit history of a local area, or the world"),
	WG_ROLLBACK("command.rollback",4,"Allows users to rollback edits in a local area, or the entire world within a timeframe"),
	WG_SNAPSHOT("command.snapshot",4,"Allows users to make a snap-shot of all blocks in the local area, to rollback to");*/

	public static final WGGenericPermission WG_ROOT = new WGGenericPermission("worldguard",-1,"Basic Permission for World Guard",RootPermission.ROOT);
	public static final WGGenericPermission WG_COMMAND = new WGGenericPermission("commands",-1,"Basic Permission for World Guard commands",WG_ROOT);
	public static final WGGenericPermission WG_WORLDTRACKER = new WGGenericPermission("worldtracker",4,"Basic Permission for commands related to worldtracker",WG_COMMAND);
	public static final WGGenericPermission WG_CHUCKPROTECTOR = new WGGenericPermission("chunkprotector",4,"Basic Permissions for commands related to chunkprotector",WG_COMMAND);
	public static final WGGenericPermission WG_PROTECT = new WGGenericPermission("protect",4,"Permission to add chunk permissions",WG_CHUCKPROTECTOR);
	public static final WGGenericPermission WG_SETREQUIREMENTS = new WGGenericPermission("requirements",4,"Permission to change a chunk's permissions",WG_CHUCKPROTECTOR);
	public static final WGGenericPermission WG_QUERY = new WGGenericPermission("query",4,"Permission to query block edit history",WG_WORLDTRACKER);
	public static final WGGenericPermission WG_ROLLBACK = new WGGenericPermission("rollback",4,"Permission to rollback a local area or the entire world within a timeframe",WG_WORLDTRACKER);
	public static final WGGenericPermission WG_SNAPSHOT = new WGGenericPermission("snapshot",4,"Permission to make a snap-shot of the local area to rollback to",WG_WORLDTRACKER);


	WGGenericPermission(String name,int level,String description,IPermission root){
		super(root,name,description,level);
	}



}
