package com.vimensa.chat.model;

public class Response {
    private static int error;

    public static int getError() {
        return error;
    }

    public static void setError(int error) {
        Response.error = error;
    }
}
