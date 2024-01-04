package com.example.vault.privatebrowser;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebIconDatabase;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import android.webkit.WebViewClient;
import android.webkit.WebViewDatabase;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.example.vault.privatebrowser.adapter.SearchAdapter;
import com.example.vault.privatebrowser.adapter.UrlAdapter;
import com.example.vault.privatebrowser.model.DownloadFileEnt;
import com.example.vault.privatebrowser.util.AnimatedProgressBar;
import com.example.vault.privatebrowser.util.BookmarkDAL;
import com.example.vault.privatebrowser.util.BrowserHistoryDAL;
import com.example.vault.privatebrowser.util.DownloadFileDAL;
import com.example.vault.privatebrowser.util.DownloadHandler;
import com.example.vault.privatebrowser.util.SecureBrowserSharedPreferences;
import com.rey.material.app.Dialog;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.example.vault.R;
import com.example.vault.BaseActivity;
import com.example.vault.common.Constants;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.Common.BrowserMenuType;
import com.example.vault.utilities.Common.DownloadStatus;
import com.example.vault.utilities.Common.DownloadType;
import com.example.vault.utilities.Utilities;

import org.apache.http.protocol.HTTP;

@SuppressLint({"NewApi"})
public class SecureBrowserActivity extends BaseActivity {
    private static final int Download = 1;
    static final int FILECHOOSER_RESULTCODE = 1;
    static final int INPUT_FILE_REQUEST_CODE = 2;
    static List<String> autocompleted;

    public BrowserHistoryDAL browserHistoryDAL;

    public ClipboardManager clipboard;
    public Context con;

    public Dialog dialogDownload;

    public Dialog dialogUrl;

    public Dialog dialogUrlRecent;

    public long downloadReference;

    public boolean isClearChache;

    public boolean isClearCookies;

    public boolean isClearHistory;
    private boolean isDownloadedFile = false;

    public int isDownloadedFileItem = 0;
    private Boolean isLoading = Boolean.valueOf(false);

    public boolean isSaveFormData;
    private Boolean isvisible = Boolean.valueOf(true);
    private ImageView iv_settings;
    private LinearLayout ll_Bottom;
    LayoutParams ll_Hide_Params;
    LayoutParams ll_Show_Params;
    LinearLayout ll_background;
    private String mCameraPhotoPath;

    public Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    MenuItem mItemClrCacheOnExit;
    MenuItem mItemClrCookiesOnExit;
    MenuItem mItemClrHistoryOnExit;
    MenuItem mItemSaveFormData;
    private AnimatedProgressBar mProgressBar;
    private SearchAdapter mSearchAdapter;

    public ValueCallback<Uri> mUploadMessage;


    public WebView secureBrowser;
    SecureBrowserSharedPreferences secureBrowserSharedPreferences;

    public AutoCompleteTextView txturl;

    public List<String> urlList = null;

    private class MyBrowser extends WebViewClient implements DownloadListener {
        private MyBrowser() {
        }

        public void onPageStarted(WebView webView, String str, Bitmap bitmap) {
            super.onPageStarted(webView, str, bitmap);
            SecureBrowserActivity.this.txturl.setText(str);
            SecureBrowserActivity.this.setIsLoading();
        }


        public boolean shouldOverrideUrlLoading(WebView webView, String str) {
            boolean z;
            try {
                String fileExtensionFromUrl = MimeTypeMap.getFileExtensionFromUrl(str);
                if (fileExtensionFromUrl != null) {
                    String mimeTypeFromExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtensionFromUrl);
                    if (mimeTypeFromExtension != null) {
                        if (!mimeTypeFromExtension.toLowerCase().contains("video") && !fileExtensionFromUrl.toLowerCase().contains("mov") && !fileExtensionFromUrl.toLowerCase().contains("avi") && !fileExtensionFromUrl.toLowerCase().contains("flv") && !fileExtensionFromUrl.toLowerCase().contains("mkv") && !fileExtensionFromUrl.toLowerCase().contains("wmv")) {
                            if (!fileExtensionFromUrl.toLowerCase().contains("mp4")) {
                                if (!fileExtensionFromUrl.toLowerCase().contains("mp3")) {
                                    if (!fileExtensionFromUrl.toLowerCase().contains("wav")) {
                                        if (!fileExtensionFromUrl.toLowerCase().contains(".jpg") && !fileExtensionFromUrl.toLowerCase().contains(".png") && !fileExtensionFromUrl.toLowerCase().contains(".gif")) {
                                            fileExtensionFromUrl.toLowerCase().contains(".bmp");
                                        }
                                    }
                                }
                            }
                        }
                        String guessFileName = URLUtil.guessFileName(str, null, null);
                        SecureBrowserActivity.this.isDownloadedFileItem = DownloadType.Video.ordinal();
                        SecureBrowserActivity.this.downloadFile("Video", str, guessFileName, SecureBrowserActivity.this.isDownloadedFileItem);
                        z = false;
                        if (z) {
                            webView.loadUrl(str);
                            SecureBrowserActivity.this.browserHistoryDAL.AddBrowserHistory(str);
                        }
                    }
                    z = true;
                    if (z) {

                        webView.loadUrl(str);
                        SecureBrowserActivity.this.browserHistoryDAL.AddBrowserHistory(str);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            webView.setDownloadListener(this);
            return true;
        }

        public void onPageFinished(WebView webView, String str) {
            super.onPageFinished(webView, str);
            Common.LastWebBrowserUrl = str;
            SecureBrowserActivity.this.setIsFinishedLoading();
        }

        public void onDownloadStart(String str, String str2, String str3, String str4, long j) {
            String guessFileName = URLUtil.guessFileName(str, str3, str4);
            final String str5 = str;
            final String str6 = str2;
            final String str7 = str3;
            final String str8 = str4;
            OnClickListener r0 = new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == -1) {
                        DownloadHandler.onDownloadStart(SecureBrowserActivity.this, str5, str6, str7, str8, false);
                    }
                }
            };
            new Builder(SecureBrowserActivity.this).setTitle(guessFileName).setMessage(SecureBrowserActivity.this.getResources().getString(R.string.dialog_download)).setPositiveButton(SecureBrowserActivity.this.getResources().getString(R.string.action_download), r0).setNegativeButton(SecureBrowserActivity.this.getResources().getString(R.string.action_cancel), r0).show();
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        private MyWebChromeClient() {
        }

