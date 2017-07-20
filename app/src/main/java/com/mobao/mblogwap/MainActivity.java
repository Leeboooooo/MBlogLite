package com.mobao.mblogwap;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.shuyu.action.web.CustomActionWebView;

public class MainActivity extends AppCompatActivity {
    private CustomActionWebView mWebView;
    private ProgressBar mProgress;
    private RelativeLayout mP;
    private String mUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews(){
        mProgress = (ProgressBar) findViewById(R.id.progress_bar);
        mP = (RelativeLayout)findViewById(R.id.progress) ;
        mP.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        initWebView();
    }

    private void initWebView(){
        mWebView = (CustomActionWebView) findViewById(R.id.webview);
        mUrl = getIntent().getStringExtra("URL");
        if (TextUtils.isEmpty(mUrl)){
            mUrl = "https://mblog.ng/wap/index.action";
        }
        //加载url
        mWebView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl(mUrl);
            }
        }, 500);
        initWebSettings();
        initWebClient(); //内政
        initWebChromeClient(); //外交
    }

    private void initWebSettings(){
        WebSettings s = mWebView.getSettings();
        //支持获取手势焦点
        mWebView.requestFocusFromTouch();
        //支持JS
        s.setJavaScriptEnabled(true);
        //支持插件
        s.setPluginState(WebSettings.PluginState.ON);
        //设置适应屏幕
        s.setUseWideViewPort(true);
        s.setLoadWithOverviewMode(true);
        //支持缩放，隐藏原生的缩放空间
        s.setDisplayZoomControls(false);
        s.setSupportZoom(false);
        //支持内容重新布局
        s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        s.supportMultipleWindows();
        s.setSupportMultipleWindows(true);
        //设置缓存模式
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        s.setAppCacheEnabled(true);
        s.setAppCachePath(mWebView.getContext().getCacheDir().getAbsolutePath());

        //设置可访问文件
        s.setAllowFileAccess(true);
        //当调用requestFocus时为webview设置节点
        s.setNeedInitialFocus(true);
        //支持自动加载图片
        if (Build.VERSION.SDK_INT >= 19){
            s.setLoadsImagesAutomatically(true);
        }else {
            s.setLoadsImagesAutomatically(false);
        }
        //设置编码格式
        s.setDefaultTextEncodingName("UTF-8");
    }

    private void initWebClient(){
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showProgressBar(true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                showProgressBar(false);
            }

            //是否在Webview页面中加载新页面。不用跳转到手机浏览器
//            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                view.loadUrl(request.getUrl().toString());
//                return true;
//            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                if (url.contains("publishBlog_nav")){
                    Toast.makeText(MainActivity.this,"publishBlog_nav",Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            /**
             * 网络错误时的回掉
             * @param view
             * @param request
             * @param error
             */
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
            }
        });
    }

    private void initWebChromeClient(){
        mWebView.setWebChromeClient(new WebChromeClient(){
            private Bitmap mDefaultVideoPoster;//默认的视频展示图

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            @Override
            public Bitmap getDefaultVideoPoster() {
                if (mDefaultVideoPoster == null){
                    mDefaultVideoPoster = BitmapFactory.decodeResource(getResources(),R.mipmap.default_video_play);
                    return mDefaultVideoPoster;
                }
                return super.getDefaultVideoPoster();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //如果按下的是回退键且历史记录里确实还有数据
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()){
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.floating_btn:
                mWebView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("https://mblog.ng/t?l=c");
                        showProgressBar(true);
                    }
                }, 500);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mWebView != null) {
            mWebView.dismissAction();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void showProgressBar(boolean isShow){
        if (isShow){
            mP.setVisibility(View.VISIBLE);
        }else {
            mP.setVisibility(View.GONE);
        }
    }


}
