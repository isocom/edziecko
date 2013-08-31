package name.prokop.bart.gae.edziecko.util;

public class CardNumberToolbox {

    /**
     * Major Industry Identifier (MII)
     *
     * The first digit of a credit card number is the Major Industry Identifier
     * (MII), which represents the category of entity which issued the credit
     * card. Different MII digits represent the following issuer categories: 0
     * 鈥?ISO/TC 68 and other future industry assignments 1 鈥?Airlines 2
     * 鈥?Airlines and other future industry assignments 3 鈥?Travel and
     * entertainment and banking/financial 4 鈥?Banking and financial 5 鈥?Banking
     * and financial 6 鈥?Merchandising and banking/financial 7 鈥?Petroleum and
     * other future industry assignments 8 鈥?Healthcare, telecommunications and
     * other future industry assignments 9 鈥?National assignment
     *
     * For example, American Express, Diner's Club, and Carte Blanche are in the
     * travel and entertainment category, VISA, MasterCard, and Discover are in
     * the banking and financial category, and Sun Oil and Exxon are in the
     * petroleum category.
     *
     */
    private final static String MII = "9";
    /**
     * ISO 3166-1 is part of the ISO 3166 standard published by the
     * International Organization for Standardization (ISO), and defines codes
     * for the names of countries, dependent territories, and special areas of
     * geographical interest. The official name of the standard is Codes for the
     * representation of names of countries and their subdivisions 鈥?Part 1:
     * Country codes. It defines three sets of country codes:[1] ISO 3166-1
     * alpha-2 鈥?two-letter country codes which are the most widely used of the
     * three, and used most prominently for the Internet's country code
     * top-level domains (with a few exceptions). ISO 3166-1 alpha-3
     * 鈥?three-letter country codes which allow a better visual association
     * between the codes and the country names than the alpha-2 codes. ISO
     * 3166-1 numeric 鈥?three-digit country codes which are identical to those
     * developed and maintained by the United Nations Statistics Division, with
     * the advantage of script (writing system) independence, and hence useful
     * for people or systems using non-Latin scripts.
     *
     * The alphabetic country codes were first included in ISO 3166 in 1974, and
     * the numeric country codes were first included in 1981. The country codes
     * have been published as ISO 3166-1 since 1997, when ISO 3166 was expanded
     * into three parts to include codes for subdivisions and former
     * countries.[2]
     *
     */
    private final static String PL_ISO3166_1_PL = "616";
    /**
     * Issuer identifier number (IIS)
     *
     * The first six digits, including the major industry identifier, compose
     * the issuer identifier number (IIN). This identifies the issuing
     * organization. The American Bankers Association is the registration
     * authority for IINs. The official ISO registry of IINs, the "ISO Register
     * of Card Issuer Identification Numbers", is not available to the general
     * public. It is only available to institutions which hold IINs, issue
     * plastic cards, or act as a financial network or processor. Institutions
     * in the third category must sign a license agreement before they are given
     * access to the registry. Several IINs are well known, especially those
     * representing credit card issuers.
     */
    private final static String IIS = MII + PL_ISO3166_1_PL + "99";

    public static String generate(int number) {
        String retVal = IIS; // first 6 numbers

        if (number < 0 || number > 999999999) {
            throw new IllegalArgumentException("number < 0 || number > 999999999");
        }
        String n = number + "";
        while (n.length() < 9) {
            n = "0" + n;
        }
        retVal += n;

        return retVal + generateLuhnDigit(retVal); // last 16th number
    }

    public static String generateType0(int prefix, int number) {
        if (number < 0 || number > 9999) {
            throw new IllegalArgumentException("number < 0 || number > 9999");
        }
        if (prefix < 0 || prefix > 9999) {
            throw new IllegalArgumentException("prefix < 0 || prefix > 9999");
        }

        return generate(prefix * 10000 + number);
    }

    public static String generateType4(int prefix, int division, int number) {
        if (number < 0 || number > 9999) {
            throw new IllegalArgumentException("number < 0 || number > 9999");
        }
        if (division < 0 || division > 9) {
            throw new IllegalArgumentException("prefix < 0 || prefix > 9999");
        }
        if (prefix < 0 || prefix > 999) {
            throw new IllegalArgumentException("prefix < 0 || prefix > 9999");
        }

        return generate(4 * 100000000 + prefix * 100000 + division * 10000 + number);
    }

    /**
     * Calculates Luhn digit for specified number code
     *
     * @param s number code to be used to generate Luhn digits. All digits are
     * used for calculation from 0 to length()-1.
     * @return Luhn number as String. In fact it is String[1] containing single
     * decimal digit
     */
    public static String generateLuhnDigit(String s) {
        int digit = calcLuhn(s, true) % 10;
        if (digit != 0) {
            digit = 10 - digit;
        }
        return "" + digit;
    }

    /**
     * Checks if given string is passes Luhn test. We assume that last digit
     * contains Luhn digit.
     *
     * @param s number code to be tested
     * @return true if Luhn test was passed, false otherwise
     */
    public static boolean isValidLuhnNumber(String s) {
        return calcLuhn(s, false) % 10 == 0;
    }

    /**
     * performs necessary calculation to calculate Luhn digit
     *
     * @param numberCode
     * @param even - if rightmost position is treated as odd or even. It is nice
     * hack to use the same function for both generating and validating Luhn
     * digit
     * @return Luhn digit
     */
    private static int calcLuhn(String numberCode, boolean even) {
        int luhnSum = 0;
        for (int i = numberCode.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(numberCode.substring(i, i + 1));
            if (even) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            luhnSum += n;
            even = !even;
        }

        return luhnSum;
    }

    public static void main(String... args) {
        System.out.println(generateType0(1, 1));
    }
}
