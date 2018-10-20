package caceresenzo.apps.excel2debitpro.models;

import java.util.List;

/**
 * Data class
 * 
 * @author Enzo CACERES
 */
public class CutPage {
	
	/* Variables */
	private final String name;
	private final List<DebitProCut> cuts;
	
	/* Constructor */
	public CutPage(String name, List<DebitProCut> cuts) {
		this.name = name;
		this.cuts = cuts;
	}
	
	/**
	 * @return Cut page name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return {@link List} of cuts that must be done
	 */
	public List<DebitProCut> getCuts() {
		return cuts;
	}
	
}