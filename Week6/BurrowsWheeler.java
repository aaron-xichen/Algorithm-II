public class BurrowsWheeler {
    private static int SIZE = 256;
    public static void encode() {
        String s = BinaryStdIn.readString();
        BinaryStdIn.close();
        int length = s.length();
        CircularSuffixArray csa = new CircularSuffixArray(s);
        for (int i = 0; i < length; i++) {
            if (csa.index(i) == 0) {
                BinaryStdOut.write(i);
                break;
            }
        }
        for (int i = 0; i < length; i++) {
            char tail = s.charAt((csa.index(i) + length - 1) % length);
            BinaryStdOut.write(tail);
        }
        BinaryStdOut.close();
    }

    public static void decode() {
        int first = BinaryStdIn.readInt();
        String s = BinaryStdIn.readString();
        int length = s.length();
        BinaryStdIn.close();

        // System.out.println("pre:" + s);
        int counts[] = new int[SIZE + 1];
        for (int i = 0; i < length; i++) {
            char c = (char)(s.charAt(i) + 1);
            counts[c]++;
        }

        // sum up
        for (int i = 1; i < SIZE + 1; i++) {
            counts[i] = counts[i - 1] + counts[i];
        }

        // build next[]
        int next[] = new int[length];
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            int pos = counts[c];
            counts[c]++;
            next[pos] = i;
        }

        // build string
        char result[] = new char[length];
        int idx = length - 1;
        for (int i = 0; i < length; i++) {
            result[idx] = s.charAt(first);
            first = next[first];
            idx = (idx + 1) % length;
        }
        String str = String.valueOf(result);
        BinaryStdOut.write(str);
        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        String flag = args[0];
        if (flag.equalsIgnoreCase("-")) {
            BurrowsWheeler.encode();
        } else {
            BurrowsWheeler.decode();
        }
    }
}
