package com.smn.features;

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

        }
    
}
