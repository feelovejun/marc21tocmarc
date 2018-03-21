package com.wavecheng.marc21tocmarc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.marc.Record;
import org.marc4j.util.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.hw.lib.core.marc.Field;
import com.hw.lib.core.marc.Marc;
import com.hw.lib.core.marc.impl.MarcImpl;

/**
 * Hello world!
 *
 */
public class App 
{
	private static Logger log = LoggerFactory.getLogger(App.class);
	
    public static void main( String[] args ) throws Exception
    {

//        String marc21 = "";
        String filename ="20171205_450880_demo-vzhao_Full-MARC8";
        filename = "c:/cmarc";
//        MarcReader mr = new MarcStreamReader(new FileInputStream(filename+ ".mrc"));
//        while(mr.hasNext()) {
//        	Record r = mr.next();       	
//        	System.out.println(r.toString());
//        }         
//        testRuleJSON();
    	
    	runTrans();
    }

    private static void runTrans() throws Exception, JsonIOException, FileNotFoundException {
    	Gson gson = new Gson();
    	String input ="20171205_450880_demo-vzhao_Full-MARC8.mrc";
    	String output ="output.txt";
    	
    	input = "20180321_567329_demo-vzhao_Full-MARC8_5.mrc";
    	
    	String path = App.class.getClassLoader().getResource("rules.json").getPath();
        TransRule trr = gson.fromJson(new FileReader(path), TransRule.class);
        
//    	MarcTransformer transformer = new MarcTransformer(trr);
    	MarcHardcodeTransformer transformer = new MarcHardcodeTransformer();
        transformer.transform(input, output);
    	
    }
	private static void testRuleJSON() throws FileNotFoundException {
        
        TransRule tr = new TransRule();
        
        FieldTransRule frule = new FieldTransRule();
        frule.setFrom("050");
        frule.setTo("680");
        frule.setIndi1From('1');
        frule.setIndi1To(' ');
        frule.setIndi2From(' ');
        frule.setIndi2To('1');
        
        Map<Character,Character> subf = new HashMap<Character,Character>();
        subf.put('a', 'b');
        subf.put('b', 'h');
        frule.setSubfieldMapping(subf);
        
        Map<Character,String> whole = new HashMap<Character, String>();
        whole.put('4', "330a");
        whole.put('a', "843b");
        frule.setWholeFieldMapping(whole);
        //frule.validateSubfieldsRule();
        Set<Character> ingores = new HashSet<Character>();
        ingores.add('e');
        frule.setIgnoreSubfields(ingores);
        
		Gson gson = new Gson();
        String json = gson.toJson(frule);
        System.out.println("" + json);
        
        FieldTransRule ff = gson.fromJson(json, FieldTransRule.class);
        System.out.println(ff);
        
        FieldReplaceRule frr = new FieldReplaceRule();
        frr.setField("245a");
        frr.setPattern("[\\[|\\]]");
        frr.setReplacement("");
        String strFrr = gson.toJson(frr);
        System.out.println(strFrr);
        
        tr.setTransRules(Arrays.asList(frule,frule));
        tr.setReplaceRules(Arrays.asList(frr,frr));
        System.out.println(gson.toJson(tr));
        System.out.println();
        String path = App.class.getClassLoader().getResource("rules.json").getPath();

        TransRule trr = gson.fromJson(new FileReader(path), TransRule.class);
        System.out.println(trr.getTransRules());
        
        log.info("something test");
	}
      
}
