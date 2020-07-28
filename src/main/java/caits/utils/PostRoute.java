package caits.utils;

public class PostRoute {

	public int fromPO;
	public int toPO;
	public int toCountry;
	public int fromAvia;
	public int toAvia;
	public PostOfficeProperty.PostLimitType trans;
	public float val;
	public float valNDS;
	public int transcnt;
	public int ratezone;
	public boolean limit;

	public PostRoute() {
		super();
		clear();
	}

	public PostRoute(PostRoute source, boolean isAvia) {
		this();
		copy(source);
		transSet(isAvia);
	}

	public PostRoute(int fromPO) {
		this();
		this.fromPO = fromPO;
	}

	public PostRoute(int fromPO, int toPO, PostOfficeProperty.PostLimitType trans) {
		this();
		this.fromPO = fromPO;
		this.toPO = toPO;
		this.trans = trans;
	}

	public PostRoute(int rateNDS, int fromPO, PostOfficeProperty.PostOfficeLimit lim) {
		this();
		this.fromPO = fromPO;
		if (lim == null)
			return;
		toPO = lim.transfer;
		trans = lim.type;
		limit = trans != PostOfficeProperty.PostLimitType.CLOSEDAVIA;		
		// if (trans==TariffTransType.Ground) trans = TariffTransType.Combo;
		valNDS = lim.baserate * 1000;
		if (valNDS == 0)
			valNDS = 1;
		float nds = rateNDS;
		nds = nds / 100 + 1;
		val = Math.round(lim.basecoeff / nds) / valNDS;
		valNDS = lim.basecoeff / valNDS;
		transcnt = lim.transfcnt;
		ratezone = lim.ratezone;
	}

	public PostRoute(int fromPO, int toCountry) {
		this();
		this.toCountry = toCountry;
		this.fromPO = fromPO;
	}
	
	public void clear() {
		fromPO = 0;
		toPO = 0;
		toCountry = 0;
		fromAvia = 0;
		toAvia = 0;
		trans = PostOfficeProperty.PostLimitType.NONE;
		val = 0;
		valNDS = 0;
		transcnt = 0;
		ratezone = 0;
		limit = false;
	}

	public void copyTrans(PostRoute source) {
		if (source == null)
			return;
		this.trans = source.trans;
		this.val = source.val;
		this.valNDS = source.valNDS;
		this.transcnt = source.transcnt;
		this.ratezone = source.ratezone;
		this.limit = source.limit;
	}

	public void copy(PostRoute source) {
		if (source != null) {
			fromPO = source.fromPO;
			toPO = source.toPO;
			toCountry = source.toCountry;
			fromAvia = source.fromAvia;
			toAvia = source.toAvia;
			copyTrans(source);
		} else
			clear();
	}

	public void transSet(boolean isAvia) {
		if (trans != PostOfficeProperty.PostLimitType.NONE)
			;
		else if (isAvia)
			trans = PostOfficeProperty.PostLimitType.COMBOAVIA;
		else
			trans = PostOfficeProperty.PostLimitType.COMBOGROUND;
	}
	
	public String toJSON(boolean isPretty) {
		String sp = (isPretty ? "  " : "");
		String res = sp + "{";				
		sp = "," + sp;
		res = res + "\"trans\":" + trans.ordinal() + sp +"\"transid\":" + trans.id;
		if (fromPO > 0)
			res = res + sp + "\"from_postoffice\":" + fromPO;
		if (fromAvia > 0)
			res = res + sp + "\"from_aviaport\":" + fromAvia;
		if (toCountry > 0)
			res = res + sp + "\"to_country\":" + toCountry;
		if (toPO > 0)
			res = res + sp + "\"to_postoffice\":" + toPO;
		if (toAvia > 0)
			res = res + sp + "\"to_aviaport\":" + toAvia;
		if (limit)
			res = res + sp + "\"limit\":true";
		res = res + "}";
		return res;
	}
	
	@Override
	public String toString() {
		String res = new String();				
		if (fromPO > 0)
			res = res + fromPO;
		else
			res = res + "?";
		res = res + " > ";
		if (toPO > 0)
			res = res + toPO;
		else if (toCountry > 0)
			res = res + toCountry;
		else
			res = res + "?";
		if (trans!=null)
			res = res + " "+trans.desc;
		return res;
	}
	
	
}
