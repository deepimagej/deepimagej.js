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

package deepimagej.tools;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import deepimagej.Parameters;
import ij.IJ;

public class YamlParser {
	
	private String rawYaml = "";
	private String backUpRawYaml = "";
	private HashMap<String, Object> yaml;
	String separator;
	
	/**
	 * Constructor that stores the String corresponding to the raw yaml
	 * @param yaml: String corresponding to the raw yaml
	 */
	public YamlParser(String yaml) {
		this.rawYaml = yaml;
		this.backUpRawYaml = yaml;
	}
	public static void main(String[] args) throws Exception {
		/*
		String raw = "\n"
				+ "format_version: 0.3.0\n" + 
				"name: M2Unet\n" + 
				"description: A light-weight Unet.\n" + 
				"date: 2019\n" + 
				"cite:\n" + 
				"authors:\n" + 
				"  - Wei Ouyang\n" + 
				"documentation: \n" + 
				"covers: []\n" + 
				"tags:\n" + 
				"  - unet\n" + 
				"  - deepimagej\n" + 
				"license: BSD 3\n" + 
				"language: Java\n" + 
				"framework: Tensorflow\n" + 
				"git_repo: \n" + 
				"weights:\n" + 
				"  tensorflow_protobuffer:\n" + 
				"    name: v1\n" + 
				"    source: https://zenodo.org/record/4244821/files/defcon_density_map_estimation_tf_model.zip?download=1\n" + 
				"    sha256: ea57590caefa41a493808420d5bc029d7b6c8ad8b1633d8feeb166a99d71f45d\n" + 
				"    test_input:\n" + 
				"      - ./exampleImage.tiff\n" + 
				"    test_output:\n" + 
				"      - ./resultImage.tiff\n" + 
				"inputs:\n" + 
				"  - name: raw\n" + 
				"    axes: byxc\n" + 
				"    data_type: float32\n" + 
				"    data_range: [-inf, inf]\n" + 
				"    shape:\n" + 
				"      min: [1, 1, 1, 1]\n" + 
				"      step: [0, 1, 1, 0]\n" + 
				"    preprocessing:\n" + 
				"      name: min_max_normalization\n" + 
				"      kwargs:\n" + 
				"        mode: fixed\n" + 
				"        axes: xy\n" + 
				"        min: 0.0\n" + 
				"        max: 65535.0\n" + 
				"outputs:\n" + 
				"  - name: segmentation\n" + 
				"    axes: byxc\n" + 
				"    data_type: float32\n" + 
				"    data_range: [0, 1]\n" + 
				"    halo: [0, 10, 10, 0]\n" + 
				"    shape:\n" + 
				"      reference_input: raw\n" + 
				"      scale: [1, 1, 1, 1]\n" + 
				"      offset: [0, 0, 0, 0]\n" + 
				"config:\n" + 
				"  deepimagej:\n" + 
				"    pyramidal_model: false\n" + 
				"    allow_tiling: true\n" + 
				"    model_keys:\n" + 
				"      model_tag: tf.saved_model.tag_constants.SERVING\n" + 
				"      signature_definition: tf.saved_model.signature_constants.DEFAULT_SERVING_SIGNATURE_DEF_KEY\n" + 
				"    test_information:\n" + 
				"      inputs:\n" + 
				"        - name: exampleImage.tiff\n" + 
				"          size: 64 x 64 x 1 x 1\n" + 
				"          pixel_size:\n" + 
				"            x: 1 pixel\n" + 
				"            y: 1 pixel\n" + 
				"            z: 1.0 pixel\n" + 
				"      outputs:\n" + 
				"        - name: resultImage.tiff\n" + 
				"          type: image\n" + 
				"          size: 64 x 64 x 1 x 1\n" + 
				"      memory_peak: 62.9 Mb\n" + 
				"      runtime: 0.1 s\n" + 
				"    prediction:\n" + 
				"      preprocess:\n" + 
				"      -   spec: ij.IJ::runMacroFile\n" + 
				"          kwargs: preprocessing.txt\n" + 
				"      postprocess:\n" + 
				"      -   spec: ij.IJ::runMacroFile\n" + 
				"          kwargs: postprocessing.txt";
		System.out.print(raw);
		*/
		String yamlPath = "C:\\Users\\angel\\OneDrive\\Documentos\\pasteur\\git"
				+ "\\model-runner-java\\models\\B. Sutilist bacteria segmentation -"
				+ " Widefield microscopy - 2D UNet_08092023_182317\\rdf.yaml";
		yamlPath = "C:\\Users\\angel\\OneDrive\\Documentos\\pasteur\\git\\model"
				+ "-runner-java\\models\\StarDist H&E Nuclei Segmentation_06092023_020924\\rdf.yaml";
		Path path = Paths.get(yamlPath);

        List<String> yamlLines = Files.readAllLines(path);
        
        String raw = String.join(System.lineSeparator(), yamlLines);
		Parameters pp = new Parameters(raw);
		YamlParser yml = new YamlParser(raw);
		HashMap<String, Object> obj = yml.parseYaml();
		System.out.print("jej");
	}

