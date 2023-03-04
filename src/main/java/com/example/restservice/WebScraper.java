package com.example.restservice;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.jayway.jsonpath.JsonPath;

import org.asynchttpclient.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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


import java.io.FileOutputStream;
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
        HtmlPage myPage = webClient.getPage("https://www.instagram.com/reel/CmV4inaPmRj/?utm_source=ig_web_copy_link");

        // convert page to generated HTML and convert to document
        Document doc = Jsoup.parse(myPage.asXml());
        String tmp = doc.toString();

        Elements videoElement = getScriptElementContainingVideoUrl(doc);
        List<String> relevantTagWithMp4Url = getSingleScriptElementWithVideoUrl(videoElement);
        String scriptInnerHtml = relevantTagWithMp4Url.get(0);
        scriptInnerHtml.strip();
        if (scriptInnerHtml.startsWith("\r\n//<![CDATA["))
            scriptInnerHtml = scriptInnerHtml.substring("\r\n//<![CDATA[".length());
        if(scriptInnerHtml.contains("//]]>"))
            scriptInnerHtml = scriptInnerHtml.substring(0, scriptInnerHtml.indexOf("//]]>"));

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(scriptInnerHtml);

        String url = ((JSONObject)((JSONArray)json.get("video")).get(0)).get("contentUrl").toString();
        webClient.close();


        AsyncHttpClient client = Dsl.asyncHttpClient();
        FileOutputStream stream = new FileOutputStream("video2.mp4");

        client.prepareGet(url).execute(new AsyncCompletionHandler<FileOutputStream>() {

            @Override
            public State onBodyPartReceived(HttpResponseBodyPart bodyPart)
                    throws Exception {
                stream.getChannel().write(bodyPart.getBodyByteBuffer());
                return State.CONTINUE;
            }

            @Override
            public FileOutputStream onCompleted(Response response)
                    throws Exception {
                System.out.println("Save Complete");
                return stream;
            }
        });

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

}
