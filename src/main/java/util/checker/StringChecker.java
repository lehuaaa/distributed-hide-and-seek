package util.checker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringChecker {

    public static boolean containsIllegalsCharacters(String toExamine) {
        Pattern pattern = Pattern.compile("[-._~:/?*#\\[\\]\"@!$&'()+,;=\\s^]");
        Matcher matcher = pattern.matcher(toExamine);
        return matcher.find();
    }
}
