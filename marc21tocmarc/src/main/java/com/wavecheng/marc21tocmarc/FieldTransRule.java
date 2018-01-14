package com.wavecheng.marc21tocmarc;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * field to field translate,including subfield to another whole field trans
 * @author boch
 *
 */
public class FieldTransRule {
	private String from;
	private String to;
	private Character indi1From;
	private Character indi1To;
	private Character indi2From;
	private Character indi2To;
	private Map<Character,Character> subfieldMapping = new HashMap<Character,Character>();
	private Map<Character,String> wholeFieldMapping = new HashMap<Character, String>();
	private Set<Character> ignoreSubfields = new HashSet<Character>();
	
	
	public Set<Character> getIgnoreSubfields() {
		return ignoreSubfields;
	}
	public void setIgnoreSubfields(Set<Character> ignoreSubfields) {
		this.ignoreSubfields = ignoreSubfields;
	}
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
	public Character getIndi1From() {
		return indi1From;
	}
	public void setIndi1From(Character indi1From) {
		this.indi1From = indi1From;
	}
	public Character getIndi1To() {
		return indi1To;
	}
	public void setIndi1To(Character indi1To) {
		this.indi1To = indi1To;
	}
	public Character getIndi2From() {
		return indi2From;
	}
	public void setIndi2From(Character indi2From) {
		this.indi2From = indi2From;
	}
	public Character getIndi2To() {
		return indi2To;
	}
	public void setIndi2To(Character indi2To) {
		this.indi2To = indi2To;
	}	
	public Map<Character, Character> getSubfieldMapping() {
		return subfieldMapping;
	}
	public void setSubfieldMapping(Map<Character, Character> subfieldMapping) {
		this.subfieldMapping = subfieldMapping;
	}
	public Map<Character, String> getWholeFieldMapping() {
		return wholeFieldMapping;
	}
	public void setWholeFieldMapping(Map<Character, String> wholeFieldMapping) {
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
