package tw.edu.ntu.netdb.demo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResultView extends LinearLayout {

	public ResultView(Context context, String name, String addr, String intro) {
		super(context);
//		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		init(context, name, addr, intro);
	}

	public ResultView(Context context, AttributeSet attrs) {
		super(context, attrs);
//		setOrientation(LinearLayout.HORIZONTAL);
//		setGravity(Gravity.CENTER_VERTICAL);
//		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		inflater.inflate(R.layout.resultview, this, true);
		
	}

	public ResultView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
//		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		inflater.inflate(R.layout.resultview, this, true);
	}

	public void init(Context context, String name, String addr, String intro) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rootView = inflater.inflate(R.layout.resultview, this, true);
		TextView nameTextView = (TextView) rootView.findViewById(R.id.textView_name);
		nameTextView.setText(name);
		TextView addrTextView = (TextView) rootView.findViewById(R.id.textView_addr);
		addrTextView.setText(addr);
		TextView introTextView = (TextView) rootView.findViewById(R.id.textView_intro);
		introTextView.setText(intro);
	}
}