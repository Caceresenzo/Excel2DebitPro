package caceresenzo.apps.excel2debitpro.codec;

import java.io.File;
import java.util.List;

import caceresenzo.apps.excel2debitpro.models.CutPage;

public abstract class CutCodec {
	
	public List<CutPage> read(File file) throws Exception {
		throw new Exception("This CutCodec don't support reading.");
	}
	
	public void save(File file, List<CutPage> cutPages) throws Exception {
		throw new Exception("This CutCodec don't support saving.");
	}
	
}