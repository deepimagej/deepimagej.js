/*
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import deepimagej.tools.DijTensor;
import deepimagej.tools.YamlParser;
import ij.ImagePlus;

public class Parameters2 {

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
	 * List of dependencies needed for the Java pre-processing
	 */
	public ArrayList<String> preAttachments = new ArrayList<String>();
	/*
	 * List of dependencies needed for the Java post-processing
	 */
	public ArrayList<String> postAttachments = new ArrayList<String>();
	/*
	 * List of dependencies needed for the Java pre- and post-processing.
	 * This variable is the union of preAttachments and postAttachments
	 */
	public ArrayList<String> attachments = new ArrayList<String>();
	/*
	 * List of dependencies needed for the Java pre- and post-processing.
	 * This variable is the union of preAttachments and postAttachments
	 * that are not included in the model folder. This variable will only be
	 * used by the DIJ Run
	 */
	public ArrayList<String> attachmentsNotIncluded = new ArrayList<String>();
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
	
	/*
	 *  Boolean informing if the model file sh256 corresponds to the
	 *  one saved specified in the model.yaml
	 */
	public boolean incorrectSha256 = false;
	
	/*
	 *  Boolean informing if the config file contains the ModelCharacteristics
	 *  parameters, needed to load the model
	 */
	public boolean completeConfig = true;
	
	/*
	 * Missing fields in the yaml file
	 */
	public ArrayList<String> fieldsMissing = null;
	
	/*
	 * Version of the DJL Pytorch being used to run Pytorch
	 */
	public String pytorchVersion = "";
		
	/*
	 *  Parameters providing ModelInformation
	 */
	public String		name					= "n.a.";
	public List<HashMap<String, String>>	author					= new ArrayList<HashMap<String, String>>();
	public String		timestamp				= "";
	public String		format_version			= "0.3.0";
	/*
	 * Citation: contains the reference articles and the corresponding dois used
	 * to create the model
	 */
	public List<HashMap<String, String>> cite;
	
	public String		documentation			= null;
	private String[] 	deepImageJTag			= {"deepImageJ"};
	public List<String>	infoTags				= Arrays.asList(deepImageJTag);
	public String		license					= null;
	public String		language				= "java";
	public String		framework				= "";
	public String		source					= null;
	public String		description				= null;
	public String		git_repo				= null;

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
	 * Checksum of the tf Bioimage model zoo file. Only useful if
	 * we use a Bioimage Zoo model that comes with a zipped model.
	 */
	public String tfSha256 = "";
	/*
	 * Checksum of the Pytorch scripts file. Only useful if
	 * there is a Pytorch model
	 */
	public String ptSha256 = "";
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
	public String selectedModelPath = "";
	
	public Parameters2(String raw) throws Exception {
		YamlParser yml = new YamlParser(raw);
		HashMap<String, Object> obj = yml.parseYaml();
		// Until every parameter is checked complete config is false
		completeConfig = false;

		Set<String> kk = obj.keySet();

		format_version = "" + obj.get("format_version");
		if (kk.contains("name")) {
			name = (String) obj.get("name");
		} else {
			completeConfig = false;
			return;
		}
		// Adapt to versions 0.3.2, 0.3.1 and 0.3.0 of the yaml file
		// 0.3.2 provides a list of dictionaries
		if (obj.get("authors") instanceof List &&  ((List<Object>) obj.get("authors")).get(0) instanceof HashMap) {
			author = (List<HashMap<String, String>>) obj.get("authors");
		// 0.3.0 and 0.3.1 provide a list of Strings
		} else if (obj.get("authors") instanceof List &&  ((List<Object>) obj.get("authors")).get(0) instanceof String) {
			List<String> auxList = (List<String>) obj.get("authors");
			author = new ArrayList<HashMap<String, String>>();
			for (String element : auxList) {
				HashMap<String, String> auxMap = new HashMap<String, String>();
				auxMap.put("name", element);
				auxMap.put("affiliation", null);
				auxMap.put("orcid", null);
				author.add(auxMap);
			}
			
		// Python Dij packager provides a String
		} else if (obj.get("authors") instanceof String) {
			String aux = "" + obj.get("authors");
			HashMap<String, String> auxMap = new HashMap<String, String>();
			auxMap.put("name", aux);
			auxMap.put("affiliation", "");
			auxMap.put("orcid", "");
			author = new ArrayList<HashMap<String, String>>();
			author.add(auxMap);
		// If nothing is recognised
		} else {
			HashMap<String, String> auxMap = new HashMap<String, String>();
			auxMap.put("name", "n/a");
			auxMap.put("affiliation", "");
			auxMap.put("orcid", "");
			author = new ArrayList<HashMap<String, String>>();
			author.add(auxMap);
		}
		timestamp = "" +  obj.get("timestamp");

		// Citation
		if (!(cite instanceof List))
				cite = null;
		else
			cite = (List<HashMap<String, String>>) obj.get("cite");
		
		if (cite == null) {
			cite = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> c = new HashMap<String, String>();
			c.put("text", "");
			c.put("doi", "");
			cite.add(c);
		}
		
		documentation = (String) "" + obj.get("documentation");
		license = (String) "" + obj.get("license");
		framework = (String) "" + obj.get("framework");
		git_repo = (String) "" + obj.get("git_repo");
		
		ArrayList<String> attachmentsAux = null;
		if (obj.get("attachments") instanceof ArrayList)
			attachmentsAux = (ArrayList<String>) obj.get("attachments");
		
		attachments = new ArrayList<String>();
		attachmentsNotIncluded = new ArrayList<String>();
		String defaultFlag = "Include here any plugin that might be required for pre- or post-processing";
		if (attachmentsAux != null) {
			for (String str : attachmentsAux) {
				if (new File(path2Model, str).isFile() && !str.contentEquals(""))
					attachments.add(new File(path2Model, str).getAbsolutePath());
				else if (!str.contentEquals(defaultFlag))
					attachmentsNotIncluded.add(str);
			}
		}
		
		HashMap<String, HashMap<String, Object>> weights = (HashMap<String, HashMap<String, Object>>) obj.get("weights");
		// Look for the valid weights tags
		Set<String> weightFormats = weights.keySet();
		boolean tf = false;
		boolean pt = false;
		for (String format : weightFormats) {
			if (format.equals("tensorflow_saved_model_bundle"))
				tf = true;
			else if (format.equals("pytorch_script"))
				pt = true;
		}
		
		if (tf && pt) {
			framework = "tensorflow/pytorch";
			ptSha256 = (String) "" + weights.get("pytorch_script").get("sha256");
			tfSha256 = (String) "" + weights.get("tensorflow_saved_model_bundle").get("sha256");
		} else if (tf) {
			framework = "tensorflow";
			tfSha256 = (String) "" + weights.get("tensorflow_saved_model_bundle").get("sha256");
		} else if (pt) {
			framework = "pytorch";
			ptSha256 = (String) "" + weights.get("pytorch_script").get("sha256");
		} else if (!tf && !pt) {
			completeConfig = false;
			// TODO return;
		}
		
		// Model metadata
		Map<String, Object> config = (Map<String, Object>) obj.get("config");
		Map<String, Object> deepimagej = (Map<String, Object>) config.get("deepimagej");
		pyramidalNetwork =  ((String) deepimagej.get("pyramidal_model")).contentEquals("true");
		allowPatching = ((String) deepimagej.get("allow_tiling")).contentEquals("true");
		// Model keys
		if (deepimagej.keySet().contains("model_keys") && deepimagej.get("model_keys") != null) {
			Map<String, Object> model_keys = (Map<String, Object>) deepimagej.get("model_keys");
			tag = (String) "" + model_keys.get("tensorflow_model_tag");
			graph = (String) "" + model_keys.get("tensorflow_siganture_def");
		}		
		
		
		List<Map<String, Object>> inputs = (List<Map<String, Object>>) obj.get("inputs");
		// Check that the previous version field is complete
		if (inputs == null || inputs.size() == 0) {
			fieldsMissing = new ArrayList<String>();
			fieldsMissing.add("Inputs are not defined correctly");
			completeConfig = false;
			return;
		}
		inputList = new ArrayList<DijTensor>();
		
		Map<String, Object> test_information = (Map<String, Object>) deepimagej.get("test_information");

		List<LinkedHashMap<String, Object>> input_information = new ArrayList <LinkedHashMap<String, Object>>();
		if (test_information.get("inputs") instanceof LinkedHashMap) {
			LinkedHashMap<String, Object> aux = (LinkedHashMap<String, Object>) test_information.get("inputs");
			input_information.add(aux);
		} else if (test_information.get("inputs") instanceof List){
			input_information = (List<LinkedHashMap<String, Object>>) test_information.get("inputs");
		}
		
		int tensorCounter = 0;
		try {
			for (Map<String, Object> inp : inputs) {
				DijTensor inpTensor = new DijTensor((String) "" + inp.get("name"));
				inpTensor.form = ((String) "" + inp.get("axes")).toUpperCase();
				inpTensor.dataType = (String) "" + inp.get("data_type");
				//TODO do we assume inputs in the yaml are always images?
				inpTensor.tensorType = "image";
				//TODO List<Object> auxDataRange = (ArrayList<Object>) inp.get("data_range");
				//TODO inpTensor.dataRange = castListToDoubleArray(auxDataRange);
				
				// Find by trial and error if the shape of the input is fixed or not
				Object objectShape = inp.get("shape");
				if (objectShape instanceof List<?>) {
					String shape = (String) objectShape;
					inpTensor.recommended_patch = castStringToIntArray(shape);
					inpTensor.tensor_shape = inpTensor.recommended_patch;
					inpTensor.minimum_size = castStringToIntArray(shape);
					inpTensor.step = new int[inpTensor.minimum_size.length];
					fixedInput = true;
				} else if (objectShape instanceof Map<?, ?>) {
					Map<String, Object> shape = (Map<String, Object>) objectShape;
					String auxMinimumSize = (String) shape.get("min");
					inpTensor.minimum_size = castStringToIntArray(auxMinimumSize);
					String auxStepSize = (String) shape.get("step");
					inpTensor.step = castStringToIntArray(auxStepSize);
					inpTensor.recommended_patch = new int[inpTensor.step.length];
					inpTensor.tensor_shape = new int[inpTensor.step.length];
					// Recreate the tensor shape of the model with the information
					// of the YAML
					for (int i = 0; i < inpTensor.step.length; i ++) {
						if (inpTensor.step[i] == 0) {
							inpTensor.tensor_shape[i] = inpTensor.minimum_size[i];
						} else {
							inpTensor.tensor_shape[i] = -1;
						}
					}
					fixedInput = false;
				}

				// Check that the output definition fields are complete
				if (inpTensor.form == null || inpTensor.dataType == null || inpTensor.minimum_size == null
						|| inpTensor.tensor_shape == null || inpTensor.step == null || inpTensor.recommended_patch == null) {
					completeConfig = false;
					return;
				}
				
				// Now find the test information of this tensor
				HashMap<String, Object> info = input_information.get(tensorCounter ++);
				try {
					inpTensor.exampleInput = (String) "" + info.get("name");
					inpTensor.inputTestSize =  (String) "" + info.get("size");
					Map<String, Object>  pixel_size =  (Map<String, Object>) info.get("pixel_size");
					inpTensor.inputPixelSizeX = (String) "" + pixel_size.get("x");
					inpTensor.inputPixelSizeY = (String) "" + pixel_size.get("y");
					inpTensor.inputPixelSizeZ = (String) "" + pixel_size.get("z");
				} catch (Exception ex) {
					inpTensor.exampleInput = (String) "";
					inpTensor.inputTestSize =  (String) "";
					Map<String, Object>  pixel_size =  (Map<String, Object>) info.get("pixel_size");
					inpTensor.inputPixelSizeX = (String) "";
					inpTensor.inputPixelSizeY = (String) "";
					inpTensor.inputPixelSizeZ = (String) "";
				}
				
				inputList.add(inpTensor);
			}
		} catch (Exception ex) {
			fieldsMissing = new ArrayList<String>();
			fieldsMissing.add("Inputs are not defined correctly");
			completeConfig = false;
			return;
		}

		List<Map<String, Object>> outputs = (List<Map<String, Object>>) obj.get("outputs");
		if (outputs == null || outputs.size() == 0) {
			fieldsMissing = new ArrayList<String>();
			fieldsMissing.add("outputs are not defined correctly");
			completeConfig = false;
			return;
		}
		try {
			outputList = new ArrayList<DijTensor>();
			
			for (Map<String, Object> out : outputs) {
				DijTensor outTensor = new DijTensor((String) out.get("name"));
				outTensor.form = (String) out.get("axes");
				outTensor.form = outTensor.form == null ? null : outTensor.form.toUpperCase();
				outTensor.tensorType = outTensor.form == null ? "list" : "image";
				if (outTensor.form == null || outTensor.form.contains("R") || (outTensor.form.length() <= 2 && (outTensor.form.contains("B") || outTensor.form.contains("C"))))
					outTensor.tensorType = "list";
				// TODO List auxDataRange = (List) out.get("data_range");
				// TODO outTensor.dataRange = castListToDoubleArray(auxDataRange);
				outTensor.dataType = (String) "" + out.get("data_type");
				if (outTensor.tensorType.contains("image") && !pyramidalNetwork) {
					String auxHalo = (String) out.get("halo");
					outTensor.halo = castStringToIntArray(auxHalo);
				} else if (outTensor.tensorType.contains("image")) {
					outTensor.halo = new int[outTensor.form.length()];
				}
				
	
				// Find by trial and error if the shape of the input is fixed or not
				Object objectShape = out.get("shape");
				if (objectShape instanceof List<?>) {
					String shape = (String) objectShape;
					outTensor.recommended_patch = castStringToIntArray(shape);
					outTensor.scale = new float[outTensor.recommended_patch.length];
					outTensor.offset = new int[outTensor.recommended_patch.length];
					if (pyramidalNetwork)
						outTensor.sizeOutputPyramid = outTensor.recommended_patch;
				} else if (objectShape instanceof HashMap<?,?>) {
					Map<String, Object> shape = (Map<String, Object>) objectShape;
					outTensor.referenceImage = (String) shape.get("reference_input");
					String auxScale = (String) shape.get("scale");
					outTensor.scale = castStringToFloatArray(auxScale);
					String auxOffset = (String) shape.get("offset");
					outTensor.offset = castStringToIntArray(auxOffset);
				} else {
					
				}
				
				// Check that the output definition fields are complete
				if ((outTensor.form == null && outTensor.tensorType.contentEquals("image")) 
						|| outTensor.dataType == null || outTensor.scale == null
						|| outTensor.offset == null) {
					completeConfig = false;
					return;
				}
				
				outputList.add(outTensor);
			}
		} catch(Exception ex) {
			fieldsMissing = new ArrayList<String>();
			fieldsMissing.add("Outputs are not defined correctly");
			completeConfig = false;
			return;
		}
		// Output test information
		List<HashMap<String, Object>> output_information = new ArrayList <HashMap<String, Object>>();
		if (test_information.get("outputs") instanceof HashMap) {
			HashMap<String, Object> aux = (HashMap<String, Object>) test_information.get("outputs");
			output_information.add(aux);
		} else if (test_information.get("outputs") instanceof List){
			output_information = (List<HashMap<String, Object>>) test_information.get("outputs");
		}
		
		savedOutputs = new ArrayList<HashMap<String, String>>();
		for (HashMap<String, Object> out : output_information) {
			HashMap<String, String> info = new LinkedHashMap<String, String>();
			String outName =  (String) "" + out.get("name");
			info.put("name", outName);
			String size =  (String) "" + out.get("size");
			info.put("size", size);
			String type = (String) "" + out.get("type");
			info.put("type", type);

			savedOutputs.add(info);
		}
		
		// Info about runtime and memory
		memoryPeak = (String) test_information.get("memory_peak") + "";
		runtime = (String) "" + test_information.get("runtime");

		
		// Get all the preprocessings available in the Yaml
		Map<String, Object> prediction = (Map<String, Object>) deepimagej.get("prediction");
		if (prediction == null)
			prediction = new HashMap<String, Object>();
		pre = new HashMap<String, String[]>();
		post = new HashMap<String, String[]>();
		Set<String> keys = prediction.keySet();
		for (String key : keys) {
			if (key.contains("preprocess")) {
				List<Map<String, Object>> preprocess = (List<Map<String, Object>>) prediction.get(key);
				// TODO convert into a list of processings
				String[] commands = new String[preprocess.size()];
				int processingCount = 0;
				for (Map<String, Object> processing : preprocess) {
					String spec = "" + processing.get("spec");
					if (spec != null && processing.containsKey("kwargs")) {
						commands[processingCount] = "" + processing.get("kwargs");
					} else if (spec != null && !processing.containsKey("kwargs") && spec.contains(".jar")) {
						int extensionPosition = spec.indexOf(".jar");
						if (extensionPosition != -1)
							commands[processingCount] = spec.substring(0, extensionPosition + 4);
					} else if (spec != null && !processing.containsKey("kwargs") && spec.contains(".class")) {
						int extensionPosition = spec.indexOf(".class");
						if (extensionPosition != -1)
							commands[processingCount] = spec.substring(0, extensionPosition + 6);
					} else if (spec == null) {
						commands = null;
					}
					
					processingCount ++;
				}
				pre.put(key, commands);
			}
			if (key.contains("postprocess")) {
				List<Map<String, Object>> postprocess = (List<Map<String, Object>>) prediction.get(key);
				// TODO convert into a list of processings
				String[] commands = new String[postprocess.size()];
				int processingCount = 0;
				for (Map<String, Object> processing : postprocess) {
					String spec = "" + processing.get("spec");
					if (spec != null && processing.containsKey("kwargs")) {
						commands[processingCount] = "" + processing.get("kwargs");
					} else if (spec != null && !processing.containsKey("kwargs") && spec.contains(".jar")) {
						int extensionPosition = spec.indexOf(".jar");
						if (extensionPosition != -1)
							commands[processingCount] = spec.substring(0, extensionPosition + 4);
					} else if (spec != null && !processing.containsKey("kwargs") && spec.contains(".class")) {
						int extensionPosition = spec.indexOf(".class");
						if (extensionPosition != -1)
							commands[processingCount] = spec.substring(0, extensionPosition + 6);
					} else if (spec == null) {
						commands = null;
					}
					
					processingCount ++;
				}
				post.put(key, commands);
			}
		}
		
		
		name = name != null ? (String) name : "n/a";
		documentation = documentation != null ? documentation : "n/a";
		format_version = format_version != null ? format_version : "n/a";
		license = license != null ? license : "n/a";
		memoryPeak = memoryPeak != null ? memoryPeak : "n/a";
		runtime = runtime != null ?  runtime : "n/a";
		tag = tag != null ? tag : "serve";
		graph = graph != null ? graph : "serving_default";
		completeConfig = true;
		
		
	}
	
	public static String[] castListToStringArray(List list) {
		String[] array = new String[list.size()];
		int c = 0;
		for (Object in : list) {
			array[c ++] = (String) in;
		}
		return array;
	}
	
	public static int[] castStringToIntArray(String list) {
		list = list.substring(1, list.length() - 1);
		String[] listArr = list.split(",");
		int[] array = new int[listArr.length];
		int c = 0;
		for (Object in : listArr) {
			array[c ++] = Integer.parseInt(in.toString().trim());
		}
		return array;
	}
	
	public static double[] castStringToDoubleArray(String list) {
		list = list.substring(1, list.length() - 1);
		String[] listArr = list.split(",");
		try {
			double[] array = new double[listArr.length];
			int c = 0;
			for (Object in : listArr) {
				array[c ++] = Double.parseDouble(in.toString());
			}
			return array;
		} catch (Exception ex) {
			return null;
		}
	}
	
	public static float[] castStringToFloatArray(String list) {
		list = list.substring(1, list.length() - 1);
		String[] listArr = list.split(",");
		try {
			float[] array = new float[listArr.length];
			int c = 0;
			for (Object in : listArr) {
				array[c ++] = Float.parseFloat(in.toString());
			}
			return array;
		} catch (ClassCastException ex) {
			return null;			
		}
	}
}