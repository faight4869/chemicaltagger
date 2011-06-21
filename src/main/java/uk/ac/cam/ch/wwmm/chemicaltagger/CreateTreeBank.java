package uk.ac.cam.ch.wwmm.chemicaltagger;

import java.io.File;
import java.io.IOException;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.Text;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public class CreateTreeBank {
	private static final Logger LOG = Logger.getLogger(CreateTreeBank.class);
	
	
	public String getContent(String sourceFile) {
		Builder builder = new Builder();
		Document doc = null;
        LOG.info("Extracting data from "+sourceFile);
		String content = "";

		try {
			doc = builder.build(sourceFile);
			Nodes sections = doc.query("//p");
			for (int i = 0; i < sections.size(); i++) {

				Node node = sections.get(i);
				for (int j = 0; j < node.getChildCount(); j++) {
					if (node.getChild(j) instanceof Text) {
						String cleanNode = node.getChild(j).getValue().trim();
						if (cleanNode.toLowerCase().startsWith("tlc") ||cleanNode.toLowerCase().startsWith("mass sp")) {
							break;
						} else {
							content = content + " " + cleanNode;
						}
						
					}

				}
			}

			String spectra = "";
			Nodes spectrum = doc.query("//spectrum");
			for (int i = 0; i < spectrum.size(); i++) {
				String cleanSpectrum = spectrum.get(i).getValue().trim().replace("\n", "");
				if (StringUtils.isNotEmpty(cleanSpectrum)) {
					spectra = spectra + " " + cleanSpectrum;
				}

			}

		} catch (ParsingException ex) {
			LOG.fatal("ParsingException " + ex.getMessage(),
					new RuntimeException());
		} catch (IOException ex) {

			LOG.fatal(ex.getMessage(), new RuntimeException());
		}
		return content;
	}
	
	public static void main (String[] args) throws IOException {
		
		String path = args[0];
		File patentDirectory = new File(path);
		String[] patentDir = patentDirectory.list();
		XMLtoAST xmlAst = new XMLtoAST();
		
		for (String file : patentDir) {
			String resourcePath = path + file;
			CreateTreeBank extract = new CreateTreeBank();
			String content = extract.getContent(resourcePath);
			Document doc = Utils.runChemicalTagger(content); 
			xmlAst.convert(doc);
			Utils.writeListToFile(xmlAst.getSentenceList(),"target/"+file.replace("xml", "txt"));
		}
	}
}