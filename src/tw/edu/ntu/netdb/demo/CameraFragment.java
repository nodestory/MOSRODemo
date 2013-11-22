package tw.edu.ntu.netdb.demo;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class CameraFragment extends Fragment implements SurfaceHolder.Callback {
	private SurfaceView mPreview;
	private ImageButton mTakeButton;

	boolean mIspreviewing = false;

	private Camera mCamera;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_camera, null);
		mPreview = (SurfaceView) rootView.findViewById(R.id.surfaceView_preview);
		mPreview.getHolder().addCallback(this);
		mTakeButton = (ImageButton) rootView.findViewById(R.id.imageButton_take);
		mTakeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCamera.takePicture(new ShutterCallback() {
					@Override
					public void onShutter() {
					}
				}, new PictureCallback() {
					@Override
					public void onPictureTaken(byte[] arg0, Camera arg1) {
					}
				}, new PictureCallback() {
					@Override
					public void onPictureTaken(byte[] arg0, Camera arg1) {
						Bitmap displayedBitmap = BitmapFactory.decodeByteArray(arg0, 0, arg0.length);
						Bitmap processedBitmap = Bitmap.createBitmap(displayedBitmap, 0, displayedBitmap.getHeight() / 2
								- displayedBitmap.getWidth() / 2, displayedBitmap.getWidth(),
								displayedBitmap.getWidth());
						((MainActivity) getActivity()).savePicture("display.jpg", displayedBitmap, 90);
						((MainActivity) getActivity()).savePicture("process.jpg", processedBitmap, 90);
					}
				});
			}
		});
		return rootView;
	}

	@Override
	public void onStop() {
		super.onStop();
		mCamera.release();
		mCamera = null;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if (mIspreviewing) {
			mCamera.stopPreview();
			mIspreviewing = false;
		}

		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mCamera.startPreview();
		mIspreviewing = true;
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		mCamera = Camera.open();
		mCamera.setDisplayOrientation(90);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mCamera != null) {
			mCamera.stopPreview();
		}
	}
}