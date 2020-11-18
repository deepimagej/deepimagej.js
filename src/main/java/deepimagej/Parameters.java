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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import deepimagej.tools.DijTensor;
import ij.ImagePlus;

public class Parameters {

	/*
	 *  Directory where the model is located
	 */
	public String path2Model;
	/*
	 *  Parameter that checks if the plugin in use is DeepImageJ Run (false)
	 *  or DeepImageJ Build BundledModel (true)
	 */
	public boolean developer;
	/* TODO make the standard
	 * Path to the first pre-processing applied to the image, if there is any.
	 * It can be either a macro file ('.ijm' or '.txt'), java code (a '.class' file,
	 * a '.jar' file or a folder containing classes) or null if there is no processing.
	 */
	public String[]		preprocessing = new String[4];
	/*
	 * Path to the first pre-processing applied to the image, if there is any.
	 * It can be either a macro file ('.ijm' or '.txt'), java code (a '.class' file,
	 * a '.jar' file or a folder containing classes) or null if there is no processing.
	 */
	public String		firstPreprocessing = null;
	/*
	 * Path to the second pre-processing applied to the image, if there is any.
	 * It can be either a macro file ('.ijm' or '.txt'), java code (a '.class' file,
	 * a '.jar' file or a folder containing classes) or null if there is no processing.
	 */
	public String		secondPreprocessing = null;
	/*
	 * Path to the first first post-processing applied to the image, if there is any.
	 * It can be either a macro file ('.ijm' or '.txt'), java code (a '.class' file,
	 * a '.jar' file or a folder containing classes) or null if there is no processing.
	 */
	public String		firstPostprocessing = null;
	/*
	 *Path to the second post-processing applied to the image, if there is any.
	 * It can be either a macro file ('.ijm' or '.txt'), java code (a '.class' file,
	 * a '.jar' file or a folder containing classes) or null if there is no processing.
	 */
	public String		secondPostprocessing = null;
	/*
	 * Class and method called to run preprocessing
	 */
	public ArrayList<String> javaPreprocessingClass = new ArrayList<String>();
	/*
	 * Class and method called to run postprocessing
	 */
	public ArrayList<String> javaPostprocessingClass = new ArrayList<String>();
	/*
	 * Whether the network has a pyramidal pooling structure or not.
	 * If it has it, the way to define the model changes. By default
	 * it is false.
	 */
	public boolean pyramidalNetwork = false;
	/*
	 * Whether the model allows patching or has to use the whole image
	 * always.
	 */
	public boolean allowPatching = true;
	/*
	 * Image used to test the model
	 */
	public ImagePlus testImage;
	/*
	 * Copy created to return the original image in the case
	 * that the model fails after applying the macros
	 */
	public ImagePlus testImageBackup;
	/*
	 * List of all the images and ResultsTables that
	 * make the final output of the model
	 */
	public List<HashMap<String, String>> savedOutputs = new ArrayList<HashMap<String, String>>();
	/*
	 *  Directory specified by the user to save the model
	 */
	public String saveDir;
	
	// Boolean informing if the config file contains the ModelCharacteristics
	// parameters, needed to load the model
	public boolean completeConfig = true;
		
	/*
	 *  Parameters providing ModelInformation
	 */
	public String		name					= "";
	public List<String>	author					= new ArrayList<String>();
	public String		version					= "";
	public String		format_version			= "";
	public String		date					= "";
	public String		git_repo				= "";
	/*
	 * Citation: contains the reference articles and the corresponding dois used
	 * to create the model
	 */
	public List<HashMap<String, String>> cite;
	
	public String		documentation			= null;
	private String[] 	deepImageJTag			= {"deepImageJ"};
	public List<String>	infoTags				= Arrays.asList(deepImageJTag);
	public String		license					= null;
	public String		language				= "Java";
	public String		framework				= "Tensorflow";
	public String		source					= null;
	public String		coverImage				= null;
	public String		description				= null;

	// in ModelTest
	public String		memoryPeak				= "";
	public String		runtime					= "";

	public String		tag						= "";
	public String		graph					= "";
	public Set<String> 	graphSet;

	// Parameters for the correct execution of the model on an image.
	// They range from the needed dimensions and dimensions organization
	// to the size of the patches and overlap needed for the network to work
	// properly and not to crash because of different issues such as memory.
	// They also regard the requirements for the input image
	
