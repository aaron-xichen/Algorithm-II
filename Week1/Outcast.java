public class Outcast {
    private WordNet wordnet;
    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int len = nouns.length;
        int[][] distance = new int[len][len];
        for (int i = 0; i < len - 1; i++) {
            for (int j = i + 1; j < len; j++) {
                distance[i][j] = wordnet.distance(nouns[i], nouns[j]);
            }
        }

        // print
        // for (int i = 0; i < len; i++) {
        // for (int j = 0; j < len; j++) {
        // System.out.print(distance[i][j] + " ");
        // }
        // System.out.println();
        // }

        int maxIndex = -1;
        int maxValue = Integer.MIN_VALUE;
        for (int i = 0; i < len; i++) {
            // cal sum
            int sum = 0;
            for (int j = 0; j < len; j++) {
                if (j < i) sum += distance[j][i];
                if (j == i) continue;
                if (j > i) sum += distance[i][j];
            }
            if (sum > maxValue) {
                maxIndex = i;
                maxValue = sum;
            }
        }
        return nouns[maxIndex];
    }

    // see test client below
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