	/**
	 * Obtain the yaml dictionary from a String
	 * @return yaml dictionary
	 * @throws Exception 
	 */
	public HashMap<String, Object> parseYaml() throws Exception {
		rawYaml = backUpRawYaml;
		yaml = new HashMap<String, Object>();
		// Find out if the file was written using 
		// Windows line end (\r\n) or Linux (\n)
		if (findOccurences(rawYaml, "\r\n") == findOccurences(rawYaml, "\n") && findOccurences(rawYaml, "\\n") == 0 && findOccurences(rawYaml, "\\r\n") == 0) {
			separator = "\r\n";
		} else {
			separator = "\n";
		}
		// REmove empty lines
		rawYaml= rawYaml.replaceAll("(?m)^[ \\t]*\\r?\\n","");
		if (!rawYaml.endsWith(separator))
			rawYaml += separator;
		if (rawYaml == null || rawYaml.contentEquals(""))
			return null;
		while (rawYaml.indexOf(separator) != -1) {
			String line = rawYaml.substring(0, rawYaml.indexOf(separator));
			// Remove from the rawYaml String the part that as already been read
			rawYaml = rawYaml.substring(rawYaml.indexOf(separator) + separator.length());
			// Ignore comments
			if (line.trim().startsWith("#") || line.equals(""))
				continue;
			
			// Split the line into key and value
			// Array with key and value
			int colonInd = indToSplitKeyValue(line);
			if (colonInd == -1)
				throw new Exception();
			String[] lineArr = new String[] {line.substring(0, colonInd), line.substring(colonInd + 1).trim()};
			/// Key of a field in the yaml
			String key = lineArr[0];
			// Value
			String value = lineArr[1];
			
			// The values can be either Strings, Arrays or HashMaps.
			// Find out first whether it is a String or not
			// If the value was not a String, the same line would be empty
			boolean sameSpaces = (getSpacesAtTheBegging(line) == getSpacesAtTheBegging(rawYaml));
			boolean startsWithDash = rawYaml.startsWith("-");
			if (value.trim().contentEquals("") && (!sameSpaces || startsWithDash)) {
				String valueType = isValueDictionaryOrArray();
				if (valueType.contentEquals("hashmap")) {
					yaml.put(key.trim(), getHashMap());
				} else if (valueType.contentEquals("array")) {
					yaml.put(key.trim(), getArray());
				}
			} else if (value.trim().contentEquals("")){
				// Put the value as null, this is an error in the yaml
				yaml.put(key.trim(), null);
			} else {
				// Put the value and key in the yaml HashMap
				yaml.put(key.trim(), value.trim());
			}
		}
		return yaml;
	}
	