	/*
	 * List of the selected input tensors to the model
	 */
	public List<DijTensor> inputList 	= new ArrayList<>();
	/*
	 * List of the selected output tensors of the model
	 */
	public List<DijTensor> outputList 	= new ArrayList<>();
	/*
	 * List of all the input tensors to the model
	 */
	public List<DijTensor> totalInputList 	= new ArrayList<>();
	/*
	 * List of all the output tensors of the model
	 */
	public List<DijTensor> totalOutputList 	= new ArrayList<>();
	/*
	 * If the input is fixed, only show the input size in the yaml file
	 */
	public boolean		fixedInput			= false;
	/*
	 * Checksum of the saved_model.pb file. Only useful if
	 * we use a Bioimage Zoo model.
	 */
	public String saved_modelSha256;
	/*
	 * Specifies if the folder contains a Bioimage Zoo model
	 */
	public boolean biozoo = false;
	/*
	 * List of all the available preprocessings from
	 * the yaml file
	 */
	public HashMap<String, String[]> pre;
	/*
	 * List of all the available postprocessings from
	 * the yaml file
	 */
	public HashMap<String, String[]> post;
	/*
	 * Path to the model, in the case a Pytorch model is used. The Pytorch model
	 * is always a .pt or .pth file. In the case of a Tensorflow model, path to the 
	 * weights folder
	 */
	public String selectedModelPath;
	
	/* TODO remove
	public static void main(String[] args) {
		String raw = "format_version: 0.3.0\r\n" + 
				"name: DEFCoN density map estimation\r\n" + 
				"description: Density Estimation by Fully Convolutional Networks (DEFCoN) - A fluorescent spot counter for single molecule localization microscopy.\r\n" + 
				"date: 2019\r\n" + 
				"cite:\r\n" + 
				"  - text: DEFCoN was written by Baptiste Ottino as a Master's thesis project under the guidance of Kyle M. Douglass and Suliana Manley in the Laboratory of Experimental Biophysics.\r\n" + 
				"authors:\r\n" + 
				"  - Baptiste Ottino\r\n" + 
				"  - Kyle M. Douglass\r\n" + 
				"  - Suliana Manley\r\n" + 
				"documentation: https://github.com/LEB-EPFL/DEFCoN-ImageJ/wiki.\r\n" + 
				"covers: [./cover_image.jpg]\r\n" + 
				"tags:\r\n" + 
				"  - deepimagej\r\n" + 
				"  - smlm\r\n" + 
				"  - defcon\r\n" + 
				"  - density estimation\r\n" + 
				"license: BSD 3\r\n" + 
				"language: Java\r\n" + 
				"framework: Tensorflow\r\n" + 
				"git_repo: https://github.com/LEB-EPFL/DEFCoN\r\n" + 
				"weights:\r\n" + 
				"  tensorflow_protobuffer:\r\n" + 
				"    name: v1\r\n" + 
				"    source: https://zenodo.org/record/4244821/files/defcon_density_map_estimation_tf_model.zip?download=1\r\n" + 
				"    sha256: ea57590caefa41a493808420d5bc029d7b6c8ad8b1633d8feeb166a99d71f45d\r\n" + 
				"    test_input:\r\n" + 
				"      - ./exampleImage.tiff\r\n" + 
				"    test_output:\r\n" + 
				"      - ./resultImage.tiff\r\n" + 
				"inputs:\r\n" + 
				"  - name: raw\r\n" + 
				"    axes: byxc\r\n" + 
				"    data_type: float32\r\n" + 
				"    data_range: [-inf, inf]\r\n" + 
				"    shape:\r\n" + 
				"      min: [1, 1, 1, 1]\r\n" + 
				"      step: [0, 1, 1, 0]\r\n" + 
				"    preprocessing:\r\n" + 
				"      name: min_max_normalization\r\n" + 
				"      kwargs:\r\n" + 
				"        mode: fixed\r\n" + 
				"        axes: xy\r\n" + 
				"        min: 0.0\r\n" + 
				"        max: 65535.0\r\n" + 
				"outputs:\r\n" + 
				"  - name: segmentation\r\n" + 
				"    axes: byxc\r\n" + 
				"    data_type: float32\r\n" + 
				"    data_range: [0, 1]\r\n" + 
				"    halo: [0, 10, 10, 0]\r\n" + 
				"    shape:\r\n" + 
				"      reference_input: raw\r\n" + 
				"      scale: [1, 1, 1, 1]\r\n" + 
				"      offset: [0, 0, 0, 0]\r\n" + 
				"config:\r\n" + 
				"# custom config for DeepImageJ, see https://github.com/bioimage-io/configuration/issues/23\r\n" + 
				"  deepimagej:\r\n" + 
				"    pyramidal_model: false\r\n" + 
				"    allow_tiling: true\r\n" + 
				"    model_keys:\r\n" + 
				"      model_tag: tf.saved_model.tag_constants.SERVING\r\n" + 
				"      signature_definition: tf.saved_model.signature_constants.DEFAULT_SERVING_SIGNATURE_DEF_KEY\r\n" + 
				"    test_information:\r\n" + 
				"      inputs:\r\n" + 
				"        - name: exampleImage.tiff\r\n" + 
				"          size: 64 x 64 x 1 x 1\r\n" + 
				"          pixel_size:\r\n" + 
				"            x: 1 pixel\r\n" + 
				"            y: 1 pixel\r\n" + 
				"            z: 1.0 pixel\r\n" + 
				"      outputs:\r\n" + 
				"        - name: resultImage.tiff\r\n" + 
				"          type: image\r\n" + 
				"          size: 64 x 64 x 1 x 1\r\n" + 
				"      memory_peak: 62.9 Mb\r\n" + 
				"      runtime: 0.1 s\r\n" + 
				"    prediction:\r\n" + 
				"      preprocess:\r\n" + 
				"      -   spec: ij.IJ::runMacroFile\r\n" + 
				"          kwargs: preprocessing.txt\r\n" + 
				"      postprocess:\r\n" + 
				"      -   spec: ij.IJ::runMacroFile\r\n" + 
				"          kwargs: postprocessing.txt";
		System.out.print(raw);
		new Parameters(raw);
	}*/
	
	
	
