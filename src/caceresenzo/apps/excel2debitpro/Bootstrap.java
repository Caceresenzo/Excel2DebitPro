package caceresenzo.apps.excel2debitpro;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import caceresenzo.apps.excel2debitpro.codec.DebitProImportCodec;
import caceresenzo.apps.excel2debitpro.codec.ExcelCutCodec;
import caceresenzo.apps.excel2debitpro.codec.SimpleCsvCutCodec;
import caceresenzo.apps.excel2debitpro.config.Language;
import caceresenzo.apps.excel2debitpro.models.CutPage;
import caceresenzo.libs.filesystem.FileUtils;
import caceresenzo.libs.internationalization.i18n;
import caceresenzo.libs.parse.ParseUtils;

public class Bootstrap {
	
	public static void main(String[] args) {
		i18n.setDebug(false);
		Language.getLanguage().initialize();
		
		/*
		 * CLI parsing
		 */
		Options options = new Options();
		
		Option guiOption = new Option("g", "gui", false, "enable the gui");
		guiOption.setRequired(false);
		guiOption.setType(Boolean.class);
		options.addOption(guiOption);
		
		Option inputOption = new Option("i", "input", true, "input file");
		inputOption.setRequired(true);
		options.addOption(inputOption);
		
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
			System.out.println(exception.getMessage());
			formatter.printHelp("utility-name", options);
			
			JOptionPane.showMessageDialog(null, i18n.getString("error.parse-cli", exception.getLocalizedMessage()), i18n.getString("error.title"), JOptionPane.ERROR_MESSAGE);
			
			System.exit(1);
		}
		
		@SuppressWarnings("unused")
		boolean enableGui = ParseUtils.parseBoolean(commandLine.getOptionValue("gui"), false);
		boolean enableLog = ParseUtils.parseBoolean(commandLine.getOptionValue("log"), true);
		File inputFile = new File(commandLine.getOptionValue("input"));
		
		if (!enableLog) {
			File logOutput = new File("./excel2debitpro.log");
			try {
				logOutput.createNewFile();
				System.setOut(new PrintStream(new FileOutputStream(logOutput)));
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
		
		List<CutPage> cutPages = null;
		
		try {
			switch (FileUtils.getExtension(inputFile)) {
				case ".csv":
					cutPages = new SimpleCsvCutCodec().read(inputFile);
					break;
				case ".xlsx":
					cutPages = new ExcelCutCodec().read(inputFile);
					break;
				
				default:
					break;
			}
			
			new DebitProImportCodec().save(inputFile, cutPages);
		} catch (Exception exception) {
			exception.printStackTrace();
			
			JOptionPane.showMessageDialog(null, i18n.getString("error.codec.error", exception.getLocalizedMessage()), i18n.getString("error.title"), JOptionPane.ERROR_MESSAGE);
		}
	}
	
}
