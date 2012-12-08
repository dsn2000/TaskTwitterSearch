package ru.task.utils;

/**
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 27.11.12
 * Time: 16:11
 * To change this template use File | Settings | File Templates.
 */
public class Twitt {

    private final String profile_image_url;
    private final String text;
    private final String url;

    public Twitt(String profile_image_url, String text, String url) {
        this.profile_image_url = profile_image_url;
        this.text = text;
        this.url = url;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public String getUrl() {
        return url;
    }

    public String getText() {
        return text;
    }
}
