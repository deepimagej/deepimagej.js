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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import deepimagej.tools.Log;
import deepimagej.tools.DijTensor;
import ij.IJ;
import ij.gui.GenericDialog;

public class DeepImageJ {

	private Log 					log;
	public Parameters				params;
	private boolean					valid 			= true;
	public ArrayList<String>		msgChecks		= new ArrayList<String>();
	public ArrayList<String>		msgLoads		= new ArrayList<String>();
	public ArrayList<String[]>		msgArchis		= new ArrayList<String[]>();
	
	public DeepImageJ(String raw) {
		this.params = new Parameters(raw);
		//this.valid = checkUser(p);
	}

	public boolean getValid() {
		return this.valid;
	}
	
	static public DeepImageJ ImjoyYaml2DijYaml(String raw) {
		DeepImageJ dp = new DeepImageJ(raw);
		return dp;
	}
	/*
	public static HashMap<String, DeepImageJ> getModelsFromGithub(String webUrl, HashMap<String, DeepImageJ> dpsMap) throws MalformedURLException, IOException, URISyntaxException {
		RestTemplate restTemplate = new RestTemplate();
		List<Map> response = restTemplate.getForObject(
				webUrl + "?ref={branch}", List.class, "deepimagej",
				"models", "master");

 
		// Iterate through list of file metadata from response.
		for (Map fileMetaData : response) {
 
			// Get file name & raw file download URL from response.
			String fileName = (String) fileMetaData.get("name");
			String downloadUrl = (String) fileMetaData.get("download_url");
			System.out.println("File Name = " + fileName + " | Download URL = " + downloadUrl);
 
			// We will only fetch read me file for this example.
			if (downloadUrl != null && downloadUrl.contains("model.yaml")) {
				DeepImageJ dp = new DeepImageJ(downloadUrl);
				dpsMap.put(downloadUrl, dp);
				return dpsMap;
			} else if (downloadUrl == null && !fileName.contains(".github")) {
				dpsMap = getModelsFromGithub(webUrl + "/" + fileName, dpsMap);
			}
		}
		return dpsMap;
	}
	*/

