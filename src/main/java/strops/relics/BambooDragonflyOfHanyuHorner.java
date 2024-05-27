//本遗物的一部分patch写在了血誓的patch里面

package strops.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import strops.helpers.ModHelper;
import strops.utilities.IntSliderSetting;
import strops.utilities.RelicSetting;

import java.util.ArrayList;

public class BambooDragonflyOfHanyuHorner extends StropsAbstractRelic{
    public static final String ID = ModHelper.makePath(BambooDragonflyOfHanyuHorner.class.getSimpleName());
    private static final String IMG_PATH = ModHelper.makeIPath(BambooDragonflyOfHanyuHorner.class.getSimpleName());
    private static final String IMG_PATH_O = ModHelper.makeOPath(BambooDragonflyOfHanyuHorner.class.getSimpleName());
    private static final RelicTier RELIC_TIER = RelicTier.BOSS;
    private static final LandingSound LANDING_SOUND = LandingSound.CLINK;

    public static final int NUM1=9;

    public static final IntSliderSetting THRESHOLD=new IntSliderSetting("BDoHH_Threshold", "N1", NUM1, 5,20);
    public static final IntSliderSetting MH=new IntSliderSetting("BDoHH_MH","MH",0,-20,20);
    public static final IntSliderSetting G=new IntSliderSetting("BDoHH_G","G",0,-100,100);
    public ArrayList<RelicSetting> BuildRelicSettings() {
        ArrayList<RelicSetting> settings = new ArrayList<>();
        settings.add(THRESHOLD);
        settings.add(MH);
        settings.add(G);
        return settings;
    }

    public BambooDragonflyOfHanyuHorner() {
        super(ID, ImageMaster.loadImage(IMG_PATH), ImageMaster.loadImage(IMG_PATH_O), RELIC_TIER, LANDING_SOUND);
        showMHaG(MH,G);
        canCopy=false;
    }

    @Override
    public void onEquip(){
        onEquipMods(MH,G);
        AbstractDungeon.player.energy.energyMaster++;
    }

    @Override
    public void onUnequip() {
        AbstractDungeon.player.energy.energyMaster--;
    }

    @Override
    public void onCardDraw(AbstractCard drawnCard){
        counter++;
    }

    @Override
    public void onVictory() {
        counter = -1;
    }

    @Override
    public String getUpdatedDescription() {
        return String.format(this.DESCRIPTIONS[0], THRESHOLD.value);
    }


    public ArrayList<String> getUpdatedDescription2() {
        ArrayList<String> str_out=new ArrayList<>();
        str_out.add(String.format(this.DESCRIPTIONS[0], THRESHOLD.value));
        str_out.add("");
        str_out.add(getMHaG(MH,G));
        return str_out;
    }
}
