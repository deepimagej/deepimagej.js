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

import java.awt.Frame;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageWindow;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;
import ij.text.TextWindow;

public class ArrayOperations {

	public static ImagePlus convertArrayToImagePlus(double[][][][][] array, int[] shape) {
		int nx = shape[0];
		int ny = shape[1];
		int nz = shape[3];
		int nc = shape[2];
		int nt = shape[4];
		ImagePlus imp = IJ.createImage("out", "32-bit", nx, ny, nc, nz, nt);
		for (int t = 0; t < nt; t++) {
			for (int c = 0; c < nc; c++) {
				for (int z = 0; z < nz; z++) {
					imp.setPositionWithoutUpdate(c + 1, z + 1, t + 1);
					ImageProcessor ip = imp.getProcessor();
					for (int x = 0; x < nx; x++)
						for (int y = 0; y < ny; y++)
							ip.putPixelValue(x, y, array[x][y][c][z][t]);
				}
			}
		}
		return imp;
	}

	public static ImagePlus extractPatch(ImagePlus image, int[] sPatch, int xStart, int yStart, int zStart,
										int overlapX, int overlapY, int overlapZ) {
		// This method obtains a patch with the wanted size, starting at 'x_start' and
		// 'y_start' and returns it as RandomAccessibleInterval with the dimensions
		// already adjusted
		ImagePlus patchImage = IJ.createImage("aux", "32-bit", sPatch[0], sPatch[1], sPatch[2], sPatch[3], 1);
		int zi = -1;
		for (int z = zStart - overlapZ; z < zStart - overlapZ + sPatch[3]; z++) {
			zi ++; 
			for (int c = 0; c < sPatch[2]; c++) {
				image.setPositionWithoutUpdate(c + 1, z + 1, 1);
				patchImage.setPositionWithoutUpdate(c + 1, zi + 1, 1);
				ImageProcessor ip = image.getProcessor();
				ImageProcessor op = patchImage.getProcessor();
				// The actual patch with false and true information goes from patch_size/2
				// number of pixels before the actual start of the patch until patch_size/2 number of pixels after
				int xi = -1;
				int yi = -1;
				for (int x = xStart - overlapX; x < xStart - overlapX + sPatch[0]; x++) {
					xi++;
					yi = -1;
					for (int y = yStart - overlapY; y < yStart - overlapY + sPatch[1]; y++) {
						yi++;
						op.putPixelValue(xi, yi, (double) ip.getPixelValue(x, y));
					}
				}
				patchImage.setProcessor(op);
			}
		}
		return patchImage;
	}

	public static void imagePlusReconstructor(ImagePlus fImage, ImagePlus patch,
											   int xImageStartPatch, int xImageEndPatch,
											   int yImageStartPatch, int yImageEndPatch,
											   int zImageStartPatch, int zImageEndPatch,
											   int leftoverX, int leftoverY, int leftoverZ) {
		// This method inserts the pixel values of the true part of the patch into its corresponding location
		// in the image
		int[] patchDimensions = patch.getDimensions();
		int channels = patchDimensions[2];
		ImageProcessor patchIp;
		ImageProcessor imIp;
		// Horizontal size of the roi
		int roiX = xImageEndPatch - xImageStartPatch;
		// Vertical size of the roi
		int roiY = yImageEndPatch - yImageStartPatch;
		// Transversal size of the roi
		int roiZ = zImageEndPatch - zImageStartPatch;
		
		int zImage = zImageStartPatch - 1;
		for (int zMirror = leftoverZ; zMirror < leftoverZ + roiZ; zMirror ++) {
			zImage ++;
			for (int c = 0; c < channels; c ++) {
				int xImage = xImageStartPatch - 1;
				int yImage = yImageStartPatch - 1;
				patch.setPositionWithoutUpdate(c + 1, zMirror + 1, 1);
				fImage.setPositionWithoutUpdate(c + 1, zImage + 1, 1);
				patchIp = patch.getProcessor();
				imIp = fImage.getProcessor();
				// The information non affected by 'the edge effect' is the one important to us. 
				// This is why we only take the center of the patch. The size of this center is 
				// the size of the patch minus the distorted number of pixels at each side (overlap)
				for (int xMirror = leftoverX; xMirror < leftoverX + roiX; xMirror ++) {
					xImage ++;
					yImage = yImageStartPatch - 1;
					for (int yMirror = leftoverY; yMirror < leftoverY + roiY; yMirror ++) {
						yImage ++;
						imIp.putPixelValue(xImage, yImage, (double) patchIp.getPixelValue(xMirror, yMirror));
					}
				}
				fImage.setProcessor(imIp);
			}
		}
	}
	
	
	public static int[][] findAddedPixels(int[] size, int[] padding, int[] roi) {
		// This method calculates the number of pixels that have to be
		// added at each side of the image to create the mirrored image with the exact needed size
		// The resulting vector is a 4 dims vector of this shape --> [x_left, x_right, y_top, y_bottom]
		// All the arrays containing dimensions are organised as follows [x, y, c, z] 
		int[][] extraPixels = new int[2][4];
		int[] needed = new int[4];
		for (int i = 0; i < needed.length; i++) {
			if (roi[i] > size[i]) {
				needed[i] = roi[i] - size[i] + 2 * padding[i];
			} else if (roi[i] <= size[i]) {
				needed[i] = 2 * padding[i];
			}
			extraPixels[0][i] = (int) Math.ceil((double) needed[i] / 2);
			extraPixels[1][i] = needed[i] - extraPixels[0][i];
		}
		
		return extraPixels;
	}
	
