package com.emanuel;

import java.math.BigInteger;
import java.util.*;

public class Main {

    private static final HashMap<Character, String> letterToCode = new LinkedHashMap<>();
    private static final HashMap<String, Character> codeToLetter = new LinkedHashMap<>();

    static {
        letterToCode.put(' ', "00");
        letterToCode.put('A', "01");
        letterToCode.put('B', "02");
        letterToCode.put('C', "03");
        letterToCode.put('D', "04");
        letterToCode.put('E', "05");
        letterToCode.put('F', "06");
        letterToCode.put('G', "07");
        letterToCode.put('H', "08");
        letterToCode.put('I', "09");
        letterToCode.put('J', "10");
        letterToCode.put('K', "11");
        letterToCode.put('L', "12");
        letterToCode.put('M', "13");
        letterToCode.put('N', "14");
        letterToCode.put('O', "15");
        letterToCode.put('P', "16");
        letterToCode.put('Q', "17");
        letterToCode.put('R', "18");
        letterToCode.put('S', "19");
        letterToCode.put('T', "20");
        letterToCode.put('U', "21");
        letterToCode.put('V', "22");
        letterToCode.put('W', "23");
        letterToCode.put('X', "24");
        letterToCode.put('Y', "25");
        letterToCode.put('Z', "26");

        for (Map.Entry<Character, String> entry : letterToCode.entrySet()) {
            codeToLetter.put(entry.getValue(), entry.getKey());
        }
    }

    public static void main(String[] args) {

        String message = null;
        List<Long> encryptedBlocks = new ArrayList<>();
        long encrypt;
        long modulo;
        int blocksize;

        try {
            if (args.length < 3) {
                throw new Exception();
            }

            encrypt = Long.parseLong(args[0]);
            modulo = Long.parseLong(args[1]);
            blocksize = Integer.parseInt(args[2]);

            try {
                for (int i = 3; i < args.length; i++) {
                    encryptedBlocks.add(Long.parseLong(args[i]));
                }
            } catch (NumberFormatException e) {
                encryptedBlocks.clear();

                StringBuilder messageBuilder = new StringBuilder();
                for (int i = 3; i < args.length; i++) {
                    messageBuilder.append(args[i]);
                    messageBuilder.append(" ");
                }

                message = messageBuilder.toString();
                message = message.substring(0, message.length() - 1);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Usage: <Encrypt-Number> <Modulo-Number> <Block-Size> <Block1> [Block2] [Block3]... OR <Encrypt-Number> <Modulo-Number> " +
                    "<Message>");
            return;
        }

        header(message, encryptedBlocks, encrypt, modulo);

        if (message != null) {
            encryptedBlocks = encrypt(message, encrypt, modulo, blocksize);
        }

        long decrypt = breakPrivateKey(encrypt, modulo);
        System.out.println("DECRYPTED MESSAGE: " + decrypt(encryptedBlocks, decrypt, modulo, blocksize));


    }

    private static void header(String message, List<Long> encryptedBlocks, long encrypt, long modulo) {
        System.out.println("================================================================");
        System.out.println("===***********************RSA-BREAKER************************===");
        System.out.println("================================================================");
        System.out.println("Given values:");
        if (message != null) {
            System.out.println("Unencrypted Message: " + message + "     n = " + modulo + "     e = " + encrypt);
        } else {
            StringBuilder blocksBuilder = new StringBuilder();
            for (Long l : encryptedBlocks) {
                blocksBuilder.append(l).append(" ");
            }
            System.out.println("Blocks: " + blocksBuilder.toString() + "    n = " + modulo + "     e = " + encrypt);
        }
        System.out.println();
    }

    private static List<Long> encrypt(String message, long e, long n, int blocksize) {
        System.out.println("===========================ENCRYPTION===========================");

        StringBuilder blockBuilder = new StringBuilder();

        for (int i = 0; i < message.length(); i++) {
            blockBuilder.append(letterToCode.get(message.charAt(i)));
        }

        while (blockBuilder.length() % blocksize != 0) {
            blockBuilder.append("0");
        }
        String blocks = blockBuilder.toString();
        String displayBlocks = blocks;
        for (int i = displayBlocks.length() - blocksize; i > 0; i -= blocksize) {
            displayBlocks = displayBlocks.substring(0, i) + "|" + displayBlocks.substring(i);
        }
        System.out.println("Message as unencrypted Blocks: " + displayBlocks);
        System.out.println();

        BigInteger biE = BigInteger.valueOf(e);
        BigInteger biN = BigInteger.valueOf(n);
        StringBuilder encryptedBlocksBuilder = new StringBuilder();
        List<Long> encryptedBlocksList = new ArrayList<>();

        System.out.println("y = xᵉ mod n");
        for (int yIndex = 0; blocks.length() > 0; yIndex++) {
            String currentBlock = blocks.substring(0, blocksize);
            blocks = blocks.substring(blocksize);

            BigInteger currentBlockNum = BigInteger.valueOf(Long.parseLong(currentBlock));
            BigInteger encryptedBlockNum = currentBlockNum.modPow(biE, biN);
            String encryptedBlock = encryptedBlockNum.toString();

            encryptedBlocksBuilder.append(encryptedBlock).append(" ");
            encryptedBlocksList.add(encryptedBlockNum.longValueExact());
            System.out.println("y" + subscript(String.valueOf(yIndex + 1)) + " = " + currentBlock + superscript(String.valueOf(e)) + " mod " + n + " = " +
                    encryptedBlock);
        }

        System.out.println();
        System.out.println("Message as encrypted Blocks: " + encryptedBlocksBuilder.toString());
        System.out.println();

        return encryptedBlocksList;
    }

