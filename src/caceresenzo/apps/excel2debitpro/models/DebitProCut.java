package caceresenzo.apps.excel2debitpro.models;

/**
 * Cut data class
 * 
 * @author Enzo CACERES
 */
public class DebitProCut {
	
	/* Variables */
	private final String reference;
	private final int quantity, length, width, thickness;
	
	/* Constructor */
	public DebitProCut(String reference, int quantity, int length, int width) {
		this(reference, quantity, length, width, 0);
	}
	
	/* Constructor */
	public DebitProCut(String reference, int quantity, int length, int width, int thickness) {
		this.reference = reference;
		this.quantity = quantity;
		this.length = length;
		this.width = width;
		this.thickness = thickness;
	}
	
	/**
	 * @return Cut's name/reference
	 */
	public String getReference() {
		return reference;
	}
	
	/**
	 * @return Quantity to cut
	 */
	public int getQuantity() {
		return quantity;
	}
	
	/**
	 * @return Cut length
	 */
	public int getLength() {
		return length;
	}
	
	/**
	 * @return Cut width
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * @return Metal sheet thickness
	 */
	public int getThickness() {
		return thickness;
	}
	
}