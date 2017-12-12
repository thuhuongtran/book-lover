package com.vimensa.core.utils;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vimensa.config.EmailConfig;
import com.vimensa.core.dao.LoginSession;
import com.vimensa.core.dao.UserData;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Collections;
import java.util.Properties;
import java.util.Random;

public class Utils {
    private static final JsonParser jsonParser = new JsonParser();

    public static void main(String[] args){
//        String src = "password";
//        System.out.println(sha256(src));
        System.out.println("data: "+ getInfoUserFacebook("EAADjWXQQfZAQBAGFf16R2loZA15trjOcNckw0Iw7X1mjyFLWMeCgHrwF5A9lvBwwTodIuw5ivxyUw4BqaZAip2NHqSYl9EOWajIBdeqvVqoL8ZCGStlJty3N7AS8ZAuSCMZCbD86uvjYElCtb2FUIIw91NKImHwyg1eiOeGSSzZAC4CI5dl4Llv6AZAClexiidAXhxZBCdrBRcAZDZD"));
    }

    public static String sha256(String src){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(src.getBytes());
            byte byteData[] = md.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        }catch (Exception e){
            return "";
        }
    }

    private static Gson gson = new Gson();
    public static JsonObject toJsonObject(String json){
        try {
            return gson.fromJson(json, JsonObject.class);
        }catch (Exception e){
            return null;
        }
    }
    public static LoginSession getLoginSession(String nickname, String accessToken){

        return null;
    }

    public static UserData getInfoUserFacebook(String access_token){
        UserData userData = new UserData();

        try {
            StringBuilder sb = new StringBuilder("https://graph.facebook.com/v2.10/me?fields=email,first_name,last_name,picture&access_token=").append(access_token);
            System.out.println(sb.toString());
            String res = HttpUtils.requestJson(sb.toString());
            if (!res.isEmpty()) {
                JsonObject json = jsonParser.parse(res).getAsJsonObject();
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(json.get("first_name").getAsString()).append(" ").append(json.get("last_name").getAsString());
                userData.setUsername(stringBuffer.toString()) ;
                try{
                    if(json.get("email").getAsString()!=null && !json.get("email").getAsString().equals("")){
                        userData.setEmail(json.get("email").getAsString());
                    }else {
                        userData.setEmail("");
                    }
                }catch (Exception e){
                    userData.setEmail("");
                }

                userData.setAvatar(json.get("picture").getAsJsonObject().get("data").getAsJsonObject().get("url").getAsString());
                userData.setSocicalId(json.get("id").getAsString());
                userData.setSocialType("fb");
                return userData;
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static UserData getInfoUserGoogle(String id_token){
        UserData userData = new UserData();
        NetHttpTransport transport = new NetHttpTransport();
        JsonFactory mJFactory = new GsonFactory();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport,mJFactory)
                .setAudience(Collections.singletonList("750758825130-51kn5v7c9da3ai9bbgg64gvkr1ohamjv.apps.googleusercontent.com"))
                .build();

        System.out.println(verifier);

        GoogleIdToken idToken = null;
        try {
            idToken = verifier.verify(id_token);

            System.out.println("idToken: "+idToken);

            if (idToken != null) {
                Payload payload = idToken.getPayload();

                // Print user identifier
                String userId = payload.getSubject();
                System.out.println("User ID: " + userId);

                // Get profile information from payload
                String email = payload.getEmail();
                boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");
                String locale = (String) payload.get("locale");
                String familyName = (String) payload.get("family_name");
                String givenName = (String) payload.get("given_name");

                System.out.println("email: "+email);

            } else {
                System.out.println("Invalid ID token.");
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return userData;
    }

    public static LoginSession createAccessToken(String app_id){
        LoginSession loginSession = new LoginSession();

        Calendar calendar = Calendar.getInstance();
        long timestamp = calendar.getTimeInMillis();

        StringBuilder sb = new StringBuilder(app_id);
        Random random = new Random();
        int subResult = 100000 + random.nextInt(900000);
        sb.append(subResult);
        sb.append(timestamp);
        loginSession.setAppId(app_id);
        loginSession.setAccessToken(sha256(sb.toString()));
        loginSession.setTimeCreated(calendar);

        return loginSession;
    }
    public static String createTokenResetPassword(String email){
        Calendar calendar = Calendar.getInstance();
        long timestamp = calendar.getTimeInMillis();

        StringBuilder sb = new StringBuilder(email);
        Random random = new Random();
        int subResult = 100000 + random.nextInt(900000);
        sb.append(subResult);
        sb.append(timestamp);
        return sha256(sb.toString());
    }
    public static String toMd5(String src){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(src.getBytes());
            byte byteData[] = md.digest();

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        }catch (Exception e){
            return "";
        }
    }

    public static String sha1(String src){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(src.getBytes());
            byte byteData[] = md.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        }catch (Exception e){
            return "";
        }
    }
    public static void sendEmail(String toEmail,String subject,String contents) throws UnsupportedEncodingException, MessagingException {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(EmailConfig.EMAIL, EmailConfig.PASSWORD);
                    }
                });


            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EmailConfig.EMAIL,EmailConfig.EMAIL_NAME));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(contents);

            Transport.send(message);

            System.out.println("Done");
    }
    public static String randomAlphaNumeric(int count) {
        String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder();

        while (count-- != 0) {

            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());

            builder.append(ALPHA_NUMERIC_STRING.charAt(character));

        }

        return builder.toString();
    }
}
