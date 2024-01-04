package com.example.vault.privatebrowser.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;

import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.example.vault.R;
import com.example.vault.privatebrowser.util.HistoryItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class SearchAdapter extends BaseAdapter implements Filterable {
    private static final int API = VERSION.SDK_INT;
    private static final String ENCODING = "ISO-8859-1";
    private static final long INTERVAL_DAY = 86400000;

    public final Context mContext;
    private SearchFilter mFilter;

    public List<HistoryItem> mFilteredList = new ArrayList();

    public boolean mIsExecuting = false;

    public final String mSearchSubtitle;

    public List<HistoryItem> mSuggestions = new ArrayList();

    private class NameFilter implements FilenameFilter {
        private static final String ext = ".sgg";

        private NameFilter() {
        }

        public boolean accept(File file, String str) {
            return str.endsWith(ext);
        }
    }

    private class RetrieveSearchSuggestions extends AsyncTask<String, Void, List<HistoryItem>> {
        private XmlPullParserFactory mFactory;
        private XmlPullParser mXpp;

        private RetrieveSearchSuggestions() {
        }



        public List<HistoryItem> doInBackground(String... strArr) {
            InputStream inputStream;
            SearchAdapter.this.mIsExecuting = true;
            ArrayList arrayList = new ArrayList();
            int i = 0;
            String str = strArr[0];
            try {
                str = str.replace(" ", "+");
                URLEncoder.encode(str, "ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            File access$800 = SearchAdapter.this.downloadSuggestionsForQuery(str);
            if (!access$800.exists()) {
                return arrayList;
            }
            try {
                inputStream = new BufferedInputStream(new FileInputStream(access$800));
                try {
                    if (this.mFactory == null) {
                        this.mFactory = XmlPullParserFactory.newInstance();
                        this.mFactory.setNamespaceAware(true);
                    }
                    if (this.mXpp == null) {
                        this.mXpp = this.mFactory.newPullParser();
                    }
                    this.mXpp.setInput(inputStream, "ISO-8859-1");
                    int eventType = this.mXpp.getEventType();
                    while (true) {
                        if (eventType != 1) {
                            if (eventType == 2 && "suggestion".equals(this.mXpp.getName())) {
                                String attributeValue = this.mXpp.getAttributeValue(null, "data");
                                StringBuilder sb = new StringBuilder();
                                sb.append(SearchAdapter.this.mSearchSubtitle);
                                sb.append(" \"");
                                sb.append(attributeValue);
                                sb.append('\"');
                                arrayList.add(new HistoryItem(sb.toString(), attributeValue, (int) R.drawable.ic_search));
                                i++;
                                if (i >= 5) {
                                    break;
                                }
                            }
                            eventType = this.mXpp.next();
                        }
                    }
                    try {
                        inputStream.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                    return arrayList;
                } catch (Exception unused) {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e3) {
                            e3.printStackTrace();
                        }
                    }
                    return arrayList;
                } catch (Throwable th) {
                    th = th;
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e4) {
                            e4.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (Exception unused2) {
                inputStream = null;
                if (inputStream != null) {
                }
                return arrayList;
            } catch (Throwable th2) {

                inputStream = null;
                if (inputStream != null) {
                }
                try {
                    throw th2;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
            return null;
        }


        public void onPostExecute(List<HistoryItem> list) {
            SearchAdapter.this.mSuggestions = list;
            SearchAdapter searchAdapter = SearchAdapter.this;
            searchAdapter.mFilteredList = searchAdapter.mSuggestions;
            SearchAdapter.this.notifyDataSetChanged();
            SearchAdapter.this.mIsExecuting = false;
        }
    }

    public class SearchFilter extends Filter {
        public SearchFilter() {
        }


        public FilterResults performFiltering(CharSequence charSequence) {
            FilterResults filterResults = new FilterResults();
            if (charSequence == null) {
                return filterResults;
            }
            String lowerCase = charSequence.toString().toLowerCase(Locale.getDefault());
            if (!SearchAdapter.this.mIsExecuting) {
                new RetrieveSearchSuggestions().execute(new String[]{lowerCase});
            }
            return filterResults;
        }

        public CharSequence convertResultToString(Object obj) {
            return ((HistoryItem) obj).getUrl();
        }


        public void publishResults(CharSequence charSequence, FilterResults filterResults) {
            SearchAdapter searchAdapter = SearchAdapter.this;
            searchAdapter.mFilteredList = searchAdapter.mSuggestions;
            SearchAdapter.this.notifyDataSetChanged();
        }
    }

    private class SuggestionHolder {
        ImageView mImage;
        TextView mTitle;
        TextView mUrl;

        private SuggestionHolder() {
        }
    }

    public long getItemId(int i) {
        return 0;
    }

    public SearchAdapter(Context context) {
        this.mContext = context;
        this.mSearchSubtitle = this.mContext.getString(R.string.suggestion);
        new Thread(new Runnable() {
            public void run() {
                SearchAdapter searchAdapter = SearchAdapter.this;
                searchAdapter.deleteOldCacheFiles(searchAdapter.mContext);
            }
        }).start();
    }


    public void deleteOldCacheFiles(Context context) {
        String[] list;
        File file = new File(context.getCacheDir().toString());
        long currentTimeMillis = System.currentTimeMillis() - INTERVAL_DAY;
        for (String str : file.list(new NameFilter())) {
            StringBuilder sb = new StringBuilder();
            sb.append(file.getPath());
            sb.append(str);
            File file2 = new File(sb.toString());
            if (currentTimeMillis > file2.lastModified()) {
                file2.delete();
            }
        }
    }

    public int getCount() {
        List<HistoryItem> list = this.mFilteredList;
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public Object getItem(int i) {
        return this.mFilteredList.get(i);
    }

    @SuppressLint({"NewApi"})
    public View getView(int i, View view, ViewGroup viewGroup) {
        SuggestionHolder suggestionHolder;
        if (view == null) {
            view = ((Activity) this.mContext).getLayoutInflater().inflate(R.layout.two_line_autocomplete, viewGroup, false);
            suggestionHolder = new SuggestionHolder();
            suggestionHolder.mTitle = (TextView) view.findViewById(R.id.title);
            suggestionHolder.mUrl = (TextView) view.findViewById(R.id.url);
            suggestionHolder.mImage = (ImageView) view.findViewById(R.id.suggestionIcon);
            view.setTag(suggestionHolder);
        } else {
            suggestionHolder = (SuggestionHolder) view.getTag();
        }
        HistoryItem historyItem = (HistoryItem) this.mFilteredList.get(i);
        suggestionHolder.mTitle.setText(historyItem.getTitle());
        suggestionHolder.mUrl.setText(historyItem.getUrl());
        return view;
    }

    public void setContents(List<HistoryItem> list) {
        List<HistoryItem> list2 = this.mFilteredList;
        if (list2 != null) {
            list2.clear();
            this.mFilteredList.addAll(list);
        } else {
            this.mFilteredList = list;
        }
        notifyDataSetChanged();
    }

    public Filter getFilter() {
        if (this.mFilter == null) {
            this.mFilter = new SearchFilter();
        }
        return this.mFilter;
    }


    public File downloadSuggestionsForQuery(String str) {
        File cacheDir = this.mContext.getCacheDir();
        StringBuilder sb = new StringBuilder();
        sb.append(str.hashCode());
        sb.append(".sgg");
        File file = new File(cacheDir, sb.toString());
        if (System.currentTimeMillis() - INTERVAL_DAY < file.lastModified() || !isNetworkConnected(this.mContext)) {
            return file;
        }
        try {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("http://google.com/complete/search?q=");
            sb2.append(str);
            sb2.append("&output=toolbar&hl=en");
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(sb2.toString()).openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            InputStream inputStream = httpURLConnection.getInputStream();
            if (inputStream != null) {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                while (true) {
                    int read = inputStream.read();
                    if (read == -1) {
                        break;
                    }
                    fileOutputStream.write(read);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
            }
            file.setLastModified(System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    private boolean isNetworkConnected(Context context) {
        NetworkInfo activeNetworkInfo = getActiveNetworkInfo(context);
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return null;
        }
        return connectivityManager.getActiveNetworkInfo();
    }
}
