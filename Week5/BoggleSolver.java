import java.util.HashSet;
public class BoggleSolver {
    private class TrieNode {
        private char val;
        private TrieNode []next;
        private boolean isLeaf;
        public TrieNode() {
            this('0');
        }
        public TrieNode(char c) {
            this.val = c;
            next = new TrieNode[26];
            isLeaf = false;
        }
        public boolean hasNode(char c) {
            assert(c >= 'A' && c <= 'Z');
            return next[c - 'A'] != null;
        }
        public void setLeaf() {
            this.isLeaf = true;
        }
        public boolean isLeaf() {
            return isLeaf;
        }
        public TrieNode createNode(char c) {
            if (!hasNode(c)) {
                next[c - 'A'] = new TrieNode(c);
            }
            return next[c - 'A'];
        }
        public TrieNode getNode(char c) {
            assert(c >= 'A' && c <= 'Z');
            return next[c - 'A'];
        }
        public char getValue() {
            return val;
        }
        public boolean contains(String word) {
            return contains(word, 0);
        }
        private boolean contains(String word, int idx) {
            if (idx >= word.length()) {
                return isLeaf() ? true : false;
            }
            char c = word.charAt(idx);
            if (!hasNode(c))
                return false;
            return getNode(c).contains(word, idx + 1);
        }
    }
    private TrieNode dict_root;
    public BoggleSolver(String[] dictionary) {
        dict_root = new TrieNode();
        for (String word : dictionary) {
            TrieNode tmp = dict_root;
            int idx = 0;
            while (idx < word.length()) {
                char c = word.charAt(idx);
                tmp = tmp.createNode(c);
                idx++;
            }
            tmp.setLeaf();
        }
    }

    public Iterable<String> getAllValidWords(BoggleBoard board) {
        SET<String> valid_words = new SET<String>();
        if (null == board) return valid_words;
        int width = board.cols();
        int height = board.rows();
        if (0 == width || 0 == height) return valid_words;

        // initialization
        boolean onStack[][] = new boolean[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                onStack[i][j] = false;
            }
        }

        // begin to search
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                DFS(dict_root, "", onStack, board, i, j, valid_words);
            }
        }
        return valid_words;
    }

    private void DFS(TrieNode currentNode, String word, boolean onStack[][], BoggleBoard board, int row, int col, SET<String> valid_words) {
        //isLeaf, save
        if (currentNode.isLeaf() && word.length() >= 3) {
            valid_words.add(word);
        }

        // next char to check
        int height = board.rows();
        int width = board.cols();
        if (row < 0 || row > height - 1 || col < 0 || col > width - 1 || onStack[row][col]) return;
        char c = board.getLetter(row, col);

        // legal, go deeper
        if (currentNode.hasNode(c)) {
            TrieNode next_node = currentNode.getNode(c);
            String next_word = word + c;
            onStack[row][col] = true;
            if (c != 'Q') {
                // go left, right, up, down, left-up, right-up, left-down, right-down respectively
                DFS(next_node, next_word, onStack, board, row, col - 1, valid_words);
                DFS(next_node, next_word, onStack, board, row, col + 1, valid_words);
                DFS(next_node, next_word, onStack, board, row - 1, col, valid_words);
                DFS(next_node, next_word, onStack, board, row + 1, col, valid_words);
                DFS(next_node, next_word, onStack, board, row - 1, col - 1, valid_words);
                DFS(next_node, next_word, onStack, board, row - 1, col + 1, valid_words);
                DFS(next_node, next_word, onStack, board, row + 1, col - 1, valid_words);
                DFS(next_node, next_word, onStack, board, row + 1, col + 1, valid_words);
            } else if (c == 'Q' && next_node.hasNode('U')) {
                next_node = next_node.getNode('U');
                next_word = next_word + 'U';
                // go left, right, up, down, left-up, right-up, left-down, right-down respectively
                DFS(next_node, next_word, onStack, board, row, col - 1, valid_words);
                DFS(next_node, next_word, onStack, board, row, col + 1, valid_words);
                DFS(next_node, next_word, onStack, board, row - 1, col, valid_words);
                DFS(next_node, next_word, onStack, board, row + 1, col, valid_words);
                DFS(next_node, next_word, onStack, board, row - 1, col - 1, valid_words);
                DFS(next_node, next_word, onStack, board, row - 1, col + 1, valid_words);
                DFS(next_node, next_word, onStack, board, row + 1, col - 1, valid_words);
                DFS(next_node, next_word, onStack, board, row + 1, col + 1, valid_words);
            }
            onStack[row][col] = false;
        }
    }

    public int scoreOf(String word) {
        int len = word.length();
        if (len <= 2 || !dict_root.contains(word)) return 0;
        else if (len == 3 || len == 4) return 1;
        else if (len == 5) return 2;
        else if (len == 6) return 3;
        else if (len == 7) return 5;
        else return 11;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        int size = 0;
        for (String word : solver.getAllValidWords(board)) {
            size++;
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
        StdOut.println("Num = " + size);
    }
}
