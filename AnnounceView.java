package view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dingda.app.R;
import com.dingda.app.activity.ActivityWebViewActivity;
import com.dingda.app.activity.GameWebViewActivity;
import com.dingda.app.activity.LoginActivity;
import com.dingda.app.activity.WebViewActivity;
import com.jtkj.manager.UserTokenManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import base.RxBus;
import bean.JpushModel;
import bean.User;
import utils.AppConfig;
import utils.AppUtils;
import utils.SharedPreferencesUtils;

import static utils.ScanUtils.TruncateUrlPage;


/**
 * Created by yangdong on 2017/9/5.
 */

public class AnnounceView extends LinearLayout implements View.OnClickListener {

    String mWebUrl;
    Context mContext;

    UpDown mUpDown;
    ObjectAnimator downAnimator, upAnimator;
    public String imageUrl;
    ImageView ivAnnoc, ivUpDown;
    LinearLayout ivCross;
    LinearLayout llView;
    TextView announceTitle;
    float up, width;
    LinearLayout llAnnounce;
    int llVIewwidth;
    public  boolean upDown = true;
    int height;
    int dialogHeight;
    int dialogWidth;
    float dialogY;
    Callback callback;
    public static boolean secondIn = false;

    public AnnounceView(Context context) {
        this(context, null);
    }

