package org.kgcc.fantalmod.registry;

import org.kgcc.fantalmod.skill.*;
import org.kgcc.fantalmod.skill.HammerSkill;
import org.kgcc.fantalmod.skill.SmeltSkill;

import java.util.LinkedList;
import java.util.List;

/**
 * 自作スキルの管理用クラス
 * バニラのItemやBlockを参考にした
 */
public class FantalModSkills {
    public static final List<BaseSkill> SKILLS = new LinkedList<>();
    
    public static final BaseSkill SMELT = registerSkill(new SmeltSkill());
    public static final BaseSkill HAMMER = registerSkill(new HammerSkill());
    public static final BaseSkill RECALL = registerSkill(new RecallSkill());
    public static final BaseSkill BLINK = registerSkill(new BlinkSkill());
    public static final BaseSkill PLACE_TORCH = registerSkill(new PlaceTorchSkill());
    public static final BaseSkill MINING = registerSkill(new MiningSkill());
    public static final BaseSkill HASTE = registerSkill(new HasteSkill());
    public static final BaseSkill HEALTH_BOOST = registerSkill(new HealthBoostSkill());
    public static final BaseSkill STRENGTH = registerSkill(new StrengthSkill());
    public static final BaseSkill NONE = registerSkill(new NoneSkill());
    
    
    private static BaseSkill registerSkill(BaseSkill skill) {
        SKILLS.add(skill);
        return skill;
    }
    
    public static void initialize() {
    }
}
