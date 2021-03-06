package caceresenzo.apps.excel2debitpro.units;

import java.io.File;
import java.util.List;

import caceresenzo.apps.excel2debitpro.codec.implementations.ExcelCutCodec;
import caceresenzo.apps.excel2debitpro.models.CutPage;
import caceresenzo.apps.excel2debitpro.models.DebitProCut;
import caceresenzo.libs.logger.Logger;

public class TestUnits {
	
	public static void main(String[] args) throws Exception {
		// List<CutPage> cutPages = new SimpleCsvCutCodec().read(new File("template.csv"));
		List<CutPage> cutPages = new ExcelCutCodec().read(new File("D:\\Downloads\\BT 215 ROISSY EN BRIE.xlsx"));
		
		/**
		 * Csv read
		 */
		Logger.debug("%-20s | %-20s | %-20s | %-20s | %-20s | %-20s", "PAGE", "REFERENCE", "QUANTITY", "LENGTH", "WIDTH", "THICKNESS");
		for (CutPage cutPage : cutPages) {
			Logger.debug("%-20s | %-20s | %-20s | %-20s | %-20s | %-20s", cutPage.getName(), "", "", "", "", "");
			
			for (DebitProCut cut : cutPage.getCuts()) {
				Logger.debug("%-20s | %-20s | %-20s | %-20s | %-20s | %-20s", "", cut.getReference(), cut.getQuantity(), cut.getLength(), cut.getWidth(), cut.getThickness());
			}
		}
		
		/**
		 * DebitPro save
		 */
		// Logger.debug("Saving...");
		// new DebitProImportCodec().save(new File("debitpro.txt"), cutPages);
		// Logger.debug("Saved!");
		
		// List<CutPage> cutPages = new ExcelCutCodec().read(new File("xPliage VIERGE - sandrine .xlsx"));
	}
	
}