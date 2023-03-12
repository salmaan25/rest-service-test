package com.example.restservice;

import com.example.restservice.storage.StorageService;
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
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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

    public ResponseEntity<Resource> findVideoUrl(String url, StorageService storageService) throws Exception {
        WebClient webClient = new WebClient();
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        HtmlPage myPage = webClient.getPage(url);

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

        String videoUrl = ((JSONObject)((JSONArray)json.get("video")).get(0)).get("contentUrl").toString();
        webClient.close();
//        return videoUrl;

        URL vidUrl = new URL(videoUrl);
        HttpURLConnection httpConnection = (HttpURLConnection) vidUrl.openConnection();
        httpConnection.setRequestMethod("HEAD");
        long removeFileSize = httpConnection.getContentLengthLong();


        AsyncHttpClient client = Dsl.asyncHttpClient();
        FileOutputStream stream = new FileOutputStream("video.mp4");

        client.prepareGet(videoUrl).execute(new AsyncCompletionHandler<FileOutputStream>() {

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

        while(removeFileSize > stream.getChannel().size()) {
            System.out.println("copied " + (stream.getChannel().size()/removeFileSize) + "%");
        }
            Resource file = storageService.loadAsResource("video.mp4");
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + file.getFilename() + "\"").body(file);

//        return url;

    }

    public static InputStream LoadFile()  throws Exception {
        File file = new File("/Users/salmaanshahid/Documents/GitHub/rest-service-test/src/main/java/com/example/restservice/video2.mp4");

        InputStream inputStream = new FileInputStream(file);
        return inputStream;
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
