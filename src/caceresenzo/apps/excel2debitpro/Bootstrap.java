package caceresenzo.apps.excel2debitpro;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JOptionPane;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import caceresenzo.apps.excel2debitpro.codec.CutCodec;
import caceresenzo.apps.excel2debitpro.codec.implementations.DebitProImportCodec;
import caceresenzo.apps.excel2debitpro.codec.implementations.ExcelCutCodec;
import caceresenzo.apps.excel2debitpro.codec.implementations.SimpleCsvCutCodec;
import caceresenzo.apps.excel2debitpro.config.Language;
import caceresenzo.libs.filesystem.FileUtils;
import caceresenzo.libs.internationalization.i18n;
import caceresenzo.libs.logger.Logger;
import caceresenzo.libs.parse.ParseUtils;

public class Bootstrap {
	
	public static void main(String[] args) {
		i18n.setDebug(false);
		Language.getLanguage().initialize();
		
		/* CLI parsing */
		Options options = new Options();
		
		Option guiOption = new Option("g", "gui", false, "enable the gui");
		guiOption.setRequired(false);
		guiOption.setType(Boolean.class);
		options.addOption(guiOption);
		
		Option inputOption = new Option("i", "input", true, "input file");
		inputOption.setRequired(true);
		options.addOption(inputOption);
		
		Option commentOption = new Option("c", "comment", true, "add comment to the line");
		commentOption.setRequired(false);
		options.addOption(commentOption);
		
		Option logOption = new Option("l", "log", true, "log output");
		logOption.setRequired(false);
		guiOption.setType(Boolean.class);
		options.addOption(logOption);
		
		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine commandLine = null;
		try {
			commandLine = parser.parse(options, args);
		} catch (ParseException exception) {
			Logger.exception(exception, "Failed to parse command line;");
			
			formatter.printHelp("utility-name", options);
			
			JOptionPane.showMessageDialog(null, i18n.getString("error.parse-cli", exception.getLocalizedMessage()), i18n.getString("error.title"), JOptionPane.ERROR_MESSAGE);
			
			System.exit(1);
		}
		
		@SuppressWarnings("unused")
		boolean enableGui = ParseUtils.parseBoolean(commandLine.getOptionValue("gui"), false);
		boolean enableLog = ParseUtils.parseBoolean(commandLine.getOptionValue("log"), true);
		File inputFile = new File(commandLine.getOptionValue("input"));
		
		/* Logs */
		if (!enableLog) {
			File logOutput = new File("./excel2debitpro.log");
			try {
				logOutput.createNewFile();
				System.setOut(new PrintStream(new FileOutputStream(logOutput)));
			} catch (IOException exception) {
				Logger.exception(exception, "Failed to create log file.");
			}
		}
		
		/* Conversion */
		try {
			String extension = FileUtils.getExtension(inputFile);
			
			CutCodec codec = null;
			switch (extension) {
				case ".csv": {
					codec = new SimpleCsvCutCodec();
					break;
				}
				
				case ".xlsx": {
					codec = new ExcelCutCodec();
					break;
				}
				
				default: {
					throw new IllegalStateException("Unsupported file type: " + extension);
				}
			}
			
			new DebitProImportCodec().save(inputFile, codec.read(inputFile));
		} catch (Exception exception) {
			Logger.exception(exception, "Failed to convert.");
			
			JOptionPane.showMessageDialog(null, i18n.getString("error.codec.error", exception.getLocalizedMessage()), i18n.getString("error.title"), JOptionPane.ERROR_MESSAGE);
		}
	}
	
}
