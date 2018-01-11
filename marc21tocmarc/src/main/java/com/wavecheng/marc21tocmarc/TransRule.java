package com.wavecheng.marc21tocmarc;

import java.util.List;

public class TransRule {

	private List<FieldTransRule> transRules;
	private List<FieldReplaceRule> replaceRules;
	public List<FieldTransRule> getTransRules() {
		return transRules;
	}
	public void setTransRules(List<FieldTransRule> transRules) {
		this.transRules = transRules;
	}
	public List<FieldReplaceRule> getReplaceRules() {
		return replaceRules;
	}
	public void setReplaceRules(List<FieldReplaceRule> replaceRules) {
		this.replaceRules = replaceRules;
	}
		
}