	/**
	 * Create array from the yaml
	 * @return the array of values from the yaml
	 * @throws Exception 
	 */
	public ArrayList<Object> getArray() throws Exception {
		ArrayList<Object> arr = new ArrayList<Object>();
		boolean sameSpaces = true;
		int prevSpaces = -1;
		while (rawYaml.indexOf(separator) != -1 && sameSpaces) {
			String line = rawYaml.substring(0, rawYaml.indexOf(separator));
			// Ignore the comments
			if (line.trim().startsWith("#")) {
				// Remove from the rawYaml String the part that as already been read
				if (rawYaml.length() <= (rawYaml.indexOf(separator) + separator.length()))
					rawYaml = "";
				else
					rawYaml = rawYaml.substring(rawYaml.indexOf(separator) + separator.length());
				continue;
			}
			
			// Get number of blank spaces at the beginning of the line
			int nSpaces = getSpacesAtTheBegging(line);
			if (prevSpaces != -1)
				sameSpaces = (nSpaces == prevSpaces);
			// If the spaces at the beginning change, stop the loop
			if (!sameSpaces || !line.trim().startsWith("-"))
				break;
			prevSpaces = nSpaces;
			// Remove from the rawYaml String the part that as already been read
			if (rawYaml.length() <= (rawYaml.indexOf(separator) + separator.length()))
				rawYaml = "";
			else
				rawYaml = rawYaml.substring(rawYaml.indexOf(separator) + separator.length());
			// Ignore the spaces in the line, and the "-" that is always at the beginning of lists
			// Also find out how many spaces are between "-" and the beginning of the line
			line = line.trim();
			int ogLen = line.length();
			line = line.substring(1).trim();
			int nLen = line.length();
			// Split the line into key and value
			// Array with key and value
			int colonInd = indToSplitKeyValue(line);
			if (line.length() > 0 && line.substring(0, 1).contentEquals("- ")) {
				throw new Exception("Yaml file does not allow an array inside another array");
			} else if (colonInd != -1) {
				String key = line.substring(0, colonInd).trim();
				String value = line.substring(colonInd + 1).trim();
				if (!value.contentEquals("")) {
					HashMap<String, Object> mapVal = getHashMap(prevSpaces + ogLen - nLen);
					mapVal.put(key, value);
					arr.add(mapVal);
				} else if(getSpacesAtTheBegging(rawYaml) == (prevSpaces + ogLen - nLen)) {
					HashMap<String, Object> mapVal = getHashMap(prevSpaces + ogLen - nLen);
					mapVal.put(key, null);
					arr.add(mapVal);
				} else {
					HashMap<String, Object>mapVal1 = getHashMap();
					HashMap<String, Object> mapVal2 = getHashMap(prevSpaces + ogLen - nLen);
					mapVal2.put(key, mapVal1);
					arr.add(mapVal2);
				}
			} else if(line.length() == 0 && colonInd == -1) {
				arr.add(null);
			} else if(colonInd == -1) {
				arr.add(line);
			}
		}
		return arr;
	}
	
	/**
	 * Create HashMap from the yaml
	 * @return the HashMap of values from the yaml
	 * @throws Exception 
	 */
	public HashMap<String, Object> getHashMap() throws Exception {
		return getHashMap(-1);
	}
	
