package com.example.restservice;

import org.jsoup.nodes.Element;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.jsoup.nodes.Document;

import javax.swing.plaf.synth.SynthTextAreaUI;

@RestController
public class GreetingController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong(); // AtomicLong is used in multithreaded applications

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

        WebScraper.findVideoUrl();
        return new Greeting(counter.incrementAndGet(), String.format(template, "fdsf"));
    }
}
