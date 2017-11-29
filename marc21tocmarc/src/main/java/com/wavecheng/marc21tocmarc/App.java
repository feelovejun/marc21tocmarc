package com.wavecheng.marc21tocmarc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


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
        System.out.println( "Hello World!" );
        String marc21 = "";
        FileReader fr = new FileReader("20171117_423294_ntuedu_Full-MARC8.mrc");
        BufferedReader br = new BufferedReader(fr);
        marc21 = br.readLine();
        Marc m = new MarcImpl(marc21);
        for(Field f : m.listFields())
        System.out.println(f.getName() + ":" + f.getData());
    }
}
