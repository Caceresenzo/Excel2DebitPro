package caceresenzo.apps.excel2debitpro.utils;

import java.io.File;
import java.io.IOException;

import caceresenzo.libs.filesystem.FileUtils;
import caceresenzo.libs.string.StringUtils;

public class Utils {
	
	public static String checkFile(File file, String extension, boolean tryReading) throws Exception {
		if (file == null || !file.exists() || file.isDirectory() || !file.canRead()) {
			throw new IOException("File (" + file.getAbsolutePath() + ") is not accessible: " + (file == null) + !file.exists() + file.isDirectory() + !file.canRead());
		}
		
		if (extension != null && !FileUtils.getExtension(file).toLowerCase().equals("." + extension.toLowerCase())) {
			throw new Exception("File not a csv file");
		}
		
		if (tryReading) {
			String data = StringUtils.fromFile(file);
			
			if (data == null || data.isEmpty()) {
				throw new Exception("File is empty or not readable");
			}
		}
		
		return null;
	}
	
	public static String read(File file) throws Exception {
		if (file == null || !file.exists() || file.isDirectory() || !file.canRead()) {
			throw new IOException("File is not accessible");
		}
		
		if (!FileUtils.getExtension(file).toLowerCase().equals(".csv")) {
			throw new Exception("File not a csv file");
		}
		
		String data = StringUtils.fromFile(file);
		
		if (data == null || data.isEmpty()) {
			throw new Exception("File is empty or not readable");
		}
		
		return checkFile(file, "csv", true);
	}
	
}