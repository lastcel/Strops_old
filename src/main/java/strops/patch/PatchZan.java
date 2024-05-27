package strops.patch;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import strops.potions.Blizzard;
import strops.relics.Zan;

public class PatchZan {

    @SpirePatch(
            clz= AbstractPlayer.class,
            method="obtainPotion",
            paramtypez = {AbstractPotion.class}
    )
    public static class PatchTool1 {
        @SpireInsertPatch(rloc = 17)
        public static SpireReturn<Boolean> Insert(AbstractPlayer __inst, AbstractPotion potionToObtain) {
            if(AbstractDungeon.player.hasRelic(Zan.ID)&&
                    AbstractDungeon.getCurrRoom().phase!=AbstractRoom.RoomPhase.COMBAT){
                AbstractRelic zan=AbstractDungeon.player.getRelic(Zan.ID);
                zan.flash();
                int healAmount,overHealAmount;
                if(__inst.potions.get(0).ID.equals(Blizzard.POTION_ID)){
                    healAmount=Zan.BLIZZARD_BONUS;
                } else {
                    switch(__inst.potions.get(0).rarity){
                        case COMMON:healAmount=Zan.COMMON_BONUS.value;break;
                        case UNCOMMON:healAmount=Zan.UNCOMMON_BONUS.value;break;
                        case RARE:healAmount=Zan.RARE_BONUS.value;break;
                        default:healAmount=0;break;
                    }
                }

                overHealAmount=AbstractDungeon.player.currentHealth+healAmount-AbstractDungeon.player.maxHealth;
                if(overHealAmount>0&&Zan.STORAGE.value>0){
                    zan.counter+=overHealAmount;
                    if(zan.counter>Zan.STORAGE.value){
                        zan.counter=Zan.STORAGE.value;
                    }
                }

                AbstractDungeon.player.heal(healAmount,true);

                //logger.info("药水栏位数="+__inst.potionSlots);
                for(int comb=0;comb<=__inst.potionSlots-2;comb++){
                    //logger.info("梳子="+comb);
                    //logger.info("左药水="+__inst.potions.get(comb).ID);
                    //logger.info("右药水="+__inst.potions.get(comb+1).ID);
                    __inst.potions.set(comb,__inst.potions.get(comb+1));
                    __inst.potions.get(comb).setAsObtained(comb);
                }
                //logger.info("待得药水="+potionToObtain.ID);
                __inst.potions.set(__inst.potionSlots-1,potionToObtain);
                potionToObtain.setAsObtained(__inst.potionSlots-1);
                potionToObtain.flash();
                AbstractPotion.playPotionSound();
                return SpireReturn.Return(true);
            }
            return SpireReturn.Continue();
        }
    }
}
