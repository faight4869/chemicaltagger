package uk.ac.cam.ch.wwmm.chemicaltagger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Document;
import nu.xom.Serializer;

import org.xmlcml.cml.base.CMLConstants;

/********************************************
 * A Utils class.
 * 
 * @author lh359, pm286
 ********************************************/
public class Utils {

	public static void checkOutputDir(String outputDir) {
		if (!new File(outputDir).exists()) {
			new File(outputDir).mkdir();
		}
	}

	public static List addToList(String string) {
		List<String> arrayList = new ArrayList<String>();

		for (String item : string.split(" ")) {
			arrayList.add(item);
		}
		return arrayList;
	}

	/******************************************
	 * replaces all non-XML characters with _
	 * 
	 * @param text
	 * @return
	 *******************************************/
	public static String makeNCName(String text) {
		if (text == null) {
			text = "emptyName";
		} else if (text.trim().length() == 0) {
			text = "emptyName";
		} else {
			text = text.trim();
			char c = text.charAt(0);
			if (!Character.isLetter(c) && c != '_') {
				text = '_' + text;
			}
			text = text.replaceAll("[^A-Za-z0-9_.-]", CMLConstants.S_UNDER);
		}
		return text;
	}

	public static void writeXMLToFile(Document doc, String xmlFilename) {

		try {
			Serializer serializer = new Serializer(new FileOutputStream(
					xmlFilename));
			serializer.write(doc);
			serializer.flush();
		} catch (IOException ex) {
			System.err.println("error" + ex);
		}
	}
	private boolean isNumeric(String input) {
		boolean flag = false;

		try {
			Integer.parseInt(input);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

}
