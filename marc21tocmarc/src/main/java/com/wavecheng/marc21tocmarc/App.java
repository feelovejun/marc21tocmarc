package com.wavecheng.marc21tocmarc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.marc.Record;
import org.marc4j.util.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

import com.google.gson.Gson;
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
	
    public static void main( String[] args ) throws IOException
    {
        String marc21 = "";
        String filename ="20171205_450880_demo-vzhao_Full-MARC8";
        //filename = "20171117_423294_ntuedu_Full-MARC8";
        //filename = "proquest-Cmarc";
//        FileReader fr = new FileReader(filename +".mrc");
//        BufferedReader br = new BufferedReader(fr);
//        marc21 = br.readLine();
//        Marc m = new MarcImpl(marc21);
//        for(Field f : m.listFields())
//        System.out.println(f.getName() + ":" + f.getData());
        
        MarcReader mr = new MarcStreamReader(new FileInputStream(filename+ ".mrc"));
        while(mr.hasNext()) {
        	Record r = mr.next();       	
        	System.out.println(r.toString());
        }   
        
        TransRule tr = new TransRule();
        
        FieldTransRule frule = new FieldTransRule();
        frule.setFrom("050");
        frule.setTo("680");
        frule.setIndi1From('1');
        frule.setIndi1To(' ');
        frule.setIndi2From(' ');
        frule.setIndi2To('1');
        
        Map<String,String> subf = new HashMap<String,String>();
        subf.put("a", "b");
        subf.put("b", "h");
        frule.setSubfieldMapping(subf);
        
        Map<String,String> whole = new HashMap<String, String>();
        whole.put("4", "330a");
        whole.put("a", "843b");
        frule.setWholeFieldMapping(whole);
        //frule.validateSubfieldsRule();
        
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
        path = path;
        TransRule trr = gson.fromJson(new FileReader(path), TransRule.class);
        System.out.println(trr.getTransRules());
        
        log.info("something test");
    }
    
    
   
}
