package tw.edu.ntu.netdb.demo;

import com.google.android.gms.maps.model.LatLng;

public class DemoPosition {
	private int imgResId;
	private LatLng latLng;
	private int heading;
	private int fov;
	private int pitch;

	public DemoPosition(int imgResId, double lat, double lng, int heading, int fov, int pitch) {
		setImgResId(imgResId);
		setLatLng(new LatLng(lat, lng));
		setHeading(heading);
		setFov(fov);
		setPitch(pitch);
	}

	public int getImgResId() {
		return imgResId;
	}

	public void setImgResId(int imgResId) {
		this.imgResId = imgResId;
	}

	public LatLng getLatLng() {
		return latLng;
	}

	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}

	public int getHeading() {
		return heading;
	}

	public void setHeading(int heading) {
		this.heading = heading;
	}

	public int getFov() {
		return fov;
	}

	public void setFov(int fov) {
		this.fov = fov;
	}

	public int getPitch() {
		return pitch;
	}

	public void setPitch(int pitch) {
		this.pitch = pitch;
	}

}