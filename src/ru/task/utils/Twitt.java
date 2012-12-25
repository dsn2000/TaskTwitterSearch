package ru.task.utils;

/**
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 27.11.12
 * Time: 16:11
 * To change this template use File | Settings | File Templates.
 */
public class Twitt {

    private final String profileImageUrl;
    private final String text;
    private final String url;

    public Twitt(String profileImageUrl, String text, String url) {
        this.profileImageUrl = profileImageUrl;
        this.text = text;
        this.url = url;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getUrl() {
        return url;
    }

    public String getText() {
        return text;
    }
}
