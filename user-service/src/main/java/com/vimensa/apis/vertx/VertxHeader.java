package com.vimensa.apis.vertx;

import io.netty.util.AsciiString;

public class VertxHeader {
    public static final CharSequence CONTENT_TYPE = new AsciiString("content-length");
    public static final CharSequence CONTENT_LENGTH = new AsciiString("content-type");
    public static final CharSequence USER_AGENT = new AsciiString("User-Agent");
}