	public Parameters(String rawYaml) {
		// Parse the yaml fields from the raw Yaml written in 
		// string format
		pyramidalNetwork = false;
		allowPatching = true;
		fixedInput = false;
		completeConfig = true;
		while (rawYaml.contains("\n")) {
			int lineEnd = rawYaml.indexOf("\n");
			String line = rawYaml.substring(0, lineEnd);
			int fieldEnd = line.indexOf(":");
			String field = fieldEnd != - 1 ? line.substring(0, fieldEnd) : "";
			boolean reduceStr = true;
			switch (field) {
				case "format_version":
					format_version = line.substring(fieldEnd + 1).trim();
					break;
				case "name":
					name = line.substring(fieldEnd + 1).trim();
					break;
				case "description":
					description = line.substring(fieldEnd + 1).trim();
					break;
				case "date":
					date = line.substring(fieldEnd + 1).trim();
					break;
				case "cite":
					cite = new ArrayList<HashMap<String, String>>();
					rawYaml = rawYaml.substring(lineEnd + 1);
					rawYaml = getYamlCitation(rawYaml);
					reduceStr = false;
					break;
				case "authors":
					author = new ArrayList<String>();
					rawYaml = rawYaml.substring(lineEnd + 1);
					rawYaml = getYamlEnum(rawYaml, author);
					reduceStr = false;
					break;
				case "documentation":
					documentation = line.substring(fieldEnd + 1).trim();
					break;
				case "covers":
					break;
				case "tags":
					infoTags = new ArrayList<String>();
					rawYaml = rawYaml.substring(lineEnd + 1);
					rawYaml = getYamlEnum(rawYaml, infoTags);
					reduceStr = false;
					break;
				case "license":
					license = line.substring(fieldEnd + 1).trim();
					break;
				case "language":
					language = line.substring(fieldEnd + 1).trim();
					break;
				case "framework":
					framework = line.substring(fieldEnd + 1).trim();
					break;
				case "git_repo":
					git_repo = line.substring(fieldEnd + 1).trim();
					break;
				case "weights":
					path2Model = "";
					rawYaml = rawYaml.substring(lineEnd + 1);
					rawYaml = getYamlModel(rawYaml);
					reduceStr = false;
					break;
				case "inputs":
					inputList = new ArrayList<DijTensor>();
					rawYaml = rawYaml.substring(lineEnd + 1);
					rawYaml = getYamlInputs(rawYaml);
					reduceStr = false;
					break;
				case "outputs":
					outputList = new ArrayList<DijTensor>();
					rawYaml = rawYaml.substring(lineEnd + 1);
					rawYaml = getYamlOutputs(rawYaml);
					reduceStr = false;
					break;
				case "config":
					break;
			}
			if (reduceStr)
				rawYaml = rawYaml.substring(lineEnd + 1);
		}
	}
	
