package com.zakaria.cut;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.MessageFormat;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

/**
 * XML Cutter, cette classe prend le fichier TXT et le subdivise en plusieurs fichiers XML
 * 
 */
public class XmlCutter {

 private static final String SOURCE = "src/com/zakaria/cut/inputfolder";	/* le dossier source contient le fichier TXT */
	
 private static final String OUTPUT_FILE_NAME = "/file";	/* Nom à attribuer aux fichiers XML produits */
 private static String USER_HOME = System.getProperty("user.home");		/* Variable globale définissant le dossier Home */
 private static final String INPUT_FOLDER = USER_HOME + "/inputfolder";		/* Dossier du HDD contenant le fichier TXT */
 private static String OUTPUT_FOLDER = USER_HOME + "/outputfolder"; 	/* Dossier qui va contenir les fichiers XML produits */
 private static Logger LOG = Logger.getLogger("XmlCutter"); 	/* Log */
 private static long COUNTER = 0;

 /* Constructeur */
 
 public XmlCutter() {
	super();
	createFolders(INPUT_FOLDER);
	createFolders(OUTPUT_FOLDER);
	copyDirectoryContent(SOURCE, INPUT_FOLDER);
}
 
 /**
  * Fonction cut() teste l'existance d'un fichier TXT dans le dossier puis lance la fonction processFile()
  * */

public void cut() {
  Handler h = new ConsoleHandler();
  h.setLevel(Level.FINE);
  LOG.addHandler(h);
  LOG.setLevel(Level.FINE);

  File inputDir = new File(INPUT_FOLDER);
  File[] filesInInputDir = inputDir.listFiles();
  for (File f : filesInInputDir) {
   if ((f.getName()).endsWith(".txt")) {
    LOG.fine((MessageFormat.format(
      "Found a text file {0}. Processing docs...",
      f.getName())));

    processFile(f);
   }
  }
 }

	/* Fonction createFolders() crée les dossiers INPUT_FOLDER et OUTPUT_FOLDER */

 private static void createFolders(String name) {
	 
	File file = new File(name);
	if (!file.exists()) {
		if (file.mkdir()) {
			System.out.println("Directory is created!");
		} else {
			System.out.println("Failed to create directory!");
		}
	}
	 
 }
 
 /* La fonction copyDirectoryContent() copie le fichier TXT du projet le dossier INPUT_FOLDER */
 
 private static void copyDirectoryContent(String fileSource, String fileDestination)
 {

     File srcDir = new File(fileSource);

     File destDir = new File(fileDestination);

         try {
			FileUtils.copyDirectory(srcDir, destDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
     
 }
 

 /** 
  * La fonction processFile() effectue le traitement: Elle lit le fichier TXT depuis INPUT_FOLDER et le découpe
  * en plusieurs fichiers XML.
  * 
  * */
 
 private static void processFile(File f) {
  StringBuilder out = new StringBuilder();
  char prev = '#';
  try {
   BufferedReader br = new BufferedReader(new InputStreamReader(
     new FileInputStream(f), "UTF8"));
   char[] buf = new char[1];
   while (br.read(buf) >= 0) {
    out.append(buf[0]);
    if (prev == '<' && buf[0] == '?') {
     LOG.finest((MessageFormat.format(
       "Start of XML PI Found: {0}{1}", prev, buf[0])));
     if (out.length() > 2) {
      flushToFile(out.substring(0, out.length() - 2));
     }
     out.setLength(2);
    }
    prev = buf[0];
   }
   LOG.finest("Writing final file");
   flushToFile(out.toString());
   br.close();
  } catch (IOException e) {
   LOG.fine(e.getMessage());
  }
  LOG.fine(MessageFormat.format("Generated {0} XML Documents", COUNTER));
 }

 /* La fonction flushToFile() recopie le code XML depuis le fichier TXT vers les fichiers XML */
 
 private static void flushToFile(String s) {
  File f = new File(OUTPUT_FOLDER + OUTPUT_FILE_NAME + (++COUNTER)
    + ".xml");
  LOG.finest(MessageFormat.format("Writing file: {0}", f.getName()));
  try {
   FileOutputStream fos = new FileOutputStream(f);
   OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF8");
   osw.write(s);
   osw.flush();
  } catch (IOException e) {
   LOG.fine(e.getMessage());
  }
 }
}