	public static String findPixelSize(ImagePlus im) {
		// Time the model run lasted (child of "ModelTest")
		float pixDepth = (float) im.getCalibration().pixelDepth;
		float pixWidth = (float) im.getCalibration().pixelWidth;
		float pixHeight = (float) im.getCalibration().pixelHeight;
		
		String units = im.getCalibration().getUnits();
		String pixSize = String.format("%.2E", pixWidth) + units + "x" +
						 String.format("%.2E", pixHeight) + units+ "x" +
						 String.format("%.2E", pixDepth) + units;
		return pixSize;
	}

	/*
	 * Method that displays the outputs that have not been shown
	 */
	public static void displayMissingOutputs(String[] finalImages, String[] finalFrames,
												HashMap<String, Object> output) {

		List<String> frameList = Arrays.asList(finalFrames);
		List<String> framesList = Arrays.asList(finalImages);
		
		for (String outs : output.keySet()) {
			Object f = output.get(outs);
			if (f != null && (f instanceof ResultsTable)) {
	        	ResultsTable table = (ResultsTable) f;
				String title = table.getTitle();
				
				// Check that the output does not correspond to any
				// of the already displayed tables
				boolean alreadyDisplayed = false;
				for (String displayedFrame : frameList) {
					if (!displayedFrame.contains(title)) 
						continue;
					Frame displayedRT = WindowManager.getFrame(title);
		        	ResultsTable alreadyDisplayedTable = null;
			        if (displayedRT!=null && (displayedRT instanceof TextWindow)) 
			        	alreadyDisplayedTable = ((TextWindow)displayedRT).getResultsTable();
			        if (alreadyDisplayedTable.getResultsTable().equals(table.getResultsTable())) {
						alreadyDisplayed = true;
						break;
					}					
				}
				if (alreadyDisplayed)
					continue;
				String newTitle = title;
				int c = 1;
				// While we find a table that is called thesame
				while (frameList.contains(newTitle)) {
					newTitle = title + "-" + c;
					c ++;
				}
				title = newTitle;
				table.show(title);
			} else if (f != null && f instanceof ImagePlus) {
				String title = ((ImagePlus) f).getTitle();

				// Check that the output does not correspond to any
				// of the already displayed tables
				boolean alreadyDisplayed = false;
				for (String displayedIm : framesList) {
					if (!displayedIm.contains(title)) 
						continue;
					ImagePlus displayedImP = WindowManager.getImage(title);
			        if (displayedImP != null) 
			        	alreadyDisplayed = displayedImP.equals(((ImagePlus) f));
					if (alreadyDisplayed && displayedImP.getWindow() == null) {
						ImageWindow ww = new ImageWindow(displayedImP);
						ww.setVisible(true);
						break;	
					} else if (alreadyDisplayed) {
						break;
					}
				}
				if (alreadyDisplayed)
					continue;
				String newTitle = title;
				int c = 1;
				// While we find a table that is called thesame
				while (frameList.contains(newTitle)) {
					newTitle = title + "-" + c;
					c ++;
				}
				((ImagePlus) f).setTitle(newTitle);
				((ImagePlus) f).getWindow().setVisible(true);
			}
		}
	}
	
