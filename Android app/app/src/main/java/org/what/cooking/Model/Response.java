package org.what.cooking.Model;

import java.net.URL;

/**
 * Created by marica on 15/02/14.
 */
public class Response {

    public String getTitle() {
        return title;
    }

    private String title;
    private URL url;

    public Response(String title,URL url) {
        this.title = title;
        this.url = url;
    }
}
