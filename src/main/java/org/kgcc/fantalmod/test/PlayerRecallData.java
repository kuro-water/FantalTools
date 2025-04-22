package org.kgcc.fantalmod.test;

import net.minecraft.server.network.ServerPlayNetworkHandler;

import java.util.LinkedList;

public class PlayerRecallData {
    private final LinkedList<RecallData> recallDataList = new LinkedList<>();
    
    /**
     * ServerPlayNetworkHandler.requestTeleport()を用いてリコールする
     *
     * @param networkHandler
     */
    public void recallOnce(ServerPlayNetworkHandler networkHandler) {
        RecallData data = recallDataList.removeLast();
        // プレイヤーを指定した位置にテレポート
        networkHandler.requestTeleport(data.pos.x, data.pos.y, data.pos.z, data.yaw, data.pitch);
    }
}
