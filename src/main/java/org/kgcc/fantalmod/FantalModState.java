package org.kgcc.fantalmod;

public class FantalModState {
    public enum ImagePosition {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        CUSTOM
    }
    
    private static boolean showImage = true;
    private static ImagePosition imagePosition = ImagePosition.TOP_LEFT;
    private static int customX = 0;
    private static int customY = 0;
    
    // クライアント側で管理するpollutionデータ
    private static int playerPollution = 0;
    private static int totalPollution = 0;
    
    public static boolean isShowImage() {
        return showImage;
    }
    
    public static void setShowImage(boolean value) {
        showImage = value;
    }
    
    public static ImagePosition getImagePosition() {
        return imagePosition;
    }
    
    public static void setImagePosition(ImagePosition pos) {
        imagePosition = pos;
    }
    
    public static int getCustomX() {
        return customX;
    }
    
    public static void setCustomX(int x) {
        customX = x;
    }
    
    public static int getCustomY() {
        return customY;
    }
    
    public static void setCustomY(int y) {
        customY = y;
    }
    
    // プレイヤー個別のpollutionを取得
    public static int getPlayerPollution() {
        return playerPollution;
    }
    
    // プレイヤー個別のpollutionを設定
    public static void setPlayerPollution(int pollution) {
        playerPollution = pollution;
    }
    
    // サーバー全体のpollutionを取得
    public static int getTotalPollution() {
        return totalPollution;
    }
    
    // サーバー全体のpollutionを設定
    public static void setTotalPollution(int pollution) {
        totalPollution = pollution;
    }

}
