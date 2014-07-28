package com.external.cameraService.inter;

import android.view.View;
import android.view.WindowManager.LayoutParams;

public interface ServiceInterface {
    public LayoutParams getViewParams();
    public void refreshView(float x, float y);
    public View getFocusView();
}
