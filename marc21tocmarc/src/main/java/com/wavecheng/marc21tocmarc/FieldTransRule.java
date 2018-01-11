package com.wavecheng.marc21tocmarc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * field to field translate,including subfield to another whole field trans
 * @author boch
 *
 */
public class FieldTransRule {
	private String from;
	private String to;
	private char indi1From;
	private char indi1To;
	private char indi2From;
	private char indi2To;
	private Map<String,String> subfieldMapping = new HashMap<String,String>();
	private Map<String,String> wholeFieldMapping = new HashMap<String, String>();
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public char getIndi1From() {
		return indi1From;
	}
	public void setIndi1From(char indi1From) {
		this.indi1From = indi1From;
	}
	public char getIndi1To() {
		return indi1To;
	}
	public void setIndi1To(char indi1To) {
		this.indi1To = indi1To;
	}
	public char getIndi2From() {
		return indi2From;
	}
	public void setIndi2From(char indi2From) {
		this.indi2From = indi2From;
	}
	public char getIndi2To() {
		return indi2To;
	}
	public void setIndi2To(char indi2To) {
		this.indi2To = indi2To;
	}
	public Map<String, String> getSubfieldMapping() {
		return subfieldMapping;
	}
	public void setSubfieldMapping(Map<String, String> subfieldMapping) {
		this.subfieldMapping = subfieldMapping;
	}
	public Map<String, String> getWholeFieldMapping() {
		return wholeFieldMapping;
	}
	public void setWholeFieldMapping(Map<String, String> wholeFieldMapping) {
		this.wholeFieldMapping = wholeFieldMapping;
	}
	
	public void validateSubfieldsRule() {
		boolean noOverlap = Collections.disjoint(this.subfieldMapping.keySet(), this.wholeFieldMapping.keySet());
		if(!noOverlap)
			throw new RuntimeException( this.from + " rule's subfields has same setting different replacement:single subfiled="
					+ this.subfieldMapping.keySet() + ", whole field="
					+ this.wholeFieldMapping.keySet());
	}
	
	@Override
	public String toString() {
		return from + "=>" + to + ",indi1:[" + indi1From + "]=>["+ indi1To
				+ "],indi2:[" + indi2From + "]=>[" +indi2To + "],subfields:" + 
				subfieldMapping + ",whole:" + 
				wholeFieldMapping;
	}
}
