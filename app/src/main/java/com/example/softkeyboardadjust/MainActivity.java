package com.example.softkeyboardadjust;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.lxj.xpopup.XPopup;

/**
 *  the default adjustPan/ adjustResize only shift the page up just enough to not cover the current focused editText
 *
 *  Using the idea from this library
 *  原理就是通过ViewTreeObserver.addOnGlobalLayoutListener监听Layout的变化。
 *  https://github.com/llwl1982/FloatOnKeyboardLayout/blob/master/library/src/main/java/github/ll/view/FloatOnKeyboardLayout.java
 *
 *
 * */

public class MainActivity extends AppCompatActivity {

    private final boolean USE_IMMERSIVE_MODE = true;
    public final boolean DISABLE_IMMERSIVE_MODE_ON_KEYBOARD_OPEN = false; // might be helpful to solve keyboard jumping issue when pop up

    public AndroidBug5497Workaround2 helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidBug5497Workaround2.SoftKeyBoardStatusListener listener = new AndroidBug5497Workaround2.SoftKeyBoardStatusListener() {
            @Override
            public void onKeyBoardShow(View rootView, int totalScreenHeight) {
            }

            @Override
            public void onKeyBoardHide(View rootView, int totalScreenHeight) {

                if (USE_IMMERSIVE_MODE){
                    returnToImmersiveMode();
                }
            }
        };
        helper = AndroidBug5497Workaround2.assistActivity(this, listener);


        View v = findViewById(R.id.btn);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new XPopup.Builder(MainActivity.this)
                        .autoOpenSoftInput(true)
                        .asCustom(new CustomEditTextBottomPopup(MainActivity.this))
                        .show();
            }
        });

    }


    // hide status bar and navigation bar until user slide up
    // when softkeyboard appear, it breaks the immersive mode, so we need to manually call immersive mode again after softkeyboard disappear
    // https://developer.android.com/training/system-ui/immersive.html
    public void immersiveMode() {
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void disableImmersiveMode() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    /**
     * on most cases when dismissing softkeyboard, onWIndowFocusChanged will get executed
     * but on motorola XT1097 API 23 and motorola Moto E (4) Plus
     * onWindowFocusChanged DOES NOT get triggered when dismiss the softkeyboard
     *
     * it is better to rely on keyboard close callback to manually call immersive mode()
     */
/*    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        returnToImmersiveMode();
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        returnToImmersiveMode();
                    }
                });
    }*/

    /**
     * on motorola Moto E (4) Plus
     * returning to immersive mode after keyboard close cause onGlobalLayout()
     * cause the listener to not think the keyboard closed
     * For some reason adding a delay avoid this problem
     */
    private void returnToImmersiveMode(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                immersiveMode();
            }
        }, 300);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (USE_IMMERSIVE_MODE){
            immersiveMode();
        }
    }

}