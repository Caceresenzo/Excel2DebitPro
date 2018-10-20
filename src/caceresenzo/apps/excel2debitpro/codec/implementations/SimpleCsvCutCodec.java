package caceresenzo.apps.excel2debitpro.codec.implementations;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import caceresenzo.apps.excel2debitpro.codec.CutCodec;
import caceresenzo.apps.excel2debitpro.models.CutPage;
import caceresenzo.apps.excel2debitpro.models.DebitProCut;
import caceresenzo.libs.filesystem.FileChecker;
import caceresenzo.libs.parse.ParseUtils;

public class SimpleCsvCutCodec extends CutCodec {
	
	@Override
	public List<CutPage> read(File file) throws Exception {
		String data = FileChecker.checkFileAndRead(file, "csv");
		String[] lines = data.split("\n");
		
		List<CutPage> cutPages = new ArrayList<CutPage>();
		List<DebitProCut> cuts = new ArrayList<DebitProCut>();
		
		String stopReference = lines[0].split(",")[1];
		
		for (int line = 3; true; line++) {
			if (lines[line] == null || lines[line].isEmpty()) {
				continue;
			}
			
			String[] cutInfo = lines[line].split(",");
			
			if (cutInfo == null || cutInfo.length < 5) {
				continue;
			}
			
			String reference = cutInfo[0];
			int quantity = ParseUtils.parseInt(cutInfo[1], 1);
			int length = ParseUtils.parseInt(cutInfo[2], 1);
			int width = ParseUtils.parseInt(cutInfo[3], 1);
			int thickness = ParseUtils.parseInt(cutInfo[4], 0);
			
			cuts.add(new DebitProCut(reference, quantity, length, width, thickness));
			
			if (stopReference.equalsIgnoreCase(reference)) {
				break;
			}
		}
		
		cutPages.add(new CutPage("MAIN", cuts));
		return cutPages;
	}
	
}