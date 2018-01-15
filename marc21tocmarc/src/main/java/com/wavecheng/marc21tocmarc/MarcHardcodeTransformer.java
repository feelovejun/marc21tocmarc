package com.wavecheng.marc21tocmarc;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcStreamWriter;
import org.marc4j.MarcWriter;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;
import org.marc4j.marc.impl.DataFieldImpl;
import org.marc4j.marc.impl.RecordImpl;
import org.marc4j.marc.impl.SubfieldImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarcHardcodeTransformer {

	private static Logger log = LoggerFactory.getLogger(MarcHardcodeTransformer.class);
	private int total;
	private int success;
	private int error;
	
	public int transform(String fileIn, String fileOutput) throws IOException {
		MarcReader reader = new MarcStreamReader(new FileInputStream(fileIn));
		MarcWriter writer = new MarcStreamWriter(new FileOutputStream(fileOutput),"utf-8");
        while(reader.hasNext()) {
        	Record record = reader.next();
        	total ++;
        	try {        	
	        	Record cmarc = new RecordImpl();
	        	List<VariableField> fieldsList = new ArrayList<>(30);
	        	handleLeader(record,cmarc);
	        	handleControlFields(record,fieldsList);
	        	handleDataFields(record,fieldsList);
	        	
	        	//sort and insert into marc
	        	fieldsList.sort(new MarcTagComparator());
	        	for(VariableField f : fieldsList) {
	        		cmarc.addVariableField(f);
	        	}
	        	log.info(cmarc.toString());
	        	//writer.write(cmarc);
	        	success++;
	        	
        	}catch(Exception ex) {
        		error ++;
        		log.error("failed:" + ex);
        		log.error("failed:" + record.toString());
        	}
        	log.info("total=" + total + ",success=" + success + ",failed="+ error);
        } 
        writer.close();
		return -1;
	}

	
	private void handleControlFields(Record record, List<VariableField> fieldsList) {
		List<ControlField> cfs = record.getControlFields();
		for(ControlField cf : cfs) {
			switch(cf.getTag()) {
				case "001":
					fieldsList.add(cf);
					break;
				case "008":
					handle008(cf,fieldsList);
					break;
			}
		}
	}

	private void handleDataFields(Record record, List<VariableField> fieldsList) {
		List<DataField> dfs =  record.getDataFields();
		for(DataField df : dfs) {
			switch(df.getTag()) {
				case "020":
					handle020(df,fieldsList);
					break;
				case "043":
					df.setTag("660");
					log.debug("043 => 660:" + df.toString());
					fieldsList.add(df);
					break;
				case "050":
					df.setTag("680");
					df.setIndicator2(' ');
					log.debug("050 => 680:" + df.toString());
					fieldsList.add(df);
					break;
				case "082":
					df.setTag("676");
					df.setIndicator1(' ');
					df.setIndicator2(' ');
					df = transSubfield(df, '2', 'v');
					log.debug("082 => 676:" + df.toString());
					fieldsList.add(df);
					break;
				case "100":
				case "700":
					handle100and700(df, fieldsList);
					break;
				case "245":
					handle245(df,fieldsList);
					break;
				case "260":
					handle260(df,fieldsList);
					break;
				case "264":
					//not existing rule by Excel
					break;
				case "300":
					handle300(df,fieldsList);
					break;
				case "490":
					df.setTag("225");
					df.setIndicator1('1');
					log.debug("490 => 225:" + df.toString());
					fieldsList.add(df);
					break;
				case "504":
					handle504(df,fieldsList);
					break;
				case "513":
				case "514":
				case "518":
				case "524":
				case "526":
				case "533":
				case "588":
				case "590":
					handle533and588and590(df,fieldsList);
					break;
				case "650":
					handle650(df,fieldsList);
					break;
				case "651":
					handle651(df,fieldsList);
					break;
				case "655":
					handle655(df,fieldsList);
					break;
				case "710":
				case "797":
					df.setIndicator1('0');
					df.setIndicator2('2');
					df.setTag("712");
					log.debug("710/797 => 712:" + df.toString());
					fieldsList.add(df);
					break;
				case "856":
					log.debug("856 => 856:" + df.toString());
					fieldsList.add(df);	
					break;
			}
		}
	}

	private void handle008(ControlField df, List<VariableField> fieldsList) {
		String data = df.getData();
		fieldsList.add(buildDataField("102a", data.substring(15, 17)));
		fieldsList.add(buildDataField("101a", data.substring(35, 37)));
	}


	private void handle655(DataField df, List<VariableField> fieldsList) {
		DataField nwDf = new DataFieldImpl("610", '1',' ');
		for(Subfield sub: df.getSubfields()) {
			sub.setData(removeLastCharAndTrim(sub.getData(), new char[] {'.'}));
			nwDf.addSubfield(sub);		
		}	
		log.debug("655 => 610:" + nwDf.toString());
		fieldsList.add(nwDf);	
	}


	private void handle650(DataField df, List<VariableField> fieldsList) {
		char indi2 = df.getIndicator2();
		String subject = "";
		switch(indi2) {
			case '0':	subject = "lc"; break;
			case '1':	subject = "lcc"; break;
			case '2':	subject = "mesh"; break;
			case '3':	subject = "nal"; break;			
			case '5':	subject = "cae"; break;
			case '6':	subject = "caf"; break;
			default:  break;
		}
		
		DataField nwDf = new DataFieldImpl("606", df.getIndicator1(),' ');
		df.addSubfield(new SubfieldImpl('2', subject));
		for(Subfield sub: df.getSubfields()) {
			if(sub.getCode() == 'z') {
				sub.setCode('y');
			}else if(sub.getCode() == 'v') {
				sub.setCode('x');
			}else if(sub.getCode() == 'y') {
				sub.setCode('z');
			}
			sub.setData(removeLastCharAndTrim(sub.getData(), new char[] {'.'}));
			nwDf.addSubfield(sub);		
		}	
		log.debug("650 => 606:" + nwDf.toString());
		fieldsList.add(nwDf);	
	}

	private void handle651(DataField df, List<VariableField> fieldsList) {
		char indi2 = df.getIndicator2();
		String subject = "";
		switch(indi2) {
			case '0':	subject = "lc"; break;
			case '1':	subject = "lcc"; break;
			case '2':	subject = "mesh"; break;
			case '3':	subject = "nal"; break;			
			case '5':	subject = "cae"; break;
			case '6':	subject = "caf"; break;
			default:  break;
		}
		
		DataField nwDf = new DataFieldImpl("607", df.getIndicator1(),' ');
		df.addSubfield(new SubfieldImpl('2', subject));
		for(Subfield sub: df.getSubfields()) {
			if(sub.getCode() == 'z') {
				sub.setCode('y');
			}else if(sub.getCode() == 'v') {
				sub.setCode('x');
			}else if(sub.getCode() == 'y') {
				sub.setCode('z');
			}
			sub.setData(removeLastCharAndTrim(sub.getData(), new char[] {'.'}));
			nwDf.addSubfield(sub);		
		}	
		log.debug("651 => 607:" + nwDf.toString());
		fieldsList.add(nwDf);	
	}
	
	private void handle533and588and590(DataField df, List<VariableField> fieldsList) {
		DataField nwDf = new DataFieldImpl("300", df.getIndicator1(),df.getIndicator2());
		for(Subfield sub: df.getSubfields()) {
			sub.setData(removeLastCharAndTrim(sub.getData(), new char[] {'.'}));
			nwDf.addSubfield(sub);		
		}	
		log.debug("5xx => 300:" + nwDf.toString());
		fieldsList.add(nwDf);		
	}


	private void handle504(DataField df, List<VariableField> fieldsList) {
		DataField nwDf = new DataFieldImpl("320", df.getIndicator1(),df.getIndicator2());
		for(Subfield sub: df.getSubfields()) {
			if(sub.getCode() == 'a') {
				sub.setData(removeLastCharAndTrim(sub.getData(), new char[] {'.'}));
				nwDf.addSubfield(sub);
			}else {
				nwDf.addSubfield(sub);
			}
		}	
		log.debug("504 => 320:" + nwDf.toString());
		fieldsList.add(nwDf);
		
	}


	private void handle300(DataField df, List<VariableField> fieldsList) {
		DataField nwDf = new DataFieldImpl("210", '1', ' ');
		for(Subfield sub: df.getSubfields()) {
			if(sub.getCode() == 'a') {
				sub.setData(removeLastCharAndTrim(sub.getData(), new char[] {':','.',','}));
				nwDf.addSubfield(sub);
			}else if(sub.getCode() == 'b') {
				sub.setCode('c');
				sub.setData(removeLastCharAndTrim(sub.getData(), new char[] {'.'}));
				nwDf.addSubfield(sub);
			}else {
				nwDf.addSubfield(sub);
			}
		}
		
		log.debug("260 => 210:" + nwDf.toString());
		fieldsList.add(nwDf);
		
	}


	private void handle260(DataField df, List<VariableField> fieldsList) {
		DataField nwDf = new DataFieldImpl("210", '1', ' ');
		
		for(Subfield sub: df.getSubfields()) {
			if(sub.getCode() == 'a') {
				sub.setData(removeLastCharAndTrim(sub.getData(), new char[] {':',';',','}));
				nwDf.addSubfield(sub);
			}else if(sub.getCode() == 'b') {
				sub.setCode('c');
				sub.setData(removeLastCharAndTrim(sub.getData(), new char[] {';',':',','}));
				nwDf.addSubfield(sub);
			}else if(sub.getCode() == 'c') {
				sub.setCode('d');
				sub.setData(removeLastCharAndTrim(sub.getData(), new char[] {'.'}));
				nwDf.addSubfield(sub);
			}else {
				nwDf.addSubfield(sub);
			}
		}		
		log.debug("260 => 210:" + nwDf.toString());
		fieldsList.add(nwDf);
		
	}


	private void handle245(DataField df, List<VariableField> fieldsList) {
		DataField nwDf = new DataFieldImpl("200", '1', ' ');
		
		for(Subfield sub: df.getSubfields()) {
			if(sub.getCode() == 'h') {
				//to 204a and remove "[]"
				DataField df1 = buildDataField("204",'1',' ','a',sub.getData().replaceAll("(\\[|\\]|/)",""));
				log.debug("245h => 204a: " + df1.toString());
				fieldsList.add(df1);
			}else if(sub.getCode() == 'a') {
				sub.setData(removeLastCharAndTrim(sub.getData(), new char[] {':','='}));
				nwDf.addSubfield(sub);
			}else if(sub.getCode() == 'b') {
				sub.setCode('e');
				sub.setData(removeLastCharAndTrim(sub.getData(), new char[] {'/'}));
				nwDf.addSubfield(sub);
			}else if(sub.getCode() == 'c') {
				sub.setCode('f');
				sub.setData(removeLastCharAndTrim(sub.getData(), new char[] {'.'}));
				nwDf.addSubfield(sub);
			}else {
				nwDf.addSubfield(sub);
			}
		}
		
		log.debug("245 => 200:" + nwDf.toString());
		fieldsList.add(nwDf);
	}


	private void handle100and700(DataField df, List<VariableField> fieldsList) {
		char indi1 = df.getIndicator1();	
		char indi2 = df.getIndicator2();
		DataField nwDf = new DataFieldImpl("700", indi1, indi2);
		
		for(Subfield sub: df.getSubfields()) {
			if(sub.getCode() == 'a') {
				String[] vs= sub.getData().split("(,|\\|)");
				nwDf.addSubfield(new SubfieldImpl('a', vs[0]));
				if(vs.length >1) {
					for(int i=1;i<vs.length; i++) {
						String data = vs[i].trim();
						if(data.charAt(data.length()-1) == '.')
							data = data.substring(0, data.length()-1);
						nwDf.addSubfield(new SubfieldImpl('b', data));
					}
				}
			}else if(sub.getCode() == 'e'){
				continue;
			}else {
				nwDf.addSubfield(sub);
			}
		}
		nwDf = transSubfield(nwDf, 'd', 'f');
		log.debug("100/700 => 700:" + nwDf.toString());
		fieldsList.add(nwDf);
	}


	//change subfield tag 
	private DataField transSubfield(DataField vf, char from, char to) {
		for(Subfield sf : vf.getSubfields()) {
			if(sf.getCode() == from)
				sf.setCode(to);
		}
		return vf;
	}
	
	private void handle020(DataField df, List<VariableField> fieldsList) {
		char indi1 = df.getIndicator1();	
		char indi2 = df.getIndicator2();
		DataField nwDf = new DataFieldImpl("010", indi1, indi2);
		
		for(Subfield sub : df.getSubfields()) {
			//handle 020$z=aaa(****) where *** to 010$b
			if(sub.getCode() == 'z' || sub.getCode() == 'a') {
				String data = sub.getData();
				int posStart = data.indexOf("(");
				int posEnd = data.indexOf(")");
				if(posStart != -1 && posEnd > 1) {
					sub.setData(data.substring(0, posStart).trim());
					nwDf.addSubfield(sub);
					nwDf.addSubfield(new SubfieldImpl('b', data.substring(posStart+1, posEnd)));
				}else {
					nwDf.addSubfield(sub);
				}
				nwDf.setIndicator1('1');
			}else {
				nwDf.addSubfield(sub);
			}
		}
		log.debug("020 => 010:" + nwDf.toString());
		fieldsList.add(nwDf);
	}

	private DataField buildDataField(String tagWithSub, String data) {
		return buildDataField(tagWithSub.substring(0, 3), ' ', ' ', tagWithSub.charAt(3), data);
	}
	private DataField buildDataField(String tag, char indi1, char indi2,char sub, String data) {
		DataField df = new DataFieldImpl(tag, indi1, indi2);
		df.addSubfield(new SubfieldImpl(sub, data));
		return df;  
	}
	
	private String removeLastCharAndTrim(String in,char[] removes) {
		String out = in.trim();
		
		for(char c : removes) {
			if(c == out.charAt(out.length()-1)) {
				out = out.substring(0, out.length()-1);
			}
		}
		return out.trim();
	}
	private void handleLeader(Record record, Record cmarc) {
		cmarc.setLeader(record.getLeader());
	}

}
