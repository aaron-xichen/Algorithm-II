import java.lang.StringBuilder;
public class MoveToFront {
    private class LinkList {
        private LinkList next;
        public LinkList() {
            this.next = null;
        }
        public LinkList getNext() {
            return this.next;
        }
        public void setNext(LinkList next) {
            this.next = next;
        }
    }
    private static int SIZE = 256;
    public static void encode() {
        String s = BinaryStdIn.readString();
        BinaryStdIn.close();
        int length = s.length();

        // build the codebook
        char book[] = new char[SIZE];
        for (int i = 0; i < SIZE; i++) {
            book[i] = (char)i;
        }

        // encode
        char encode[] = new char[length];
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            for (int j = 0; j < SIZE; j++) {
                if (c == book[j]) {
                    encode[i] = (char)j;

                    // move front
                    int k = j;
                    char tmp = book[k];
                    while (k > 0) {
                        book[k] = book[k - 1];
                        k--;
                    }
                    book[0] = tmp;
                    break;
                }
            }
        }

        String str = String.valueOf(encode);
        BinaryStdOut.write(str);
        BinaryStdOut.close();

    }

    public static void decode() {
        String s = BinaryStdIn.readString();
        int length = s.length();

        // build the codebook
        char book[] = new char[SIZE];
        for (int i = 0; i < SIZE; i++) {
            book[i] = (char)i;
        }

        // decode
        char decode[] = new char[length];
        for (int i = 0; i < length; i++) {
            int idx = s.charAt(i);
            char c = book[idx];
            decode[i] = c;
            if (idx == 0)
                continue;

            // move front
            while (idx > 0) {
                book[idx] = book[idx - 1];
                idx--;
            }
            book[0] = c;
        }

        String str = String.valueOf(decode);
        BinaryStdOut.write(str);
        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        String flag = args[0];
        if (flag.equalsIgnoreCase("-")) {
            MoveToFront.encode();
        } else {
            MoveToFront.decode();
        }
    }
}
