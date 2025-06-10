package org.kgcc.fantalmod.tool;

import org.kgcc.fantalmod.registry.FantalModSkills;
import org.kgcc.fantalmod.skill.BaseSkill;

public interface FantalTool {
    // todo:nbtにしないと。
    void setSkill(BaseSkill skill);
    
    default void setSkill(String skillName) {
        setSkill(FantalModSkills.SKILLS.get(skillName));
    }
    
    // todo:ツールチップか何かに表示したい
    BaseSkill getSkill();
}
