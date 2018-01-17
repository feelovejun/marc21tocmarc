package com.wave.marctrans;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class App 
{
	private static Logger log = LoggerFactory.getLogger(App.class);
	
    public static void main( String[] args ) throws IOException
    {
    	if(args.length !=4  ) {
    		System.out.println("Usage: -input in.mrc8 -output out.cmarc");	
    		System.exit(-1);
    	};
    	
    	MarcHardcodeTransformer transformer = new MarcHardcodeTransformer();
    	transformer.transform(args[1], args[3]);
    	log.info("trans from [" + args[1] + "]=>[" + args[3] + "] done!");
    	
    }
}
