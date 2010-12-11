package uk.ac.cam.ch.wwmm.chemicaltagger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;

import org.antlr.runtime.tree.Tree;

public class RoleIdentifier {

	private String text;

	public RoleIdentifier() {
		
	}

	public void setText(String text){
		this.text = text;
	}
	public HashMap<String, String> getRoles() {
		HashMap<String, String> roleMap = new HashMap<String, String>();
		
		Document parsedDoc = runChemicalTagger();
		System.out.println(parsedDoc.toXML());
		Nodes nodes = parsedDoc.query("//MOLECULE");
		for (int i = 0; i < nodes.size(); i++) {
			Element nodeElement = (Element) nodes.get(i);
			String role = "None";
			String chemicalName = "";
			if (nodeElement.getAttribute("role") != null)
				role = nodeElement.getAttribute("role").getValue();
			Nodes chemicalNodes = nodeElement.query(".//OSCAR-CM");
			for (int j = 0; j < chemicalNodes.size(); j++) {
				Element chemicalElement = (Element) chemicalNodes.get(j);
				chemicalName = chemicalName +" "+chemicalElement.getValue();
			}
	 
			int count = 1;
			while (roleMap.containsKey(chemicalName+"_"+String.valueOf(count)))
				count = count + 1;
			roleMap.put(chemicalName+"_"+String.valueOf(count), role);
		}
		
		return roleMap;
	}

	private Document runChemicalTagger() {
		ChemistryPOSTagger chemPos = ChemistryPOSTagger.getInstance();
		POSContainer posContainer = chemPos.runTaggers(text);
		String tagged = posContainer.getTokenTagTupleAsString();

		InputStream in = null;
		try {
			in = new ByteArrayInputStream(tagged.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ChemistrySentenceParser chemistrySentenceParser = new ChemistrySentenceParser(
				in);

		Tree t = chemistrySentenceParser.parseTags();
		ASTtoXML ast2XML = new ASTtoXML();
		Document doc = ast2XML.convert(t, true);
		return doc;
	}

	public static void main(String[] args) {
		RoleIdentifier roleIdent = new RoleIdentifier();
        roleIdent.setText("Potassium was washed with acetone. Salt in water");
		HashMap<String, String> identifiedRoles = roleIdent.getRoles();
		System.out.println(identifiedRoles);
	}
}