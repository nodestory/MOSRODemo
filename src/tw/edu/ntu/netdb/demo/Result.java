package tw.edu.ntu.netdb.demo;

import org.opencv.samples.tutorial4.MOSROCategory;

import android.graphics.Bitmap;

public class Result {
	private MOSROCategory category;
	private Bitmap maskBitmap;
	private boolean[][] mask;
	private int centerX;
	private int centerY;

	public Result(MOSROCategory category, Bitmap maskBitmap, boolean[][] mask) {
		setCategory(category);
		setMaskBitmap(maskBitmap);
		setMask(mask);

		// find the center of the a logo
		int left = 0;
		int top = 0;
		int right = 0;
		int bottom = 0;
		for (int i = 0; i < maskBitmap.getWidth(); i++) {
			for (int j = 0; j < maskBitmap.getHeight(); j++) {
				if (mask[i][j]) {
					left = i;
					top = j;
					break;
				}
			}
		}
		for (int i = maskBitmap.getWidth() - 1; i >= 0; i--) {
			for (int j = maskBitmap.getHeight() - 1; j >= 0; j--) {
				if (mask[i][j]) {
					right = i;
					bottom = j;
					break;
				}
			}
		}
		setCenterX((left + right) / 2);
		setCenterY((top + bottom) / 2);
	}

	public MOSROCategory getCategory() {
		return category;
	}

	public void setCategory(MOSROCategory category) {
		this.category = category;
	}

	public Bitmap getMaskBitmap() {
		return maskBitmap;
	}

	public void setMaskBitmap(Bitmap maskBitmap) {
		this.maskBitmap = maskBitmap;
	}

	public boolean[][] getMask() {
		return mask;
	}

	public void setMask(boolean[][] mask) {
		this.mask = mask;
	}

	public int getCenterX() {
		return centerX;
	}

	public void setCenterX(int centerX) {
		this.centerX = centerX;
	}

	public int getCenterY() {
		return centerY;
	}

	public void setCenterY(int centerY) {
		this.centerY = centerY;
	}
}