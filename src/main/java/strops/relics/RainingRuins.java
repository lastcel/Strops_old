//useCheck() is copied from Snecko of Downfall
package strops.relics;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.actions.common.ShuffleAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DrawCardNextTurnPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.NewExpr;
import strops.actions.GeneralDrawPileToHandAction;
import strops.helpers.ModHelper;
import strops.utilities.IntSliderSetting;
import strops.utilities.RelicSetting;
import strops.utilities.StaticHelpers;

import java.util.ArrayList;
import java.util.function.Predicate;

public class RainingRuins extends StropsAbstractRelic implements ClickableRelic {
    public static final String ID = ModHelper.makePath(RainingRuins.class.getSimpleName());
    private static final String IMG_PATH = ModHelper.makeIPath(RainingRuins.class.getSimpleName());
    private static final String IMG_PATH_O = ModHelper.makeOPath(RainingRuins.class.getSimpleName());
    private static final RelicTier RELIC_TIER = RelicTier.UNCOMMON;
    private static final LandingSound LANDING_SOUND = LandingSound.SOLID;

    public static boolean bruh = false;

    public static final int NUM1=2;

    public static final IntSliderSetting COOLDOWN=new IntSliderSetting("RainyRuins_COOLDOWN", "N1", NUM1, 1,3);
    public static final IntSliderSetting MH=new IntSliderSetting("RainyRuins_MH","MH",0,-20,20);
    public static final IntSliderSetting G=new IntSliderSetting("RainyRuins_G","G",0,-100,100);
    public ArrayList<RelicSetting> BuildRelicSettings() {
        ArrayList<RelicSetting> settings = new ArrayList<>();
        settings.add(COOLDOWN);
        settings.add(MH);
        settings.add(G);
        return settings;
    }

    public RainingRuins() {
        super(ID, ImageMaster.loadImage(IMG_PATH), ImageMaster.loadImage(IMG_PATH_O), RELIC_TIER, LANDING_SOUND);
        showMHaG(MH,G);
        this.tips.add(new PowerTip(this.DESCRIPTIONS[1], this.DESCRIPTIONS[2]));
    }

    @Override
    public void onEquip(){
        onEquipMods(MH,G);
        counter=0;
        beginLongPulse();
    }

    @Override
    public void atBattleStart(){
        if(counter!=0){
            counter--;
        }
        if(counter==0){
            beginLongPulse();
            flash();
        }
    }

    @Override
    public void onVictory(){
        if(counter==1){
            beginLongPulse();
        }
        /*
        if((counter!=0)&&(counter!=1)){
            stopPulse();
        } else if(counter==1){
            beginLongPulse();
        }

         */
    }

    @Override
    public void onRightClick() {
        if(!StaticHelpers.canClickRelic(this)){
            return;
        }

        if(counter!=0){
            return;
        }

        if (AbstractDungeon.player.drawPile.isEmpty()) {
            if (AbstractDungeon.player.discardPile.size() > 0) {
                addToBot(new EmptyDeckShuffleAction());
                addToBot(new ShuffleAction(AbstractDungeon.player.drawPile, false));
            }
            return;
        }

        CardGroup tmp = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
            if (useCheck(c)){
                tmp.addToRandomSpot(c);
            }
        }
        if (tmp.size() == 0) {
            if (AbstractDungeon.player.discardPile.size() > 0) {
                addToBot(new EmptyDeckShuffleAction());
                addToBot(new ShuffleAction(AbstractDungeon.player.drawPile, false));
            }
            return;
        }

        addToBot(new GeneralDrawPileToHandAction(1, myNeeds()));
        counter=COOLDOWN.value;
        stopPulse();
    }

    @Override
    public void setCounter(int setCounter){
        counter=setCounter;
        if (counter == 1||counter == 0) {
            flash();
            beginLongPulse();
        }
    }

    public boolean useCheck(AbstractCard card) {
        bruh = false;

        try {
            ClassPool pool = Loader.getClassPool();
            CtClass ctClass = pool.get(card.getClass().getName());
            ctClass.defrost();

            CtMethod useMethod;
            try {
                useMethod = ctClass.getDeclaredMethod("use");
            } catch (NotFoundException var6) {
                return false;
            }

            useMethod.instrument(new ExprEditor() {
                public void edit(NewExpr e) {
                    try {
                        CtConstructor ctConstructor = e.getConstructor();
                        CtClass cls = ctConstructor.getDeclaringClass();
                        if (cls != null) {
                            CtClass parent = cls;

                            do {
                                parent = parent.getSuperclass();
                            } while(parent != null && !parent.getName().equals(AbstractGameAction.class.getName()) && !parent.getName().equals(AbstractPower.class.getName()));

                            if (parent != null && (parent.getName().equals(AbstractGameAction.class.getName()) || !parent.getName().equals(AbstractPower.class.getName())) && (ctConstructor.getDeclaringClass().getName().equals(DrawCardAction.class.getName()) || ctConstructor.getDeclaringClass().getName().equals(DrawCardNextTurnPower.class.getName()))) {
                                RainingRuins.bruh = true;
                            }
                        }
                    } catch (NotFoundException var5) {
                    }

                }
            });
        } catch (CannotCompileException | NotFoundException var7) {
            var7.printStackTrace();
        }

        return bruh;
    }

    public Predicate<AbstractCard> myNeeds() {
        return this::useCheck;
    }

    @Override
    public String getUpdatedDescription() {
        return String.format(this.DESCRIPTIONS[0], COOLDOWN.value);
    }


    public ArrayList<String> getUpdatedDescription2() {
        ArrayList<String> str_out=new ArrayList<>();
        str_out.add(String.format(this.DESCRIPTIONS[0], COOLDOWN.value));
        str_out.add("");
        str_out.add(getMHaG(MH,G));
        str_out.add(this.DESCRIPTIONS[1]);
        str_out.add(this.DESCRIPTIONS[2]);
        return str_out;
    }

    public AbstractRelic makeCopy() {return new RainingRuins();}
}
