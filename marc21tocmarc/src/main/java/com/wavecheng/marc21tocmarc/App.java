package com.wavecheng.marc21tocmarc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.marc.Record;

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
        
    }
}
