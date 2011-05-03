package ch.hsr.eyecam.view;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.View;
import android.widget.FrameLayout;
import ch.hsr.eyecam.Orientation;
import ch.hsr.eyecam.R;

/**
 * The BubbleView class represents a simple bubble with the possibility to 
 * display an arbitrary View inside. Since we have to start our application
 * in Landscape mode in order to correctly display the camera preview, the 
 * biggest challenge was to make this class orientation aware without relying
 * on screen orientation system of Android itself.
 * 
 * The orientation awareness is achieved by rotating the whole canvas according
 * to the orientation of the device.
 * 
 * Please note, that this class will only work correctly if you force your 
 * Activity to be started in Landscape mode.
 * 
 * @author Dominik Spengler
 *
 */
public class BubbleView extends FrameLayout {

	private Orientation mOrientation;
	private Matrix mRotationMatrix;
	private View mContentView;
	private FrameLayout mFrame;
	private Matrix mFrameMatrix;
	
	/**
	 * Constant for defining a central placed arrow of the bubble. The 
	 * arrow will be displayed in the center on the bottom of the bubble.
	 * 
	 * @see #setArrowStyle(int)
	 */
	public static final int ARROW_CENTER = R.drawable.popup_arrow_center;
	/**
	 * Constant for defining a left placed arrow of the bubble. The 
	 * arrow will be displayed in the left on the bottom of the bubble.
	 * 
	 * @see #setArrowStyle(int)
	 */
	public static final int ARROW_LEFT = R.drawable.popup_arrow_left;
	/**
	 * Constant for defining a right placed arrow of the bubble. The 
	 * arrow will be displayed in the right on the bottom of the bubble.
	 * 
	 * @see #setArrowStyle(int)
	 */
	public static final int ARROW_RIGHT = R.drawable.popup_arrow_right;
	
	public BubbleView(View contentView) {
		super(contentView.getContext());
		mFrame = new FrameLayout(contentView.getContext());
		mContentView = contentView;
		
		mFrame.addView(mContentView);
		addView(mFrame);
		
		mRotationMatrix = new Matrix();
		mRotationMatrix.reset();
		
		mFrame.setBackgroundResource(ARROW_CENTER);
		setOrientation(Orientation.PORTRAIT);
	}
	/**
	 * This method sets the orientation of the View independently of the
	 * screen orientation of Android (hence independently of the orientation
	 * reported by the devices sensor)
	 * 
	 * The View will not be redrawn if you change the orientation while the 
	 * bubble is showing. To be exact, the rotation matrix will only be rebuild 
	 * if onMeasure() gets called.
	 * 
	 * @param orientation you wish the bubble to be shown in.
	 * 
	 * @see Orientation
	 * @see <a href="http://developer.android.com/reference/
	 * 			android/view/View.html#onMeasure(int,int)">
	 * 			android.view.View#onMeasure(int,int)</a>
	 */
	public void setOrientation(Orientation orientation){
		mOrientation = orientation;
		measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
	}

	/**
	 * Sets the arrow style as defined in the ARROW_X constants. The view will
	 * not be redrawn if the bubble is already showing. 
	 * 
	 * @param arrowstyle as defined in the ARROW_X constants.
	 * 
	 * @see #ARROW_CENTER
	 * @see #ARROW_LEFT
	 * @see #ARROW_RIGHT
	 */
	public void setArrowStyle(int arrowstyle){
		mFrame.setBackgroundResource(arrowstyle);
	}
	
	/**
	 * This method is used to specify whether only the content of the bubble should
	 * be rotated instead of the whole bubble (including the arrow).
	 * 
	 * @param frameRotation. true if you want to rotate the whole bubble.
	 */
	public void setFrameRotation(boolean frameRotation){
		if (!frameRotation) mFrameMatrix = new Matrix(mRotationMatrix);
		else mFrameMatrix = null;
	}
	
	/**
	 * onMeasure() had to be overwritten in order to provide the correct View 
	 * measurement when in Portrait mode.
	 * 
	 * @see <a href="http://developer.android.com/reference/
	 * 			android/view/View.html#onMeasure(int,int)">
	 * 			android.view.View#onMeasure(int,int)</a>
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int h = getMeasuredHeight();
		int w = getMeasuredWidth();
		
		if (mOrientation == Orientation.PORTRAIT) setMeasuredDimension(h, w);
		updateMatrix();
	}

	private void updateMatrix() {
		mRotationMatrix.reset();
		
		float width = getWidth();
		float height = getHeight();
		
		switch(mOrientation){
		case LANDSCAPE_LEFT:
			break;
		case LANDSCAPE_RIGHT:
			mRotationMatrix.setRotate(180, width/2.0f, height/2.0f);
			break;
		case PORTRAIT:
			mRotationMatrix.setRotate(-90, 0.0f, 0.0f);
			mRotationMatrix.postTranslate(0.0f, height);
			break;
		}
	}
	
	/**
	 * As for custom Views usual, the onDraw() method was overwritten. This is 
	 * where the rotation is applied to the canvas in order to correctly display
	 * the bubble according to the orientation set in {@link #setOrientation(Orientation)}.
	 * 
	 * @see <a href="http://developer.android.com/reference/
	 * 			android/view/View.html#onDraw(android.graphics.Canvas)">
	 * 			android.view.View#onDraw(Canvas)</a>
	 */
	@Override
	public void onDraw(Canvas canvas) {
		if (mFrameMatrix == null) {
			canvas.setMatrix(mRotationMatrix);
			super.onDraw(canvas);
		} else {
			canvas.setMatrix(mFrameMatrix);
			super.onDraw(canvas);
			canvas.setMatrix(mRotationMatrix);
			mContentView.draw(canvas);
		}
	}
}