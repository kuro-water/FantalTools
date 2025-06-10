package org.kgcc.fantalmod.tool;

import org.kgcc.fantalmod.skill.BaseSkill;

public interface FantalTool {
    BaseSkill skill = null;
    
    // todo:nbtにしないと。
    void setSkill(BaseSkill skill);
    
    // todo:ツールチップか何かに表示したい
    BaseSkill getSkill();
}
