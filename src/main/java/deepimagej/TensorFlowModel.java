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

import deepimagej.tools.DijTensor;
import deepimagej.tools.Index;

public class TensorFlowModel {

	// Same as the tag used in export_saved_model in the Python code.
	private static final String[] MODEL_TAGS = {"serve", "inference", "train", "eval", "gpu", "tpu"};
	private static final String DEFAULT_TAG = "serve";
	
	
	private static final String[] TF_MODEL_TAGS = {"tf.saved_model.tag_constants.SERVING",
											   	   "tf.saved_model.tag_constants.INFERENCE",
											   	   "tf.saved_model.tag_constants.TRAINING",
											   	   "tf.saved_model.tag_constants.EVAL",
											   	   "tf.saved_model.tag_constants.GPU",
											   	   "tf.saved_model.tag_constants.TPU"};
	
	
	private static final String[] SIGNATURE_CONSTANTS = {"serving_default",
												   	     "inputs",
												   	     "tensorflow/serving/classify",
												   	     "classes",
												   	     "scores",
												   	     "inputs",
												   	     "tensorflow/serving/predict",
												   	     "outputs",
												   	     "inputs",
												   	     "tensorflow/serving/regress",
												   	     "outputs",
												   	     "train",
												   	     "eval",
												   	     "tensorflow/supervised/training",
												   	     "tensorflow/supervised/eval"};

	private static final String[] TF_SIGNATURE_CONSTANTS = {"tf.saved_model.signature_constants.DEFAULT_SERVING_SIGNATURE_DEF_KEY",
												   	     "tf.saved_model.signature_constants.CLASSIFY_INPUTS",
												   	     "tf.saved_model.signature_constants.CLASSIFY_METHOD_NAME",
												   	     "tf.saved_model.signature_constants.CLASSIFY_OUTPUT_CLASSES",
												   	     "tf.saved_model.signature_constants.CLASSIFY_OUTPUT_SCORES",
												   	     "tf.saved_model.signature_constants.PREDICT_INPUTS",
												   	     "tf.saved_model.signature_constants.PREDICT_METHOD_NAME",
												   	     "tf.saved_model.signature_constants.PREDICT_OUTPUTS",
												   	     "tf.saved_model.signature_constants.REGRESS_INPUTS",
												   	     "tf.saved_model.signature_constants.REGRESS_METHOD_NAME",
												   	     "tf.saved_model.signature_constants.REGRESS_OUTPUTS",
												   	     "tf.saved_model.signature_constants.DEFAULT_TRAIN_SIGNATURE_DEF_KEY",
												   	     "tf.saved_model.signature_constants.DEFAULT_EVAL_SIGNATURE_DEF_KEY",
												   	     "tf.saved_model.signature_constants.SUPERVISED_TRAIN_METHOD_NAME",
												   	     "tf.saved_model.signature_constants.SUPERVISED_EVAL_METHOD_NAME"};

	


	public static int nChannelsOrSlices(DijTensor tensor, String channelsOrSlices) {
		// Find the number of channels or slices in the corresponding tensor
		String letter = "";
		if (channelsOrSlices.equals("channels")) {
			letter = "C";
		} else {
			letter = "Z";
		}
		
		int nChannels;
		String inputForm = tensor.form;
		int ind = Index.indexOf(inputForm.split(""), letter);
		if (ind == -1) {
			nChannels = 1;
		}
		else {
			nChannels = tensor.minimum_size[ind];
		}
		return nChannels;
	}
	
	public static String hSize(Parameters params, String inputForm) {
		// Find the number of channels in the input
		String nChannels;
		int ind = Index.indexOf(inputForm.split(""), "Y");
		if (ind == -1) {
			nChannels = "-1";
		}
		else {
			nChannels = Integer.toString(params.inputList.get(0).tensor_shape[ind]);
		}
		return nChannels;
	}
	
	public static String wSize(Parameters params, String inputForm) {
		// Find the number of channels in the input
		String nChannels;
		int ind = Index.indexOf(inputForm.split(""), "X");
		if (ind == -1) {
			nChannels = "-1";
		}
		else {
			nChannels = Integer.toString(params.inputList.get(0).tensor_shape[ind]);
		}
		return nChannels;
	}
	
	// Method added to allow multiple possible batch sizes
	public static String nBatch(int[] dims, String inputForm) {
		// Find the number of channels in the input
		String inBatch;
		int ind = Index.indexOf(inputForm.split(""), "B");
		if (ind == -1) {
			inBatch = "1";
		} else {
			inBatch = Integer.toString(dims[ind]);
		}
		if (inBatch.equals("-1")) {
			inBatch = "1";
		}
		return inBatch;
	}
	
	public static String returnTfTag(String tag) {
		String tfTag;
		int tagInd = Index.indexOf(MODEL_TAGS, tag);
		if (tagInd == -1) {
			tfTag = tag;
		} else {
			tfTag = TF_MODEL_TAGS[tagInd];
		}
		return tfTag;
	}
	
	public static String returnStringTag(String tfTag) {
		String tag;
		int tagInd = Index.indexOf(TF_MODEL_TAGS, tfTag);
		if (tagInd == -1) {
			tag = tfTag;
		} else {
			tag = MODEL_TAGS[tagInd];
		}
		return tag;
	}
	
	public static String returnStringSig(String tfSig) {
		String sig;
		int sigInd = Index.indexOf(TF_SIGNATURE_CONSTANTS, tfSig);
		if (sigInd == -1) {
			sig = tfSig;
		} else {
			sig = SIGNATURE_CONSTANTS[sigInd];
		}
		return sig;
	}
	
	public static String returnTfSig(String sig) {
		String tfSig;
		int tfSigInd = Index.indexOf(SIGNATURE_CONSTANTS, sig);
		if (tfSigInd == -1) {
			tfSig = sig;
		} else {
			tfSig = TF_SIGNATURE_CONSTANTS[tfSigInd];
		}
		return tfSig;
	}

}
