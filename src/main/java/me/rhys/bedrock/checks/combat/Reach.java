package me.rhys.bedrock.checks.combat;

import me.rhys.bedrock.base.check.api.Check;
import me.rhys.bedrock.base.check.api.CheckInformation;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.tinyprotocol.api.Packet;
import me.rhys.bedrock.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.rhys.bedrock.util.PastLocation;
import me.rhys.bedrock.util.PlayerLocation;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "Reach", description = "Simple past-location reach check.", lagBack = false)
public class Reach extends Check {

    private Entity lastAttacked;
    private final PastLocation pastLocation = new PastLocation();
    private long lastAttack;
    private double threshold;

    @Override
    public void onConnection(User user) {
        if (this.lastAttacked != null && this.lastAttacked instanceof Player
                 && ((Player) this.lastAttacked).isOnline()
                && this.lastAttacked.getWorld() == user.getPlayer().getWorld()) {
            //For a more accurate location we are adding them when they send back the keepalive.
            this.pastLocation.addLocation(new PlayerLocation(this.lastAttacked.getWorld(),
                    this.lastAttacked.getLocation().getX(),
                    this.lastAttacked.getLocation().getY(), this.lastAttacked.getLocation().getZ(),
                    0, 0, true));
        }
    }

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket wrappedInUseEntityPacket = new WrappedInUseEntityPacket(event.getPacket(),
                        event.getUser().getPlayer());

                if (wrappedInUseEntityPacket.getEntity() != null
                        && wrappedInUseEntityPacket.getAction() == WrappedInUseEntityPacket
                        .EnumEntityUseAction.ATTACK) {

                    //Fixes false flag when switching entities
                    if (this.lastAttacked != null && this.lastAttacked != wrappedInUseEntityPacket.getEntity()) {
                        this.pastLocation.getPreviousLocations().clear();
                        this.threshold = 0;
                    }

                    this.lastAttacked = wrappedInUseEntityPacket.getEntity();
                    this.lastAttack = event.getTimestamp();
                }
                break;
            }

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if ((event.getTimestamp() - this.lastAttack) < 1L
                        && this.lastAttacked != null && this.pastLocation.getPreviousLocations().size() > 5) {

                    List<Vector> playerLocations = new ArrayList<>();
                    for (PlayerLocation loc : this.pastLocation.getEstimatedLocation(
                            user.getConnectionProcessor().getPing(), 150, event.getTimestamp())) {
                        playerLocations.add(loc.toVector());
                    }

                    if (playerLocations.size() > 0) {
                        Location currentLocation = user.getCurrentLocation()
                                .toBukkitLocation(user.getPlayer().getWorld());
                        float distance = (float) playerLocations.stream().mapToDouble(vector ->
                                vector.clone().setY(0).distance(currentLocation.toVector().clone().setY(0)) - 0.3f)
                                .min().orElse(0);

                        //Around the default bedrock client reach
                        if (distance > 3.435) {
                            if ((this.threshold += .50) > 1.4) {

                                boolean cancelHit = distance > 3.7;
                                if (cancelHit) {
                                    user.cancelAttackTicks = 20;
                                }

                                this.flag(user,
                                        "distance=" + distance,
                                        "locations=" + playerLocations.size(),
                                        "threshold=" + this.threshold,
                                        "cancel="+ cancelHit
                                );
                            }
                        } else {
                            this.threshold -= (this.threshold > 0 ? .85 : 0);
                        }
                    }
                }
                break;
            }
        }
    }
}
