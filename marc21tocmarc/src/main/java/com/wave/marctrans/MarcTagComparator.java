package com.wave.marctrans;

import java.util.Comparator;

import org.marc4j.marc.VariableField;

public class MarcTagComparator implements Comparator<VariableField> {

	@Override
	public int compare(VariableField vf1, VariableField vf2) {
		return vf1.getTag().compareTo(vf2.getTag());
	}

}
