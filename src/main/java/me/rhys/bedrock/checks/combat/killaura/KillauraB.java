package me.rhys.bedrock.checks.combat.killaura;

import me.rhys.bedrock.base.check.api.Check;
import me.rhys.bedrock.base.check.api.CheckInformation;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.tinyprotocol.api.Packet;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "KillAura", checkType = "B",
        description = "Checks for flaws in the arm animation packet, flags a few killauras")
public class KillauraB extends Check {

    private long lastSwing, lastAttack;
    private int streak;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();
        switch (event.getType()) {

            case Packet.Client.ARM_ANIMATION: {

                if ((System.currentTimeMillis() - this.lastAttack) < 1000L) {
                    long delta = (System.currentTimeMillis() - this.lastSwing);

                    //why, just why.
                    if (delta == 0L) {
                        this.streak = 0;
                    } else if (this.streak++ > 20) {
                        this.flag(user,
                                "streak: " + this.streak,
                                "time: " + delta
                        );
                    }
                } else if (user.getTick() % 30 == 0) {
                    this.streak -= (this.streak > 0 ? 1 : 0);
                }

                this.lastSwing = System.currentTimeMillis();
                break;
            }

            case Packet.Client.USE_ENTITY: {
                this.lastAttack = System.currentTimeMillis();
                break;
            }
        }
    }
}
