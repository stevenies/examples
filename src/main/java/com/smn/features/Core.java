package com.smn.features;

import java.util.regex.*;

public class Core {

        public static void main(String[] args) {
            System.out.println("Examples of various core Java features:");

            System.out.println("\nMultiline data including line breaks: ");
            String xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <note>
                    <to>Sheri</to>
                    <from>Nies</from>
                    <heading>Reminder</heading>
                    <body>Don't forget me this weekend!</body>
                </note>
                """;
            System.out.println(xml);

            System.out.println("\nPattern matching using RegEx: ");
            // Brackets match a range of characters. For example:
            //      [abc]	Find one character from the options between the brackets
            //      [^abc]	Find one character NOT between the brackets
            //      [0-9A-Za-z]	Find one character from the range 0 to 9, A to Z, or a to z
            // Metacharacters:
            //      .	Find just one instance of any character
            //      ^	Finds a match at the beginning of a string as in: ^Hello
            //      $	Finds a match at the end of the string as in: World$
            //      |	Find a match for any one of the patterns separated by | as in: cat|dog|fish
            //      \d	Find a digit
            //      \s	Find a whitespace character
            //      \b	Find a match at the beginning of a word like this: \bWORD, or at the end of a word like this: WORD\b
            //      \w	Find a word character [a-zA-Z0-9_]
            // Quantifiers:
            //      n+	Matches any string that contains at least one n
            //      n*	Matches any string that contains zero or more occurrences of n
            //      n?	Matches any string that contains zero or one occurrences of n
            //      n{x}	Matches any string that contains a sequence of X n's
            //      n{x,y}	Matches any string that contains a sequence of X to Y n's
            //      n{x,}	Matches any string that contains a sequence of at least X n's            
            Pattern pattern = Pattern.compile("<(.+)>([^<]+)</\\1>");
            Matcher matcher = pattern.matcher(xml);
            while (matcher.find()) {
                System.out.println(matcher.group(2));
            }

        }
    
}
