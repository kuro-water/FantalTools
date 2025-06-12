package org.kgcc.fantalmod.tool;

import org.kgcc.fantalmod.registry.FantalModSkills;
import org.kgcc.fantalmod.skill.BaseSkill;

public interface FantalTool {
    BaseSkill getSkill();
    
    // todo:nbtにしないと。
    void setSkill(BaseSkill skill);
    
    default void setSkill(String skillName) {
        setSkill(FantalModSkills.SKILLS.stream()
                                       .filter(s -> s.getName().equals(skillName))
                                       .findFirst()
                                       .orElse(FantalModSkills.NONE));
    }
}
