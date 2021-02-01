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

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import deepimagej.Constants;
import deepimagej.DeepImageJ;
import deepimagej.Promise;
import deepimagej.RunnerTf;
import deepimagej.exceptions.JavaProcessingError;
import deepimagej.exceptions.MacrosError;
import deepimagej.processing.ProcessingBridge;
import deepimagej.tools.ArrayOperations;
import deepimagej.tools.DijTensor;
import deepimagej.tools.Index;
import deepimagej.tools.Log;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;


import com.leaningtech.client.Global;



public class DeepImageJ_Run implements PlugIn, ItemListener {

	private TextArea					info		= new TextArea("Information on the model", 10, 58, TextArea.SCROLLBARS_BOTH);
	private Choice[]					choices		= new Choice[5];
	private TextField[]	    			texts		= new TextField[2];
	private Label[]						labels		= new Label[8];
	private String[]					processingFile = new String[2];
	private Log							log			= new Log();
	private int[]						patch;
	private DeepImageJ					dp			= null;
	//private HashMap<String, String>		fullnames	= new HashMap<String, String>();

	private String						rawYaml 	= "";
	private String[]					modelList;
	private GenericDialog dlg;
	private static Object modelLock;

	@Override
	public void run(String arg) {

		ImagePlus imp = WindowManager.getTempCurrentImage();
		if (imp == null) {
			imp = WindowManager.getCurrentImage();
		}
		if (imp == null) {
			IJ.error("Please open an image.");
			return;
		}
		
		info.setEditable(false);

		Panel panel = new Panel();
		panel.setLayout(new BorderLayout());
		panel.add(info, BorderLayout.CENTER);

		dlg = new GenericDialog("DeepImageJ Run [" + Constants.version + "]");
		
		modelLock = new Object();
		// return a list of names (string)
		Global.jsCall("callPlugin", "ImJoyModelRunner", "getModels", new Promise(){
			public void resolveString(String result){
				modelList = result.split(",");
				System.out.println("model list" + result);
				modelLock.notify();
			}
			public void resolveImagePlus(ImagePlus result){
				modelLock.notify();
			}
			public void reject(String error){
				IJ.error("Cannot fetch list of models from Bioimage Model Zoo, error: "+error);
				modelLock.notify();
			}
		});
		try {
			modelLock.wait();
		} catch (InterruptedException e) {
			// TODO: throw the error
			System.out.println(e.toString());
		}

		String[] items = new String[modelList.length + 1];
		items[0] = "<select a model from this list>";
		
		for (int i = 0; i < modelList.length; i++)
			items[i + 1] = modelList[i];
		dlg.addChoice("Model", items, items[0]);
		dlg.addChoice("Format", new String[]         { "-----------------Select format-----------------" }, "-----------------Select format-----------------");
		dlg.addChoice("Preprocessing ", new String[] { "-----------Select preprocessing----------- " }, "-----------Select preprocessing----------- ");
		dlg.addChoice("Postprocessing", new String[] { "-----------Select postprocessing----------" }, "-----------Select postprocessing----------");
		
		dlg.addStringField("Axes order", "", 30);
		dlg.addStringField("Tile size", "", 30);
		
		dlg.addChoice("Logging", new String[] { "mute", "normal                                                       ", "verbose", "debug" }, "normal                                                       ");
		
		dlg.addHelp(Constants.url);
		dlg.addPanel(panel);
		String msg = "Note: the output of a deep learning model strongly depends on the\n"
			+ "data and the conditions of the training process. A pre-trained model\n"
			+ "may require re-training. Please, check the documentation of this\n"
			+ "model to get user guidelines: Help button.";
		
		Font font = new Font("Helvetica", Font.BOLD, 12);
		dlg.addMessage(msg, font, Color.BLACK);
		
		
		int countChoice = 0;
		int countLabels = 0;
		int countTxt = 0;
		for (Component c : dlg.getComponents()) {
			if (c instanceof Choice) {
				Choice choice = (Choice) c;
				if (countChoice == 0)
					choice.addItemListener(this);
				choices[countChoice++] = choice;
			}
			if (c instanceof TextField) {
				texts[countTxt ++] = (TextField) c;
			}
			if (c instanceof Label && ((Label) c).getText().trim().length() > 1) {
				labels[countLabels++] = (Label) c;
			}
		}
		texts[0].setEditable(false);
		texts[1].setEditable(false);
		
		info.setCaretPosition(0);
		info.setText("");
		info.append("Using Tensorflow Javascript 1.15.0" + ".\n");
		info.append("<Please select a model>\n");
		
		dlg.showDialog();
		if (dlg.wasCanceled())
			return;
		// This is used for the macro, as in the macro, there is no selection from the list
		String fullname = dlg.getNextChoice();
		// The index is the method that is going to be used normally to select a model.
		// The plugin looks at the index of the selection of the user and retrieves the
		// directory associated with it. With this, it allows to have the same model with
		// different configurations in different folders.
		String index = Integer.toString(choices[0].getSelectedIndex());
		// If we are running from a macro, the user does not change the model selecting
		// it from the list. Then the selection is 0, which yields an error. So the index
		// has to be selected again 
		if (index.equals("0") == true) {
			index = Integer.toString(Index.indexOf(items, fullname));
		}
		if (index.equals("-1") || index.equals("0")) {
			IJ.error("Select a valid model.");
		}

		if (dp == null) {
			IJ.error("No model selected");
			return;
		}
		
		String format = (String) choices[1].getSelectedItem();
		dp.params.framework = format.contains("pytorch") ? "Pytorch" : "Tensorflow";

		processingFile[0] = (String) choices[2].getSelectedItem();
		processingFile[1] = (String) choices[3].getSelectedItem();
		
		info.setText("");
		info.setCaretPosition(0);
		info.append("Loading model. Please wait...\n");


		dp.params.firstPreprocessing = null;
		dp.params.secondPreprocessing = null;
		dp.params.firstPostprocessing = null;
		dp.params.secondPostprocessing = null;
		/*
		if (!processingFile[0].equals("no preprocessing")) {
			if (dp.params.pre.get(processingFile[0]) != null && dp.params.pre.get(processingFile[0]).length > 0) {
				dp.params.firstPreprocessing = dp.getPath() + File.separator + dp.params.pre.get(processingFile[0])[0];
			}
			if(dp.params.pre.get(processingFile[0]) != null && dp.params.pre.get(processingFile[0]).length > 1) {
				dp.params.secondPreprocessing = dp.getPath() + File.separator + dp.params.pre.get(processingFile[0])[1];
			}
		}
		if (!processingFile[1].equals("no postprocessing")) {
			if (dp.params.post.get(processingFile[1]) != null && dp.params.post.get(processingFile[1]).length > 0) {
				dp.params.firstPostprocessing = dp.getPath() + File.separator + dp.params.post.get(processingFile[1])[0];
			}
			if(dp.params.post.get(processingFile[1]) != null && dp.params.post.get(processingFile[1]).length > 1) {
				dp.params.secondPostprocessing = dp.getPath() + File.separator + dp.params.post.get(processingFile[1])[1];
			}
		}
		*/
		String tensorForm = dp.params.inputList.get(0).form;
		int[] tensorMin = dp.params.inputList.get(0).minimum_size;
		int[] min = DijTensor.getWorkingDimValues(tensorForm, tensorMin); 
		int[] tensorStep = dp.params.inputList.get(0).step;
		int[] step = DijTensor.getWorkingDimValues(tensorForm, tensorStep); 
		String[] dims = DijTensor.getWorkingDims(tensorForm);

		
		patch = ArrayOperations.getPatchSize(dims, dp.params.inputList.get(0).form, texts[1].getText(), texts[1].isEditable());
		if (patch == null) {
			IJ.error("Please, introduce the patch size as integers separated by commas.\n"
					+ "For the axes order 'Y,X,C' with:\n"
					+ "Y=256, X=256 and C=1, we need to introduce:\n"
					+ "'256,256,1'\n"
					+ "Note: the key 'auto' can only be used by the plugin.");
			run("");
			return;
		}
		int level = dlg.getNextChoiceIndex();
		log.setLevel(level);
		log.reset();

		for (int i = 0; i < patch.length; i ++) {
			int p = 0 ;
			switch (tensorForm.split("")[i]) {
				case "B":
					p = 1;
					break;
				case "Y":
					p = imp.getHeight();
					break;
				case "X":
					p = imp.getWidth();
					break;
				case "Z":
					p = imp.getNSlices();
					break;
				case "C":
					p = imp.getNChannels();
					break;
			}
			if (p * 3 < patch[i]) {
				String errMsg = "Error: Tiles cannot be bigger than 3 times the image at any dimension\n";
				errMsg += " - X = " + imp.getWidth() + ", maximum tile size at X = " + (imp.getWidth() * 3 - 1) + "\n";
				errMsg += " - Y = " + imp.getHeight() + ", maximum tile size at Y = " + (imp.getHeight() * 3 - 1) + "\n";
				if (tensorForm.contains("C"))
					errMsg += " - C = " + imp.getNChannels() + ", maximum tile size at C = " + (imp.getNChannels() * 3 - 1) + "\n";
				if (tensorForm.contains("Z"))
					errMsg += " - Z = " + imp.getNSlices() + ", maximum tile size at Z = " + (imp.getNSlices() * 3 - 1) + "\n";
				IJ.error(errMsg);
				run("");
				return;
			}
		}
		for (DijTensor inp: dp.params.inputList) {
			for (int i = 0; i < min.length; i ++) {
				if (inp.step[i] != 0 && (patch[i] - inp.minimum_size[i]) % inp.step[i] != 0 && patch[i] != -1 && dp.params.allowPatching) {
					int approxTileSize = ((patch[i] - inp.minimum_size[i]) / inp.step[i]) * inp.step[i] + inp.minimum_size[i];
					IJ.error("Tile size at dim: " + dims[i] + " should be product of:\n  " + min[i] +
							" + " + step[i] + "*N, where N can be any positive integer.\n"
								+ "The immediately smaller valid tile size is " + approxTileSize);
					run("");
					return;
				} else if (inp.step[i] == 0 && patch[i] != inp.minimum_size[i]) {
					IJ.error("Patch size at dim: " + dims[i] + " should be " + min[i]);
					run("");
					return;
				}
			}
		}
		// TODO generalise for several image inputs
		dp.params.inputList.get(0).recommended_patch = patch;
		
		String selecteModel = choices[0].getSelectedItem();
		calculateImage(imp, selecteModel);
		
		// Free memory allocated by the plugin 
		this.dp = null;
		imp = null;
		this.dp = null;
		imp = null;
		System.gc();
		
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == choices[0]) {
			info.setText("");
			String modelName = choices[0].getSelectedItem().trim();
			if (modelName.contentEquals("<select a model from this list>")) {
				setGUIOriginalParameters();
				return;
			}
			rawYaml = null;
			Global.jsCall("callPlugin", "ImJoyModelRunner", "getModelInfo", modelName,  new Promise(){
				public void resolveString(String result){
					rawYaml = result;
					System.out.println(rawYaml);
					dp = null;
					dp = DeepImageJ.ImjoyYaml2DijYaml(rawYaml);
					
					if (dp == null) {
						setGUIOriginalParameters();
						return;
					}

					if (dp.params.framework.equals("Tensorflow/Pytorch")) {
						choices[1].removeAll();
						choices[1].addItem("-----------------Select format-----------------");
						choices[1].addItem("tensorflow_saved_model_bundle");
						choices[1].addItem("pytorch_script");
					} else if (dp.params.framework.equals("Pytorch")) {
						choices[1].removeAll();
						choices[1].addItem("pytorch_script");
					} else if (dp.params.framework.equals("Tensorflow")) {
						choices[1].removeAll();
						choices[1].addItem("tensorflow_saved_model_bundle");
					}

					info.setCaretPosition(0);
					info.append("Loading model info. Please wait...\n");

					choices[2].removeAll();
					choices[3].removeAll();
					Set<String> preKeys = dp.params.pre.keySet();
					Set<String> postKeys = dp.params.post.keySet();
					for (String p : preKeys) {
						if (dp.params.pre.get(p) != null)
							choices[2].addItem(Arrays.toString(dp.params.pre.get(p)));
					}
					if (choices[2].getItemCount() == 0)
						choices[2].addItem("no preprocessing");
					
					for (String p : postKeys) {
						if (dp.params.post.get(p) != null)
							choices[3].addItem(Arrays.toString(dp.params.post.get(p)));
					}
					choices[3].addItem("no postprocessing");
					// Get basic information about the input from the yaml
					String tensorForm = dp.params.inputList.get(0).form;
					// Patch size if the input size is fixed, all 0s if it is not
					int[] tensorPatch = dp.params.inputList.get(0).recommended_patch;
					// Minimum size if it is not fixed, 0s if it is
					int[] tensorMin = dp.params.inputList.get(0).minimum_size;
					// Step if the size is not fixed, 0s if it is
					int[] tensorStep = dp.params.inputList.get(0).step;
					int[] haloSize = ArrayOperations.findTotalPadding(dp.params.inputList.get(0), dp.params.outputList, dp.params.pyramidalNetwork);
					int[] dimValue = DijTensor.getWorkingDimValues(tensorForm, tensorPatch); 
					int[] min = DijTensor.getWorkingDimValues(tensorForm, tensorMin); 
					int[] step = DijTensor.getWorkingDimValues(tensorForm, tensorStep); 
					int[] haloVals = DijTensor.getWorkingDimValues(tensorForm, haloSize); 
					String[] dim = DijTensor.getWorkingDims(tensorForm);
					
					HashMap<String, String> letterDefinition = new HashMap<String, String>();
					letterDefinition.put("X", "width");
					letterDefinition.put("Y", "height");
					letterDefinition.put("C", "channels");
					letterDefinition.put("Z", "depth");

					info.setText("");
					info.setCaretPosition(0);
					info.append("\n");
					info.append("SELECTED MODEL: " + dp.params.name.toUpperCase());
					info.append("\n");
					info.append("\n");
					info.append("---- TILING SPECIFICATIONS ----\n");
					String infoString = "";
					for (String dd : dim)
						infoString += dd + ": " + letterDefinition.get(dd) + ", ";
					infoString = infoString.substring(0, infoString.length() - 2);
					info.append(infoString + "\n");
					info.append("  - minimum_size: ");
					String minString = "";
					for (int i = 0; i < dim.length; i ++)
						minString += dim[i] + "=" + min[i] + ", ";
					minString = minString.substring(0, minString.length() - 2);
					info.append(minString + "\n");
					info.append("  - step: ");
					String stepString = "";
					for (int i = 0; i < dim.length; i ++)
						stepString += dim[i] + "=" + step[i] + ", ";
					stepString = stepString.substring(0, stepString.length() - 2);
					info.append(stepString + "\n");
					info.append("\n");
					info.append("Each dimension is calculated as:\n");
					info.append("  - tile_size = minimum_size + step * n, where n is any positive integer\n");
					String optimalPatch = ArrayOperations.optimalPatch(dimValue, haloVals, dim, step, min, dp.params.allowPatching);
					info.append("\n");
					info.append("Default tile_size for this model: " + optimalPatch + "\n");
					info.append("\n");
					info.setEditable(false);

					dp.writeParameters(info);
					info.setCaretPosition(0);
					
					String axesAux = "";
					for (String dd : dim) {axesAux += dd + ",";}
					texts[0].setText(axesAux.substring(0, axesAux.length() - 1));
					texts[0].setEditable(false);
					
					texts[1].setText(optimalPatch);
					int auxFixed = 0;
					for (int ss : step)
						auxFixed += ss;

					texts[1].setEditable(true);
					if (!dp.params.allowPatching || dp.params.pyramidalNetwork || auxFixed == 0) {
						texts[1].setEditable(false);
					}
					dlg.getButtons()[0].setEnabled(true);
				}
				public void resolveImagePlus(ImagePlus result){
					rawYaml = "";
				}
				public void reject(String error){
					IJ.error("Unable to fetch the model yaml from the Bioimage Zoo, error:" + error);
					rawYaml = "";
				}
			});
		}
	}

	
	public void calculateImage(ImagePlus inp, String modelName) {
		// Convert RGB image into RGB stack 
		ImageWindow windToClose = inp.getWindow();
		windToClose.dispose();
		ImagePlus aux = ij.plugin.CompositeConverter.makeComposite(inp);
		inp = aux == null ? inp : aux;
		windToClose.setImage(inp);
		windToClose.setVisible(true);
		
		dp.params.inputList.get(0).recommended_patch = patch;
		int runStage = 0;
		// Create parallel process for calculating the image
		try {
			ImagePlus im = inp.duplicate();
			String correctTitle = inp.getTitle();
			im.setTitle("tmp_" + correctTitle);
			windToClose = inp.getWindow();
			windToClose.dispose();
			
			WindowManager.setTempCurrentImage(inp);
			log.print("start preprocessing");
			HashMap<String, Object> inputsMap = ProcessingBridge.runPreprocessing(inp, dp.params);
			im.setTitle(correctTitle);
			runStage ++;
			if (inputsMap.keySet().size() == 0)
				throw new Exception();
			// Show the image
			im.show();
			WindowManager.setTempCurrentImage(null);
			log.print("end preprocessing");
			log.print("start runner");
			HashMap<String, Object> output = null;
			if (dp.params.framework.equals("Tensorflow")) {
				RunnerTf runner = new RunnerTf(dp, inputsMap, modelName, log);
				output = runner.call();
			}
			
			inp.changes = false;
			inp.close();
			if (output == null) 
				throw new Exception();
			runStage ++;
			output = ProcessingBridge.runPostprocessing(dp.params, output);

			// Print the outputs of the postprocessing
			// Retrieve the opened windows and compare them to what the model has outputed
			// Display only what has not already been displayed

			String[] finalFrames = WindowManager.getNonImageTitles();
			String[] finalImages = WindowManager.getImageTitles();
			ArrayOperations.displayMissingOutputs(finalImages, finalFrames, output);
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		} catch(MacrosError ex) {
			if (runStage == 0) {
				IJ.error("Error during Macro preprocessing.");
			} else if (runStage == 2) {
				IJ.error("Error during Macro postprocessing.");
			}
			return;
		
		} catch (JavaProcessingError e) {
			if (runStage == 0) {
				IJ.error("Error during Java preprocessing.");
			} else if (runStage == 2) {
				IJ.error("Error during Java postprocessing.");
			}
			return;
		} catch (Exception ex) {
			ex.printStackTrace();
			IJ.log("Exception " + ex.toString());
			for (StackTraceElement ste : ex.getStackTrace()) {
				IJ.log(ste.getClassName());
				IJ.log(ste.getMethodName());
				IJ.log("line:" + ste.getLineNumber());
			}
			IJ.log("Exception " + ex.getMessage());
			if (runStage == 0){
				IJ.error("Error during preprocessing.");
			} else if (runStage == 1) {
				IJ.error("Error during the aplication of the model.");
			} else if (runStage == 2) {
				IJ.error("Error during postprocessing.");
			}
			return;
		}
	}
	
	/*
	 * Set the parameters for when no model is selected
	 */
	public void setGUIOriginalParameters() {
		info.setText("Using Tensorflow Javascript 1.15.0 CPU");
		choices[1].removeAll();
		choices[1].addItem("-----------------Select format-----------------");
		choices[2].removeAll();
		choices[2].addItem("-----------Select preprocessing----------- ");
		choices[3].removeAll();
		choices[3].addItem("-----------Select postprocessing-----------");
		texts[0].setText("");
		texts[1].setText("");
		texts[1].setEditable(false);
		info.setCaretPosition(0);
	}

	/*
	 * For a model whose model.yaml file does not contain the necessary information,
	 * indicate which fields have missing information or are incorrect
	 */
	private void setUnavailableModelText(ArrayList<String> fieldsMissing) {
		info.setText("\nThe selected model contains error in the model.yaml.\n");
		info.append("The errors are in the following fields:\n");
		for (String err : fieldsMissing)
			info.append(" - " + err + "\n");
		dlg.getButtons()[0].setEnabled(false);
		info.setCaretPosition(0);
	}


}