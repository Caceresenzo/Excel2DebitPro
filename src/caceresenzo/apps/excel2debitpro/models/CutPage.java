package caceresenzo.apps.excel2debitpro.models;

import java.util.List;

public class CutPage {
	
	private final String name;
	private final List<DebitProCut> cuts;
	
	public CutPage(String name, List<DebitProCut> cuts) {
		this.name = name;
		this.cuts = cuts;
	}
	
	public String getName() {
		return name;
	}
	
	public List<DebitProCut> getCuts() {
		return cuts;
	}
	
}