	/*
	 * Gets an array with the input patch size, as defined in the model
	 * from a comma separated string
	 */
	public static int[] getPatchSize(String[] dim, String form, String sizes, boolean editable) {
		String[] definedSizes = sizes.split(",");
		int[] patch = new int[form.split("").length]; 
		// Dimensions of the patch: [x, y, c, z]
		int batchInd = Index.indexOf(form.split(""), "B");
		int count = 0;
		for (int c = 0; c < patch.length; c ++) {
			if (c != batchInd){
				try {
					if (definedSizes[count].trim().equals("auto") && !editable) {
						patch[c] = -1;	
					} else {
						int value = Integer.parseInt(definedSizes[count].trim());
						patch[c] = value;	
					}
					count += 1;
				} catch (Exception ex) {
					return null;
				}				
			} else {
				patch[c] = 1;
			}
		}
		return patch;
	}
	
	public static float[] findTotalPadding(DijTensor input, List<DijTensor> outputs, boolean pyramidal) {
		// Create an object of int[] that contains the output dimensions
		// of each patch.
		// This dimensions are always in the form of the input
		String[] targetForm = input.form.split("");
		float[] padding = new float[targetForm.length];
		if (!pyramidal) {
			for (DijTensor out: outputs) {
				if (out.tensorType.contains("image") && !Arrays.equals(out.scale, new float[out.scale.length])) {
					for (int i = 0; i < targetForm.length; i ++) {
						int ind = Index.indexOf(out.form.split(""), targetForm[i]);
						if (ind != -1 && !targetForm[i].toLowerCase().equals("b")  && !targetForm[i].toLowerCase().equals("c") && (out.offset[ind] + out.halo[ind]) > padding[i])  {
							padding[i] = -1 * out.offset[ind] + out.halo[ind];
						}
						// Only allow offsets that can be divided by 0.5
						if (out.offset[ind] % 0.5 != 0)
							return null;
					}
				}
			}
		}
		return padding;
	}

	/*
	 * This method looks for the optimal patch size regarding the
	 * minimum size, step, halo and image size. The optimal patch
	 * is regarded as the smallest possible patch that allows 
	 * processing the image as a whole in only one run.
	 * Regard that it might be too memory consuming for some 
	 * computers/images.
	 */
	public static String optimalPatch(float[] haloArr, String[] dimCharArr, int[] stepArr, int[] minArr, boolean allowPatch) {

		ImagePlus imp = WindowManager.getCurrentImage();
		return optimalPatch(imp, haloArr, dimCharArr, stepArr, minArr, null, allowPatch);
			
	}

	/*
	 * This method looks for the optimal patch size regarding the
	 * minimum size, step, halo and image size. The optimal patch
	 * is regarded as the smallest possible patch that allows 
	 * processing the image as a whole in only one run.
	 * Regard that it might be too memory consuming for some 
	 * computers/images.
	 */
	public static String optimalPatch(ImagePlus imp, float[] haloArr, String[] dimCharArr, int[] stepArr, int[] minArr, boolean allowPatch) {

		return optimalPatch(imp, haloArr, dimCharArr, stepArr, minArr, null, allowPatch);
			
	}