	private String getYamlOutputs(String rawYaml) {
		int lineEnd;
		String txtLine;
		DijTensor output = null;
		while (rawYaml.trim().indexOf("config:") != 0) {
			// Get line
			lineEnd = rawYaml.indexOf("\n");
			txtLine = rawYaml.substring(1, lineEnd).trim();
			int contentStarts = txtLine.indexOf(":");
			rawYaml = rawYaml.substring(lineEnd + 1);
			if (txtLine.indexOf("-") == 0 && output != null) {
				output.tensorType = "image";
				outputList.add(output);
			} else if (txtLine.indexOf("-") == 0) {
				int endInd = txtLine.indexOf(":");
				String name = txtLine.substring(contentStarts, endInd).trim();
				output = new DijTensor(name);
			} else if (txtLine.indexOf("axes") == 0) {
				output.form = txtLine.substring(contentStarts + 1).trim().toUpperCase();
			} else if (txtLine.indexOf("halo") == 0) {
				int startInd = txtLine.indexOf("[");
				int endInd = txtLine.indexOf("]");
				String[] line = txtLine.substring(startInd + 1, endInd).split(",");
				output.halo = new int[line.length];
				for (int i = 0; i < line.length; i ++)
					output.halo[i] = Integer.parseInt(line[i].trim());
			} else if (txtLine.indexOf("shape") == 0 && txtLine.equals("shape:")) {
				lineEnd = rawYaml.indexOf("\n");
				txtLine = rawYaml.substring(1, lineEnd).trim();
				rawYaml = rawYaml.substring(lineEnd + 1);
				int startInd = txtLine.indexOf(":");
				output.referenceImage = txtLine.substring(startInd + 1).trim();
				
				lineEnd = rawYaml.indexOf("\n");
				txtLine = rawYaml.substring(1, lineEnd).trim();
				rawYaml = rawYaml.substring(lineEnd + 1);
				startInd = txtLine.indexOf("[");
				int endInd = txtLine.indexOf("]");
				String[] line = txtLine.substring(startInd + 1, endInd).split(",");
				output.scale = new float[line.length];
				for (int i = 0; i < line.length; i ++)
					output.scale[i] = Float.parseFloat(line[i].trim());
				lineEnd = rawYaml.indexOf("\n");
				txtLine = rawYaml.substring(1, lineEnd).trim();
				rawYaml = rawYaml.substring(lineEnd + 1);
				startInd = txtLine.indexOf("[");
				endInd = txtLine.indexOf("]");
				line = txtLine.substring(startInd + 1, endInd).split(",");
				output.offset = new int[line.length];
				for (int i = 0; i < line.length; i ++)
					output.offset[i] = Integer.parseInt(line[i].trim());
			} else if (txtLine.indexOf("shape") == 0 && !txtLine.equals("shape:")) {
				int startInd = txtLine.indexOf("[");
				int endInd = txtLine.indexOf("]");
				String[] line = txtLine.substring(startInd + 1, endInd).split(",");
				output.recommended_patch = new int[line.length];
				for (int i = 0; i < line.length; i ++)
					output.recommended_patch[i] = Integer.parseInt(line[i].trim());
				output.offset = new int[output.recommended_patch.length];
				output.scale = new float[output.recommended_patch.length];
			}
		}
		output.tensorType = "image";
		outputList.add(output);
		return rawYaml;
	}
	
	private String getYamlInputs(String rawYaml) {
		int lineEnd;
		String txtLine;
		DijTensor input = null;
		while (rawYaml.trim().indexOf("outputs:") != 0) {
			// Get line
			lineEnd = rawYaml.indexOf("\n");
			txtLine = rawYaml.substring(1, lineEnd).trim();
			int contentStarts = txtLine.indexOf(":");
			rawYaml = rawYaml.substring(lineEnd + 1);
			if (txtLine.indexOf("-") == 0 && input != null) {
				input.tensorType = "image";
				inputList.add(input);
			} else if (txtLine.indexOf("-") == 0) {
				int endInd = txtLine.indexOf(":");
				String name = txtLine.substring(contentStarts, endInd).trim();
				input = new DijTensor(name);
			} else if (txtLine.indexOf("axes") == 0 && input.form == null) {
				input.form = txtLine.substring(contentStarts + 1).trim().toUpperCase();
			} else if (txtLine.indexOf("shape") == 0 && txtLine.equals("shape:")) {
				lineEnd = rawYaml.indexOf("\n");
				txtLine = rawYaml.substring(1, lineEnd).trim();
				rawYaml = rawYaml.substring(lineEnd + 1);
				int startInd = txtLine.indexOf("[");
				int endInd = txtLine.indexOf("]");
				String[] line = txtLine.substring(startInd + 1, endInd).split(",");
				input.minimum_size = new int[line.length];
				for (int i = 0; i < line.length; i ++)
					input.minimum_size[i] = Integer.parseInt(line[i].trim());
				lineEnd = rawYaml.indexOf("\n");
				txtLine = rawYaml.substring(1, lineEnd).trim();
				rawYaml = rawYaml.substring(lineEnd + 1);
				startInd = txtLine.indexOf("[");
				endInd = txtLine.indexOf("]");
				line = txtLine.substring(startInd + 1, endInd).split(",");
				input.step = new int[line.length];
				for (int i = 0; i < line.length; i ++)
					input.step[i] = Integer.parseInt(line[i].trim());
				input.recommended_patch = new int[input.minimum_size.length];
			} else if (txtLine.indexOf("shape") == 0 && !txtLine.equals("shape:")) {
				int startInd = txtLine.indexOf("[");
				int endInd = txtLine.indexOf("]");
				String[] line = txtLine.substring(startInd + 1, endInd).split(",");
				input.minimum_size = new int[line.length];
				for (int i = 0; i < line.length; i ++)
					input.minimum_size[i] = Integer.parseInt(line[i].trim());
				input.step = new int[input.minimum_size.length];
				input.recommended_patch = input.minimum_size;
			}
		}
		
		input.tensorType = "image";
		inputList.add(input);
		return rawYaml;
	}



