package cn.ezandroid.lib.sgf.v2.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Util {

    //@formatter:off
    public static final String[] ALPHABET = new String[]{"A", "B", "C", "D"
            , "E", "F", "G", "H", "J"
            , "K", "L", "M", "N", "O"
            , "P", "Q", "R", "S", "T"};

    public static final Map<String, Integer> ALPHA_TO_COORD = new HashMap<String, Integer>() {{
        put("a", 0);
        put("b", 1);
        put("c", 2);
        put("d", 3);
        put("e", 4);
        put("f", 5);
        put("g", 6);
        put("h", 7);
        put("i", 8);
        put("j", 9);
        put("k", 10);
        put("l", 11);
        put("m", 12);
        put("n", 13);
        put("o", 14);
        put("p", 15);
        put("q", 16);
        put("r", 17);
        put("s", 18);
        put("t", 19);
    }};

    public static final Map<Integer, String> COORD_TO_ALPHA = new HashMap<Integer, String>() {{
        put(0, "a");
        put(1, "b");
        put(2, "c");
        put(3, "d");
        put(4, "e");
        put(5, "f");
        put(6, "g");
        put(7, "h");
        put(8, "i");
        put(9, "j");
        put(10, "k");
        put(11, "l");
        put(12, "m");
        put(13, "n");
        put(14, "o");
        put(15, "p");
        put(16, "q");
        put(17, "r");
        put(18, "s");
        put(19, "t");
    }};

    //@formatter:on
    private Util() {
    }

    public static void printNodeTree(GameNode rootNode) {
        if (rootNode.hasChildren()) {
            Set<GameNode> children = rootNode.getChildren();
            for (GameNode node : children) {
                printNodeTree(node);
            }
        }
    }

    public static int[] alphaToCoords(String input) {
        if (input == null || input.length() < 2) {
            throw new RuntimeException("Coordinate cannot be less than 2 characters. Input '" + input + "'");
        }
        return new int[]{ALPHA_TO_COORD.get(input.charAt(0) + ""), ALPHA_TO_COORD.get(input.charAt(1) + "")};
    }

    public static Map<String, String> extractLabels(String str) {
        HashMap<String, String> rtrn = new HashMap<String, String>();
        // the LB property comes like 'fb:A][gb:C][jd:B
        if (str == null)
            return rtrn;
        String[] labels = str.split("\\]\\[");
        for (int i = 0; i < labels.length; i++) {
            String[] label = labels[i].split(":");
            rtrn.put(label[0], label[1]);
        }
        return rtrn;
    }

    public static String[] coordSequencesToSingle(String addBlack) {
        List<String> rtrn = new ArrayList<>();
        String[] blackStones = addBlack.split(",");
        for (String blackStone : blackStones) {
            if (blackStone.contains(":")) {
                String[] seq = blackStone.split(":");
                if (seq[0].charAt(0) == seq[1].charAt(0)) {
                    for (int j = Util.ALPHA_TO_COORD.get(seq[0].charAt(1) + ""); j <= Util.ALPHA_TO_COORD.get(seq[1].charAt(1) + ""); j++) {
                        rtrn.add(seq[0].charAt(0) + COORD_TO_ALPHA.get(j));
                    }
                } else {
                    for (int j = Util.ALPHA_TO_COORD.get(seq[0].charAt(0) + ""); j <= Util.ALPHA_TO_COORD.get(seq[1].charAt(0) + ""); j++) {
                        rtrn.add(COORD_TO_ALPHA.get(j) + seq[0].charAt(1));
                    }
                }
            } else {
                rtrn.add(blackStone);
            }
        }
        return rtrn.toArray(new String[]{});
    }

    /**
     * Convert text into SGF escaped sequence.
     *
     * @param input normal text from user
     * @return SGF escaped text
     */
    public static String sgfEscapeText(String input) {
        // some helpers I used for parsing needs to be undone - see the Parser.java
        // in sgf4j project
        input = input.replaceAll("\\\\\\[", "@@@@@");
        input = input.replaceAll("\\\\\\]", "#####");

        // lets do some replacing - see http://www.red-bean.com/sgf/sgf4.html#text
        input = input.replaceAll(":", "\\\\:");
        input = input.replaceAll("\\]", "\\\\]");
        input = input.replaceAll("\\[", "\\\\[");

        return input;
    }

    /**
     * Convert SGF escaped text into end user display text
     *
     * @param input text from SGF
     * @return text to show for end user
     */
    public static String sgfUnescapeText(String input) {
        // some helpers I used for parsing needs to be undone - see the Parser.java
        // in sgf4j project
        input = input.replaceAll("@@@@@", "\\\\\\[");
        input = input.replaceAll("#####", "\\\\\\]");

        // lets do some replacing - see http://www.red-bean.com/sgf/sgf4.html#text
        input = input.replaceAll("\\\\\n", "");
        input = input.replaceAll("\\\\:", ":");
        input = input.replaceAll("\\\\\\]", "]");
        input = input.replaceAll("\\\\\\[", "[");

        return input;
    }
}