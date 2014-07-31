/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.gae.edziecko.util;

import java.math.BigInteger;


/**
 *
 * @author Bartłomiej P. Prokop
 */
public class TextGenerator {

    public static void main(String[] args) throws Exception {
        for (double d = 0.0; d <= 1000.0; d++) {
            System.out.println(currencyToText(d));
        }
        for (double d = 0.0; d <= 1.0; d += 0.01) {
            System.out.println(currencyToText(d));
        }
        System.out.println(secondsToText(3601));
    }

    private static String triplet2Text(int price) {
        String retVal = "";
        if (price == 0) {
            retVal = "zero";
        }

        if (price > 999) {
            throw new IllegalArgumentException();
        }

        if ((price / 100) > 0) {

            switch (price / 100) {
                case 1: {
                    retVal = "sto";
                    break;
                }
                case 2: {
                    retVal = "dwieście";
                    break;
                }
                case 3: {
                    retVal = "trzysta";
                    break;
                }
                case 4: {
                    retVal = "czterysta";
                    break;
                }
                case 5: {
                    retVal = "pięćset";
                    break;
                }
                case 6: {
                    retVal = "sześćset";
                    break;
                }
                case 7: {
                    retVal = "siedemset";
                    break;
                }
                case 8: {
                    retVal = "osiemset";
                    break;
                }
                case 9: {
                    retVal = "dziewięćset";
                    break;
                }
                default:
                    retVal = "";
            }//end switch
        }


        if ((price >= 100) && (price % 100 > 0)) {
            retVal = retVal + " ";
        }
        price = price % 100;
        if (price >= 20) {
            switch (price / 10) {
                case 2: {
                    retVal = retVal + "dwadzieścia";
                    break;
                }
                case 3: {
                    retVal = retVal + "trzydzieści";
                    break;
                }
                case 4: {
                    retVal = retVal + "czterdzieści";
                    break;
                }
                case 5: {
                    retVal = retVal + "pięćdziesiąt";
                    break;
                }
                case 6: {
                    retVal = retVal + "sześćdziesiąt";
                    break;
                }
                case 7: {
                    retVal = retVal + "siedemdziesiąt";
                    break;
                }
                case 8: {
                    retVal = retVal + "osiemdziesiąt";
                    break;
                }
                case 9: {
                    retVal = retVal + "dziewięćdziesiąt";
                    break;
                }
                default: {
                    retVal = retVal + " ";
                }
            }// end switch
        }//end if (value >=20)

        if ((price > 20) && (price % 10 > 0)) {
            retVal = retVal + " ";
        }

        if ((price <= 19) && (price >= 10)) {
            switch (price) {
                case 10: {
                    retVal = retVal + "dziesięć";
                    break;
                }
                case 11: {
                    retVal = retVal + "jedenaście";
                    break;
                }
                case 12: {
                    retVal = retVal + "dwanaście";
                    break;
                }
                case 13: {
                    retVal = retVal + "trzynaście";
                    break;
                }
                case 14: {
                    retVal = retVal + "czternaście";
                    break;
                }
                case 15: {
                    retVal = retVal + "piętnaście";
                    break;
                }
                case 16: {
                    retVal = retVal + "szesnaście";
                    break;
                }
                case 17: {
                    retVal = retVal + "siedemnaście";
                    break;
                }
                case 18: {
                    retVal = retVal + "osiemnaście";
                    break;
                }
                case 19: {
                    retVal = retVal + "dziewiętnaście";
                    break;
                }
                default: {
                    retVal = retVal + " ";
                }
            }// end switch(price)
        } else {
            price = price % 10;
            switch (price) {
                case 1: {
                    retVal = retVal + "jeden";
                    break;
                }
                case 2: {
                    retVal = retVal + "dwa";
                    break;
                }
                case 3: {
                    retVal = retVal + "trzy";
                    break;
                }
                case 4: {
                    retVal = retVal + "cztery";
                    break;
                }
                case 5: {
                    retVal = retVal + "pięć";
                    break;
                }
                case 6: {
                    retVal = retVal + "sześć";
                    break;
                }
                case 7: {
                    retVal = retVal + "siedem";
                    break;
                }
                case 8: {
                    retVal = retVal + "osiem";
                    break;
                }
                case 9: {
                    retVal = retVal + "dziewięć";
                    break;
                }
                default: {
                    retVal = retVal + " ";
                }
            }// end of switch(value)
        }// end else

        return retVal;
    }// end of triplet2Text

    private static String sign2Text(double value) {
        String retVal = "";
        if (value < 0) {
            retVal = "minus";
        } else {
            retVal = "";
        }
        return retVal;
    }//end of function Sign2Text

