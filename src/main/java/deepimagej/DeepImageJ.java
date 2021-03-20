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

import deepimagej.tools.DijTensor;

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
		info.append("----------- BASIC INFO -----------\n");
		info.append("Name: " + params.name + "\n");
		info.append("Authors" + "\n");
		for (String auth : params.author)
			info.append("  - " + auth + "\n");
		info.append("References" + "\n");
		// TODO robustness
		for (HashMap<String, String> ref : params.cite) {
			info.append("  - Text: " + ref.get("text") + "\n");
			info.append("    Doi: " + ref.get("doi") + "\n");
		}
		info.append("Framework:" + params.framework + "\n");
		

		info.append("------------ METADATA ------------\n");
		info.append("Allow tiling: " + params.allowPatching + "\n");

		info.append("Dimensions: ");
		info.append("Input:");
		for (DijTensor inp2 : params.inputList)
			info.append(" " + inp2.name + " (" + inp2.form + ")");
		info.append("\n");
		info.append("Output:");
		for (DijTensor out : params.outputList)
			info.append(" " + out.name + " (" + out.form + ")");
		info.append("\n");
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