	/**
	 * Create HashMap from the yaml
	 * @return the HashMap of values from the yaml
	 * @throws Exception 
	 */
	public HashMap<String, Object> getHashMap(int prevSpaces) throws Exception {
		HashMap<String, Object> dict = new HashMap<String, Object>();
		boolean sameSpaces = true;
		while (rawYaml.indexOf(separator) != -1 && sameSpaces) {
			String line = rawYaml.substring(0, rawYaml.indexOf(separator));
			
			// Ignore the comments
			if (line.trim().startsWith("#")) {
				// Remove from the rawYaml String the part that as already been read
				if (rawYaml.length() <= (rawYaml.indexOf(separator) + separator.length()))
					rawYaml = "";
				else
					rawYaml = rawYaml.substring(rawYaml.indexOf(separator) + separator.length());
				continue;
			}
			
			// Get number of blank spaces at the beginning of the line
			int nSpaces = getSpacesAtTheBegging(line);
			if (prevSpaces != -1)
				sameSpaces = (nSpaces == prevSpaces);
			// If the spaces at the beginning change, stop the loop
			if (!sameSpaces)
				break;
			prevSpaces = nSpaces;
			// Remove from the rawYaml String the part that as already been read
			if (rawYaml.length() <= (rawYaml.indexOf(separator) + separator.length()))
				rawYaml = "";
			else
				rawYaml = rawYaml.substring(rawYaml.indexOf(separator) + separator.length());
			// Ignore the spaces in the line
			line = line.trim();
			// Split the line into key and value
			// Array with key and value
			int colonInd = indToSplitKeyValue(line);
			if (colonInd == -1)
				throw new Exception();
			String[] lineArr = new String[] {line.substring(0, colonInd), line.substring(colonInd + 1).trim()};
			/// Key of a field in the yaml
			String key = lineArr[0];
			// Value
			String value = lineArr[1];
			
			// The values can be either Strings, Arrays or HashMaps.
			// Find out first whether it is a String or not
			// If the value was not a String, the same line would be empty
			int nextSpaces = getSpacesAtTheBegging(rawYaml);
			boolean nextStartWithDash = rawYaml.trim().startsWith("-");
			if (value.trim().contentEquals("") && 
					(nextSpaces > prevSpaces || (nextSpaces == prevSpaces && nextStartWithDash))) {
				String valueType = isValueDictionaryOrArray();
				if (valueType.contentEquals("hashmap")) {
					dict.put(key.trim(), getHashMap());
				} else if (valueType.contentEquals("array")) {
					dict.put(key.trim(), getArray());
				}
			} else if(value.trim().contentEquals("")) {
				// Put the value and key in the yaml HashMap
				dict.put(key.trim(), null);
			} else {
				// Put the value and key in the yaml HashMap
				dict.put(key.trim(), value.trim());
			}
		}
		return dict;
	}
	
	/**
	 * Returns whether the value in the yaml is a hashmap or string
	 * @return "array" if the value is an array or "hashmap" if it is a hashmap
	 */
	public String isValueDictionaryOrArray() {
		// If the value is an array, the next line will start with "-"
		String line = rawYaml.substring(0, rawYaml.indexOf(separator));
		String type = "hashmap";
		if (line.trim().startsWith("-")) {
			type = "array";
		}
		return type;
	}
	
	/**
	 * Get the number of spaces at the beginning of a line
	 * @param line: text to evaluate
	 * @return number of spaces
	 */
	public static int getSpacesAtTheBegging(String line) {
		boolean keepGoing = true;
		String spaces = "";
		while (keepGoing) {
			spaces += " ";
			keepGoing = line.startsWith(spaces);
		}
		return (spaces.length() - 1);
	}

	/**
	 * Finds th colon character ':', which indicates where to split key and value
	 * @param line
	 * @return index of the colon in the line
	 */
	public static int indToSplitKeyValue(String line) {
		int ind = line.indexOf(":");
		if (ind == -1)
			return ind;
		// Find out whether the ":" character corresponds to a String,
		// for example in the case --> title: "Best players ever: Messi and Ronaldo"
		// one colon separates key and value and the other one is inside the value
		int quoteInd = line.indexOf("\"");
		if (quoteInd != -1 && quoteInd < ind) {
			int secondQuote =  line.indexOf("\"", quoteInd);
			if (secondQuote > ind) 
				ind = -1;
		}
		return ind;
	}
	
	/**
	 * Find number of occurences of substring inside a String
	 * @param s: string that contains substring
	 * @param sub: substring to be counted in the string
	 * @return number of occurences
	 */
	public static int findOccurences(String s, String sub) {
		String temp = s.replace(sub, "");
		int occ = (s.length() - temp.length()) / sub.length();
		return occ;
	}

}
