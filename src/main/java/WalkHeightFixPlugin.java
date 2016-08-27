import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.HabboPlugin;
import com.eu.habbo.plugin.events.users.UserTakeStepEvent;

/**
 * This plugin prevents the user from walking if the next step is too high
 */
public class WalkHeightFixPlugin extends HabboPlugin implements EventListener
{
    /**
     * This is triggered whenever the plugin gets loaded by the emulator.
     *
     * Use it as your entry point for your plugin.
     */
    @Override
    public void onEnable()
    {
        /**
         * You can use the Arcturus system.
         */
        Emulator.getLogging().logStart("Walk height fix by Beny. loaded!");

        /**
         * To register events, you call this function.
         * Make sure to implement the EventListener interface.
         */
        Emulator.getPluginManager().registerEvents(this, this);
    }

    /**
     * This is triggered whenever the plugin is going to be disposed.
     * You can still use the full Arcturus system as system integrity is
     * guaranteed.
     */
    @Override
    public void onDisable()
    {
        Emulator.getLogging().logShutdownLine("Walk height fix by Beny. has been disabled!");
    }

    /**
     * To register an event implement the interface EventListener
     * @param event The event to listen for.
     */
    @EventHandler
    public static void UserTakeStepEvent(UserTakeStepEvent event)
    {
        double maxNextStep = Double.parseDouble(Emulator.getConfig().getValue("plugins.beny.walkheightfix.maxdifference", "2.5"));
        
        HabboItem topItem = event.habbo.getHabboInfo().getCurrentRoom().getTopItemAt(event.toLocation.getX(), event.toLocation.getY());
        double oldZ = event.fromLocation.Z;
        double newZ = event.habbo.getHabboInfo().getCurrentRoom().getStackHeight(event.toLocation.getX(), event.toLocation.getY(), false);
        
        //RoomChatMessage r = new RoomChatMessage("oldZ: " + oldZ + " newZ: " + newZ + " diff: " + (newZ - oldZ) + " max: " + maxNextStep, event.habbo, RoomChatMessageBubbles.WIRED);
        //event.habbo.getClient().sendResponse(new RoomUserTalkComposer(new RoomChatMessage(r)));
        
        if(newZ - oldZ > maxNextStep || (topItem != null && (topItem.getZ() + topItem.getBaseItem().getHeight()) > maxNextStep))
        {
            event.habbo.getRoomUnit().getPathFinder().getPath().clear();
            event.habbo.getRoomUnit().getStatus().remove("mv");
            event.habbo.getHabboInfo().getCurrentRoom().sendComposer(new RoomUserStatusComposer(event.habbo.getRoomUnit()).compose());
            
            event.setCancelled(true);
        }
    }

    @Override
    public boolean hasPermission(Habbo habbo, String key) {
        return false;
    }
}