    public AnnounceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnnounceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView(mContext);
    }

    private void initView(Context mContext) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View inflate = layoutInflater.inflate(R.layout.view_announce, this);
        findViewById(R.id.iv_cross).setOnClickListener(this);
        findViewById(R.id.iv_annoc).setOnClickListener(this);
        ivCross = (LinearLayout) findViewById(R.id.iv_cross);
        ivAnnoc = (ImageView) findViewById(R.id.iv_annoc);
        ivUpDown = (ImageView) findViewById(R.id.iv_up_down);
        announceTitle = (TextView) findViewById(R.id.announce_title);
        llView = (LinearLayout) findViewById(R.id.ll_view);
        llAnnounce = (LinearLayout) findViewById(R.id.ll_announce);
    }

    boolean isFirst = true;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_annoc:
                Intent intent;
                String strUrlParam = TruncateUrlPage(mWebUrl);
                if (strUrlParam == null) {
                    if(mWebUrl.contains(AppConfig.kAdverKey)) {
                        intent = new Intent(mContext, WebViewActivity.class);
                        mWebUrl = mWebUrl+"?accessToken=" + UserTokenManager.getToken() +"&type=Diiing";
                        intent.putExtra("action", new String[]{mWebUrl});
                    }else if(mWebUrl.contains(AppConfig.kGameKey)){
                        intent = new Intent(mContext, GameWebViewActivity.class);
                        mWebUrl =  mWebUrl +"?accessToken=" + UserTokenManager.getToken() +"&type=Diiing";
                        intent.putExtra("action", new String[]{mWebUrl,"1"});
                    }else {
                        intent = new Intent(mContext, WebViewActivity.class);
                        intent.putExtra("action", new String[]{mWebUrl});
                    }
                }else {
                    if(mWebUrl.contains(AppConfig.kAdverKey)) {
                        intent = new Intent(mContext, WebViewActivity.class);
                        mWebUrl = mWebUrl+"&accessToken=" + UserTokenManager.getToken() +"&type=Diiing";
                        intent.putExtra("action", new String[]{mWebUrl});
                    }else if(mWebUrl.contains(AppConfig.kGameKey)){
                        intent = new Intent(mContext, GameWebViewActivity.class);
                        mWebUrl =  mWebUrl +"&accessToken=" + UserTokenManager.getToken()  +"&type=Diiing";
                        intent.putExtra("action", new String[]{mWebUrl,"1"});
                    }else {
                        intent = new Intent(mContext, WebViewActivity.class);
                        intent.putExtra("action", new String[]{mWebUrl});
                    }
                }


                mContext.startActivity(intent);
                String s = "AnnounceView";
                RxBus.getInstance().post(s);
                AnnounceView.this.invalidate();
                JpushModel jpushModel = new JpushModel();
                jpushModel.setPicUrl("");
                SharedPreferencesUtils.getInstance(mContext).setObject(AppConfig.AnnounceRead, jpushModel);
                break;
            case R.id.iv_cross:
                final LinearLayout llAnnounce = (LinearLayout) findViewById(R.id.ll_announce);
                Rect outRect = new Rect();
                ((Activity) mContext).getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
                int height = outRect.height();
                int dialogHeight = llAnnounce.getHeight();
                int dialogWidth = llAnnounce.getWidth();

                if (isFirst) {

                    Log.e("ObjectAnimator", "dialogWidth: " + dialogWidth + " ,dialogHeight: " + dialogHeight + " ,屏幕宽： " + getResources().getDisplayMetrics().widthPixels + " ,屏幕高： " + getResources().getDisplayMetrics().heightPixels);
                    dialogWidth = getResources().getDisplayMetrics().widthPixels * 3 / 4;
                    dialogHeight = dialogWidth;
                    isFirst = false;
                }
                Log.e("ObjectAnimator", "dialogWidth: " + dialogWidth + " ,dialogHeight: " + dialogHeight +
                        " ,屏幕宽： " + getResources().getDisplayMetrics().widthPixels +
                        " ,屏幕高： " + getResources().getDisplayMetrics().heightPixels);
                if (upDown) {
                    LinearLayout.LayoutParams layoutParams = (LayoutParams) llAnnounce.getLayoutParams();
                    layoutParams.height = dialogHeight;
                    layoutParams.setMargins((getResources().getDisplayMetrics().widthPixels - llVIewwidth) / 2, 0, (getResources().getDisplayMetrics().widthPixels - llVIewwidth) / 2, 0);
//                  layoutParams.weight =dialogHeight;
                    layoutParams.width = dialogWidth;
                    layoutParams.height = dialogHeight;
                    llAnnounce.setLayoutParams(layoutParams);
                    downAnimator = ObjectAnimator.ofFloat(llAnnounce, "y", ((height - AppUtils.dip2px(mContext, 40)) - dialogHeight) / 2, height - AppUtils.dip2px(mContext, 40));
                    downAnimator.setRepeatMode(ObjectAnimator.REVERSE);
                    downAnimator.setDuration(400);
                    downAnimator.start();
                    upDown = false;
                    mUpDown.downing();
                    if (downAnimator != null) {
                        downAnimator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                ivUpDown.setBackgroundResource(R.mipmap.arrow_up);
                                if (mUpDown != null)
                                    mUpDown.downed();
                            }
                        });
                    }

                } else {
                    upAnimator = ObjectAnimator.ofFloat(llAnnounce, "y", height - AppUtils.dip2px(mContext, 40), ((height - AppUtils.dip2px(mContext, 40)) - dialogHeight) / 2);
                    upAnimator.setRepeatMode(ObjectAnimator.REVERSE);
                    upAnimator.setDuration(400);
                    upAnimator.start();
                    upDown = true;
                    mUpDown.uping();
                    upAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            ivUpDown.setBackgroundResource(R.mipmap.arrow_down);
                            mUpDown.uped();
                        }
                    });

                }

                break;

        }
    }

    public void setUrl(String title, String imageUrl, String webUrl, boolean isDown) {
        Transformation transformation = new Transformation() {
            float imageWidth, imageHeight;

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public Bitmap transform(Bitmap source) {

                    imageWidth = getResources().getDisplayMetrics().widthPixels * 3 / 4;
                    imageHeight = imageWidth;
                    Log.e("ObjectAnimator", "计算后dialogWidth: " + dialogWidth + " ,dialogHeight: " + dialogHeight +
                            " ,屏幕宽： " + getResources().getDisplayMetrics().widthPixels +
                            " ,屏幕高： " + getResources().getDisplayMetrics().heightPixels);

                    if (source.getWidth() != 0 && source.getHeight() != 0) {
                        Bitmap result = Bitmap.createScaledBitmap(source, (int) imageWidth, (int) imageHeight, false);
                        if (result != source) {
                            // Same bitmap is returned if sizes are the same
                            source.recycle();
                        }
                        return result;

                    }

                return source;
            }

            @Override
            public String key() {
                return "transformation" + " source";
            }
        };

        Log.e("yangdong", "onReceive:picUrl11111 " + imageUrl);
        AnnounceView.this.invalidate();
        Log.e("yangdong", "secondIn: " + secondIn);
        if (isDown || secondIn) {
            secondIn = true;
            Picasso.with(mContext).load(imageUrl)
                    .error(R.mipmap.gary)
                    .fit()
                    .into(ivAnnoc);
        } else {
            Picasso.with(mContext).load(imageUrl)
                    .transform(transformation)
                    .into(ivAnnoc);
        }

        mWebUrl = webUrl;
        announceTitle.setText(title);
    }


    public interface UpDown {
        public void uped();

        public void uping();

        public void downed();

        public void downing();
    }

    public void setUpDown(UpDown upDown) {
        this.mUpDown = upDown;

    }





}