    private static String decrypt(List<Long> encryptedBlocks, long d, long n, int blocksize) {
        System.out.println("===========================DECRYPTION===========================");

        StringBuilder encryptedBlocksBuilder = new StringBuilder();
        for (Long l : encryptedBlocks) {
            encryptedBlocksBuilder.append(l).append(" ");
        }
        System.out.println("Message as encrypted Blocks: " + encryptedBlocksBuilder.toString());
        System.out.println();

        System.out.println("x = yᵈ mod n");
        BigInteger biD = BigInteger.valueOf(d);
        BigInteger biN = BigInteger.valueOf(n);
        StringBuilder decryptedBlocksBuilder = new StringBuilder();
        for (int xIndex = 0; xIndex < encryptedBlocks.size(); xIndex++) {

            BigInteger encryptedBlockNum = BigInteger.valueOf(encryptedBlocks.get(xIndex));
            BigInteger decryptedBlockNum = encryptedBlockNum.modPow(biD, biN);
            StringBuilder decryptedBlock = new StringBuilder(decryptedBlockNum.toString());

            while (decryptedBlock.length() < blocksize) {
                decryptedBlock.insert(0, "0");
            }

            decryptedBlocksBuilder.append(decryptedBlock);
            System.out.println("x" + subscript(String.valueOf(xIndex + 1)) + " = " + encryptedBlocks.get(xIndex) + superscript(String.valueOf(d)) + " mod " +
                    n + " = " + decryptedBlock);
        }

        String decryptedBlocksString = decryptedBlocksBuilder.toString();

        if (decryptedBlocksString.length() % 2 != 0) {
            if (decryptedBlocksString.charAt(decryptedBlocksString.length() - 1) == '0') {
                decryptedBlocksString = decryptedBlocksString.substring(0, decryptedBlocksString.length() - 1);
            } else {
                decryptedBlocksString += "0";
            }
        }


        String decryptedBlocksDisplayString = decryptedBlocksString;
        for (int i = decryptedBlocksDisplayString.length() - 2; i > 0; i -= 2) {
            decryptedBlocksDisplayString = decryptedBlocksDisplayString.substring(0, i) + "|" + decryptedBlocksDisplayString.substring(i);
        }
        System.out.println();
        System.out.println("Message as decrypted Blocks: " + decryptedBlocksDisplayString);

        StringBuilder messageBuilder = new StringBuilder();
        while (decryptedBlocksString.length() > 0) {
            String letter = decryptedBlocksString.substring(0, 2);
            decryptedBlocksString = decryptedBlocksString.substring(2);
            messageBuilder.append(codeToLetter.get(letter));
        }

        return messageBuilder.toString();
    }


    private static long breakPrivateKey(long e, long n) {
        System.out.println("=======================BREAK PRIVATE KEY========================");

        long phi = eulerPhiForTwoPrimes(n);

        System.out.println("     e * d mod Φ = 1");
        System.out.println("     " + e + " * d mod " + phi + " = 1");
        System.out.println();

        long d = euclidModInverse(e, phi);

        if (d < 0) {
            System.out.println("     d < 0 --> d = d + Φ");
            System.out.println("     d = " + d + " + " + phi + " = " + (d + phi));
            System.out.println();
            d = d + phi;
        }

        long result = (e * d) % phi;
        System.out.println("     " + e + " * " + d + " mod " + phi + " = " + result);
        System.out.println();

        return d;


    }

