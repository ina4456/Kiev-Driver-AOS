package com.kiev.driver.aos.view.customview;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class AdaptingTextView extends AppCompatTextView {

	public AdaptingTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public AdaptingTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AdaptingTextView(Context context) {
		super(context);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		// set fitting lines to prevent cut text
		int fittingLines = h / this.getLineHeight();
		if (fittingLines > 0) {
			this.setLines(fittingLines);
		}
	}

}
