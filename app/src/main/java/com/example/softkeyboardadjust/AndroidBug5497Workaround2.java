package com.example.softkeyboardadjust;


import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.Toast;


public class AndroidBug5497Workaround2{

    // For more information, see https://issuetracker.google.com/issues/36911528
    // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.

    public interface SoftKeyBoardStatusListener {
        void onKeyBoardShow(View rootView, int totalScreenHeight);
        void onKeyBoardHide(View rootView, int totalScreenHeight);
    }

    private int currentlyScrolled = 0;


    public static AndroidBug5497Workaround2 assistActivity (Activity activity, SoftKeyBoardStatusListener listener) {
        return new AndroidBug5497Workaround2(activity, listener);
    }

    private Activity activity;
    private View mChildOfContent;
    private int usableHeightPrevious;
    private int totalScreenHeight;
    private FrameLayout.LayoutParams frameLayoutParams;


    private AndroidBug5497Workaround2(Activity activity, final SoftKeyBoardStatusListener listener) {
        this.activity = activity;
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);  // 就是我们用setContentView放进去的View。
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent(listener);
            }
        });
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }

    private void possiblyResizeChildOfContent(SoftKeyBoardStatusListener listener) {
        int usableHeightNow = computeUsableHeight();

        if (usableHeightNow != usableHeightPrevious) {
            totalScreenHeight = mChildOfContent.getRootView().getHeight();

            int heightDifference = usableHeightPrevious - usableHeightNow;

            if (heightDifference > (totalScreenHeight/4)) {
                // keyboard probably just became visible

                frameLayoutParams.height = totalScreenHeight - heightDifference;
                listener.onKeyBoardShow(mChildOfContent, totalScreenHeight);
                //mChildOfContent.requestLayout();  // DO NOT request layout after scroll up to avoid white blank space between keyboard and content

            } else if (heightDifference < (totalScreenHeight/4) * -1) {
                // keyboard probably just became hidden
                handleShiftDown();
                frameLayoutParams.height = totalScreenHeight;
                mChildOfContent.requestLayout();
                listener.onKeyBoardHide(mChildOfContent, totalScreenHeight);
            }
            usableHeightPrevious = usableHeightNow;
        }

    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);
    }

    public void handleShiftDown() {

        currentlyScrolled = 0;
        mChildOfContent.scrollTo(0,0);
    }

    public void handleShiftUp(View focusedView) {

        if (((MainActivity)activity).DISABLE_IMMERSIVE_MODE_ON_KEYBOARD_OPEN){
            ((MainActivity)activity).disableImmersiveMode();
        }

        if (focusedView == null){
            Toast.makeText(activity, "focusedView is null", Toast.LENGTH_SHORT).show();
            return;
        }

        int[] location = new int[2];
        focusedView.getLocationInWindow(location);
        int absY = location[1];

        int oneFourth = totalScreenHeight/4;

        if (absY > oneFourth){

            int distanceToScroll = absY - oneFourth + currentlyScrolled;
            currentlyScrolled = distanceToScroll;
            mChildOfContent.scrollTo(0,distanceToScroll);
            Toast.makeText(activity, "Shift up " + distanceToScroll, Toast.LENGTH_SHORT).show();
        }
    }

}