    private static long eulerPhiForTwoPrimes(long n) {
        System.out.println("     =====================FIND P AND Q=====================     ");
        long nSqrt = (long) Math.ceil(Math.sqrt(n));
        System.out.println("     sqrt(" + n + ") ≈ " + nSqrt);

        long remainder = nSqrt * nSqrt - n;
        System.out.println("     " + nSqrt + "² - " + n + " = " + remainder);

        long rSqrt = (long) Math.ceil(Math.sqrt(remainder));
        System.out.println("     " + nSqrt + "² - " + n + " = " + rSqrt + "²");

        long p = nSqrt + rSqrt;
        long q = nSqrt - rSqrt;
        System.out.println("     (" + nSqrt + " + " + rSqrt + ") * (" + nSqrt + " - " + rSqrt + ") = " + n);
        System.out.println("     " + p + " * " + q + " = " + n);
        if (isPrime(p) && isPrime(q) && (p * q == n)) {
            System.out.println("     Statement is TRUE");
        } else bruteforce:{
            System.out.println("     Statement is FALSE, now brute-forcing p and q by iterating");
            System.out.print("     ...");
            for (int tryP = 0; tryP < n; tryP++) {
                if (tryP % 10 == 0) {
                    System.out.print(".");
                }

                if (!isPrime(tryP)) {
                    continue;
                }

                if (n % tryP != 0) {
                    continue;
                }

                long tryQ = n / tryP;
                if (!isPrime(tryQ)) {
                    continue;
                }

                System.out.println();
                p = tryP;
                q = tryQ;
                System.out.println("     Brute-forced p and q");
                break bruteforce;
            }
            System.out.println();
            System.out.println("     n can not be factored to two primes - aborting");
            return 0;
        }

        System.out.println("     p = " + p + "   q = " + q);
        System.out.println();

        System.out.println("     ====================CALCULATE PHI=====================     ");

        long phi = (p - 1) * (q - 1);
        System.out.println("     (" + p + " - 1) * (" + q + " - 1) = " + phi);
        System.out.println("     Φ = " + phi);
        System.out.println();
        return phi;
    }

    private static long euclidModInverse(long factor, long mod) {
        System.out.println("     ================MOD-INVERSE VIA EUCLID================     ");
        long remainder;

        long dividend = mod;
        long divisor = factor;
        int maxStringLen = String.valueOf(mod).length();

        List<Long> numbers = new ArrayList<>();
        numbers.add(dividend);
        numbers.add(divisor);

        do {
            remainder = dividend % divisor;

            numbers.add(remainder);

            System.out.printf("     %" + maxStringLen + "d mod %" + maxStringLen + "d = %" + maxStringLen + "d \n", dividend, divisor, remainder);
            dividend = divisor;
            divisor = remainder;
        } while (remainder != 0);

        System.out.println();


        long s = 0;
        long a;
        long t = 1;
        long b;
        long oldS;
        long oldT;

        for (int row = numbers.size() - 2; row >= 2; row--) {
            oldS = s;
            oldT = t;

            s = oldT;
            a = numbers.get(row - 2);
            t = -(numbers.get(row - 2)) / (numbers.get(row - 1)) * oldT + oldS;
            b = numbers.get(row - 1);

            System.out.printf("     " + numbers.get(numbers.size() - 2) + " = %" + maxStringLen + "d * %" + maxStringLen + "d " + (t < 0 ? "-" : "+") + " %"
                    + maxStringLen + "d * %" + maxStringLen + "d \n", s, a, Math.abs(t), b);
        }

        System.out.println();
        System.out.println("     --> d = " + t);
        System.out.println();

        return t;
    }

    private static String superscript(String str) {
        str = str.replaceAll("0", "⁰");
        str = str.replaceAll("1", "¹");
        str = str.replaceAll("2", "²");
        str = str.replaceAll("3", "³");
        str = str.replaceAll("4", "⁴");
        str = str.replaceAll("5", "⁵");
        str = str.replaceAll("6", "⁶");
        str = str.replaceAll("7", "⁷");
        str = str.replaceAll("8", "⁸");
        str = str.replaceAll("9", "⁹");
        return str;
    }

    private static String subscript(String str) {
        str = str.replaceAll("0", "₀");
        str = str.replaceAll("1", "₁");
        str = str.replaceAll("2", "₂");
        str = str.replaceAll("3", "₃");
        str = str.replaceAll("4", "₄");
        str = str.replaceAll("5", "₅");
        str = str.replaceAll("6", "₆");
        str = str.replaceAll("7", "₇");
        str = str.replaceAll("8", "₈");
        str = str.replaceAll("9", "₉");
        return str;
    }

    private static boolean isPrime(long num) {
        if (num < 2) return false;
        if (num == 2) return true;
        if (num % 2 == 0) return false;
        for (int i = 3; i * i <= num; i += 2)
            if (num % i == 0) return false;
        return true;
    }

}
