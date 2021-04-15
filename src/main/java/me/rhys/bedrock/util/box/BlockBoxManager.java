package me.rhys.bedrock.util.box;


import lombok.Getter;
import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import me.rhys.bedrock.util.box.boxes.*;

@Getter
public class BlockBoxManager {
    private BlockBox blockBox;

    public BlockBoxManager() {
        String version = ProtocolVersion.getGameVersion().getServerVersion().replaceAll("v", "");

        switch (version) {
            case "1_7_R4": {
                blockBox = new BlockBox1_7_R4();
                break;
            }

            case "1_8_R1": {
                blockBox = new BlockBox1_8_R1();
                break;
            }

            case "1_8_R2": {
                blockBox = new BlockBox1_8_R2();
                break;
            }

            case "1_8_R3": {
                blockBox = new BlockBox1_8_R3();
                break;
            }

            case "1_9_R1":{
                blockBox = new BlockBox1_9_R1();
                break;
            }

            case "1_9_R2":{
                blockBox = new BlockBox1_9_R2();
                break;
            }

            case "1_10_R1": {
                blockBox = new BlockBox1_10_R1();
                break;
            }

            case "1_11_R1": {
                blockBox = new BlockBox1_11_R1();
                break;
            }

            case "1_12_R1": {
                blockBox = new BlockBox1_12_R1();
                break;
            }

            case "1_13_R1": {
                blockBox = new BlockBox1_13_R1();
                break;
            }

            case "1_13_R2": {
                blockBox = new BlockBox1_13_R2();
                break;
            }
        }
    }
}
