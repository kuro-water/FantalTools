package org.kgcc.fantalmod.util;

/**
 * プレイヤーの汚染度などFantalModに関する情報を保存するクラス
 * 基本的にはそのまま使用することはなく、FantalStateManagerを通して使用する
 */
public class PlayerFantalData {
    // プレイヤーの汚染度
    private int fantalPollution = 0;
    public int getFantalPollution() {
        return fantalPollution;
    }
    public void setFantalPollution(int fantalPollution) {
        if(fantalPollution < 0) {
            fantalPollution = 0;
        }
        this.fantalPollution = fantalPollution;
    }
}
