package org.kgcc.fantalmod.registry;

import org.kgcc.fantalmod.skill.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 自作スキルの管理用クラス
 * バニラのItemやBlockを参考にした
 */
public class FantalModSkills {
    public static final Map<String, BaseSkill> SKILLS = new LinkedHashMap<>();
    
    public static final BaseSkill BLINK = registerSkill("Blink", new BlinkSkill());
    public static final BaseSkill HASTE = registerSkill("Haste", new HasteSkill());
    public static final BaseSkill HEALTH_BOOST = registerSkill("HealthBoost", new HealthBoostSkill());
    public static final BaseSkill MINING = registerSkill("Mining", new MiningSkill());
    public static final BaseSkill PLACE_TORCH = registerSkill("PlaceTorch", new PlaceTorchSkill());
    public static final BaseSkill RECALL = registerSkill("Recall", new RecallSkill());
    public static final BaseSkill STRENGTH = registerSkill("Strength", new StrengthSkill());
    
    
    private static BaseSkill registerSkill(String name, BaseSkill skill) {
        SKILLS.put(name, skill);
        return skill;
    }
    
    public static void initialize() {
    }
}
