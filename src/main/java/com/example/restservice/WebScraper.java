package com.example.restservice;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WebScraper {
    // Define the search term
    String searchQuery = "iphone 13";
    public WebScraper() {
    }

    public static Document getDocument(String url) {
        Connection conn = Jsoup.connect(url);
//        conn.userAgent("custom user agent");
        Document document = null;
        try {
            document = conn.get();
        } catch (IOException e) {
            e.printStackTrace();
            // handle error
        }
        return document;
    }

    public static String findHeading(Document document) {
        Element firstHeading= document.selectFirst(".firstHeading");
        System.out.println(firstHeading.text());
        return firstHeading.text();
    }

    public static void findVideoUrl() throws Exception {
        WebClient webClient = new WebClient();
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
//        HtmlPage page = webClient.getPage("https://librivox.org/the-first-men-in-the-moon-by-hg-wells");
        HtmlPage myPage = webClient.getPage("https://www.instagram.com/reel/CpS6RIDjC7D/?utm_source=ig_web_copy_link");

        // convert page to generated HTML and convert to document
        Document doc = Jsoup.parse(myPage.asXml());
        String tmp = doc.toString();

        Elements videoElement = getScriptElementContainingVideoUrl(doc);
        List<String> relevantTagWithMp4Url = getSingleScriptElementWithVideoUrl(videoElement);
        String scriptInnerHtml = relevantTagWithMp4Url.get(0);

        List<String> videoUrl = JsonPath.read(scriptInnerHtml, "$..video_url");

        System.out.println("Video Url: " + getVideoUrl(scriptInnerHtml));

        // iterate row and col
//        for (Element row : doc.select("table#data > tbody > tr"))
//
//            for (Element col : row.select("td"))
//
//                // print results
//                System.out.println(col.ownText());

        // clean up resources
        webClient.close();
    }

    private static Elements getScriptElementContainingVideoUrl(Document doc) {
        return doc.select("script");
    }

    private static List<String> getSingleScriptElementWithVideoUrl(Elements scriptElements) {
        List<String> relevantTagWithMp4Url = new ArrayList<>();

        for (Element element : scriptElements) {
            if (element.data().contains("mp4")) {
                relevantTagWithMp4Url.add(element.data());
            }
        }

        return relevantTagWithMp4Url;
    }

    private static String getVideoUrl(String videoElement) {
        String[] jsonResponse = videoElement.split("\"contentUrl\":");
        // $.. is equivalent to $.[*] - (a wild card matcher) - you may need to play with this
        List<String> videoUrl = JsonPath.read(jsonResponse, "$..video_url");
        return videoUrl.get(0);
    }

}
