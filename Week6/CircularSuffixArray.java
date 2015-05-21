public class CircularSuffixArray {
    private static int SIZE = 256;
    private String s;
    private int index[];
    private int aux[];
    private int length;
    public CircularSuffixArray(String s) {
        if (null == s)
            throw new java.lang.NullPointerException();
        this.s = s;
        this.length = s.length();
        this.index = new int[this.length];
        this.aux = new int[this.length];
        for (int i = 0; i < this.length; i++) {
            this.index[i] = i;
        }
        sort(0, this.length - 1, 0);
    }

    private void sort(int lo, int hi, int d) {
        // only 1 element
        if (d >= this.length || hi - lo < 1)
            return;
        // only 2 elements
        else if (hi - lo == 1) {
            char char_lo = s.charAt((index[lo] + d) % this.length);
            char char_hi = s.charAt((index[hi] + d) % this.length);
            if (char_lo > char_hi) {
                int tmp = index[lo];
                index[lo] = index[hi];
                index[hi] = tmp;
            } else if (char_lo == char_hi)
                sort(lo, hi, d + 1);
            return;
            // 3 - 5 elements
        } else if (hi - lo <= 4) {
            for (int i = lo + 1; i <= hi; i++) {
                int j = i;
                int tmp = index[j];
                while (j > lo && s.charAt((tmp + d) % this.length) < s.charAt((index[j - 1] + d) % this.length)) {
                    index[j] = index[j - 1];
                    j--;
                }
                index[j] = tmp;
            }
            int i = lo;
            int j = lo + 1;
            while (j <= hi) {
                if (s.charAt((index[i] + d) % this.length) == s.charAt((index[j] + d) % this.length))
                    j++;
                else {
                    sort(i, j - 1, d + 1);
                    i = j;
                    j++;
                }
            }
            sort(i, j - 1, d + 1);
            return;
        }
        // count

        int counts[] = new int[SIZE + 2];
        for (int i = lo; i <= hi; i++) {
            char c = (char)(s.charAt((index[i] + d) % this.length) + 2);
            counts[c] ++;
        }

        // accumulate
        for (int i = 2; i < SIZE + 2; i++) {
            counts[i] = counts[i] + counts[i - 1];
        }

        // sort
        for (int i = lo; i <= hi; i++) {
            char c = (char)(s.charAt((index[i] + d) % this.length) + 1);
            int pos = counts[c] + lo;
            counts[c]++;
            aux[pos] = this.index[i];
        }

        // copy back
        for (int i = lo; i <= hi; i++) {
            index[i] = aux[i];
        }

        for (int i = 0; i < SIZE; i++) {
            sort(lo + counts[i], lo + counts[i + 1] - 1, d + 1);
        }
    }

    public int length() {
        return this.length;
    }

    public int index(int i) {
        if (i < 0 || i >= this.length)
            throw new java.lang.IndexOutOfBoundsException();
        return index[i];
    }

    public void print() {
        for (int i = 0; i < this.length; i++) {
            int begin = index[i];
            for (int j = 0; j < this.length; j++) {
                char c = this.s.charAt((begin + j) % this.length);
                System.out.print(c);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        CircularSuffixArray csa = new CircularSuffixArray("ABRACADABRA!");
        csa.print();
    }
}
