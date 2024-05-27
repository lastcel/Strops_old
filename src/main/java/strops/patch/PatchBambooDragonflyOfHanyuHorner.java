package strops.patch;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import strops.helpers.ModHelper;
import strops.relics.BambooDragonflyOfHanyuHorner;
import strops.relics.Decanter;

public class PatchBambooDragonflyOfHanyuHorner {

    @SpirePatch(
            clz= DrawCardAction.class,
            method="update"
    )
    public static class PatchTool1 {
        @SpireInsertPatch(rloc = 5)
        public static SpireReturn<Void> Insert(DrawCardAction __inst, AbstractGameAction ___followUpAction) {
            for(AbstractRelic r:AbstractDungeon.player.relics){
                if((r.relicId.equals(ModHelper.makePath(BambooDragonflyOfHanyuHorner.class.getSimpleName())))&&
                        r.counter>=BambooDragonflyOfHanyuHorner.THRESHOLD.value){
                    if(AbstractDungeon.player.hasRelic(Decanter.ID)) {
                        AbstractRelic r2 = AbstractDungeon.player.getRelic(Decanter.ID);
                        if (((Decanter) r2).relicToDisenchant.equals(BambooDragonflyOfHanyuHorner.ID)) {
                            r2.flash();
                            return SpireReturn.Continue();
                        }
                    }
                    r.flash();
                    __inst.isDone = true;
                    if (___followUpAction != null){
                        AbstractDungeon.actionManager.addToTop(___followUpAction);
                    }
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }
    }
}
