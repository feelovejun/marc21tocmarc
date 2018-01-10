package com.wavecheng.marc21tocmarc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.marc.Record;
import org.marc4j.util.JsonParser;

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
        frule.validateSubfieldsRule();
        
        Gson gson = new Gson();
        String json = gson.toJson(frule);
        System.out.println("" + json);
        
        FieldTransRule ff = gson.fromJson(json, FieldTransRule.class);
        System.out.println(ff);
    }
    
    
   
}
