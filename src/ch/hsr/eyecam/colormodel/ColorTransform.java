package ch.hsr.eyecam.colormodel;

import android.graphics.Bitmap;
/**
 * A class representing an interface to the colortransform library.
 * 
 * @author Dominik Spengler
 *
 */
public class ColorTransform {

	static {
		System.loadLibrary("colortransform");
	}

	/**
	 * Does what it says: Nothing.
	 */
	public static final int COLOR_EFFECT_NONE = 0;
	/**
	 * This effect simulates colorblindness.
	 */
	public static final int COLOR_EFFECT_SIMULATE = 1;
	/**
	 * Introduces false colors. It actually works by exchanging the
	 * U-plane with the V-plane of the YUV colorspace
	 */
	public static final int COLOR_EFFECT_FALSE_COLORS = 2;
	/**
	 * This effect tries to intensify the difference between red and
	 * green by increasing the Y-plane part of green and lowering the
	 * Y-plane part of red.
	 */
	public static final int COLOR_EFFECT_INTENSIFY_DIFFERENCE = 3;
	/**
	 * This effect is used for testing reasons. It sets the color to
	 * black instead of transforming it.
	 */
	public static final int COLOR_EFFECT_BLACK = 4;

	/**
	 * Sets the effect to be used for the transformation. The default 
	 * effect is {@link #COLOR_EFFECT_NONE}.
	 * 
	 * @param effect
	 * 
	 * @see #COLOR_EFFECT_NONE
	 * @see #COLOR_EFFECT_SIMULATE
	 * @see #COLOR_EFFECT_FALSE_COLORS
	 */
	public static native void setEffect(int effect);

	/**
	 * Sets the partial effect to be used for the transformation. This 
	 * method sets the same effects as {@link #setEffect(int)} only that
	 * the effect is not applied to the whole preview frame, but only
	 * to the part that would be seen differently by colorblind people.
	 * 
	 * Although this is the desired behavior, it needs much more calculations
	 * and hence is much slower and unusable on slower devices such as
	 * the G1.
	 * 
	 * @param effect
	 * 
	 * @see #setEffect(int)
	 */
	public static native void setPartialEffect(int effect);
	/**
	 * This method will transform the image data given in the byte array
	 * according to the effect and write it to the bitmap specified.
	 * 
	 * 
	 * @param data the source data in yuv420sp
	 * @param width of the source data
	 * @param heigth of the source data
	 * @param bitmap to write the data to
	 */
	public static native void transformImageToBitmap(byte[] data, int width,
			int height, Bitmap bitmap);

	/**
	 * This method will transform the image data given in the byte array
	 * according to the effect and write it to the buffer specified.
	 * 
	 * 
	 * @param data the source data in yuv420sp
	 * @param width of the source data
	 * @param heigth of the source data
	 * @param buffer to write the data to
	 */
	public static native void transformImageToBuffer(byte[] data, int width,
			int height, byte[] buffer);
}