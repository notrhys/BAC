package me.rhys.bedrock.base.user;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.FloodgateAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class UserManager {
    private final Map<UUID, User> userMap = new HashMap<>();

    public UserManager() {
        Bukkit.getServer().getOnlinePlayers().forEach(this::addUser);
    }

    public void addUser(Player player) {
        if (this.isBedrock(player)) {
            this.userMap.put(player.getUniqueId(), new User(player));
        }
    }

    public User getUser(Player player) {
        return this.userMap.getOrDefault(player.getUniqueId(), null);
    }

    public void removeUser(Player player) {
        this.userMap.remove(player.getUniqueId());
    }

    boolean isBedrock(Player player) {
        return FloodgateAPI.isBedrockPlayer(player);
    }
}
