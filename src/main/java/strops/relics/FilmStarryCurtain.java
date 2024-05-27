package strops.relics;

import basemod.BaseMod;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import strops.actions.FilmStarryCurtainAction;
import strops.helpers.ModHelper;
import strops.utilities.IntSliderSetting;
import strops.utilities.RelicSetting;

import java.util.ArrayList;

public class FilmStarryCurtain extends StropsAbstractRelic {
    public static final String ID = ModHelper.makePath(FilmStarryCurtain.class.getSimpleName());
    private static final String IMG_PATH = ModHelper.makeIPath(FilmStarryCurtain.class.getSimpleName());
    private static final RelicTier RELIC_TIER = RelicTier.SHOP;
    private static final LandingSound LANDING_SOUND = LandingSound.SOLID;

    public static final IntSliderSetting MH=new IntSliderSetting("FilmStarryCurtain_MH","MH",0,-20,20);
    public static final IntSliderSetting G=new IntSliderSetting("FilmStarryCurtain_G","G",0,-100,100);
    public ArrayList<RelicSetting> BuildRelicSettings() {
        ArrayList<RelicSetting> settings = new ArrayList<>();
        settings.add(MH);
        settings.add(G);
        return settings;
    }

    public FilmStarryCurtain() {
        super(ID, ImageMaster.loadImage(IMG_PATH), RELIC_TIER, LANDING_SOUND);
        showMHaG(MH,G);
        canCopy=false;
    }

    @Override
    public void onEquip(){
        onEquipMods(MH,G);
    }

    @Override
    public void atTurnStartPostDraw(){
        flash();
        addToBot(new FilmStarryCurtainAction(AbstractDungeon.player, BaseMod.MAX_HAND_SIZE));
    }

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    public ArrayList<String> getUpdatedDescription2() {
        ArrayList<String> str_out=new ArrayList<>();
        str_out.add(this.DESCRIPTIONS[0]);
        str_out.add("");
        str_out.add(getMHaG(MH,G));
        return str_out;
    }
}
