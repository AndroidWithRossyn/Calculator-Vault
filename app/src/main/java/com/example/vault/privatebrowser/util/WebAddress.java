package com.example.vault.privatebrowser.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;

public class WebAddress {
    static final int MATCH_GROUP_AUTHORITY = 2;
    static final int MATCH_GROUP_HOST = 3;
    static final int MATCH_GROUP_PATH = 5;
    static final int MATCH_GROUP_PORT = 4;
    static final int MATCH_GROUP_SCHEME = 1;
    static final Pattern sAddressPattern = Pattern.compile("(?:(http|https|file)\\:\\/\\/)?(?:([-A-Za-z0-9$_.+!*'(),;?&=]+(?:\\:[-A-Za-z0-9$_.+!*'(),;?&=]+)?)@)?([a-zA-Z0-9 -퟿豈-﷏ﷰ-￯%_-][a-zA-Z0-9 -퟿豈-﷏ﷰ-￯%_\\.-]*|\\[[0-9a-fA-F:\\.]+\\])?(?:\\:([0-9]*))?(\\/?[^#]*)?.*", 2);
    private String mAuthInfo;
    private String mHost;
    private String mPath;
    private int mPort;
    private String mScheme;

    public WebAddress(String str) {
        if (str != null) {
            this.mScheme = "";
            this.mHost = "";
            this.mPort = -1;
            this.mPath = "/";
            this.mAuthInfo = "";
            Matcher matcher = sAddressPattern.matcher(str);
            if (matcher.matches()) {
                String group = matcher.group(1);
                if (group != null) {
                    this.mScheme = group.toLowerCase(Locale.ROOT);
                }
                String group2 = matcher.group(2);
                if (group2 != null) {
                    this.mAuthInfo = group2;
                }
                String group3 = matcher.group(3);
                if (group3 != null) {
                    this.mHost = group3;
                }
                String group4 = matcher.group(4);
                if (group4 != null && !group4.isEmpty()) {
                    try {
                        this.mPort = Integer.parseInt(group4);
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("Parsing of port number failed", e);
                    }
                }
                String group5 = matcher.group(5);
                if (group5 != null && !group5.isEmpty()) {
                    if (group5.charAt(0) == '/') {
                        this.mPath = group5;
                    } else {
                        StringBuilder sb = new StringBuilder();
                        sb.append(IOUtils.DIR_SEPARATOR_UNIX);
                        sb.append(group5);
                        this.mPath = sb.toString();
                    }
                }
                if (this.mPort == 443 && "".equals(this.mScheme)) {
                    this.mScheme = "https";
                } else if (this.mPort == -1) {
                    if ("https".equals(this.mScheme)) {
                        this.mPort = 443;
                    } else {
                        this.mPort = 80;
                    }
                }
                if ("".equals(this.mScheme)) {
                    this.mScheme = HttpHost.DEFAULT_SCHEME_NAME;
                    return;
                }
                return;
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Parsing of address '");
            sb2.append(str);
            sb2.append("' failed");
            throw new IllegalArgumentException(sb2.toString());
        }
        throw new IllegalArgumentException("address can't be null");
    }

    public String toString() {
        String str = "";
        if ((this.mPort != 443 && "https".equals(this.mScheme)) || (this.mPort != 80 && HttpHost.DEFAULT_SCHEME_NAME.equals(this.mScheme))) {
            StringBuilder sb = new StringBuilder();
            sb.append(':');
            sb.append(Integer.toString(this.mPort));
            str = sb.toString();
        }
        String str2 = "";
        if (!this.mAuthInfo.isEmpty()) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(this.mAuthInfo);
            sb2.append('@');
            str2 = sb2.toString();
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append(this.mScheme);
        sb3.append("://");
        sb3.append(str2);
        sb3.append(this.mHost);
        sb3.append(str);
        sb3.append(this.mPath);
        return sb3.toString();
    }

    public void setScheme(String str) {
        this.mScheme = str;
    }

    public String getScheme() {
        return this.mScheme;
    }

    public void setHost(String str) {
        this.mHost = str;
    }

    public String getHost() {
        return this.mHost;
    }

    public void setPort(int i) {
        this.mPort = i;
    }

    public int getPort() {
        return this.mPort;
    }

    public void setPath(String str) {
        this.mPath = str;
    }

    public String getPath() {
        return this.mPath;
    }

    public void setAuthInfo(String str) {
        this.mAuthInfo = str;
    }

    public String getAuthInfo() {
        return this.mAuthInfo;
    }
}