	public String getYamlModel(String rawYaml) {
		// Get line
		int lineEnd = rawYaml.indexOf("\n");
		String txtLine = rawYaml.substring(1, lineEnd);
		// TODO change 'tensorflow_protobuffer:' by 'tensorflow_js:'
		while (!txtLine.trim().equals("tensorflow_protobuffer:")) {
			// Get line
			lineEnd = rawYaml.indexOf("\n");
			txtLine = rawYaml.substring(1, lineEnd);
			rawYaml = rawYaml.substring(lineEnd + 1);
		}
		while (rawYaml.trim().indexOf("name") != 0) {
			// Get line
			lineEnd = rawYaml.indexOf("\n");
			txtLine = rawYaml.substring(1, lineEnd);
			rawYaml = rawYaml.substring(lineEnd + 1);
		}
		// Get model name
		lineEnd = rawYaml.indexOf("\n");
		txtLine = rawYaml.substring(1, lineEnd);
		// Get field
		int startContent = txtLine.indexOf(":");
		// Get field content
		version = txtLine.substring(startContent + 1);
		
		rawYaml = rawYaml.substring(lineEnd + 1);
		
		//  Get model source
		while (rawYaml.trim().indexOf("source") != 0) {
			// Get line
			lineEnd = rawYaml.indexOf("\n");
			txtLine = rawYaml.substring(1, lineEnd);
			rawYaml = rawYaml.substring(lineEnd + 1);
		}
		// Get model name
		lineEnd = rawYaml.indexOf("\n");
		txtLine = rawYaml.substring(1, lineEnd);
		// Get field
		startContent = txtLine.indexOf(":");
		// Get field content
		path2Model = txtLine.substring(startContent + 1);
		
		rawYaml = rawYaml.substring(lineEnd + 1);
		
		return rawYaml;
	}
	
	public String getYamlEnum(String rawYaml, List<String> enumeration) {
		while (rawYaml.trim().substring(0, 1).equals("-")) {
			// Get line
			int lineEnd = rawYaml.trim().indexOf("\n");
			String txtLine = rawYaml.trim().substring(1, lineEnd);
			enumeration.add(txtLine);
			rawYaml = rawYaml.trim().substring(lineEnd + 1);
		}
		return rawYaml;
	}
	
	public String getYamlCitation(String rawYaml) {
		cite = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> c = new HashMap<String, String>();
		while (rawYaml.trim().substring(0, 1).equals("-")) {
			// Get line
			int lineEnd = rawYaml.trim().indexOf("\n");
			String txtLine = rawYaml.trim().substring(1, lineEnd);
			// Get field name
			int startContent = txtLine.indexOf(":");
			String fieldName = txtLine.substring(0, startContent);
			// Get field content
			String txtContent = txtLine.substring(startContent + 1);
			
			if (fieldName.contains("text") && c.keySet().size() == 0) {
				c = new HashMap<String, String>();
				c.put("text", txtContent);
			} else if (fieldName.contains("text") && c.keySet().size() >0) {
				cite.add(c);
				c = new HashMap<String, String>();
				c.put("text", txtContent);
			} else if (fieldName.contains("doi")) {
				c.put("doi", txtContent);
			}
			rawYaml = rawYaml.trim().substring(lineEnd + 1);
		}
		cite.add(c);	
		return rawYaml;
	}
}