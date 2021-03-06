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

package deepimagej.processing;

import java.awt.Frame;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.leaningtech.client.Global;

import deepimagej.Parameters;
import deepimagej.Promise;
import deepimagej.exceptions.JavaProcessingError;
import deepimagej.tools.DijTensor;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.measure.ResultsTable;
import ij.text.TextWindow;

public class ProcessingBridge {
	private static  String macro = "";
	private static Object macroLock;
	private static Object macroRunLock;
	
	// TODO decide whether to allow or not more than 1 image input to the model
	public static HashMap<String, Object> runPreprocessing(ImagePlus im, Parameters params) throws JavaProcessingError {
		HashMap<String, Object> map = new HashMap<String, Object>();
		params.javaPreprocessingClass = new ArrayList<String>();
		// Assume that the image selected will result in the input image to the model
		// Assumes 'im' will be the input to the model
		map.put(params.inputList.get(0).name, im);
		if (params.firstPreprocessing != null && (params.firstPreprocessing.contains(".txt") || params.firstPreprocessing.contains(".ijm"))) {
			im = runPreprocessingMacro(im, params.firstPreprocessing, params.developer);
			map = manageInputs(map, false, params, im);
		}
		

		if (params.secondPreprocessing != null && (params.secondPreprocessing.contains(".txt") || params.secondPreprocessing.contains(".ijm"))) {
			im = runPreprocessingMacro(im, params.secondPreprocessing, params.developer);
			map = manageInputs(map, true,  params, im);
		} else if (params.secondPreprocessing == null && (params.firstPreprocessing == null || params.firstPreprocessing.contains(".txt") || params.firstPreprocessing.contains(".ijm"))) {
			map = manageInputs(map, true, params);
		} else if (params.secondPreprocessing == null && (params.firstPreprocessing.contains(".jar") || new File(params.firstPreprocessing).isDirectory())) {
			//TODO check if an input is missing. If it is missing try to recover it from the workspace.
		}
		return map;
	}
	
	private static HashMap<String, Object> manageInputs(HashMap<String, Object> map, boolean lastStep, Parameters params){
		 map = manageInputs(map, lastStep, params, null);
		 return map;
	}
	
	/*
	 * Updates the map containing the inputs. In principle, this is used only if there has not
	 * beeen Java processing before (Java processing should already output a map). 
	 * This method assumes that each model input has an ImageJ object associated. Except for
	 * the main image, where if it is not named correctly, assumes it is the originally referenced
	 * image (line 62).
	 */
	private static HashMap<String, Object> manageInputs(HashMap<String, Object> map, boolean lastStep, Parameters params, ImagePlus im) {
		for (DijTensor tensor : params.inputList) {
			if (tensor.tensorType == "image") {
				ImagePlus inputImage = WindowManager.getImage(tensor.name);
				if (inputImage != null) {
					map.put(tensor.name, inputImage);
		        } else if (im != null) {
					map.put(tensor.name, im);
		        }
			} else if (tensor.tensorType == "parameter") {
				Frame f = WindowManager.getFrame(tensor.name);
		        if (f!=null && (f instanceof TextWindow)) {
		        	 ResultsTable inputTable = ((TextWindow)f).getResultsTable();
					map.put(tensor.name, inputTable);
		        } else if (lastStep){
		        	IJ.error("There is no ResultsTable named: " + tensor.name + ".\n" +
		        			"There should be as it is one of the inputs required\n"
		        			+ "by the model.");
		        	return null;
		        }
			}
		}
		return map;
	}

	private static ImagePlus runPreprocessingMacro(ImagePlus img, String macroPath, boolean developer) {
		WindowManager.setTempCurrentImage(img);

		macro = "";
		macro = getMacroImJoy(macroPath);
		macroRunLock = new Object();
		Global.jsCall("callPlugin", "ImJoyModelRunner", "runIJMacro", macro, new Promise(){
			public void resolveString(String result){
				macroRunLock.notify();
			}
			public void resolveImagePlus(ImagePlus result){
				macroRunLock.notify();
			}
			public void reject(String error){
				IJ.error("Error during pre-processing: "+error);
				macroRunLock.notify();
			}
		});
		try {
			macroRunLock.wait();
		} catch (InterruptedException e) {
			IJ.error("Pre-processing macro could not be executed.");
			return null;
		}
		
		ImagePlus result = WindowManager.getCurrentImage();
		// If the macro opens the image, close it
		if (result.isVisible() && !developer)
			result.getWindow().dispose();
		return result;
	}
	
