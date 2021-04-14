package me.rhys.bedrock.base.check.api;

import lombok.Getter;
import lombok.Setter;
import me.rhys.bedrock.Bedrock;
import me.rhys.bedrock.base.event.CallableEvent;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

@Getter @Setter
public class Check implements CallableEvent {
    private String checkName, checkType, description;
    private boolean enabled, punished;
    private int violation, maxViolation;

    private final String alertPrefix = ChatColor.DARK_GRAY + "[" + ChatColor.RED
            + "Anticheat" + ChatColor.DARK_GRAY + "]";

    public void setup() {
        if (getClass().isAnnotationPresent(CheckInformation.class)) {
            CheckInformation checkInformation = getClass().getAnnotation(CheckInformation.class);
            this.checkName = checkInformation.checkName();
            this.checkType = checkInformation.checkType();
            this.description = checkInformation.description();
            this.enabled = checkInformation.enabled();
            this.maxViolation = checkInformation.punishmentVL();
        } else {
            Bedrock.getInstance().getLogger().warning("Unable to find CheckInformation annotation" +
                    " in the class: " + getClass().getSimpleName());
        }
    }

    public void flag(User user, String... data) {
        StringBuilder stringBuilder = new StringBuilder();

        if (data.length > 0) {
            for (String s : data) {
                stringBuilder.append(s).append(", ");
            }
        }

        if (this.violation > this.maxViolation) {
            this.violation = 0;

            new BukkitRunnable() {
                @Override
                public void run() {
                    user.getPlayer().kickPlayer(alertPrefix + " " + ChatColor.RED + "Unfair advantage.");
                    Bukkit.broadcastMessage(alertPrefix + " " + ChatColor.GRAY + "Removed " + ChatColor.RED
                            + user.getPlayer().getName() + ChatColor.GRAY + " for having an unfair advantage.");
                }
            }.runTask(Bedrock.getInstance());
        }

        String alert = this.alertPrefix + " " + ChatColor.RED + user.getPlayer().getName() +
                ChatColor.GRAY + " Failed " + ChatColor.RED + this.checkName
                + ChatColor.DARK_GRAY + " (" + ChatColor.RED + this.checkType + ChatColor.DARK_GRAY + ")"
                + ChatColor.DARK_GRAY + " " + ChatColor.RED + "x" + (this.violation++)
                + (data.length > 0 ? ChatColor.GRAY + " ["
                + ChatColor.GRAY + stringBuilder.toString().trim() + ChatColor.GRAY + "]" : "");

        Bedrock.getInstance().getUserManager().getUserMap().entrySet().parallelStream()
                .filter(entry -> entry.getValue().isAlerts()).forEach(entry ->
                entry.getValue().getPlayer().sendMessage(alert));

        // LOL
        user.getMovementProcessor().setLagBackTicks(ThreadLocalRandom.current().nextInt(160));
    }

    @Override
    public void onPacket(PacketEvent event) {
        //
    }

    @Override
    public void setupTimers(User user) {
        //
    }
}
