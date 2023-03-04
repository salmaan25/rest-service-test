package com.example.restservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jayway.jsonpath.JsonPath;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Instagram {

    private final String url;

    public Instagram(String url) {
        this.url = url;
    }

    public void start() {
        Document doc = getHtmlPage(url);
        String tmp1 = doc.toString();
        Elements videoElement = getScriptElementContainingVideoUrl(doc);
        String tmp = videoElement.toString();

        List<String> relevantTagWithMp4Url = getSingleScriptElementWithVideoUrl(videoElement);
        String scriptInnerHtml = relevantTagWithMp4Url.get(0);

        System.out.println("Video Url: " + getVideoUrl(scriptInnerHtml));
    }

    private List<String> getSingleScriptElementWithVideoUrl(Elements scriptElements) {
        List<String> relevantTagWithMp4Url = new ArrayList<>();

        for (Element element : scriptElements) {
            if (element.data().contains("mp4")) {
                relevantTagWithMp4Url.add(element.data());
            }
        }

        return relevantTagWithMp4Url;
    }

    private Elements getScriptElementContainingVideoUrl(Document doc) {
        return doc.select("script");
    }

    private String getVideoUrl(String videoElement) {
        String jsonResponse = videoElement.split(" = ")[1];
        // $.. is equivalent to $.[*] - (a wild card matcher) - you may need to play with this
        List<String> videoUrl = JsonPath.read(jsonResponse, "$..video_url");
        return videoUrl.get(0);
    }

    private Document getHtmlPage(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) {
        new Instagram("https://www.instagram.com/reel/CDok74FJzHp/?igshid=cam8ylb7okl7").start();
    }
}