	/******************************************************************************************************************
	 * Method to run the wanted post-processing wanted on the images or tables 
	 * produced by the deep learning model
	 * @param params: parameters of the moel. It contains the path to the post-processing files
	 * @param map: hashmap containing all the outputs given by the model. The keys are the names 
	 * 	given by the model to each of the outputs. And the values are either ImagePlus or ResultsTable.
	 * @return map: map containing all the paths to the processing files
	 * @throws JavaProcessingError 
	 */
	public static HashMap<String, Object> runPostprocessing(Parameters params, HashMap<String, Object> map) throws JavaProcessingError {

		params.javaPostprocessingClass = new ArrayList<String>();
		
		if (params.firstPostprocessing != null && (params.firstPostprocessing.contains(".txt") || params.firstPostprocessing.contains(".ijm"))) {
			runPostprocessingMacro(params.firstPostprocessing);
			map = manageOutputs();
		}
		

		if (params.secondPostprocessing != null && (params.secondPostprocessing.contains(".txt") || params.secondPostprocessing.contains(".ijm"))) {
			runPostprocessingMacro(params.firstPreprocessing);
			map = manageOutputs();
		} else if (params.secondPreprocessing == null && (params.firstPostprocessing == null || params.firstPostprocessing.contains(".jar") || new File(params.firstPostprocessing).isDirectory())) {
			map = manageOutputs();
		}
		return map;
	}
	

	/***************************
	 * Method to run a macro processing routine over the outputs of the model. 
	 * @param macroPath: path to the macro file
	 * @return: last image processed by the file
	 */
	private static void runPostprocessingMacro(String macroPath) {
		// Initialise the macro to an empty string
		macro = "";
		macro = getMacroImJoy(macroPath);
		macroRunLock = new Object();
		Global.jsCall("callPlugin", "ImJoyModelRunner", "runIJMacro", macro, new Promise(){
			public void resolveString(String result){
				macroRunLock.notify();
			}
			public void resolveImagePlus(ImagePlus result){
				macroRunLock.notify();
			}
			public void reject(String error){
				IJ.error("Error during post-processing: "+error);
				macroRunLock.notify();
			}
		});
		try {
			macroRunLock.wait();
		} catch (InterruptedException e) {
			IJ.error("Post-processing macro could not be executed.");
		}
	}
	
	/**
	 * Get the content of the macro file using the ImJoy API
	 * @param macroFile: name of the macro file
	 * @return macro's content in a String
	 */
	private static String getMacroImJoy(String macroFile) {
		macroLock = new Object();
		// return a list of names (string)
		Global.jsCall("callPlugin", "ImJoyModelRunner", "getFile", macroFile, new Promise(){
			public void resolveString(String result){
				System.out.println("Macro file:\n" + result);
				macroLock.notify();
				macro = result;
			}
			public void resolveImagePlus(ImagePlus result){
				IJ.error("Cannot fetch the macro '" + macroFile + "' file specified in the model.yaml.");
				macroLock.notify();
			}
			public void reject(String error){
				IJ.error("Cannot fetch the macro '" + macroFile + "' file specified in the model.yaml: " + error);
				macroLock.notify();
			}
		});
		try {
			macroLock.wait();
		} catch (InterruptedException e) {
			IJ.error("Cannot fetch the macro '" + macroFile + "' file specified in the model.yaml.");
			System.out.println(e.toString());
		}
		return macro;
	}
	
	/**************************
	 * Method that puts all the images and results tables with their names
	 * in a hashmap.
	 * @return map: hashmap containing all the images and results tables.
	 */
	private static HashMap<String, Object> manageOutputs() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		Frame[] nonImageWindows = WindowManager.getNonImageWindows();
		String[] imageTitles = WindowManager.getImageTitles();
		for (String title : imageTitles) {
			map.put(title, WindowManager.getImage(title));
		}
		for (Frame f : nonImageWindows) {
	        if (f!=null && (f instanceof TextWindow)) {
	        	String tableTitle = f.getTitle();
	        	ResultsTable table = ((TextWindow)f).getResultsTable();
				map.put(tableTitle, table);
	        }
		}
		return map;
	}

}
