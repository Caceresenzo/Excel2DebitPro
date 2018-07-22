package caceresenzo.apps.excel2debitpro.codec;

import java.io.File;
import java.util.List;

import caceresenzo.apps.excel2debitpro.models.CutPage;
import caceresenzo.apps.excel2debitpro.models.DebitProCut;
import caceresenzo.libs.filesystem.FileUtils;
import caceresenzo.libs.string.SimpleLineStringBuilder;

public class DebitProImportCodec extends CutCodec {
	
	private static final String FILE_NAME = "%s.%s.debitproimport.txt";
	private static final String FILE_HEADER = "Référence;Désignation;Nombre;Longueur;Largeur;Epaisseur;Essence;Sens du fil\n";
	private static final String FILE_ITEM = "%s;%s;%s;%s;%s;%s;Non spécifié;2";
	private static final String FILE_ITEM_NAME = "Piece%s";
	
	@Override
	public void save(File file, List<CutPage> cutPages) throws Exception {
		String directory = (file.isDirectory() ? file : new File(file.getAbsolutePath()).getParentFile()).getAbsolutePath();
		
		for (CutPage cutPage : cutPages) {
			File saveFile = new File(directory, FileUtils.remplaceIllegalChar(String.format(FILE_NAME, file.getName(), cutPage.getName())));
			
			saveFile.delete();
			saveFile.createNewFile();
			
			SimpleLineStringBuilder builder = new SimpleLineStringBuilder(FILE_HEADER);
			
			int pieceNumber = 1;
			for (DebitProCut cut : cutPage.getCuts()) {
				builder.appendln(String.format(FILE_ITEM, //
						cut.getReference(), //
						String.format(FILE_ITEM_NAME, pieceNumber++), //
						cut.getQuantity(), //
						cut.getLength(), //
						cut.getWidth(), //
						cut.getThickness() //
				));
			}
			
			FileUtils.writeStringToFile(builder.toString(), saveFile);
		}
	}
	
}