	public void writeParameters(TextArea info) {
		if (params == null) {
			info.append("No params\n");
			return;
		}
		info.append("----------- BASIC INFO -----------\n");
		info.append("Name: " + params.name + "\n");
		/* TODO remove
		 * info.append(checks.get(0) + "\n");
		// TODO remove
		if (checks.size() == 2) {
			info.append("Size: " + checks.get(1).substring(18) + "\n");
		} else {
			info.append("Size: " + FileTools.getFolderSizeKb(path + "variables") + "\n");
		}
		 */
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
		info.append("Tag: " + params.tag + "\n");
		info.append("Signature: " + params.graph + "\n");
		info.append("Allow tiling: " + params.allowPatching + "\n");

		info.append("Dimensions: ");
		/* TODO remove
		for (DijTensor inp : params.inputList) {
			info.append(Arrays.toString(inp.tensor_shape));
			int slices = 1;
			int zInd = Index.indexOf(inp.form.split(""), "Z");
			if (zInd != -1) {slices = inp.tensor_shape[zInd];}
			int channels = 1;
			int cInd = Index.indexOf(inp.form.split(""), "C");
			if (cInd != -1) {channels = inp.tensor_shape[cInd];}
			info.append(" Slices (" + slices + ") Channels (" + channels + ")\n");
		}
		*/
		info.append("Input:");
		for (DijTensor inp2 : params.inputList)
			info.append(" " + inp2.name + " (" + inp2.form + ")");
		info.append("\n");
		info.append("Output:");
		for (DijTensor out : params.outputList)
			info.append(" " + out.name + " (" + out.form + ")");
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
			// TODO Deicde info.append("  - Name: " + out.name + "\n");
			info.append("  - Type: " + out.get("type") + "\n");
			info.append("     Size: " + out.get("size")  + "\n");		
		}
		info.append("Memory peak: " + params.memoryPeak + "\n");
		info.append("Runtime: " + params.runtime + "\n");
		
	}

	// TODO remove
	/*
	public  boolean checkUser(String path) {
		File dir = new File(path);
		if (!dir.exists()) {
			return false;
		}
		if (!dir.isDirectory()) {
			return false;
		}
		boolean valid = true;
		
		File configFile = new File(path + "config.yaml");
		if (!configFile.exists() && !developer) {
			valid = false;
			return valid;
		}

		File modelFile = new File(path + "saved_model.pb");
		if (!modelFile.exists()) {
			valid = false;
		} else {
			
			Set<String> versions = this.params.previousVersions.keySet();
			boolean isThereBiozoo = false;
			String allFiles = Arrays.toString(dir.list());
			for (String v : versions) {
				if (allFiles.contains("weights_" + v + ".zip")) 
					isThereBiozoo = true;
				this.params.framework = "Tensorflow";
			}
			if (isThereBiozoo) {
				valid = true;
				return valid;
			}

			File variableFile = new File(path + "variables");
			if (!variableFile.exists()) {
				valid = false;
			}
			else {
				this.params.framework = "Tensorflow";
			}
		}
		
		// If no tf model has been found. Look for a pytorch torchscript model
		if (!valid && findPytorchModel(dir)) {
			this.params.framework = "Pytorch";
			this.params.selectedModelPath = dir.getAbsolutePath();
			valid = true;
		} else if (valid && findPytorchModel(dir) && this.developer) {
			// If in the folder there is both a tf and a pytorch model, ask the user
			// which one he wants to load
			// TODO remove askFrameworkGUI();
			valid = true;
		} else if (valid && findPytorchModel(dir) && !this.developer) {
			// If in the folder there is both a tf and a pytorch model, give
			// the end-user the option to choose between both
			this.params.framework = "Pytorch/Tensorflow";
			this.params.selectedModelPath = dir.getAbsolutePath();
			valid = true;
		}
		
		return valid;
	}
	*/
	
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

	/*
	 * Let the user select which version of the model they want to load
	 * among all that have been recovered from the folder
	 */
	private boolean selectVersion(HashMap<String, Object> modelVersions) {
		boolean tf = (boolean) modelVersions.get("tf");
		boolean pt = (boolean) modelVersions.get("pytorch");
		HashMap<String, List<String>> biozoo = (HashMap<String, List<String>>) modelVersions.get("vBiozoo");
		ArrayList<String> ptModels = (ArrayList<String>) modelVersions.get("vPt");
		ArrayList<String> tfModels = (ArrayList<String>) modelVersions.get("vTf");
		
		// If there is only one model, select it and load it
		if (biozoo == null && tf == true && pt == false && tfModels.size() == 1) {
			this.params.framework = "Tensorflow";
			this.params.selectedModelPath = this.params.path2Model + tfModels.get(0);
			return true;
		}if (biozoo == null && tf == false && pt == true && ptModels.size() == 1) {
			this.params.framework = "Pytorch";
			this.params.selectedModelPath = this.params.path2Model + ptModels.get(0);
			return true;
		} else if (biozoo != null  && biozoo.get("correct").size() == 0 && biozoo.get("faulty").size() == 0 && tf == true && pt == false && tfModels.size() == 1) {
			this.params.framework = "Tensorflow";
			this.params.selectedModelPath = this.params.path2Model + tfModels.get(0);
			return true;
		} else if (biozoo != null  && biozoo.get("correct").size() == 0 && biozoo.get("faulty").size() == 0 && tf == false && pt == true && ptModels.size() == 1) {
			this.params.framework = "Pytorch";
			this.params.selectedModelPath = this.params.path2Model + ptModels.get(0);
			return true;
		} else if (biozoo != null && tf == false && pt == false && ptModels.size() == 0 && biozoo.get("correct").size() == 1 && biozoo.get("faulty").size() == 0) {
			this.params.selectedModelPath = this.params.path2Model + biozoo.get("correct").get(0);
			if (this.params.selectedModelPath.contains(".pt") || this.params.selectedModelPath.contains(".pth"))
				this.params.framework = "Pytorch";
			else
				this.params.framework = "Tensorflow";
			return true;
		} else if (biozoo == null && tf == false && pt == false) {
			return false;
		}

		// If there is more than one model, let the user select which one they 
		// want to load
		GenericDialog dlg = new GenericDialog("Choose version of the model");
		dlg.addMessage("The folder provided contained several model versions");
		dlg.addMessage("Select which do you want to load.");
		// List all models
		String[] modelList = new String[tfModels.size() + ptModels.size() + biozoo.get("correct").size() + biozoo.get("faulty").size() + biozoo.get("missing").size()];
		int i = 0;
		int tfSuffLen = " (Tensorflow)".length();
		int ptSuffLen = " (Pytorch)".length();
		int bioCorrectSuffLen = " (Bioimage Model Zoo)".length();
		int bioFaultySuffLen = " (Bioimage Model Zoo, faulty)".length();
		int bioMissingSuffLen = " (Bioimage Model Zoo, missing)".length();
		for (String v : tfModels) 
			modelList[i ++] = v + " (Tensorflow)";
		for (String v : ptModels) 
			modelList[i ++] = v + " (Pytorch)";
		for (String v : biozoo.get("correct")) 
			modelList[i ++] = v + " (Bioimage Model Zoo)";
		for (String v : biozoo.get("missing")) 
			modelList[i ++] = v + " (Bioimage Model Zoo, missing)";
		for (String v : biozoo.get("faulty")) 
			modelList[i ++] = v + " (Bioimage Model Zoo, faulty)";
		
		dlg.addChoice("Select framework", modelList, modelList[0]);
		dlg.showDialog();
		if (dlg.wasCanceled()) {
			dlg.dispose();
			return false;
		}
		String vSelect = dlg.getNextChoice();
		int selectLen = vSelect.length();
		if (vSelect.substring(selectLen - tfSuffLen).equals(" (Tensorflow)")) {
			this.params.framework = "Tensorflow";
			this.params.selectedModelPath = this.params.path2Model + vSelect.substring(0, selectLen - tfSuffLen);
		} else if (vSelect.substring(selectLen - ptSuffLen).equals(" (Pytorch)")) {
			this.params.framework = "Pytorch";
			this.params.selectedModelPath = this.params.path2Model + vSelect.substring(0, selectLen - ptSuffLen);
		} else if (vSelect.substring(selectLen - bioCorrectSuffLen).equals(" (Bioimage Model Zoo)") && vSelect.contains(".pt")) {
			this.params.framework = "Pytorch";
			this.params.selectedModelPath = this.params.path2Model + vSelect.substring(0, selectLen - bioCorrectSuffLen);
		} else if (vSelect.substring(selectLen - bioCorrectSuffLen).equals(" (Bioimage Model Zoo)") && !vSelect.contains(".pt")) {
			this.params.framework = "Tensorflow";
			this.params.selectedModelPath = this.params.path2Model + vSelect.substring(0, selectLen - bioCorrectSuffLen);
		} else if (vSelect.substring(selectLen - bioFaultySuffLen).equals(" (Bioimage Model Zoo, faulty)") && vSelect.contains(".pt")) {
			this.params.framework = "Pytorch";
			this.params.selectedModelPath = this.params.path2Model + vSelect.substring(0, selectLen - bioFaultySuffLen);
		} else if (vSelect.substring(selectLen - bioFaultySuffLen).equals(" (Bioimage Model Zoo, faulty)") && !vSelect.contains(".pt")) {
			this.params.framework = "Tensorflow";
			this.params.selectedModelPath = this.params.path2Model + vSelect.substring(0, selectLen - bioFaultySuffLen);
		} else if (vSelect.substring(selectLen - bioMissingSuffLen).equals(" (Bioimage Model Zoo, missing)")) {
			IJ.error("The selected version is specified in the model file\n"
					+ "but cannot be found in the model folder provided.\n"
					+ "Select another version, please");
			selectVersion(modelVersions);
		}
			
		
		return true;
	}
	
	/* TODO remove
	public void askFrameworkGUI() {
		GenericDialog dlg = new GenericDialog("Choose model framework");
		dlg.addMessage("The folder provided contained both a Tensorflow and a Pytorch model");
		dlg.addMessage("Select which do you want to load.");
		dlg.addChoice("Select framework", new String[]{"Tensorflow", "Pytorch"}, "Tensorflow");
		dlg.showDialog();
		if (dlg.wasCanceled()) {
			dlg.dispose();
			return;
		}
		this.params.framework = dlg.getNextChoice();
	}
	*/

}

