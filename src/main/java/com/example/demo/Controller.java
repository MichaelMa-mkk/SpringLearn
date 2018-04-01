package com.example.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.Vector;

@RestController
@RequestMapping(value = "/Website")
public class Controller {

    public static void error(String str_) {
        throw new RuntimeException(str_);
    }

    @RequestMapping(value = "/s_begin={s_begin}&s_end={s_end}", method = RequestMethod.GET)
    public static String Website(@PathVariable String s_begin, @PathVariable String s_end) {
        String result = "";
        try {
            Graph ladder = new Graph("src/dictionary.txt");
            if (s_begin.length() == 0) return "Bad input";
            if (s_end.length() == 0) return "Bad input";
            if (s_begin.equalsIgnoreCase(s_end)) return "The two words must be different.";
            if (s_begin.length() != s_end.length()) return "The two words must be the same length.";
            ladder.init(s_begin, s_end);
            if (ladder.access(s_begin, s_end)) {
                Vector<String> path = new Vector<>();
                ladder.getLadder(s_begin, s_end, path);
                result += "A ladder from " + s_end + " back to " + s_begin + ":\n";
                for (String i : path) {
                    result += i + " ";
                }
            } else {
                result += "No word ladder found from " + s_end + " back to " + s_begin + ".";
            }
        } catch (RuntimeException e) {
            return e.getMessage();
        } catch (IOException e) {
            return e.toString();
        }
        return result;
    }
}
