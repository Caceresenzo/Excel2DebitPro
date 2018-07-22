package caceresenzo.apps.excel2debitpro.codec;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import caceresenzo.apps.excel2debitpro.config.Config;
import caceresenzo.apps.excel2debitpro.models.CutPage;
import caceresenzo.apps.excel2debitpro.models.DebitProCut;
import caceresenzo.apps.excel2debitpro.utils.Utils;
import caceresenzo.libs.parse.ParseUtils;

public class ExcelCutCodec extends CutCodec {
	
	private static final String DATA_SEPARATOR = ",";
	private static final String DATA_FILL = "-";
	
	@Override
	public List<CutPage> read(File file) throws Exception {
		Utils.checkFile(file, "xlsx", false);
		
		Iterator<String> dataIterator = getData(file).iterator();
		List<CutPage> cutPages = new ArrayList<CutPage>();
		
		CutPage sharedCutPage = null;
		
		while (dataIterator.hasNext()) {
			String line = dataIterator.next();
			
			if (line == null || line.isEmpty()) {
				continue;
			}
			
			String[] data = line.split(DATA_SEPARATOR);
			
			if (data.length < 6) {
				continue;
			}
			
			if (line.matches(".*" + Config.MATCH_PAGE_PANEL_NAME + ".*")) {
				String nextLine = dataIterator.next();
				
				String panelName = data[0];
				@SuppressWarnings("unused")
				String panelThickness = "0.0"; // Useless, but i have to keep-it
				
				if (nextLine.matches(".*" + Config.MATCH_PAGE_PANEL_THICKNESS + ".*")) {
					panelThickness = nextLine.split(DATA_SEPARATOR)[0];
				}
				
				List<DebitProCut> cuts = new ArrayList<DebitProCut>();
				
				int lastLength = 0, lastWidth = 0;
				while (dataIterator.hasNext()) { // true, but just for security
					nextLine = dataIterator.next();
					
					if (nextLine.contains(Config.PAGE_SEPARATOR)) {
						break;
					}
					
					String[] nextData = nextLine.split(DATA_SEPARATOR);
					
					String reference = nextData[1];
					int length = (int) (nextData[3].equals(DATA_FILL) ? lastLength : ParseUtils.parseFloat(nextData[3], 0));
					int width = (int) (nextData[4].equals(DATA_FILL) ? lastWidth : ParseUtils.parseFloat(nextData[4], 0));
					int quantity = (int) ParseUtils.parseFloat(nextData[5], 0);
					
					if (reference.equals(DATA_FILL) || quantity == 0) {
						continue;
					}
					
					cuts.add(new DebitProCut(reference, quantity, length, width, 0));
					
					lastLength = length;
					lastWidth = width;
				}
				
				sharedCutPage = new CutPage(panelName, cuts);
			}
			
			if (sharedCutPage != null) {
				cutPages.add(sharedCutPage);
				sharedCutPage = null;
			}
		}
		
		printOutput(cutPages);
		
		return cutPages;
	}
	
	private List<String> getData(File file) throws Exception {
		FileInputStream excelFile = new FileInputStream(file);
		Workbook workbook = new XSSFWorkbook(excelFile);
		Sheet datatypeSheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = datatypeSheet.iterator();
		
		List<String> data = new ArrayList<String>();
		
		while (iterator.hasNext()) {
			Row currentRow = iterator.next();
			Iterator<Cell> cellIterator = currentRow.iterator();
			
			String cellLine = "";
			while (cellIterator.hasNext()) {
				Cell currentCell = cellIterator.next();
				
				String cellData = DATA_FILL;
				
				switch (currentCell.getCellTypeEnum()) { // getCellTypeEnum shown as deprecated for version 3.15, ill be renamed to getCellType starting from version 4.0
					case STRING: {
						cellData = currentCell.getStringCellValue();
						break;
					}
					case NUMERIC: {
						cellData = String.valueOf((int) currentCell.getNumericCellValue());
						break;
					}
					
					default:
						break;
				}
				
				if (cellData != null) {
					cellLine += cellData + (cellIterator.hasNext() ? DATA_SEPARATOR : "");
				}
			}
			data.add(cellLine);
		}
		
		workbook.close();
		
		return data;
	}
	
	private void printOutput(List<CutPage> cutPages) {
		int columnSize = 10;
		String format = "| %-" + columnSize + "s | %-" + columnSize + "s | %-" + columnSize + "s | %-" + columnSize + "s | %-" + columnSize + "s | %-" + columnSize + "s |";
		String seperatorBar = "";
		for (int i = 0; columnSize > i; i++) {
			seperatorBar += "-";
		}
		String separator = String.format(format.replace("|", "+"), seperatorBar, seperatorBar, seperatorBar, seperatorBar, seperatorBar, seperatorBar);

		System.out.println(separator);
		System.out.println(String.format(format, "PAGE", "REFERENCE", "QUANTITY", "LENGTH", "WIDTH", "THICKNESS"));
		System.out.println(separator);
		for (CutPage cutPage : cutPages) {
			boolean pageDisplayed = false;
			
			for (DebitProCut cut : cutPage.getCuts()) {
				System.out.println(String.format(format, !pageDisplayed ? cutPage.getName() : "", cut.getReference(), cut.getQuantity(), cut.getLength(), cut.getWidth(), cut.getThickness()));
				
				if (!pageDisplayed) {
					pageDisplayed = true;
				}
			}
			
			System.out.println(separator);
		}
	}
	
}