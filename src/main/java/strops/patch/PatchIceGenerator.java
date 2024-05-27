package strops.patch;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ExplosivePower;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import strops.powers.FrozenPower;

public class PatchIceGenerator {

    /*
    @SpirePatch(
            clz=AbstractPower.class,
            method=SpirePatch.CLASS
    )
    public static class PatchTool1 {
        @SpireEnum
        public static AbstractPower.PowerType NEUTRAL;
    }

     */

    @SpirePatch(clz = GameActionManager.class, method = "getNextAction")
    public static class PatchTool2 {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals("com.megacrit.cardcrawl.monsters.AbstractMonster") && m
                            .getMethodName().equals("takeTurn"))
                        m.replace("if (!m.hasPower(strops.powers.FrozenPower.POWER_ID)) {$_ = $proceed($$);}");
                }
            };
        }
    }

    @SpirePatch(clz = AbstractMonster.class, method = "rollMove")
    public static class PatchTool3 {
        public static SpireReturn<Void> Prefix(AbstractMonster __instance) {
            if (__instance.hasPower(FrozenPower.POWER_ID)){
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = ExplosivePower.class, method = "duringTurn")
    public static class PatchTool4 {
        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn<Void> Insert(ExplosivePower __instance) {
            if (__instance.owner.hasPower(FrozenPower.POWER_ID))
                return SpireReturn.Return();
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher.NewExprMatcher newExprMatcher = new Matcher.NewExprMatcher(ReducePowerAction.class);
                return LineFinder.findInOrder(ctMethodToPatch, newExprMatcher);
            }
        }
    }
}
