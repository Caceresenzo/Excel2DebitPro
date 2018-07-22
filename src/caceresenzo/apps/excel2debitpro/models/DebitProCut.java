package caceresenzo.apps.excel2debitpro.models;

public class DebitProCut {
	
	private final String reference;
	private final int quantity, length, width, thickness;
	
	public DebitProCut(String reference, int quantity, int length, int width) {
		this(reference, quantity, length, width, 0);
	}
	
	public DebitProCut(String reference, int quantity, int length, int width, int thickness) {
		this.reference = reference;
		this.quantity = quantity;
		this.length = length;
		this.width = width;
		this.thickness = thickness;
	}
	
	public String getReference() {
		return reference;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public int getLength() {
		return length;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getThickness() {
		return thickness;
	}
	
}