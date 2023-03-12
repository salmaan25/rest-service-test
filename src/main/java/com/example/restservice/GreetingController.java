package com.example.restservice;

import com.example.restservice.storage.StorageService;
import org.json.simple.JSONObject;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.jsoup.nodes.Document;

import javax.swing.plaf.synth.SynthTextAreaUI;

@RestController
public class GreetingController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong(); // AtomicLong is used in multithreaded applications

    private final StorageService storageService;

    @Autowired
    public GreetingController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        System.out.println("fetching from greeting");
        Greeting greeting = new Greeting(counter.incrementAndGet(), String.format(template, name));
        System.out.println(greeting.getId());
        System.out.println(greeting.getContent());
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    @GetMapping("/get-html-from-url")
    public Greeting getHtmlFromUrl(@RequestParam(value = "url", defaultValue = "") String url) throws  Exception {
//        Document document = WebScraper.getDocument("https://www.instagram.com/reel/CpS6RIDjC7D/?utm_source=ig_web_copy_link");
//        String htmlString = document.toString();
////        String heading = WebScraper.findHeading(document);
//        List<Element> elements = document.getElementsByTag("video");
//        if(elements.size() == 0) {
//            System.out.println("No tags found");
//            return new Greeting(counter.incrementAndGet(), String.format(template, ""));
//        }
//        return new Greeting(counter.incrementAndGet(), String.format(template, elements.get(0).text()));

//        Instagram ig = new Instagram("https://www.instagram.com/reel/CpS6RIDjC7D/?utm_source=ig_web_copy_link");
//        ig.start();

        WebScraper webScraper = new WebScraper();

//        String videoUrl = webScraper.findVideoUrl("https://www.instagram.com/reel/CpS6RIDjC7D/?utm_source=ig_web_copy_link", storageService);
        return new Greeting(counter.incrementAndGet(), String.format(template, "videoUrl"));
    }


//    @GetMapping("/get-video/{url:.+}")
//    public ResponseEntity<Resource> getVideo(@PathVariable String url) throws  Exception {
//        WebScraper webScraper = new WebScraper();
//        return webScraper.findVideoUrl(url, storageService);
//    }

    @PostMapping(value = "/get-video")
    public ResponseEntity<Resource> getVideo(@RequestBody JSONObject url) throws  Exception {
        WebScraper webScraper = new WebScraper();
        return webScraper.findVideoUrl(url.get("url").toString(), storageService);
    }

}
