package Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData.Region;

public class Monster {
	private String denomination, clas , type, name, status;
	private int hp, mp, def, mDef, ad, ap, range;
	private int cHp, cMp, cDef, cMDef, cAd, cAp;
	private Texture image;
	private Region region;
	private int lvl, experience;
	private int position, codM;
	
	public Monster(String denomination, String name,  int codM, int lvl, int exp){
		this.denomination = denomination;
		this.codM = codM;
		this.name = name;
		//image = new Texture(Gdx.files.internal("img/" + denomination + ".png"));
		this.lvl = lvl;
		this.experience = exp;
	}

	public int getcHp() {
		return cHp;
	}

	public void setcHp(int cHp) {
		this.cHp = cHp;
	}

	public int getcMp() {
		return cMp;
	}

	public void setcMp(int cMp) {
		this.cMp = cMp;
	}

	public int getcDef() {
		return cDef;
	}

	public void setcDef(int cDef) {
		this.cDef = cDef;
	}

	public int getcMDef() {
		return cMDef;
	}

	public void setcMDef(int cMDef) {
		this.cMDef = cMDef;
	}

	public int getcAd() {
		return cAd;
	}

	public void setcAd(int cAd) {
		this.cAd = cAd;
	}

	public int getcAp() {
		return cAp;
	}

	public void setcAp(int cAp) {
		this.cAp = cAp;
	}

	public int getLvl() {
		return lvl;
	}

	public void setLvl(int lvl) {
		this.lvl = lvl;
	}

	public int getExperience() {
		return experience;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getCodM() {
		return codM;
	}

	public String getClas() {
		return clas;
	}

	public void setClas(String clas) {
		this.clas = clas;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getMp() {
		return mp;
	}

	public void setMp(int mp) {
		this.mp = mp;
	}

	public int getDef() {
		return def;
	}

	public void setDef(int def) {
		this.def = def;
	}

	public int getmDef() {
		return mDef;
	}

	public void setmDef(int mDef) {
		this.mDef = mDef;
	}

	public int getAd() {
		return ad;
	}

	public void setAd(int ad) {
		this.ad = ad;
	}

	public int getAp() {
		return ap;
	}

	public void setAp(int ap) {
		this.ap = ap;
	}
	
	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public String getDenomination() {
		return this.denomination;
	}
}