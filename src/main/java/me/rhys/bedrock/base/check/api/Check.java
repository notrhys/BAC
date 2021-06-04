package me.rhys.bedrock.base.check.api;

import lombok.Getter;
import lombok.Setter;
import me.rhys.bedrock.Bedrock;
import me.rhys.bedrock.base.event.CallableEvent;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

@Getter @Setter
public class Check implements CallableEvent {
    private String checkName, checkType, description;
    private boolean enabled, punished, lagBack, canPunish, cancelAttack;
    private int violation, maxViolation;

    public void setup() {
        if (getClass().isAnnotationPresent(CheckInformation.class)) {
            CheckInformation checkInformation = getClass().getAnnotation(CheckInformation.class);
            this.checkName = checkInformation.checkName();
            this.checkType = checkInformation.checkType();
            this.description = checkInformation.description();
            this.enabled = checkInformation.enabled();
            this.maxViolation = checkInformation.punishmentVL();
            this.lagBack = checkInformation.lagBack();
            this.canPunish = checkInformation.canPunish();
            this.cancelAttack = checkInformation.cancelAttack();
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

        if (Bedrock.getInstance().getConfigValues().isPunish() && this.canPunish && this.violation > this.maxViolation) {
            this.violation = 0;

            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Bedrock.getInstance().getConfigValues()
                            .getPunishCommand().replace("%PLAYER%", user.getPlayer().getName())
                            .replace("%CHECK%", checkName).replace("%TYPE%", checkType)
                            .replace("%VL_LEVEL%", String.valueOf(violation))
                            .replace("%PREFIX%", Bedrock.getInstance().getConfigValues().getPrefix())
                            .replaceFirst("/", ""));

                    if (Bedrock.getInstance().getConfigValues().isAnnounce()) {
                        Bukkit.broadcastMessage(Bedrock.getInstance().getConfigValues().getAnnounceMessage()
                                .replace("%PREFIX%", Bedrock.getInstance().getConfigValues().getPrefix())
                                .replace("%PLAYER%", user.getPlayer().getName()));
                    }
                }
            }.runTask(Bedrock.getInstance());
        }

        String alert = Bedrock.getInstance().getConfigValues().getPrefix()
                + " " + ChatColor.RED + user.getPlayer().getName() +
                ChatColor.GRAY + " Failed " + ChatColor.RED + this.checkName
                + ChatColor.DARK_GRAY + " (" + ChatColor.RED + this.checkType + ChatColor.DARK_GRAY + ")"
                + ChatColor.DARK_GRAY + " " + ChatColor.RED + "x" + (this.violation++)
                + (data.length > 0 ? ChatColor.GRAY + " ["
                + ChatColor.GRAY + stringBuilder.toString().trim() + ChatColor.GRAY + "]" : "");

        Bukkit.getServer().getOnlinePlayers().parallelStream().filter(player -> player.hasPermission("bac.alerts")
                || player.isOp()).forEach(player ->
                player.sendMessage(alert));

        if (this.cancelAttack) {
            user.cancelAttackTicks = 20;
        }

        if (Bedrock.getInstance().getConfigValues().isLagBack()) {
            user.getMovementProcessor().setLagBackTicks((this.lagBack ? 5 : 0));
        }
    }

    @Override
    public void onPacket(PacketEvent event) {
        //
    }

    @Override
    public void setupTimers(User user) {
        //
    }

    @Override
    public void onConnection(User user) {
        //
    }
}
