package caceresenzo.apps.excel2debitpro.codec.implementations;

import java.io.File;
import java.util.List;

import caceresenzo.apps.excel2debitpro.codec.CutCodec;
import caceresenzo.apps.excel2debitpro.models.CutPage;
import caceresenzo.apps.excel2debitpro.models.DebitProCut;
import caceresenzo.libs.filesystem.FileUtils;
import caceresenzo.libs.string.SimpleLineStringBuilder;

/**
 * Codec capable to generate output from a {@link List} of {@link CutPage} usable for the other software.
 * 
 * @author Enzo CACERES
 */
public class DebitProImportCodec extends CutCodec {
	
	/* Constants */
	public static final String FILE_NAME_FORMAT = "%s.%s.debitproimport.txt";
	public static final String FILE_HEADER = "Référence;Désignation;Nombre;Longueur;Largeur;Epaisseur;Essence;Sens du fil\n";
	public static final String FILE_ITEM = "%s;%s;%s;%s;%s;%s;Non spécifié;2";
	public static final String FILE_ITEM_NAME = "Piece%s";
	
	@Override
	public void write(File file, List<CutPage> cutPages) throws Exception {
		String directory = (file.isDirectory() ? file : new File(file.getAbsolutePath()).getParentFile()).getAbsolutePath();
		
		for (CutPage cutPage : cutPages) {
			File saveFile = new File(directory, FileUtils.replaceIllegalChar(String.format(FILE_NAME_FORMAT, file.getName(), cutPage.getName())));
			
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