package caceresenzo.apps.excel2debitpro.config;

import caceresenzo.libs.internationalization.HardInternationalization;
import caceresenzo.libs.internationalization.i18n;

public class Language {
	
	public static final String LANGUAGE_FRENCH = "Français";
	
	private static Language LANGUAGE;
	private HardInternationalization selected = null;
	
	private Language() {
		selected = new French();
	}
	
	public void initialize() {
		i18n.setSelectedLanguage(LANGUAGE_FRENCH);
	}
	
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
		}
		
	}
	
	public HardInternationalization getSelected() {
		return selected;
	}
	
	public static Language getLanguage() {
		if (LANGUAGE == null) {
			LANGUAGE = new Language();
		}
		
		return LANGUAGE;
	}
	
	public static HardInternationalization getActual() {
		return getLanguage().getSelected();
	}
	
}