    private static String long2Text(long value) {
        String retVal = "";
        String val = String.valueOf(value).toString();
        BigInteger i = new BigInteger(val);
        BigInteger bilion = new BigInteger("1000000000000");
        java.math.BigInteger miliard = new java.math.BigInteger("1000000000");
        java.math.BigInteger milion = new java.math.BigInteger("1000000");
        java.math.BigInteger tys = new java.math.BigInteger("1000");
        java.math.BigInteger sto = new java.math.BigInteger("100");
        java.math.BigInteger zero = new java.math.BigInteger("0");

        if (i.intValue() == 0) {
            retVal = triplet2Text(i.intValue());
        } else {
            retVal = " ";
        }

        if ((i.mod(bilion).divide(miliard).compareTo(zero)) == 1) {
            retVal = retVal + triplet2Text(i.mod(bilion).divide(miliard).intValue());

            switch (i.mod(bilion).divide(miliard).intValue()) {
                case 1: {
                    retVal = retVal + " miliard";
                    break;
                }
                case 2: {
                    retVal = retVal + " miliardy";
                    break;
                }
                case 3: {
                    retVal = retVal + " miliardy";
                    break;
                }
                case 4: {
                    retVal = retVal + " miliardy";
                    break;
                }
                case 5: {
                    retVal = retVal + " miliardów";
                    break;
                }
                case 6: {
                    retVal = retVal + " miliardów";
                    break;
                }
                case 7: {
                    retVal = retVal + " miliardów";
                    break;
                }
                case 8: {
                    retVal = retVal + " miliardów";
                    break;
                }
                case 9: {
                    retVal = retVal + " miliardów";
                    break;
                }
                default: {
                    retVal = retVal + " miliardów";
                }
            }
            if (i.mod(miliard).compareTo(zero) == 1) {
                retVal = retVal + " ";
            }
        }

        if ((i.mod(miliard).divide(milion).compareTo(zero)) == 1) {
            retVal = retVal + triplet2Text(i.mod(miliard).divide(milion).intValue());

            switch (i.mod(miliard).divide(milion).intValue()) {
                case 1: {
                    retVal = retVal + " milion";
                    break;
                }
                case 2: {
                    retVal = retVal + " miliony";
                    break;
                }
                case 3: {
                    retVal = retVal + " miliony";
                    break;
                }
                case 4: {
                    retVal = retVal + " miliony";
                    break;
                }
                case 5: {
                    retVal = retVal + " milionów";
                    break;
                }
                case 6: {
                    retVal = retVal + " milionów";
                    break;
                }
                case 7: {
                    retVal = retVal + " milionów";
                    break;
                }
                case 8: {
                    retVal = retVal + " milionów";
                    break;
                }
                case 9: {
                    retVal = retVal + " milionów";
                    break;
                }
                default: {
                    retVal = retVal + " milionów";
                }
            }
            if (i.mod(milion).compareTo(zero) == 1) {
                retVal = retVal + " ";
            }
        }

        if ((i.mod(milion).divide(tys).compareTo(zero)) == 1) {
            retVal = retVal + triplet2Text(i.mod(milion).divide(tys).intValue());

            switch (i.mod(milion).divide(tys).intValue()) {
                case 1: {
                    retVal = retVal + " tysiąc";
                    break;
                }
                case 2: {
                    retVal = retVal + " tysiące";
                    break;
                }
                case 3: {
                    retVal = retVal + " tysiące";
                    break;
                }
                case 4: {
                    retVal = retVal + " tysiące";
                    break;
                }
                case 5: {
                    retVal = retVal + " tysięcy";
                    break;
                }
                case 6: {
                    retVal = retVal + " tysięcy";
                    break;
                }
                case 7: {
                    retVal = retVal + " tysięcy";
                    break;
                }
                case 8: {
                    retVal = retVal + " tysięcy";
                    break;
                }
                case 9: {
                    retVal = retVal + " tysięcy";
                    break;
                }
                default: {
                    retVal = retVal + " tysięcy";
                }
            }
            if (i.mod(tys).compareTo(zero) == 1) {
                retVal = retVal + " ";
            }
        }


        if (i.mod(tys).compareTo(zero) == 1) {
            retVal = retVal + triplet2Text(i.mod(tys).intValue());
        }

        if (i.intValue() == 1) {
            retVal = retVal + " złoty";
        } else if (i.mod(sto).intValue() > 10 && i.mod(sto).intValue() < 20) {
            retVal = retVal + " złotych";
        } else if (i.mod(BigInteger.TEN).intValue() >= 2 && i.mod(BigInteger.TEN).intValue() <= 4) {
            retVal = retVal + " złote";
        } else {
            retVal = retVal + " złotych";
        }

        return retVal;
    }//end of int642String

    public static String currencyToText(double value) {
        value = BPMath.roundCurrency(value);

        String retVal = "";
        long a = 0;
        long b = 0;

        retVal = sign2Text(value);
        if (retVal.length() > 0) {
            retVal = retVal + " ";
        } else {
            retVal = "";
        }

        if (value < 0.0) {
            value = value * (-1.0);
        }

        a = BPMath.truncate(value);
        b = Math.round(BPMath.fractional(value) * 100);

        retVal = retVal + long2Text(a);

        if (b != 0) {
            retVal = retVal + " " + triplet2Text((int) b);

            if ((int) b == 1) {
                retVal = retVal + " grosz";
            } else if ((int) b % 100 > 10 && (int) b % 100 < 20) {
                retVal = retVal + " groszy";
            } else if ((int) b % 10 >= 2 && (int) b % 10 <= 4) {
                retVal = retVal + " grosze";
            } else {
                retVal = retVal + " groszy";
            }
        }
        if (retVal.indexOf("  ") != -1) {
            retVal = retVal.replaceAll("  ", " ");
        }
        return retVal.trim();
    }

    public static String secondsToText(int seconds) {
        int h = seconds / 3600;
        int m = (seconds / 60) % 60;
        int s = seconds % 60;
        return h + "h " + m + "m " + s + "s";
    }
}
