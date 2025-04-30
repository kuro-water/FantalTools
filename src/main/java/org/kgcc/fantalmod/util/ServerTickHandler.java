package org.kgcc.fantalmod.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>タスクを管理するクラス</p>
 * <p>ゲーム開始時にregister()を呼び出せば、ServerTickEventsに登録される</p>
 * <p>タスクはstartTask()で開始する</p>
 * <p>持続tick数が経過したら自動的に削除される</p>
 * <p>毎tickごとにServerTickEventsがonTick()を呼び出すことでタスクが実行される</p>
 */
public class ServerTickHandler {
    private static final List<Task> tasks = new ArrayList<>();
    
    /**
     * <p>タスクを登録する</p>
     * <p>例：TickHandler.startTask(MAX_RECORD_NUM, () -> {ここに処理を記述});</p>
     *
     * @param durationInTicks <p>タスクの持続時間（単位：tick）</p>
     * @param task            <p>タスク</p>
     */
    public static void startTask(int durationInTicks, Runnable task) {
        tasks.add(new Task(durationInTicks, task));
    }
    
    public static void register() {
        ServerTickEvents.START_SERVER_TICK.register(ServerTickHandler::onTick);
    }
    
    /**
     * <p>タスクを実行する</p>
     * <p>毎tickごとにServerTickEventsにより呼び出される</p>
     *
     * @param server <p>サーバー</p>
     */
    private static void onTick(MinecraftServer server) {
        Iterator<Task> iterator = tasks.iterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();
            task.tickCounter++;
            task.task.run(); // 毎tick
            
            if (task.tickCounter >= task.duration) {
                iterator.remove();
            }
        }
    }
    
    /**
     * <p>タスクを表すクラス</p>
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
