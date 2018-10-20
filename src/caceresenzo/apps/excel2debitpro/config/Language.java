package caceresenzo.apps.excel2debitpro.config;

import caceresenzo.libs.internationalization.HardInternationalization;
import caceresenzo.libs.internationalization.i18n;

/**
 * Language handler class
 * 
 * @author Enzo CACERES
 */
public class Language {
	
	/* Constants */
	public static final String LANGUAGE_FRENCH = "Français";
	
	/* Variables */
	private static Language LANGUAGE;
	private HardInternationalization selected = null;
	
	/* Constructor */
	private Language() {
		selected = new French();
	}
	
	/* Initializer */
	public void initialize() {
		i18n.setSelectedLanguage(LANGUAGE_FRENCH);
	}
	
	/**
	 * French {@link HardInternationalization}
	 * 
	 * @author Enzo CACERES
	 */
	private class French extends HardInternationalization {
		
		public French() {
			super();
			register(LANGUAGE_FRENCH);
		}
		
		@Override
		public void set() {
			o("error.title", "Erreur");
			o("error.parse-cli", "Erreur, l'interpreteur de commande à renvoyé une erreur: %s");
			o("error.codec.error", "Erreur, le convertisseur à rencontrer une erreur: %s\nVoir la console pour plus d'information.");
			
			o("warning.title", "Attention");
			o("warning.codec.cell.unsupported-type", "Attention, la cellule %s ne contient pas d'information valide.\nType: %s");
		}
		
	}
	
	/**
	 * @return Selected {@link HardInternationalization} instance
	 */
	public HardInternationalization getSelected() {
		return selected;
	}
	
	/**
	 * @return Language singleton
	 */
	public static Language getLanguage() {
		if (LANGUAGE == null) {
			LANGUAGE = new Language();
		}
		
		return LANGUAGE;
	}
	
	/**
	 * @return Actually selected {@link HardInternationalization}
	 */
	public static HardInternationalization getActual() {
		return getLanguage().getSelected();
	}
	
}