package org.kgcc.fantalmod.test;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TickHandler {
    private static final List<Task> tasks = new ArrayList<>();
    
    /**
     * タスクを登録する
     * @param durationInTicks タスクの持続時間（単位：tick）
     * @param task タスク
     */
    public static void startTask(int durationInTicks, Runnable task) {
        tasks.add(new Task(durationInTicks, task));
    }
    
    public static void register() {
        ServerTickEvents.START_SERVER_TICK.register(TickHandler::onTick);
    }
    
    /**
     * タスクを実行する
     * 毎tickごとにServerTickEventsにより呼び出される
     * @param server サーバー
     */
    private static void onTick(MinecraftServer server) {
        Iterator<Task> iterator = tasks.iterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();
            task.tickCounter++;
            if (task.tickCounter % 20 == 0) { // 毎秒
                task.task.run();
            }
            if (task.tickCounter >= task.duration) {
                iterator.remove();
            }
        }
    }
    
    /**
     * タスクを表すクラス
     */
    private static class Task {
        int tickCounter = 0;
        final int duration;
        final Runnable task;
        
        Task(int duration, Runnable task) {
            this.duration = duration;
            this.task = task;
        }
    }
}