package com.proquest.apac.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hw.lib.core.marc.Field;
import com.hw.lib.core.marc.Marc;
import com.hw.lib.core.marc.MarcChar;
import com.hw.lib.core.marc.impl.FieldImpl;
import com.hw.lib.core.marc.impl.MarcImpl;

public class MarcRecompiler {

	public static void main(String[] args) throws IOException {
		if(args.length != 2 ){
			System.out.println("缺少参数，usage: inputFilePath outputFilePath ");
			return;
		}
		
		String infile = args[0];
		String outputfile = args[1];
		
		//infile = "C:\\Users\\pchen\\Downloads\\cdsj\\adlib3cn.txt";
		//outputfile = "c:/cqu-catalogcn-test.marc";
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(infile),"gbk"));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputfile), "utf-8"));
		int errorCount = 0;
		int total = 0;
		String line = null;
		String id = null;
		String marc = null;
		String pline = null;
		Pattern p = Pattern.compile("^\\d{9,}");
		while( (line =br.readLine()) != null){
			if(line.length() <= 10) continue;
			Matcher matcher = p.matcher(line);
			if(matcher.find()){
				pline = line;
			}else{
				pline += line.trim();
				continue;
			}
			
			try{
				int pos = pline.indexOf("\t");
				id = pline.substring(0,pos);
				marc = pline.substring(pos+1);
				Marc m = new MarcImpl(marc);
				Field f = new FieldImpl("035","  " + MarcChar.SUBFIELD_DELIMITER +"a" + id);
				m = m.addField(f);
				bw.append(m.getStrMarc());
				bw.write("\r\n");
			}catch(Exception ex){
				System.out.println(id + "\t" + line);
				errorCount ++;
			}
			total ++;
			if(total % 1000 ==0) {
				System.out.println("processed: " + total);
			}
		}
		br.close();
		bw.close();
		System.out.println("Compelete! proceed total:" + total + ", hasError: "  + errorCount);
		System.out.println("Press any key to contiue....");
		System.in.read();
	}
}
