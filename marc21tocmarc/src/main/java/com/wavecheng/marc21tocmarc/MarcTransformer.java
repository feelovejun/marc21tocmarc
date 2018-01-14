package com.wavecheng.marc21tocmarc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;
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

public class MarcTransformer {

	private static Logger log = LoggerFactory.getLogger(MarcTransformer.class);
	private TransRule rules;
	private int total;
	private int success;
	private int error;
	
	public MarcTransformer(TransRule rules) {
		this.rules = rules;
	}
	
	public int transform(String fileIn, String fileOutput) throws IOException {
		MarcReader reader = new MarcStreamReader(new FileInputStream(fileIn));
		MarcWriter writer = new MarcStreamWriter(new FileOutputStream(fileOutput),"utf-8");
        while(reader.hasNext()) {
        	Record record = reader.next();
        	total ++;
        	try {        	
	        	Record cmarc = new RecordImpl();
	        	handleLeader(record,cmarc);
	        	handleControlFields(record,cmarc);
	        	handleDataFields(record,cmarc);
	        	cmarc.getVariableFields().stream().sorted();
	        	log.debug(cmarc.toString());
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

	private FieldTransRule getTransRuleFromRules(String tag) {
		for(FieldTransRule ftr : this.rules.getTransRules()) {
			if(tag.equals(ftr.getFrom()))
				return ftr;
		}
		return null;
	}
	
	private void handleDataFields(Record record, Record cmarc) {
		List<DataField> dfs =  record.getDataFields();
		for(DataField df : dfs) {
			FieldTransRule transRule = getTransRuleFromRules(df.getTag());
			if(transRule == null) {
				continue;
			}
			
			char indi1 = df.getIndicator1();
			if(null == transRule.getIndi1From()) {
				if(transRule.getIndi1To() == null)
					indi1 = indi1 == 1 ? ' ' : indi1;	
				else
					indi1 = transRule.getIndi1To();
			}else {
				indi1 = transRule.getIndi1To();
			}
			
			char indi2 = df.getIndicator2();
			if(null == transRule.getIndi2From()) {
				if(transRule.getIndi2To() == null)
					indi2 = indi2 == 1 ? ' ' : indi2;			
				else
					indi2 = transRule.getIndi2To();
			}else {
				indi2 = transRule.getIndi2To();
			}

			DataField nwDf = new DataFieldImpl(transRule.getTo(), indi1, indi2);
			
			for(Subfield sub : df.getSubfields()) {
				if(transRule.getIgnoreSubfields().contains(new Character(sub.getCode())))
					continue;
				
				Character code = transRule.getSubfieldMapping().get(sub.getCode());
				String wholeMap = transRule.getWholeFieldMapping().get(sub.getCode());
				
				if(code != null) {
					
					if("700".equals(df.getTag()) && sub.getCode() == 'a') {
						String[] vs= sub.getData().split("(,|\\|)");
						nwDf.addSubfield(new SubfieldImpl('a', vs[0]));
						if(vs.length >1) {
							for(int i=1;i<vs.length; i++)
								nwDf.addSubfield(new SubfieldImpl('b', vs[i].trim()));
						}
					}else {
						nwDf.addSubfield(new SubfieldImpl(code, sub.getData()));
					}
				}
				else if(wholeMap != null){
					//whole field replace to generate a new field
					DataField dataField = buildDataField(wholeMap, sub.getData());
					log.debug("whole field added:" + dataField);
					cmarc.addVariableField(dataField);					
				}else {					
					//handle 020$z=aaa(****) where *** to 010$b
					if("020".equals(df.getTag()) && (sub.getCode() == 'z' || sub.getCode() == 'a')) {
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
			}
			log.debug("old to new=>" + df.getTag() + ":" + nwDf.toString());
			cmarc.addVariableField(nwDf);
		}
	}

	private void handleControlFields(Record record, Record cmarc) {
		for(ControlField field: record.getControlFields()){	
			FieldTransRule transRule = getTransRuleFromRules(field.getTag());
			if(transRule == null) 
				continue;
			field.setTag(transRule.getTo());
			cmarc.addVariableField(field);
			
			if("008".equals(field.getTag())){
				String data = field.getData();
				cmarc.addVariableField(buildDataField("102a", data.substring(15, 17)));
				cmarc.addVariableField(buildDataField("101a", data.substring(35, 37)));
			}
		}
	}

	private DataField buildDataField(String tagWithSub, String data) {
		return buildDataField(tagWithSub.substring(0, 3), ' ', ' ', tagWithSub.charAt(3), data);
	}
	private DataField buildDataField(String tag, char indi1, char indi2,char sub, String data) {
		DataField df = new DataFieldImpl(tag, indi1, indi2);
		df.addSubfield(new SubfieldImpl(sub, data));
		return df;  
	}
	
	
	private void handleLeader(Record record, Record cmarc) {
		cmarc.setLeader(record.getLeader());
	}
	
}
