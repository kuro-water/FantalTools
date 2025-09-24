package org.kgcc.fantalmod.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.kgcc.fantalmod.FantalMod;
import org.kgcc.fantalmod.registry.FantalModSkills;
import org.kgcc.fantalmod.skill.BaseSkill;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class SkillArgumentType implements ArgumentType<BaseSkill> {
    // 全スキルの文字列
    private static final Collection<String> EXAMPLES
            = FantalModSkills.SKILLS.stream().map(BaseSkill::getTranslationKey).toList();
    private static final DynamicCommandExceptionType INVALID_SKILL_EXCEPTION = new DynamicCommandExceptionType(
            (skill) -> Text.translatable("argument.fantalmod.skill.invalid", skill));
    
    public static SkillArgumentType skill() {
        FantalMod.LOGGER.info("skill");
        return new SkillArgumentType();
    }
    
    public static BaseSkill getSkill(CommandContext<ServerCommandSource> context, String name) {
        FantalMod.LOGGER.info("getSkill");
        return context.getArgument(name, BaseSkill.class);
    }
    
    @Override
    public BaseSkill parse(StringReader stringReader) throws CommandSyntaxException {
        FantalMod.LOGGER.info("parse");
        String string = stringReader.readUnquotedString();
        BaseSkill skill = FantalModSkills.SKILLS.stream()
                                                .filter(s -> s.getTranslationKey().equals(string))
                                                .findFirst()
                                                .orElse(null);
        if (skill == null) {
            FantalMod.LOGGER.error("Invalid skill: {}", string);
            throw INVALID_SKILL_EXCEPTION.createWithContext(stringReader, string);
        }
        FantalMod.LOGGER.info("Parsed skill: {}", skill.getTranslationKey());
        return skill;
    }
    
    /**
     * <p>このメソッドは、コマンド入力補完（サジェスト）を提供するためのものです。</p>
     * <p>builder.getRemainingLowerCase() で、ユーザーが現在入力している文字列（小文字）を取得します。
     * それが "true" や "false" の先頭と一致していれば、対応する文字列を補完候補として builder.suggest() で追加します。
     * 最後に builder.buildFuture() で補完候補リストを返します。</p>
     * <p>from GitHub Copilot</p>
     */
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
//        for (String example : EXAMPLES) {
//            if (example.startsWith(builder.getRemainingLowerCase())) {
//                builder.suggest(example);
//            }
//        }
        FantalMod.LOGGER.info("listSuggestions");
        
        FantalModSkills.SKILLS
                .stream()
                .map(BaseSkill::getTranslationKey)
//                .map(Text::getString)
                .filter(example -> example.startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);
        
        return builder.buildFuture();
    }
    
    @Override
    public Collection<String> getExamples() {
        FantalMod.LOGGER.info("getExamples");
        return EXAMPLES;
    }
}
