package me.rhys.bedrock.checks.combat.killaura;

import me.rhys.bedrock.base.check.api.Check;
import me.rhys.bedrock.base.check.api.CheckInformation;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.tinyprotocol.api.Packet;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "KillAura", description = "Checks if the player attacks on post", cancelAttack = true)
public class KillauraA extends Check {

    private long lastPosition;
    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();
        switch (event.getType()) {
            case Packet.Client.POSITION_LOOK: {
                this.lastPosition = System.currentTimeMillis();
                break;
            }

            case Packet.Client.USE_ENTITY: {
                long delta = (System.currentTimeMillis() - this.lastPosition);

                if (delta < 5L) {
                    if ((this.threshold += 0.95) > 4.5) {
                        this.flag(user,
                                "threshold: " + this.threshold,
                                "time: " + delta
                        );
                    }
                } else {
                    this.threshold -= (this.threshold > 0 ? .30 : 0);
                }
                break;
            }
        }
    }
}
