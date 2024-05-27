package strops.patch;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.MawBank;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import strops.utilities.ExtendedSaveFile;

import java.util.HashMap;

import static basemod.BaseMod.gson;
import static basemod.BaseMod.logger;

public class PatchSoulCannon {

    @SpirePatch(
            clz= AbstractDungeon.class,
            method=SpirePatch.CLASS
    )
    public static class PatchTool1{
        public static StaticSpireField<Integer> notUsedCannonTurn= new StaticSpireField<>(() -> 0);
    }

    @SpirePatch(
            clz= SaveFile.class,
            method=SpirePatch.CLASS
    )
    public static class PatchTool2{
        public static SpireField<Integer> not_used_cannon_turn= new SpireField<>(()->0);
    }

    @SpirePatch(
            clz= SaveFile.class,
            method=SpirePatch.CONSTRUCTOR,
            paramtypez = {SaveFile.SaveType.class}
    )
    public static class PatchTool3 {
        @SpireInsertPatch(rloc = 67)
        public static void Insert(SaveFile __instance, SaveFile.SaveType type) {
            PatchTool2.not_used_cannon_turn.set(__instance, PatchTool1.notUsedCannonTurn.get());
        }
    }

    @SpirePatch(
            clz= SaveAndContinue.class,
            method="save"
    )
    public static class PatchTool4 {
        @SpireInsertPatch(rloc = 92,localvars = {"params"})
        public static void Insert(SaveFile save,@ByRef HashMap<Object, Object>[] params) {
            params[0].put("not_used_cannon_turn", PatchTool2.not_used_cannon_turn.get(save));
        }
    }

    @SpirePatch(
            clz= AbstractDungeon.class,
            method="loadSave"
    )
    public static class PatchTool5 {
        @SpirePrefixPatch
        public static void Prefix(AbstractDungeon __instance, SaveFile saveFile) {
            PatchTool1.notUsedCannonTurn.set(PatchTool2.not_used_cannon_turn.get(saveFile));
        }
    }

    @SpirePatch(
            clz= SaveAndContinue.class,
            method="loadSaveFile",
            paramtypez={String.class}
    )
    public static class PatchTool6 {
        @SpireInsertPatch(rloc=23,localvars={"savestr","saveFile"})
        public static void Insert(String filePath,String savestr,SaveFile saveFile) {
            ExtendedSaveFile saveFilePlus;
            saveFilePlus=gson.fromJson(savestr,ExtendedSaveFile.class);
            PatchTool2.not_used_cannon_turn.set(saveFile,saveFilePlus.not_used_cannon_turn);
        }
    }

    @SpirePatch(
            clz= AbstractDungeon.class,
            method="reset"
    )
    public static class PatchTool7 {
        @SpireInsertPatch(rloc=3)
        public static void Insert() {
            PatchTool1.notUsedCannonTurn.set(0);
        }
    }
}
