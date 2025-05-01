package com.dtb.msprofile.util.logging;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RequiredArgsConstructor
@Service
public class Helpers {

    public String maskSensitiveString(String log) {
        try {

            log = getString(log);

            List<String> maskListString = new ArrayList<>();
            maskListString.add("<req:Password>(.*?)</req:Password>");
            maskListString.add("<b>Passkey: </b>(.*?)<br/>");
            maskListString.add("<req:SecurityCredential>(.*?)</req:SecurityCredential>");
            maskListString.add("\"Password\":\\s*\"(.*?)\"");
            maskListString.add("\"SecurityCredential\":\\s*\"(.*?)\"");

            for (String str : maskListString) {
                log = maskStringRegex(log, str);
            }


            return log.replaceAll("[\n\r]+", "")  // Remove newlines
                    .replaceAll("\\s{2,}", " ") // Replace multiple spaces with a single space
                    .trim();                    // Trim leading and trailing spaces//NOSONAR


        } catch (Exception e) {
            return "Unable to mask msisdn => log content ignored";
        }
    }

    private String getString(String log) {
        Pattern pattern = Pattern.compile("\\b251\\w{4}"); //NOSONAR
        Matcher matcher = pattern.matcher(log);
        while (matcher.find()) {
            String orig = log.substring(matcher.start(), matcher.end());
            log = log.replaceAll(orig, "251****");
            matcher = pattern.matcher(log);
        }
        return log;
    }

    private static String maskStringRegex(String xml, String regex) {
        try {

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(xml);
            while (matcher.find()) {
                String orig = matcher.group(1); // Get the content inside <req:Password>...</req:Password>
                xml = xml.replace(orig, "***********************"); // Replace the original content with the masked content
            }
            return xml;
        } catch (Exception e) {
            return "Unable to mask XML Element => XML content ignored";
        }
    }



    public String maskSensitiveObject(Object object) {

        String log = parseToJsonString(object);

        ObjectMapper objectMapper = new ObjectMapper();

        try {

            List<String> maskList = new ArrayList<>();
            maskList.add("Password");
            maskList.add("email");
            maskList.add("password");
            maskList.add("firstName");
            maskList.add("lastName");
            maskList.add("SecurityCredential");

            JsonNode jsonNode = objectMapper.readTree(log);


            for (String str : maskList) {

                if(log.contains(str)){
                    replaceIdentifierFields(jsonNode,str);
                }


            }
            log=jsonNode.toString();

            log = getString(log);


            return log.trim();
        } catch (Exception e) {
            return e.getMessage()+"Unable to mask msisdn => log content ignored";
        }
    }
    private static void replaceIdentifierFields(JsonNode node, String identifier) {


        if (node.isObject()) {



            ObjectNode objectNode = (ObjectNode) node;
            objectNode.fieldNames().forEachRemaining(fieldName -> {
                JsonNode fieldValue = objectNode.get(fieldName);

                if (fieldName.equalsIgnoreCase(identifier)) {
                    objectNode.put(fieldName, "************");
                } else {
                    replaceIdentifierFields(fieldValue, identifier);
                }
            });
        } else if (node.isArray()) {

            for (JsonNode jsonNode : node) {
                replaceIdentifierFields(jsonNode, identifier);
            }
        }
    }


    public String parseToJsonString(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {

            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {

            return "";
        }
    }





}