        public void onProgressChanged(WebView webView, int i) {
            SecureBrowserActivity.this.updateProgress(i);
        }

        public void openFileChooser(ValueCallback<Uri> valueCallback) {
            SecureBrowserActivity.this.mUploadMessage = valueCallback;
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.addCategory("android.intent.category.OPENABLE");
            intent.setType("image/*");
            SecurityLocksCommon.IsAppDeactive = false;
            SecureBrowserActivity.this.startActivityForResult(Intent.createChooser(intent, "File Chooser"), 1);
        }

        public void openFileChooser(ValueCallback valueCallback, String str) {
            SecureBrowserActivity.this.mUploadMessage = valueCallback;
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.addCategory("android.intent.category.OPENABLE");
            intent.setType("*/*");
            SecurityLocksCommon.IsAppDeactive = false;
            SecureBrowserActivity.this.startActivityForResult(Intent.createChooser(intent, "File Browser"), 1);
        }

        public void openFileChooser(ValueCallback<Uri> valueCallback, String str, String str2) {
            SecureBrowserActivity.this.mUploadMessage = valueCallback;
            try {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
                if (!file.exists()) {
                    file.mkdirs();
                }
                StringBuilder sb = new StringBuilder();
                sb.append(file);
                sb.append(File.separator);
                sb.append("IMG_");
                sb.append(String.valueOf(System.currentTimeMillis()));
                sb.append(".jpg");
                SecureBrowserActivity.this.mCapturedImageURI = Uri.fromFile(new File(sb.toString()));
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra("output", SecureBrowserActivity.this.mCapturedImageURI);
                Intent intent2 = new Intent("android.intent.action.GET_CONTENT");
                intent2.addCategory("android.intent.category.OPENABLE");
                intent2.setType("image/*");
                Intent createChooser = Intent.createChooser(intent2, "File Chooser");
                createChooser.putExtra("android.intent.extra.INITIAL_INTENTS", new Parcelable[]{intent});
                SecurityLocksCommon.IsAppDeactive = false;
                SecureBrowserActivity.this.startActivityForResult(createChooser, 1);
            } catch (Exception unused) {
            }
        }

        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
            SecureBrowserActivity.this.showFileChooser(valueCallback);
            return true;
        }
    }


    public class EditorActionListener implements OnEditorActionListener {
        public EditorActionListener() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (((keyEvent != null && keyEvent.getKeyCode() == 66) || i == 6) && SecureBrowserActivity.this.txturl.getText().toString().length() > 0) {
                SecureBrowserActivity.this.browserHistoryDAL.OpenWrite();
                if (SecureBrowserActivity.this.txturl.getText().toString().contains(Constants.HTTP) || SecureBrowserActivity.this.txturl.getText().toString().contains(Constants.HTTPS)) {
                    SecureBrowserActivity.this.secureBrowser.loadUrl(SecureBrowserActivity.this.txturl.getText().toString());
                    SecureBrowserActivity.this.txturl.setText(SecureBrowserActivity.this.txturl.getText().toString());
                    SecureBrowserActivity.this.browserHistoryDAL.AddBrowserHistory(SecureBrowserActivity.this.txturl.getText().toString());
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(Constants.HTTP);
                    sb.append(SecureBrowserActivity.this.txturl.getText().toString());
                    String sb2 = sb.toString();
                    SecureBrowserActivity.this.secureBrowser.loadUrl(sb2);
                    SecureBrowserActivity.this.txturl.setText(sb2);
                    SecureBrowserActivity.this.browserHistoryDAL.AddBrowserHistory(sb2);
                }
                SecureBrowserActivity.this.browserHistoryDAL.close();
            }
            return false;
        }
    }

    public class MenuItemClickListener implements OnMenuItemClickListener {
        public MenuItemClickListener() {
        }

        public boolean onMenuItemClick(MenuItem menuItem) {
            int itemId = menuItem.getItemId();
            if (itemId != R.id.save_forms_data) {
                if (itemId == R.id.clear_chache) { /*2131296401*/
                    SecureBrowserActivity.this.clearCache();
                    Toast.makeText(SecureBrowserActivity.this, "Cache cleared!", Toast.LENGTH_SHORT).show();
                } else if (itemId == R.id.clear_chache_on_exit) { /*2131296402*/
                    SecureBrowserActivity.this.mItemClrCacheOnExit.setChecked(!SecureBrowserActivity.this.mItemClrCacheOnExit.isChecked());
                    SecureBrowserActivity.this.secureBrowserSharedPreferences.setClearCache(Boolean.valueOf(SecureBrowserActivity.this.mItemClrCacheOnExit.isChecked()));
                    SecureBrowserActivity.this.isClearChache = SecureBrowserActivity.this.mItemClrCacheOnExit.isChecked();
                } else if (itemId == R.id.clear_cookies) { /*2131296403*/
                    SecureBrowserActivity.this.clearCookies();
                    Toast.makeText(SecureBrowserActivity.this, "Cookies cleared!", Toast.LENGTH_SHORT).show();
                } else if (itemId == R.id.clear_cookies_on_exit) { /*2131296404*/
                    SecureBrowserActivity.this.mItemClrCookiesOnExit.setChecked(!SecureBrowserActivity.this.mItemClrCookiesOnExit.isChecked());
                    SecureBrowserActivity.this.secureBrowserSharedPreferences.setClearCookies(Boolean.valueOf(SecureBrowserActivity.this.mItemClrCookiesOnExit.isChecked()));
                    SecureBrowserActivity.this.isClearCookies = SecureBrowserActivity.this.mItemClrCookiesOnExit.isChecked();
                } else if (itemId == R.id.clear_history) { /*2131296405*/
                    SecureBrowserActivity.this.clearHistory();
                    Toast.makeText(SecureBrowserActivity.this, "History cleared!", Toast.LENGTH_SHORT).show();
                } else if (itemId == R.id.clear_history_on_exit) { /*2131296406*/
                    SecureBrowserActivity.this.mItemClrHistoryOnExit.setChecked(!SecureBrowserActivity.this.mItemClrHistoryOnExit.isChecked());
                    SecureBrowserActivity.this.secureBrowserSharedPreferences.setClearHistory(Boolean.valueOf(SecureBrowserActivity.this.mItemClrHistoryOnExit.isChecked()));
                    SecureBrowserActivity.this.isClearHistory = SecureBrowserActivity.this.mItemClrHistoryOnExit.isChecked();
                }
            } else {
                SecureBrowserActivity.this.mItemSaveFormData.setChecked(!SecureBrowserActivity.this.mItemSaveFormData.isChecked());
                SecureBrowserActivity.this.secureBrowserSharedPreferences.setSaveFormData(Boolean.valueOf(SecureBrowserActivity.this.mItemSaveFormData.isChecked()));
                SecureBrowserActivity.this.isSaveFormData = SecureBrowserActivity.this.mItemSaveFormData.isChecked();
            }
            return false;
        }
    }

    public class TouchListener implements OnTouchListener {
        public TouchListener() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (!(motionEvent.getX() > ((float) ((SecureBrowserActivity.this.txturl.getWidth() - SecureBrowserActivity.this.txturl.getPaddingRight()) - SecureBrowserActivity.this.txturl.getCompoundDrawables()[2].getIntrinsicWidth())))) {
                return false;
            }
            if (motionEvent.getAction() == 1) {
                SecureBrowserActivity.this.refreshOrStop();
            }
            return true;
        }
    }


    @SuppressLint({"SetJavaScriptEnabled"})
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_secure_browser);
        getWindow().addFlags(128);
        this.con = this;
        SecurityLocksCommon.IsAppDeactive = true;
        Common.CurrentActivity = this;
        this.ll_Bottom = (LinearLayout) findViewById(R.id.ll_Bottom);
        this.secureBrowser = (WebView) findViewById(R.id.webviewsecurebrowser);
        this.iv_settings = (ImageView) findViewById(R.id.iv_settings);
        this.txturl = (AutoCompleteTextView) findViewById(R.id.txturl);
        this.mProgressBar = (AnimatedProgressBar) findViewById(R.id.progress_view);
        this.clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.ll_Show_Params = new LayoutParams(-1, -2);
        this.ll_Hide_Params = new LayoutParams(-1, 0);
        this.dialogUrl = new Dialog(this, R.style.FullHeightDialog);
        this.dialogUrlRecent = new Dialog(this, R.style.FullHeightDialog);
        this.dialogDownload = new Dialog(this, R.style.FullHeightDialog);
        this.browserHistoryDAL = new BrowserHistoryDAL(this);
        this.secureBrowserSharedPreferences = SecureBrowserSharedPreferences.GetObject(this);
        this.isClearChache = this.secureBrowserSharedPreferences.getClearCache().booleanValue();
        this.isClearHistory = this.secureBrowserSharedPreferences.getClearHistory().booleanValue();
        this.isClearCookies = this.secureBrowserSharedPreferences.getClearCookies().booleanValue();
        this.isSaveFormData = this.secureBrowserSharedPreferences.getSaveFormData().booleanValue();
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, 17367043, autocompleted);
        this.dialogUrl.setContentView((int) R.layout.customurldialog);
        this.dialogDownload.setContentView((int) R.layout.customurldialog);
        this.dialogUrlRecent.setContentView((int) R.layout.customurldialog);
        this.txturl.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_refresh, 0);
        registerForContextMenu(this.secureBrowser);
        WebSettings settings = this.secureBrowser.getSettings();
        settings.setDefaultZoom(ZoomDensity.CLOSE);
        settings.setBuiltInZoomControls(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setPluginState(PluginState.ON);
        settings.setDomStorageEnabled(true);
        settings.setSupportZoom(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setSupportMultipleWindows(false);
        settings.setJavaScriptEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        if (this.isSaveFormData) {
            if (VERSION.SDK_INT < 18) {
                settings.setSavePassword(true);
            }
            settings.setSaveFormData(true);
        } else {
            if (VERSION.SDK_INT < 18) {
                settings.setSavePassword(false);
            }
            settings.setSaveFormData(false);
        }
        this.secureBrowser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        this.secureBrowser.setClickable(true);
        this.secureBrowser.setInitialScale(1);
        this.secureBrowser.setLongClickable(true);
        this.secureBrowser.requestFocus(163);
        this.secureBrowser.setWebViewClient(new MyBrowser());
        this.secureBrowser.setWebChromeClient(new MyWebChromeClient());
        registerForContextMenu(this.secureBrowser);
        if (VERSION.SDK_INT >= 5) {
            try {
                WebSettings.class.getMethod("setDomStorageEnabled", new Class[]{Boolean.TYPE}).invoke(settings, new Object[]{Boolean.TRUE});
                WebSettings.class.getMethod("setDatabaseEnabled", new Class[]{Boolean.TYPE}).invoke(settings, new Object[]{Boolean.TRUE});
                Method method = WebSettings.class.getMethod("setDatabasePath", new Class[]{String.class});
                StringBuilder sb = new StringBuilder();
                sb.append("/data/data/");
                sb.append(getPackageName());
                sb.append("/databases/");
                method.invoke(settings, new Object[]{sb.toString()});
                WebSettings.class.getMethod("setAppCacheMaxSize", new Class[]{Long.TYPE}).invoke(settings, new Object[]{Integer.valueOf(8388608)});
                Method method2 = WebSettings.class.getMethod("setAppCachePath", new Class[]{String.class});
                StringBuilder sb2 = new StringBuilder();
                sb2.append("/data/data/");
                sb2.append(getPackageName());
                sb2.append("/cache/");
                method2.invoke(settings, new Object[]{sb2.toString()});
                WebSettings.class.getMethod("setAppCacheEnabled", new Class[]{Boolean.TYPE}).invoke(settings, new Object[]{Boolean.TRUE});
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException unused) {
            }
        }
        this.browserHistoryDAL.OpenRead();
        autocompleted = this.browserHistoryDAL.GetBrowserAutoCompletedHistories();
        this.browserHistoryDAL.close();

        this.txturl.setAdapter(arrayAdapter);
        this.txturl.setDropDownBackgroundResource(R.color.White);
        AutoCompleteTextView autoCompleteTextView = this.txturl;


        autoCompleteTextView.setOnTouchListener(new TouchListener());
        AutoCompleteTextView autoCompleteTextView2 = this.txturl;


        autoCompleteTextView2.setOnEditorActionListener(new EditorActionListener());
        new Thread(new Runnable() {
            public void run() {
                SecureBrowserActivity secureBrowserActivity = SecureBrowserActivity.this;
                secureBrowserActivity.initializeSearchSuggestions(secureBrowserActivity.txturl);
            }
        }).run();
        this.secureBrowser.loadUrl(Common.LastWebBrowserUrl);
    }

    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenuInfo contextMenuInfo) {
        if (view instanceof WebView) {
            HitTestResult hitTestResult = ((WebView) view).getHitTestResult();
            String extra = hitTestResult.getExtra();
            String guessFileName = URLUtil.guessFileName(extra, null, null);
            if (hitTestResult != null) {
                try {
                    int type = hitTestResult.getType();
                    if (type != 5) {
                        if (type != 8) {
                            if (!hitTestResult.getExtra().endsWith(".mp4") && !hitTestResult.getExtra().endsWith(".3gp") && !hitTestResult.getExtra().endsWith(".avi") && !hitTestResult.getExtra().endsWith(".flv")) {
                                if (!hitTestResult.getExtra().endsWith(".mkv")) {
                                    if (!hitTestResult.getExtra().endsWith(".mp3")) {
                                        if (!hitTestResult.getExtra().endsWith(".wav")) {
                                            if (!hitTestResult.getExtra().endsWith(".pdf") && !hitTestResult.getExtra().endsWith(".doc") && !hitTestResult.getExtra().endsWith(".docx") && !hitTestResult.getExtra().endsWith(".ppt") && !hitTestResult.getExtra().endsWith(".pptx") && !hitTestResult.getExtra().endsWith(".xls") && !hitTestResult.getExtra().endsWith(".xlsx") && !hitTestResult.getExtra().endsWith(".csv") && !hitTestResult.getExtra().endsWith(".dbk") && !hitTestResult.getExtra().endsWith(".dot") && !hitTestResult.getExtra().endsWith(".dotx") && !hitTestResult.getExtra().endsWith(".gdoc") && !hitTestResult.getExtra().endsWith(".pdax") && !hitTestResult.getExtra().endsWith(".pda") && !hitTestResult.getExtra().endsWith(".rtf") && !hitTestResult.getExtra().endsWith(".rpt") && !hitTestResult.getExtra().endsWith(".stw") && !hitTestResult.getExtra().endsWith(".txt") && !hitTestResult.getExtra().endsWith(".uof") && !hitTestResult.getExtra().endsWith(".uoml") && !hitTestResult.getExtra().endsWith(".wps") && !hitTestResult.getExtra().endsWith(".wpt") && !hitTestResult.getExtra().endsWith(".wrd") && !hitTestResult.getExtra().endsWith(".xps") && !hitTestResult.getExtra().endsWith(".epub")) {
                                                if (!hitTestResult.getExtra().endsWith(".xml")) {
                                                    if (!hitTestResult.getExtra().endsWith(".7z") && !hitTestResult.getExtra().endsWith(".ace") && !hitTestResult.getExtra().endsWith(".bik") && !hitTestResult.getExtra().endsWith(".bin") && !hitTestResult.getExtra().endsWith(".bkf") && !hitTestResult.getExtra().endsWith(".bzip2") && !hitTestResult.getExtra().endsWith(".cab") && !hitTestResult.getExtra().endsWith(".daa") && !hitTestResult.getExtra().endsWith(".gzip") && !hitTestResult.getExtra().endsWith(".jar") && !hitTestResult.getExtra().endsWith(".apk") && !hitTestResult.getExtra().endsWith(".xap") && !hitTestResult.getExtra().endsWith(".lzip") && !hitTestResult.getExtra().endsWith(".rar") && !hitTestResult.getExtra().endsWith(".tgz") && !hitTestResult.getExtra().endsWith(".iso") && !hitTestResult.getExtra().endsWith(".img") && !hitTestResult.getExtra().endsWith(".mdx") && !hitTestResult.getExtra().endsWith(".dmg") && !hitTestResult.getExtra().endsWith(".acp") && !hitTestResult.getExtra().endsWith(".amf") && !hitTestResult.getExtra().endsWith(".4db") && !hitTestResult.getExtra().endsWith(".4dr") && !hitTestResult.getExtra().endsWith(".ave") && !hitTestResult.getExtra().endsWith(".fm") && !hitTestResult.getExtra().endsWith(".acl") && !hitTestResult.getExtra().endsWith(".ans") && !hitTestResult.getExtra().endsWith(".ots") && !hitTestResult.getExtra().endsWith(".egt") && !hitTestResult.getExtra().endsWith(".ftx") && !hitTestResult.getExtra().endsWith(".lwp") && !hitTestResult.getExtra().endsWith(".nb") && !hitTestResult.getExtra().endsWith(".nbp") && !hitTestResult.getExtra().endsWith(".odm") && !hitTestResult.getExtra().endsWith(".odt") && !hitTestResult.getExtra().endsWith(".ott") && !hitTestResult.getExtra().endsWith(".via") && !hitTestResult.getExtra().endsWith(".wps") && !hitTestResult.getExtra().endsWith(".wrf") && !hitTestResult.getExtra().endsWith(".wri") && !hitTestResult.getExtra().endsWith(".org") && !hitTestResult.getExtra().endsWith(".ahk") && !hitTestResult.getExtra().endsWith(".as") && !hitTestResult.getExtra().endsWith(".bat") && !hitTestResult.getExtra().endsWith(".bas") && !hitTestResult.getExtra().endsWith(".hta") && !hitTestResult.getExtra().endsWith(".ijs") && !hitTestResult.getExtra().endsWith(".js") && !hitTestResult.getExtra().endsWith(".ncf") && !hitTestResult.getExtra().endsWith(".nut") && !hitTestResult.getExtra().endsWith(".sdl") && !hitTestResult.getExtra().endsWith(".au") && !hitTestResult.getExtra().endsWith(".raw") && !hitTestResult.getExtra().endsWith(".pac") && !hitTestResult.getExtra().endsWith(".m4a") && !hitTestResult.getExtra().endsWith(".ab2") && !hitTestResult.getExtra().endsWith(".via") && !hitTestResult.getExtra().endsWith(".wps") && !hitTestResult.getExtra().endsWith(".wrf") && !hitTestResult.getExtra().endsWith(".wri") && !hitTestResult.getExtra().endsWith(".ab3") && !hitTestResult.getExtra().endsWith(".aws") && !hitTestResult.getExtra().endsWith(".clf") && !hitTestResult.getExtra().endsWith(".ods") && !hitTestResult.getExtra().endsWith(".vc") && !hitTestResult.getExtra().endsWith(".bak") && !hitTestResult.getExtra().endsWith(".bdf") && !hitTestResult.getExtra().endsWith(".tos") && !hitTestResult.getExtra().endsWith(".exe") && !hitTestResult.getExtra().endsWith(".msg") && !hitTestResult.getExtra().endsWith(".dtp") && !hitTestResult.getExtra().endsWith(".pub")) {
                                                        if (!hitTestResult.getExtra().endsWith(".zip")) {
                                                            return;
                                                        }
                                                    }
                                                    this.isDownloadedFileItem = DownloadType.Miscellaneous.ordinal();
                                                    downloadFile("Miscellaneous", extra, guessFileName, this.isDownloadedFileItem);
                                                }
                                            }
                                            this.isDownloadedFileItem = DownloadType.Document.ordinal();
                                            downloadFile("Document", extra, guessFileName, this.isDownloadedFileItem);
                                        }
                                    }
                                    this.isDownloadedFileItem = DownloadType.Music.ordinal();
                                    downloadFile("Music", extra, guessFileName, this.isDownloadedFileItem);
                                }
                            }
                            this.isDownloadedFileItem = DownloadType.Video.ordinal();
                            downloadFile("Video", extra, guessFileName, this.isDownloadedFileItem);
                        }
                    }
                    this.isDownloadedFileItem = DownloadType.Photo.ordinal();
                    String format = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    StringBuilder sb = new StringBuilder();
                    sb.append("Download_");
                    sb.append(format);
                    sb.append(".jpg");
                    downloadFile("Image", extra, sb.toString(), this.isDownloadedFileItem);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void downloadFile(String str, final String str2, String str3, int i) {
        final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), str3);
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView((int) R.layout.download_dialog);
        dialog.setCancelable(true);
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_save_media);
        TextView textView = (TextView) dialog.findViewById(R.id.tv_save_media);
        LinearLayout linearLayout2 = (LinearLayout) dialog.findViewById(R.id.ll_copy_url);
        TextView textView2 = (TextView) dialog.findViewById(R.id.tv_copy_url);
        StringBuilder sb = new StringBuilder();
        sb.append("Save ");
        sb.append(str);
        textView.setText(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Copy Url of ");
        sb2.append(str);
        textView2.setText(sb2.toString());
        final String str4 = str2;
        final String str5 = str3;
        final int i2 = i;
        final Dialog dialog2 = dialog;
        View.OnClickListener r0 = new View.OnClickListener() {
            public void onClick(View view) {
                Uri parse = Uri.parse(str4);
                parse.getLastPathSegment();
                String str = "";
                try {
                    Request request = new Request(parse);
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(1);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, str5);
                    SecureBrowserActivity.this.downloadReference = ((DownloadManager) SecureBrowserActivity.this.getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request);
                    DownloadFileEnt downloadFileEnt = new DownloadFileEnt();
                    downloadFileEnt.SetFileDownloadPath(file.getAbsolutePath());
                    downloadFileEnt.SetFileName(str5);
                    downloadFileEnt.SetReferenceId(String.valueOf(SecureBrowserActivity.this.downloadReference));
                    downloadFileEnt.SetStatus(DownloadStatus.InProgress.ordinal());
                    downloadFileEnt.SetDownloadFileUrl(str4);
                    downloadFileEnt.SetDownloadType(i2);
                    DownloadFileDAL downloadFileDAL = new DownloadFileDAL(SecureBrowserActivity.this);
                    downloadFileDAL.OpenWrite();
                    downloadFileDAL.AddDownloadFile(downloadFileEnt);
                    downloadFileDAL.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (str4.contains("image")) {
                        String str2 = str4;
                        str = SecureBrowserActivity.this.storeImage(BitmapFactory.decodeStream(new ByteArrayInputStream(Base64.decode(str2.substring(str2.indexOf(",") + 1).getBytes(), 0))));
                    }
                    dialog2.dismiss();
                    DownloadFileEnt downloadFileEnt2 = new DownloadFileEnt();
                    StringBuilder sb = new StringBuilder();
                    sb.append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
                    sb.append(File.separator);
                    sb.append(str);
                    downloadFileEnt2.SetFileDownloadPath(sb.toString());
                    downloadFileEnt2.SetFileName(str);
                    downloadFileEnt2.SetReferenceId(String.valueOf(SecureBrowserActivity.this.downloadReference));
                    downloadFileEnt2.SetStatus(DownloadStatus.Completed.ordinal());
                    downloadFileEnt2.SetDownloadFileUrl(str4);
                    downloadFileEnt2.SetDownloadType(i2);
                    DownloadFileDAL downloadFileDAL2 = new DownloadFileDAL(SecureBrowserActivity.this);
                    downloadFileDAL2.OpenWrite();
                    downloadFileDAL2.AddDownloadFile(downloadFileEnt2);
                    downloadFileDAL2.close();
                    try {
                        downloadFileDAL2.OpenRead();
                        String MovePhotoFile = downloadFileDAL2.MovePhotoFile(downloadFileEnt2.GetFileDownloadPath(), downloadFileEnt2.GetFileName());
                        if (MovePhotoFile.length() > 0) {
                            downloadFileDAL2.AddPhotoToDatabase(downloadFileEnt2.GetFileName(), MovePhotoFile);
                        }
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
                dialog2.dismiss();
            }
        };
        linearLayout.setOnClickListener(r0);
        linearLayout2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SecureBrowserActivity.this.clipboard.setPrimaryClip(ClipData.newPlainText("url", str2));
                Toast.makeText(SecureBrowserActivity.this, "Url Copied!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public String storeImage(Bitmap bitmap) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
        file.mkdirs();
        String format = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        StringBuilder sb = new StringBuilder();
        sb.append("Download_");
        sb.append(format);
        sb.append(".jpg");
        String sb2 = sb.toString();
        try {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(file.toString());
            sb3.append(File.separator);
            sb3.append(sb2);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(sb3.toString()));
            bitmap.compress(CompressFormat.JPEG, 100, bufferedOutputStream);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            return sb2;
        } catch (FileNotFoundException e) {
            StringBuilder sb4 = new StringBuilder();
            sb4.append("Error saving image file: ");
            sb4.append(e.getMessage());
            Log.w("TAG", sb4.toString());
            return "";
        } catch (IOException e2) {
            StringBuilder sb5 = new StringBuilder();
            sb5.append("Error saving image file: ");
            sb5.append(e2.getMessage());
            Log.w("TAG", sb5.toString());
            return "";
        }
    }

    private File createImageFile() throws IOException {
        String format = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        StringBuilder sb = new StringBuilder();
        sb.append("JPEG_");
        sb.append(format);
        sb.append("_");
        return File.createTempFile(sb.toString(), ".jpg", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
    }

    public void showFileChooser(ValueCallback<Uri[]> valueCallback) {
        File file;
        ValueCallback<Uri[]> valueCallback2 = this.mFilePathCallback;
        if (valueCallback2 != null) {
            valueCallback2.onReceiveValue(null);
        }
        this.mFilePathCallback = valueCallback;
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                file = createImageFile();
                intent.putExtra("PhotoPath", this.mCameraPhotoPath);
            } catch (IOException unused2) {
                file = null;
            }
            if (file != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("file:");
                sb.append(file.getAbsolutePath());
                this.mCameraPhotoPath = sb.toString();
                intent.putExtra("output", Uri.fromFile(file));
            } else {
                intent = null;
            }
        }
        Intent intent2 = new Intent("android.intent.action.GET_CONTENT");
        intent2.addCategory("android.intent.category.OPENABLE");
        intent2.setType("image/*");
        Intent[] intentArr = intent != null ? new Intent[]{intent} : new Intent[0];
        Intent intent3 = new Intent("android.intent.action.CHOOSER");
        intent3.putExtra("android.intent.extra.INTENT", intent2);
        intent3.putExtra("android.intent.extra.TITLE", "File Chooser");
        intent3.putExtra("android.intent.extra.INITIAL_INTENTS", intentArr);
        SecurityLocksCommon.IsAppDeactive = false;
        startActivityForResult(intent3, 2);
    }


    public void onActivityResult(int i, int i2, Intent intent) {
        Uri[] uriArr;
        Object obj;
        SecurityLocksCommon.IsAppDeactive = true;
        if (i == 1) {
            if (this.mUploadMessage != null) {
                if (i2 != -1) {
                    obj = null;
                } else if (intent == null) {
                    try {
                        obj = this.mCapturedImageURI;
                    } catch (Exception e) {
                        e.printStackTrace();
                        obj = null;
                    }
                } else {
                    obj = intent.getData();
                }
                this.mUploadMessage.onReceiveValue((Uri) obj);
                this.mUploadMessage = null;
            }
        } else if (i != 2 || this.mFilePathCallback == null) {
            super.onActivityResult(i, i2, intent);
        } else {
            if (i2 == -1) {
                if (intent == null) {
                    String str = this.mCameraPhotoPath;
                    if (str != null) {
                        uriArr = new Uri[]{Uri.parse(str)};
                        this.mFilePathCallback.onReceiveValue(uriArr);
                        this.mFilePathCallback = null;
                    }
                } else {
                    String dataString = intent.getDataString();
                    if (dataString != null) {
                        uriArr = new Uri[]{Uri.parse(dataString)};
                        this.mFilePathCallback.onReceiveValue(uriArr);
                        this.mFilePathCallback = null;
                    }
                }
            }
            uriArr = null;
            this.mFilePathCallback.onReceiveValue(uriArr);
            this.mFilePathCallback = null;
        }
    }


    public void initializeSearchSuggestions(final AutoCompleteTextView autoCompleteTextView) {
        autoCompleteTextView.setThreshold(2);
        autoCompleteTextView.setDropDownWidth(-1);
        autoCompleteTextView.setDropDownAnchor(R.id.ll_sb_top_baar);
        autoCompleteTextView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                try {
                    String charSequence = ((TextView) view.findViewById(R.id.url)).getText().toString();
                    if (charSequence.startsWith(SecureBrowserActivity.this.getString(R.string.suggestion))) {
                        charSequence = ((TextView) view.findViewById(R.id.title)).getText().toString();
                    } else {
                        autoCompleteTextView.setText(charSequence);
                    }
                    SecureBrowserActivity.this.searchTheWeb(charSequence);
                    ((InputMethodManager) SecureBrowserActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);
                    if (SecureBrowserActivity.this.secureBrowser != null) {
                        SecureBrowserActivity.this.secureBrowser.requestFocus();
                    }
                } catch (NullPointerException unused) {
                    Log.e("Browser Error: ", "NullPointerException on item click");
                }
            }
        });
        autoCompleteTextView.setSelectAllOnFocus(true);
        this.mSearchAdapter = new SearchAdapter(this);
        autoCompleteTextView.setAdapter(this.mSearchAdapter);
    }


    public void searchTheWeb(String str) {
        if (!str.equals("")) {
            String str2 = "http://www.google.com/search?q=";
            String trim = str.trim();
            this.secureBrowser.stopLoading();
            if (trim.startsWith("www.")) {
                StringBuilder sb = new StringBuilder();
                sb.append(Constants.HTTP);
                sb.append(trim);
                trim = sb.toString();
            } else if (trim.startsWith("ftp.")) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("ftp://");
                sb2.append(trim);
                trim = sb2.toString();
            }
            boolean contains = trim.contains(".");
            boolean z = true;
            boolean z2 = TextUtils.isDigitsOnly(trim.replace(".", "")) && trim.replace(".", "").length() >= 4 && trim.contains(".");
            boolean contains2 = trim.contains("about:");
            boolean z3 = trim.startsWith("ftp://") || trim.startsWith(Constants.HTTP) || trim.startsWith(Constants.FILE) || trim.startsWith(Constants.HTTPS) || z2;
            if ((!trim.contains(" ") && contains) || contains2) {
                z = false;
            }
            if (z2 && (!trim.startsWith(Constants.HTTP) || !trim.startsWith(Constants.HTTPS))) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append(Constants.HTTP);
                sb3.append(trim);
                trim = sb3.toString();
            }
            if (z) {
                try {
                    trim = URLEncoder.encode(trim, HTTP.UTF_8);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                WebView webView = this.secureBrowser;
                StringBuilder sb4 = new StringBuilder();
                sb4.append(str2);
                sb4.append(trim);
                webView.loadUrl(sb4.toString());
            } else if (!z3) {
                WebView webView2 = this.secureBrowser;
                StringBuilder sb5 = new StringBuilder();
                sb5.append(Constants.HTTP);
                sb5.append(trim);
                webView2.loadUrl(sb5.toString());
            } else {
                this.secureBrowser.loadUrl(trim);
            }
        }
    }

    public void setIsFinishedLoading() {
        this.txturl.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_refresh, 0);
    }

    public void setIsLoading() {
        this.txturl.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_delete, 0);
    }

    public void refreshOrStop() {
        WebView webView = this.secureBrowser;
        if (webView == null) {
            return;
        }
        if (webView.getProgress() < 100) {
            this.secureBrowser.stopLoading();
        } else {
            this.secureBrowser.reload();
        }
    }

    public void updateProgress(int i) {
        if (i >= 100) {
            setIsFinishedLoading();
        } else {
            setIsLoading();
        }
        this.mProgressBar.setProgress(i);
    }

    public void btnRefreshStop(View view) {
        if (this.isLoading.booleanValue()) {
            this.secureBrowser.stopLoading();
        } else {
            this.secureBrowser.reload();
        }
    }

    public void btnDropDown(View view) {
        if (this.isvisible.booleanValue()) {
            this.isvisible = Boolean.valueOf(false);
            this.ll_Bottom.setLayoutParams(this.ll_Hide_Params);
            this.ll_Bottom.setVisibility(View.INVISIBLE);
            return;
        }
        this.isvisible = Boolean.valueOf(true);
        this.ll_Bottom.setLayoutParams(this.ll_Show_Params);
        this.ll_Bottom.setVisibility(View.VISIBLE);
    }

    public void BtnBack(View view) {
        if (this.secureBrowser.canGoBack()) {
            this.secureBrowser.goBack();
        }
    }

    public void BtnForward(View view) {
        if (this.secureBrowser.canGoForward()) {
            this.secureBrowser.goForward();
        }
    }

    public void BtnAddBookMark(View view) {
        BookmarkDAL bookmarkDAL = new BookmarkDAL(this);
        bookmarkDAL.OpenWrite();
        try {
            if (bookmarkDAL.AddBookmark(this.txturl.getText().toString()).booleanValue()) {
                Toast.makeText(this, "Added to bookmark list", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Already exist this bookmark", Toast.LENGTH_SHORT).show();
            }
            bookmarkDAL.close();
        } catch (Exception unused) {
            bookmarkDAL.close();
        }
    }

    public void BtnBookMarkHistory(View view) {
        TextView textView = (TextView) this.dialogUrl.findViewById(R.id.lblurlhistoty);
        Button button = (Button) this.dialogUrl.findViewById(R.id.btnclearbrowser);
        ListView listView = (ListView) this.dialogUrl.findViewById(R.id.listViewhistory);
        BookmarkDAL bookmarkDAL = new BookmarkDAL(this);
        bookmarkDAL.OpenRead();
        this.urlList = bookmarkDAL.GetUrlBookmarks();
        bookmarkDAL.close();
        listView.setAdapter(new UrlAdapter(this, 17367043, this.urlList, BrowserMenuType.Bookmark.ordinal()));
        textView.setText("Bookmark List");
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (SecureBrowserActivity.this.urlList != null) {
                    SecureBrowserActivity.this.secureBrowser.loadUrl((String) SecureBrowserActivity.this.urlList.get(i));
                    SecureBrowserActivity.this.txturl.setText((CharSequence) SecureBrowserActivity.this.urlList.get(i));
                    SecureBrowserActivity.this.dialogUrl.dismiss();
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final Dialog dialog = new Dialog(SecureBrowserActivity.this, R.style.FullHeightDialog);
                dialog.setCancelable(true);
                dialog.setTitle((int) R.string.del_bookmarks);
                dialog.positiveAction((CharSequence) "Yes").negativeAction((CharSequence) "No");
                dialog.positiveActionClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        dialog.dismiss();
                        new BookmarkDAL(SecureBrowserActivity.this).DeleteBookmarks();
                        SecureBrowserActivity.this.dialogUrl.dismiss();
                        Toast.makeText(SecureBrowserActivity.this, "Bookmark(s) cleared", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.negativeActionClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        List<String> list = this.urlList;
        if (list == null || list.size() <= 0) {
            Toast.makeText(this, "No Bookmark(s)", Toast.LENGTH_SHORT).show();
        } else {
            this.dialogUrl.show();
        }
    }

    public void BtnHistoryClean(View view) {
        TextView textView = (TextView) this.dialogUrlRecent.findViewById(R.id.lblurlhistoty);
        Button button = (Button) this.dialogUrlRecent.findViewById(R.id.btnclearbrowser);
        ListView listView = (ListView) this.dialogUrlRecent.findViewById(R.id.listViewhistory);
        BrowserHistoryDAL browserHistoryDAL2 = new BrowserHistoryDAL(this);
        browserHistoryDAL2.OpenRead();
        this.urlList = browserHistoryDAL2.GetBrowserUrlHistories();
        browserHistoryDAL2.close();
        listView.setAdapter(new UrlAdapter(this, 17367043, this.urlList, BrowserMenuType.History.ordinal()));
        textView.setText("History List");
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (SecureBrowserActivity.this.urlList != null) {
                    SecureBrowserActivity.this.secureBrowser.loadUrl((String) SecureBrowserActivity.this.urlList.get(i));
                    SecureBrowserActivity.this.txturl.setText((CharSequence) SecureBrowserActivity.this.urlList.get(i));
                    SecureBrowserActivity.this.dialogUrlRecent.dismiss();
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final Dialog dialog = new Dialog(SecureBrowserActivity.this, R.style.FullHeightDialog);
                dialog.setCancelable(true);
                dialog.setTitle((int) R.string.del_history);
                dialog.positiveAction((CharSequence) "Yes").negativeAction((CharSequence) "No");
                dialog.positiveActionClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        dialog.dismiss();
                        SecureBrowserActivity.this.browserHistoryDAL.OpenWrite();
                        SecureBrowserActivity.this.browserHistoryDAL.DeleteHistories();
                        SecureBrowserActivity.this.browserHistoryDAL.close();
                        SecureBrowserActivity.this.dialogUrlRecent.dismiss();
                        Toast.makeText(SecureBrowserActivity.this, "History deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.negativeActionClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        List<String> list = this.urlList;
        if (list == null || list.size() <= 0) {
            Toast.makeText(this, "No History", Toast.LENGTH_SHORT).show();
        } else {
            this.dialogUrlRecent.show();
        }
    }

    public void BtnDownloadHistory(View view) {
        TextView textView = (TextView) this.dialogDownload.findViewById(R.id.lblurlhistoty);
        Button button = (Button) this.dialogDownload.findViewById(R.id.btnclearbrowser);
        ListView listView = (ListView) this.dialogDownload.findViewById(R.id.listViewhistory);
        DownloadFileDAL downloadFileDAL = new DownloadFileDAL(this);
        downloadFileDAL.OpenRead();
        this.urlList = downloadFileDAL.GetDownloadFileName();
        downloadFileDAL.close();
        listView.setAdapter(new UrlAdapter(this, 17367043, this.urlList, BrowserMenuType.Download.ordinal()));
        textView.setText("Download List");
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final Dialog dialog = new Dialog(SecureBrowserActivity.this, R.style.FullHeightDialog);
                dialog.setCancelable(true);
                dialog.setTitle((int) R.string.del_download_history);
                dialog.positiveAction((CharSequence) "Yes").negativeAction((CharSequence) "No");
                dialog.positiveActionClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        dialog.dismiss();
                        new DownloadFileDAL(SecureBrowserActivity.this).DeleteDownloadFile();
                        SecureBrowserActivity.this.dialogDownload.dismiss();
                        Toast.makeText(SecureBrowserActivity.this, "Download history cleared", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.negativeActionClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
        List<String> list = this.urlList;
        if (list == null || list.size() <= 0) {
            Toast.makeText(this, "No Downloads", Toast.LENGTH_SHORT).show();
        } else {
            this.dialogDownload.show();
        }
    }

    public void btnBrowserExit(View view) {
        BrowserHistoryDAL browserHistoryDAL2 = this.browserHistoryDAL;
        if (browserHistoryDAL2 != null) {
            browserHistoryDAL2.close();
        }
        Common.IsWebBrowserActive = false;
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(getApplicationContext(), Common.CurrentWebBrowserActivity.getClass()));
        finish();
        overridePendingTransition(17432576, 17432577);
    }

    public void settingsMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, this.iv_settings);

        popupMenu.setOnMenuItemClickListener(new MenuItemClickListener());
        popupMenu.inflate(R.menu.popup_menu);
        this.mItemClrCacheOnExit = popupMenu.getMenu().getItem(0);
        this.mItemClrHistoryOnExit = popupMenu.getMenu().getItem(1);
        this.mItemClrCookiesOnExit = popupMenu.getMenu().getItem(2);
        this.mItemSaveFormData = popupMenu.getMenu().getItem(3);
        this.mItemClrCacheOnExit.setChecked(this.isClearChache);
        this.mItemClrHistoryOnExit.setChecked(this.isClearHistory);
        this.mItemClrCookiesOnExit.setChecked(this.isClearCookies);
        this.mItemSaveFormData.setChecked(this.isSaveFormData);
        popupMenu.show();
    }


    public void onPause() {
        super.onPause();
        Common.LastWebBrowserUrl = this.txturl.getText().toString();
        if (SecurityLocksCommon.IsAppDeactive) {
            finish();
            System.exit(0);
        }
    }


    public void onResume() {
        super.onResume();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() != 0 || i != 4) {
            return super.onKeyDown(i, keyEvent);
        }
        if (this.secureBrowser.canGoBack()) {
            this.secureBrowser.goBack();
            WebBackForwardList copyBackForwardList = this.secureBrowser.copyBackForwardList();
            if (copyBackForwardList.getSize() > 0 && copyBackForwardList.getCurrentIndex() != 0) {
                String url = copyBackForwardList.getItemAtIndex(copyBackForwardList.getCurrentIndex() - 1).getUrl();
                this.txturl.setText(url);
                this.browserHistoryDAL.OpenWrite();
                this.browserHistoryDAL.AddBrowserHistory(url);
                this.browserHistoryDAL.close();
            }
        } else {
            BrowserHistoryDAL browserHistoryDAL2 = this.browserHistoryDAL;
            if (browserHistoryDAL2 != null) {
                browserHistoryDAL2.close();
            }
            Common.IsWebBrowserActive = false;
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(getApplicationContext(), Common.CurrentWebBrowserActivity.getClass()));
            finish();
            overridePendingTransition(17432576, 17432577);
        }
        return true;
    }


    public void onDestroy() {
        super.onDestroy();
        if (this.isClearHistory) {
            clearHistory();
        }
        if (this.isClearCookies) {
            clearCookies();
        }
        if (this.isClearChache) {
            clearCache();
        }
        this.secureBrowser.destroy();
    }

    public void clearCache() {
        this.secureBrowser.clearCache(true);
    }

    public void clearCookies() {
        WebStorage.getInstance().deleteAllData();
        CookieManager instance = CookieManager.getInstance();
        if (VERSION.SDK_INT >= 21) {
            instance.removeAllCookies(null);
            return;
        }
        CookieSyncManager.createInstance(this);
        instance.removeAllCookie();
    }

    public void clearHistory() {
        WebViewDatabase instance = WebViewDatabase.getInstance(this);
        instance.clearFormData();
        this.secureBrowser.clearHistory();
        instance.clearHttpAuthUsernamePassword();
        if (VERSION.SDK_INT < 18) {
            instance.clearUsernamePassword();
            WebIconDatabase.getInstance().removeAllIcons();
        }
        Utilities.trimCache(this);
        this.browserHistoryDAL.OpenWrite();
        this.browserHistoryDAL.DeleteHistories();
        this.browserHistoryDAL.close();
        this.dialogUrlRecent.dismiss();
    }
}
