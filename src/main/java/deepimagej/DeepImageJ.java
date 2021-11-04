/*
 * DeepImageJ
 * 
 * https://deepimagej.github.io/deepimagej/
 *
 * Conditions of use: You are free to use this software for research or educational purposes. 
 * In addition, we expect you to include adequate citations and acknowledgments whenever you 
 * present or publish results that are based on it.
 * 
 * Reference: DeepImageJ: A user-friendly plugin to run deep learning models in ImageJ
 * E. Gomez-de-Mariscal, C. Garcia-Lopez-de-Haro, L. Donati, M. Unser, A. Munoz-Barrutia, D. Sage. 
 * Submitted 2019.
 *
 * Bioengineering and Aerospace Engineering Department, Universidad Carlos III de Madrid, Spain
 * Biomedical Imaging Group, Ecole polytechnique federale de Lausanne (EPFL), Switzerland
 *
 * Corresponding authors: mamunozb@ing.uc3m.es, daniel.sage@epfl.ch
 *
 */

/*
 * Copyright 2019. Universidad Carlos III, Madrid, Spain and EPFL, Lausanne, Switzerland.
 * 
 * This file is part of DeepImageJ.
 * 
 * DeepImageJ is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 * 
 * DeepImageJ is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with DeepImageJ. 
 * If not, see <http://www.gnu.org/licenses/>.
 */

package deepimagej;

import java.awt.TextArea;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import deepimagej.tools.ArrayOperations;
import deepimagej.tools.DijTensor;
import deepimagej.tools.FileTools;

public class DeepImageJ {

	public Parameters				params			= null;
	private boolean					valid 			= true;
	public ArrayList<String>		msgChecks		= new ArrayList<String>();
	public ArrayList<String>		msgLoads		= new ArrayList<String>();
	public ArrayList<String[]>		msgArchis		= new ArrayList<String[]>();
		
	public DeepImageJ(String raw) throws Exception {
		this.params = new Parameters(raw);
	}

	public boolean getValid() {
		return this.valid;
	}
	
	static public DeepImageJ ImjoyYaml2DijYaml(String raw) {
		DeepImageJ dp;
		try {
			dp = new DeepImageJ(raw);
		} catch (Exception e) {
			System.out.println(e.toString());
			dp = null;
		}
		return dp;
	}

	public void writeParameters(TextArea info) {
		if (params == null) {
			info.append("No params\n");
			return;
		}
		info.append("---------- MODEL INFO ----------\n");
		info.append("Authors" + "\n");
		for (HashMap<String, String> auth : params.author) {
			String name = auth.get("name") == null ? "n/a" : auth.get("name");
			String aff = auth.get("affiliation") == null ? "n/a" : auth.get("affiliation");
			String orcid = auth.get("orcid") == null ? "n/a" : auth.get("orcid");
			info.append("  - Name: " + name + "\n");
			info.append("    Affiliation: " + aff + "\n");
			info.append("    Orcid: " + orcid + "\n");
		}
		info.append("References" + "\n");
		for (HashMap<String, String> ref : params.cite) {
			info.append("  - Article: " + ref.get("text") + "\n");
			info.append("    Doi: " + ref.get("doi") + "\n");
		}
		info.append("Framework: " + params.framework + "\n");
		
		if (params.framework.contains("tensorflow")) {
			info.append("Tag: " + params.tag + "\n");
			info.append("Signature: " + params.graph + "\n");
		}
		info.append("Allow tiling: " + params.allowPatching + "\n");

		info.append("\n");

		info.append("------------ TEST INFO -----------\n");
		info.append("Inputs:" + "\n");
		for (DijTensor inp : params.inputList) {
			info.append("  - Name: " + inp.exampleInput + "\n");
			info.append("    Size: " + inp.inputTestSize + "\n");
			info.append("      x: " + inp.inputPixelSizeX  + "\n");
			info.append("      y: " + inp.inputPixelSizeY  + "\n");
			info.append("      z: " + inp.inputPixelSizeZ  + "\n");			
		}
		info.append("Outputs:" + "\n");
		for (HashMap<String, String> out : params.savedOutputs) {
			info.append("  - Name: " + out.get("name") + "\n");
			info.append("  - Type: " + out.get("type") + "\n");
			info.append("     Size: " + out.get("size")  + "\n");		
		}
		info.append("Memory peak: " + params.memoryPeak + "\n");
		info.append("Runtime: " + params.runtime + "\n");
		
	}
	
	/*
	 * Method returns true if a torchscript model is found inside
	 * of the folder provided
	 */
	public static boolean findPytorchModel(File modelFolder) {
		for (String file : modelFolder.list()) {
			if (file.contains(".pt"))
				return true;
		}
		return false;
	}

}

