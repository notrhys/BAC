package me.rhys.bedrock.config;

import me.rhys.bedrock.Bedrock;

public class ConfigLoader {

    public void load() {
        Bedrock.getInstance().getConfig().options().copyDefaults(true);
        Bedrock.getInstance().saveConfig();

        Bedrock.getInstance().getConfigValues().setPrefix(this.convertColor(Bedrock.getInstance().getConfig()
                .getString("Prefix")));
        Bedrock.getInstance().getConfigValues().setLagBack(Bedrock.getInstance().getConfig()
                .getBoolean("Punishment.LagBack"));
        Bedrock.getInstance().getConfigValues().setPunish(Bedrock.getInstance().getConfig()
                .getBoolean("Punishment.Command.Enabled"));
        Bedrock.getInstance().getConfigValues().setPunishCommand(this.convertColor(Bedrock.getInstance().getConfig()
                .getString("Punishment.Command.Execute")));
        Bedrock.getInstance().getConfigValues().setAnnounce(Bedrock.getInstance().getConfig()
                .getBoolean("Punishment.Announce.Enabled"));
        Bedrock.getInstance().getConfigValues().setAnnounceMessage(this.convertColor(Bedrock.getInstance().getConfig()
                .getString("Punishment.Announce.Message")));
    }

    String convertColor(String in) {
        return in.replace("&", "§");
    }
}
