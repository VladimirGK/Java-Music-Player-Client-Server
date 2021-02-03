package com.spotify.command;

import java.util.ArrayList;
import java.util.List;

public class CommandCreator {
    private static List<String> getCommandArguments(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        boolean insideQuote = false;
        for (char c : input.toCharArray()) {
            if (c == '"') {
                insideQuote = !insideQuote;
            }
            if (c == ' ' && !insideQuote) {
                tokens.add(stringBuilder.toString().replace("\"", ""));
                stringBuilder.delete(0, stringBuilder.length());
            } else {
                stringBuilder.append(c);
            }
        }
        tokens.add(stringBuilder.toString().replace("\"", ""));
        return tokens;
    }

    public static Command newCommand(String clientInput) {
        clientInput = clientInput.replace("\n", "");
        List<String> tokens = CommandCreator.getCommandArguments(clientInput);
        String[] args = tokens.subList(1, tokens.size()).toArray(new String[0]);
        return new Command(tokens.get(0), args);
    }
}
