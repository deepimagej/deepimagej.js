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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import ij.IJ;

public class YAMLUtils {
	
	public static Map<String, Object> readConfig(String yamlFile) throws IOException {
		InputStream targetStream = new URL(yamlFile).openStream();
	    try {
			Yaml yaml = new Yaml();
			Map<String, Object> obj = yaml.load(targetStream);
			targetStream.close();
			
			return obj;
		} catch (FileNotFoundException e) {
			IJ.error("Invalid YAML file");
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	 * Method to write the output of the yaml file. The fields written
	 * depend on the type of network that we are defining.
	 */
	public static Map<String, Object> getOutput(DijTensor out, boolean pyramidal, boolean allowPatching){
		Map<String, Object> outputTensorMap = new LinkedHashMap<>();
		outputTensorMap.put("name", out.name);
		
		if (!pyramidal && out.tensorType.contains("image")) {
			outputTensorMap.put("axes", out.form.toLowerCase());
			outputTensorMap.put("data_type", "float32");
			outputTensorMap.put("data_range", Arrays.toString(out.dataRange));
			outputTensorMap.put("halo",  Arrays.toString(out.halo));
			Map<String, Object> shape = new LinkedHashMap<>();
			shape.put("reference_input", out.referenceImage);
			shape.put("scale", Arrays.toString(out.scale));
			shape.put("offset", Arrays.toString(out.offset));
			outputTensorMap.put("shape", shape);
			
		} else if (pyramidal && out.tensorType.contains("image")) {
			outputTensorMap.put("axes", out.form.toLowerCase());
			outputTensorMap.put("data_type", "float32");
			outputTensorMap.put("data_range", Arrays.toString(out.dataRange));
			outputTensorMap.put("shape", Arrays.toString(out.sizeOutputPyramid));
			
		}else if (out.tensorType.contains("list")) {
			outputTensorMap.put("axes", null);
			outputTensorMap.put("shape", Arrays.toString(out.tensor_shape));
			outputTensorMap.put("data_type", "float32");
			outputTensorMap.put("data_range", Arrays.toString(out.dataRange));
		}
		return outputTensorMap;
	}
}