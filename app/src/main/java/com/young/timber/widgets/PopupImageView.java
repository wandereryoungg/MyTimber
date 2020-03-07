package com.young.timber.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.afollestad.appthemeengine.util.TintHelper;

import static com.young.timber.utils.Constants.PREFERENCES_NAME;

@SuppressLint("AppCompatCustomView")
public class PopupImageView extends ImageView {

    public PopupImageView(Context context) {
        super(context);
        tint();
    }

    public PopupImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        tint();
    }

    public PopupImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        tint();
    }

    private void tint() {
        if(getContext().getSharedPreferences(PREFERENCES_NAME,Context.MODE_PRIVATE).getBoolean("dark_theme",false)){
            TintHelper.setTint(this, Color.parseColor("#eeeeee"));
        }else{
            TintHelper.setTint(this,Color.parseColor("#434343"));
        }
    }
}
