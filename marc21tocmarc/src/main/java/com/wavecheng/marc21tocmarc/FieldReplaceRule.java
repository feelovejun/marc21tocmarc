package com.wavecheng.marc21tocmarc;


/**
 * after trans rules to replace specific field values like [],(),etc
 * @author boch
 *
 */
public class FieldReplaceRule {

	private String field;
	private String pattern;
	private String replacement;
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public String getReplacement() {
		return replacement;
	}
	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}
	
	@Override
	public String toString() {
		return field + "," + pattern + "=>" + replacement;
	}
}
