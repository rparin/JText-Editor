public class FontOption {

	private int fontIndex;
	private int style;
	private int size;
	private String text;
	private boolean cleared;

	public FontOption(int findex, int fstyle, 
			int fsize, String input) {
		fontIndex = findex;
		style = fstyle;
		size = fsize;
		text = input;
		cleared = false;
	}
	
	public FontOption(int findex, int fstyle, 
			int fsize, String input, boolean c) {
		fontIndex = findex;
		style = fstyle;
		size = fsize;
		text = input;
		cleared = c;
	}


	public int getFont() {
		return fontIndex;
	}

	public int getStyle() {
		return style;
	}
	
	public int getSize() {
		return size;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String str) {
		text = str;
	}
	
	public void setCleared(boolean c) {
		cleared = c;
	}
	
	public boolean isCleared() {
		return cleared;
	}
	
	public boolean compareStyles(FontOption op) {
		return op.fontIndex == fontIndex && op.style == style && op.size == size;
	}

	public String toString() {
		return fontIndex + "-" + style + "-" + size + "-" + text + "-" + cleared;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof FontOption)) return false;
		FontOption obj = (FontOption) o;
		if(!obj.cleared && obj.cleared == cleared) {
			return obj.fontIndex == fontIndex 
					&& obj.style == style 
					&& obj.size == size;
		}
		return obj.fontIndex == fontIndex 
				&& obj.style == style 
				&& obj.size == size
				&& obj.text.equals(text);
	}

}