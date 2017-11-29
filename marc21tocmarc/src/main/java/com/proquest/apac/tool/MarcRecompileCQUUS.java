package com.proquest.apac.tool;

import java.awt.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.hw.lib.core.marc.Field;
import com.hw.lib.core.marc.Marc;
import com.hw.lib.core.marc.MarcChar;
import com.hw.lib.core.marc.impl.FieldImpl;
import com.hw.lib.core.marc.impl.MarcImpl;

public class MarcRecompileCQUUS {

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream("C:\\Users\\pchen\\Downloads\\cdsj\\cqu-catalogus-peri.txt"),"gbk"));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream("c:/cqu-catalogus-updates-2014-09-26-09-30-00.marc"), "utf-8"));
		int errorCount = 0;
		int total = 0;
		String line = null;
		String id = null;
		String marc = null;
		while( (line =br.readLine()) != null){
			if(line.length() <= 10) continue;
			try{
			int pos = line.indexOf("\t");
			id = line.substring(0,pos);
			marc = line.substring(pos+1);
			Marc m = new MarcImpl(marc);
			//System.out.println(id);
			//showMarc(m);
			//System.out.println("after adding 035a:");
			Field f = new FieldImpl("035","  " + MarcChar.SUBFIELD_DELIMITER +"a" + id);
			m = m.addField(f);
			bw.append(m.getStrMarc());
			bw.write("\r\n");
			//System.out.println(m.getStrMarc());
			//showMarc(m);
			//System.out.println("------------------------------------------");
			}catch(Exception ex){
				System.out.println(id + "\t" + line);
				errorCount ++;
			}
			total ++;
		}
		br.close();
		bw.close();
		System.out.println("proceed total:" + total + ", hasError: "  + errorCount);
	}

	public static void showMarc(Marc m){
		for(Field f : m.listFields()){
			System.out.println(f.toString());
		}
	}
}
