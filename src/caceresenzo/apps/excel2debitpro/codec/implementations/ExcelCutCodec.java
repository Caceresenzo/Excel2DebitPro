package caceresenzo.apps.excel2debitpro.codec.implementations;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import caceresenzo.apps.excel2debitpro.codec.CutCodec;
import caceresenzo.apps.excel2debitpro.config.Constants;
import caceresenzo.apps.excel2debitpro.models.CutPage;
import caceresenzo.apps.excel2debitpro.models.DebitProCut;
import caceresenzo.libs.filesystem.FileChecker;
import caceresenzo.libs.internationalization.i18n;
import caceresenzo.libs.parse.ParseUtils;
import caceresenzo.libs.string.SimpleLineStringBuilder;
import caceresenzo.libs.string.StringUtils;

/**
 * Codec capable to read excel document.
 * 
 * @author Enzo CACERES
 */
public class ExcelCutCodec extends CutCodec {
	
	/* Constants */
	public static final int MAX_ERROR_COUNT = 30;
	
	private static final String DATA_SEPARATOR = ",";
	private static final String DATA_FILL = "-";
	
	public static final int PRINT_COLUMN_SIZE = 10;
	
	@Override
	public List<CutPage> read(File file) throws Exception {
		FileChecker.checkFile(file, "xlsx", false);
		
		Iterator<String> dataIterator = getData(file).iterator();
		List<CutPage> cutPages = new ArrayList<>();
		
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
			
			if (line.matches(".*" + Constants.MATCH_PAGE_PANEL_NAME + ".*")) {
				String nextLine = dataIterator.next();
				
				String panelName = data[0];
				@SuppressWarnings("unused")
				String panelThickness = "0.0"; /* Useless, but i have to keep-it */
				
				if (nextLine.matches(".*" + Constants.MATCH_PAGE_PANEL_THICKNESS + ".*")) {
					panelThickness = nextLine.split(DATA_SEPARATOR)[0];
				}
				
				List<DebitProCut> cuts = new ArrayList<>();
				
				int lastLength = 0, lastWidth = 0;
				while (dataIterator.hasNext()) { /* true, but just for security */
					nextLine = dataIterator.next();
					
					System.out.println(String.format("Processing line: %s", nextLine));
					
					if (nextLine.contains(Constants.PAGE_SEPARATOR)) {
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
				
				if (!cuts.isEmpty()) {
					sharedCutPage = new CutPage(panelName, cuts);
				}
			}
			
			if (sharedCutPage != null) {
				cutPages.add(sharedCutPage);
				sharedCutPage = null;
			}
		}

		System.out.println();
		printOutput(cutPages);
		
		return cutPages;
	}
	
	/**
	 * Get a {@link List} of data.<br>
	 * This already do a lot of the work, use this data to process information more easely.
	 * 
	 * @param file
	 *            Target excel file.
	 * @return {@link List} of {@link String} formatted like this {@code -,-,<data>,-}.
	 * @throws Exception
	 *             If anything go wrong.
	 */
	private List<String> getData(File file) throws Exception {
		FileInputStream excelFile = new FileInputStream(file);
		Workbook workbook = new XSSFWorkbook(excelFile);
		Sheet datatypeSheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = datatypeSheet.iterator();
		
		List<String> data = new ArrayList<>();
		
		SimpleLineStringBuilder errorStringBuilder = new SimpleLineStringBuilder("", "\n\n");
		
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
					
					case BLANK:
					case _NONE: {
						/* Ignored */
						break;
					}
					
					default: {
						if (cellLine.contains(Constants.PAGE_SEPARATOR)) {
							continue;
						}
						
						CellAddress currentCellAddress = currentCell.getAddress();
						
						int dividend = currentCellAddress.getColumn() + 1, modulo;
						String column = "";
						
						while (dividend > 0) {
							modulo = (dividend - 1) % 26;
							column = ((char) (65 + modulo)) + column;
							dividend = (dividend - modulo) / 26;
						}
						
						String cell = column + (currentCellAddress.getRow() + 1);
						
						errorStringBuilder.appendln(i18n.getString("warning.codec.cell.unsupported-type", cell, currentCell.getCellTypeEnum()));
						
						break;
					}
				}
				
				if (cellData != null) {
					cellLine += cellData + (cellIterator.hasNext() ? DATA_SEPARATOR : "");
				}
			}
			
			data.add(cellLine);
		}
		
		workbook.close();
		
		if (StringUtils.validate(errorStringBuilder.toString())) {
			String message = errorStringBuilder.toString();
			
			String[] splitMessage = message.split("\n");
			int errorCount = splitMessage.length / 2;
			
			if (errorCount > MAX_ERROR_COUNT) {
				SimpleLineStringBuilder messageStringBuilder = new SimpleLineStringBuilder();
				
				for (int i = 0; i < MAX_ERROR_COUNT; i++) {
					messageStringBuilder.appendln(splitMessage[i]);
				}
				
				message = messageStringBuilder.toString() + (errorCount - MAX_ERROR_COUNT) + "/" + errorCount + "...";
			}
			
			JOptionPane.showMessageDialog(null, message, i18n.getString("warning.title"), JOptionPane.WARNING_MESSAGE);
		}
		
		return data;
	}
	
	/**
	 * Print a prettify version in a table to the console containing all {@link CutPage} of the {@link List}.
	 * 
	 * @param cutPages
	 *            Base data to print.
	 */
	private void printOutput(List<CutPage> cutPages) {
		String format = "| %-" + PRINT_COLUMN_SIZE + "s | %-" + PRINT_COLUMN_SIZE + "s | %-" + PRINT_COLUMN_SIZE + "s | %-" + PRINT_COLUMN_SIZE + "s | %-" + PRINT_COLUMN_SIZE + "s | %-" + PRINT_COLUMN_SIZE + "s |";
		String seperatorBar = StringUtils.multiplySequence( "-", PRINT_COLUMN_SIZE);
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