package com.shiyar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;

public class App extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    WebView webView;
    SwipeRefreshLayout swipe;

    AdRequest adRequest = new AdRequest.Builder().build();

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;

    InterstitialAd interstitial = new InterstitialAd(App.this);
    private boolean isRunning;
    @Override
    protected void onStart() {
        super.onStart();
        isRunning = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRunning = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        interstitial.setAdUnitId(getString(R.string.ADMOB_INTERSTITIAL_ID));
        interstitial.setAdListener(new AdListener() {
            public void onAdLoaded() {
                displayInterstitial();
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                playAds();

            }
        },300000);


        drawerLayout = findViewById(R.id.drawer);

        navigationView = findViewById(R.id.navigationView);

        toggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.drawerOpen,R.string.drawerClose);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);




        MobileAds.initialize(this, "ca-app-pub-2175466776333013/2415104061");
        AdView mAdView;
        mAdView = findViewById(R.id.adView);
        mAdView.loadAd(adRequest);

        webView = (WebView) findViewById(R.id.webview);
        // loadUrl
        webView.loadUrl("http://shiyarjemo.com/app");
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new App.MyChrome());

        webView.clearCache(true);

        WebSettings webSettings = webView.getSettings();
        // سماح للجافا  سكربت بالعمل
        webSettings.setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        // لسماح لتحزين
        webSettings.setDomStorageEnabled(true);
        // لتمكين اعدادت viewPort
        webSettings.setUseWideViewPort(true);

        webSettings.setDisplayZoomControls(true);
        webSettings.setSupportZoom(true);



        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                webView.reload();
                swipe.setRefreshing(false);
            } });


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.arabic:
                webView.loadUrl("http://arabic-tv.shiyarjemo.com");
                break;

            case R.id.kurdish:
                webView.loadUrl("http://kurdish-tv.shiyarjemo.com");
                break;
            case R.id.match:
                webView.loadUrl("http://livesport.shiyarjemo.com");
                break;
            case R.id.sport:
                webView.loadUrl("http://sport-tv.shiyarjemo.com");
                break;

            case R.id.academy:
                webView.loadUrl("http://academy.codershiyar.com");
                break;

            case R.id.youtube:
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/codershiyar"));
                App.this.startActivity(webIntent);
                break;

            case R.id.editor:
                webView.loadUrl("http://shiyarjemo.com/editor");
                break;

            default:
                break;
        }
        drawerLayout.closeDrawers();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Drawable  drawable = menu.findItem(R.id.academy).getIcon();
    if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        return true;

    }
        return true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    private class MyChrome extends WebChromeClient {

        private View mCustomView;
        private CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        MyChrome() {}

        public Bitmap getDefaultVideoPoster()
        {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView()
        {
            ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, CustomViewCallback paramCustomViewCallback)
        {
            if (this.mCustomView != null)
            {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846);
        }
    }



    // If Ads are loaded, show Interstitial else show nothing.
    public void displayInterstitial() {

        if (isRunning && interstitial.isLoaded()) {
            interstitial.show();
        }

    }
    public void playAds(){

        interstitial.loadAd(adRequest);
    }


    public void openFB(View view){
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/app.livesport/"));
       startActivity(webIntent);
    }
    public void openEmail(View view){
        Intent webIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+ "team.codershiyar@gmail.com"));
        startActivity(webIntent);
    }
    public void openWebsite(View view){
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.shiyarjemo.com/"));
        startActivity(webIntent);
    }
    public void openGooglePlay(View view){
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=6668599579308712490"));
       startActivity(webIntent);
    }
    public void openWebSite(View view){
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://shiyarjemo.com/app"));
        startActivity(webIntent);
    }
}