	/*
	 * This method looks for the optimal patch size regarding the
	 * minimum size, step, halo and image size. The optimal patch
	 * is regarded as the smallest possible patch that allows 
	 * processing the image as a whole in only one run.
	 * Regard that it might be too memory consuming for some 
	 * computers/images.
	 */
	public static String optimalPatch(float[] haloArr, String[] dimCharArr, int[] stepArr, int[] minArr, String testSize, boolean allowPatch) {

		ImagePlus imp = WindowManager.getCurrentImage();
		return optimalPatch(imp, haloArr, dimCharArr, stepArr, minArr, testSize, allowPatch);
			
	}

		/*
		 * This method looks for the optimal patch size regarding the
		 * minimum size, step, halo and image size. The optimal patch
		 * is regarded as the smallest possible patch that allows 
		 * processing the image as a whole in only one run.
		 * Regard that it might be too memory consuming for some 
		 * computers/images.
		 */
	public static String optimalPatch(ImagePlus imp, float[] haloArr, String[] dimCharArr, int[] stepArr, int[] minArr, String testSize, boolean allowPatch) {
			
		String patch = "";
		for (int ii = 0; ii < haloArr.length; ii ++) {
			String dimChar = dimCharArr[ii];
			float halo = haloArr[ii];
			int min = minArr[ii];
			int step = stepArr[ii];
			// If there is no image, return the test tile size specified in the yaml if there is any
			if (imp == null && testSize != null && !testSize.contentEquals("")) {
				return getTestTileSize(testSize, dimCharArr);
			} else if (imp == null && step != 0 && (dimChar.contentEquals("X") || dimChar.contentEquals("Y"))) {
				patch += "auto,";
				continue;
			} else if (imp == null && step == 0) {
				patch += min + ",";
				continue;
			}
			
			int size = 0;
			switch (dimChar) {
				case "Y":
					size = imp.getHeight();
					break;
				case "X":
					size = imp.getWidth();
					break;
				case "Z":
					size = imp.getNSlices();
					break;
				case "C":
					size = imp.getNChannels();
					break;
			}
			
			if (step != 0 && allowPatch) {
				int optimalMult = (int)Math.ceil((double)(size + 2 * halo) / (double)step) * step;
				if (optimalMult > 3 * size) {
					optimalMult = optimalMult - step;
				}
				if (optimalMult > 3 * size) {
					optimalMult = (int)Math.ceil((double)size / (double)step) * step;
				}
				patch += Integer.toString(optimalMult) + ",";

			} else if (step != 0 && !allowPatch){
				patch += "auto,";
			} else if (step == 0){
				patch += min + ",";
			}
		}
		patch = patch.substring(0, patch.length() - 1);
		return patch;
	}		
	
	/**
	 * Find the tile size used during testing and reformats it following
	 * the axes order
	 * @param tileYaml: raw String coming form the yaml file at: conig->
	 * 					deepimagej->test_information->inputs->size
	 * @param axes: the axes of the model
	 * @return tile size in a the format used by DIJ Run
	 */
public static String getTestTileSize(String tileYaml, String[] axes) {
	String tileString = "";
	String[] tile = tileYaml.toLowerCase().split("x");
	String auxAxes = Arrays.toString(axes);
	String axesTestFormat = "";
	if (tile.length == 5) {
		axesTestFormat = "xyczt";
	} else if (tile.length == 4) {
		axesTestFormat = "xycz";
	} else if (!auxAxes.toLowerCase().contains("c") && auxAxes.toLowerCase().contains("z") && tile.length == 3) {
		axesTestFormat = "xyz";
	} else if (!auxAxes.toLowerCase().contains("z") && auxAxes.toLowerCase().contains("c") && tile.length == 3) {
		axesTestFormat = "xyc";
	} else if (!auxAxes.toLowerCase().contains("z") && !auxAxes.toLowerCase().contains("c") && tile.length == 3) {
		axesTestFormat = "xyc";
	} else if (!auxAxes.toLowerCase().contains("z") && !auxAxes.toLowerCase().contains("c") && tile.length == 2) {
		axesTestFormat = "xy";
	}
	for (int i = 0; i < axes.length; i ++)
		tileString += tile[axesTestFormat.indexOf(axes[i].toLowerCase())].trim() + ",";
	// Remove last comma
	tileString = tileString.substring(0, tileString.length() - 1);
	return tileString;
}

}
