package ch.hsr.eyecam.widget;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import ch.hsr.eyecam.Orientation;
import ch.hsr.eyecam.R;

/**
 * The BubbleView class represents a simple bubble with the possibility to display an arbitrary View inside. Since we have to start our application in Landscape mode in order to
 * correctly display the camera preview, the biggest challenge was to make this class orientation aware without relying on screen orientation system of Android itself.
 * 
 * The orientation awareness is achieved by rotating the whole canvas according to the orientation of the device.
 * 
 * Please note, that this class will probably only work correctly if you force your Activity to be started in Landscape mode.
 * 
 * @author Dominik Spengler
 * 
 */
public class BubbleView extends FrameLayout {

	private final Matrix mRotationMatrix;
	private final View mContentView;
	private final FrameLayout mFrame;
	private Orientation mOrientation;
	private final Rect mDirtyRect;
	private int newHeight;
	private int newWidth;

	/**
	 * Constant for defining a central placed arrow of the bubble. The arrow will be displayed in the center on the bottom of the bubble.
	 * 
	 * @see #setArrowStyle(int)
	 */
	public static final int ARROW_CENTER = R.drawable.popup_arrow_center;
	/**
	 * Constant for defining a left placed arrow of the bubble. The arrow will be displayed in the left on the bottom of the bubble.
	 * 
	 * @see #setArrowStyle(int)
	 */
	public static final int ARROW_LEFT = R.drawable.popup_arrow_left;
	/**
	 * Constant for defining a right placed arrow of the bubble. The arrow will be displayed in the right on the bottom of the bubble.
	 * 
	 * @see #setArrowStyle(int)
	 */
	public static final int ARROW_RIGHT = R.drawable.popup_arrow_right;
	/**
	 * Constant for defining a bubble without any arrow.
	 * 
	 * @see #setArrowStyle(int)
	 */
	public static final int ARROW_NONE = R.drawable.popup_arrow_none;

	public BubbleView(View contentView) {
		super(contentView.getContext());

		mFrame = new FrameLayout(contentView.getContext());
		mContentView = contentView;

		mFrame.addView(mContentView);
		addView(mFrame);

		mRotationMatrix = new Matrix();
		mRotationMatrix.reset();

		mDirtyRect = new Rect();

		mFrame.setBackgroundResource(ARROW_CENTER);
		setOrientation(Orientation.PORTRAIT);
	}

	/**
	 * This method sets the orientation of the View independently of the screen orientation of Android (hence independently of the orientation reported by the devices sensor)
	 * 
	 * The View will not be redrawn if you change the orientation while the bubble is showing. To be exact, the rotation matrix will only be rebuild if onMeasure() gets called.
	 * 
	 * @param orientation
	 *            you wish the bubble to be shown in.
	 * 
	 * @see Orientation
	 * @see #onMeasure(int, int)
	 */
	public void setOrientation(Orientation orientation) {
		mOrientation = orientation;
	}

	public Orientation getOrientation() {
		return mOrientation;
	}

	/**
	 * Updates the BubbleView by forcing it to re-measure.
	 * 
	 * @see #onMeasure(int, int)
	 */
	public void updateView() {
		onMeasure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
	}

	/**
	 * Sets the arrow style as defined in the ARROW_X constants. The view will not be redrawn if the bubble is already showing.
	 * 
	 * @param arrowstyle
	 *            as defined in the ARROW_X constants.
	 * 
	 * @see #ARROW_CENTER
	 * @see #ARROW_LEFT
	 * @see #ARROW_RIGHT
	 */
	public void setArrowStyle(int arrowstyle) {
		mFrame.setBackgroundResource(arrowstyle);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * onMeasure() had to be overwritten in order to provide the correct View measurement when in Portrait mode.
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		newHeight = Math.max(newHeight, getMeasuredHeight());
		newWidth = Math.max(newWidth, getMeasuredWidth());

		if (mOrientation == Orientation.PORTRAIT)
			setMeasuredDimension(newHeight, newWidth);
		updateDirtyRect();
		updateMatrix();
	}

	private void updateDirtyRect() {
		int width = getWidth();
		int height = getHeight();

		mDirtyRect.bottom = height;
		mDirtyRect.right = width;
	}

	private void updateMatrix() {
		mRotationMatrix.reset();
		float width = getWidth();
		float height = getHeight();

		switch (mOrientation) {
		case LANDSCAPE_RIGHT:
			mRotationMatrix.setRotate(180, width / 2.0f, height / 2.0f);
			break;
		case PORTRAIT:
			mRotationMatrix.setRotate(-90, 0.0f, 0.0f);
			mRotationMatrix.postTranslate(0.0f, height);
			break;
		case LANDSCAPE_LEFT:
		case UNKNOW:
			break;
		}

		invalidate();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * This is where the rotation is applied to the canvas in order to correctly display the bubble according to the orientation set in {@link #setOrientation(Orientation)}.
	 * 
	 * The dispatchDraw() method was overwritten in order to make sure all the child views get rotated as well.
	 */
	@Override
	protected void dispatchDraw(Canvas canvas) {
		canvas.setMatrix(mRotationMatrix);
		super.dispatchDraw(canvas);
		invalidate(mDirtyRect);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Used to correctly map the touch input according to the orientation the view is in.
	 * 
	 * @see #setOrientation(Orientation)
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		switch (mOrientation) {
		case LANDSCAPE_RIGHT:
			return true;
		case PORTRAIT:
			return true;
		default:
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float resultingX = event.getX();
		float resultingY = event.getY();

		switch (mOrientation) {
		case PORTRAIT:
			resultingX = invert(event.getY(), getHeight());
			resultingY = event.getX();
			break;
		case LANDSCAPE_RIGHT:
			resultingX = invert(event.getX(), getWidth());
			resultingY = invert(event.getY(), getHeight());
			break;
		case LANDSCAPE_LEFT:
		case UNKNOW:
			break;
		}

		event.setLocation(resultingX, resultingY);
		return mContentView.dispatchTouchEvent(event);
	}

	private float invert(float value, float maxvalue) {
		return maxvalue - value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Drawable getBackground() {
		return mFrame.getBackground();
	}

	public void reset() {
		newHeight = 0;
		newWidth = 0;
	}
}