package name.prokop.bart.gae.edziecko.util;

import java.text.Collator;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

public class StringToolbox {

    private static final NumberFormat CURRENCY;
    private static final NumberFormat C2;
    private static final Collator COLLATOR;

    static {
        Locale locale = null;
        for (Locale l : Locale.getAvailableLocales()) {
            if (l.getLanguage().equals("pl") && l.getCountry().equals("PL")) {
                locale = l;
            }
        }
        CURRENCY = NumberFormat.getCurrencyInstance(locale);
        CURRENCY.setMinimumFractionDigits(2);
        C2 = NumberFormat.getNumberInstance(locale);
        C2.setMinimumFractionDigits(2);
        COLLATOR = Collator.getInstance(locale);
    }

    public static String cardNumberPretty(String ccn) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ccn.length(); i++) {
            sb.append(ccn.charAt(i));
            if (i % 4 == 3) {
                sb.append(" ");
            }
        }
        return sb.toString().trim();
    }

    public static String cardNumberCompress(String ccn) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ccn.length(); i++) {
            if (Character.isDigit(ccn.charAt(i))) {
                sb.append(ccn.charAt(i));
            }
        }
        return sb.toString();
    }

    /**
     * Konwertuje double na zapis walutowy
     * @param val
     * @return double jako 0,12 zł
     */
    public static String d2c(double val) {
        return CURRENCY.format(val);
    }

    /**
     * Konwersja double na zapis 0.00
     *
     * @param val
     * @return string w formacie 0.00
     */
    public static String d2s(double val) {
        return C2.format(val);
    }

    /**
     * Kolorowa wersja d2s
     * @param val
     * @return Htmlowa wersja 0.00 (minus na czerwono)
     */
    public static String d2h(double val) {
        String retVal = d2s(val);
        if (val < 0.0) {
            retVal = "<font color=FF0000>" + retVal + "</font>";
        }
        return retVal;
    }

    /**
     * Konwertuje double na zapis procentowy 0.2314 na 23,14%
     * @param val
     * @return string w formacie procentu
     */
    public static String d2r(double val) {
        String retVal = C2.format(val * 100);
        return retVal + "%";
    }

    public static String cleanCurrency(String v) {
        String retVal = "";
        for (int i = 0; i < v.length(); i++) {
            char c = v.charAt(i);
            if (c == ',') {
                c = '.';
            }
            if (Character.isWhitespace(c)) {
                continue;
            }
            if (Character.isLetter(c)) {
                continue;
            }
            retVal += c;
        }
        return retVal;
    }

    public static double pd(String s) {
        return Double.parseDouble(cleanCurrency(s));
    }

    public static int pi(String s) {
        return Integer.parseInt(s);
    }

    public static String generateRandomStringId(int length) {
        char[] charArr = new char[length];
        Random rnd = new Random();
        for (int i = 0; i < length; i++) {
            charArr[i] = (char) (65 + rnd.nextInt(26));
        }
        return new String(charArr);
    }

    public static int compare(String a, String b) {
        return COLLATOR.compare(a, b);
    }
}
