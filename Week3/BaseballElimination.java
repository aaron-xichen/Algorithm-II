public class BaseballElimination {

    private SeparateChainingHashST<String, Integer> teams;
    private SeparateChainingHashST<String, SET<String> > queries;
    private int number_of_teams;
    private int[][] against;
    private int[] losses;
    private int[] wins;
    private int[] remainings;

    public BaseballElimination(String filename) {
        In in = new In(filename);
        String line = in.readLine();
        number_of_teams = Integer.parseInt(line.trim());

        against = new int[number_of_teams][number_of_teams];
        losses = new int[number_of_teams];
        wins = new int[number_of_teams];
        remainings = new int[number_of_teams];
        teams = new SeparateChainingHashST<String, Integer>();
        queries = new SeparateChainingHashST<String, SET<String> >();

        for (int i = 0; i < number_of_teams; i++) {
            line = in.readLine();
            line = line.trim();
            String [] fields = line.split("\\s+");
            String team_name = fields[0];
            int idx = getTeamIndex(team_name);
            wins[idx] = Integer.parseInt(fields[1]);
            losses[idx] = Integer.parseInt(fields[2]);
            remainings[idx] = Integer.parseInt(fields[3]);
            for (int j = 0; j < number_of_teams; j++) {
                against[i][j] = Integer.parseInt(fields[4 + j]);
            }
        }
    }

    private int getTeamIndex(String team_name) {
        if (!teams.contains(team_name))
            teams.put(team_name, teams.size());
        return teams.get(team_name);
    }

    public int numberOfTeams() {
        return number_of_teams;
    }

    public Iterable<String> teams() {
        return teams.keys();
    }

    public int wins(String team) {
        check(team);
        return wins[getTeamIndex(team)];
    }

    public int losses(String team) {
        check(team);
        return losses[getTeamIndex(team)];
    }

    public int remaining(String team) {
        check(team);
        return remainings[getTeamIndex(team)];
    }

    public int against(String team1, String team2) {
        check(team1);
        check(team2);
        return against[getTeamIndex(team1)][getTeamIndex(team2)];
    }

    // Vertex Id allocation: 0 to number_of_teams-1 represent the teams
    // source vertx: number_of_teams, terminal vertex: number_of_teams+1
    public boolean isEliminated(String team) {
        check(team);
        if (queries.contains(team)) {
            return queries.get(team) == null ? false : true;
        }

        int source_vertex_index = number_of_teams;
        int terminal_vertex_index = number_of_teams + 1;
        int query_vertex_index = getTeamIndex(team);
        int max_wins = wins(team) + remaining(team);
        SET<String> sets = new SET<String>();

        // trivial
        for (String t : teams.keys()) {
            if (max_wins < wins(t))
                sets.add(t);
        }
        if (!sets.isEmpty()) {
            queries.put(team, sets);
            return true;
        }

        //non-trivial
        // construct the flowNetwork
        int total_vertex = 2 + number_of_teams + (number_of_teams - 1) * number_of_teams / 2;
        FlowNetwork fn = new FlowNetwork(total_vertex);
        int index_allocation = number_of_teams + 2;
        for (int i = 0; i < number_of_teams; i++) {
            for (int j = i + 1; j < number_of_teams; j++) {
                if (i == query_vertex_index || j == query_vertex_index) continue;
                FlowEdge source_to_layer1 = new FlowEdge(source_vertex_index, index_allocation, against[i][j]);
                FlowEdge layer1_to_layer2_1 = new FlowEdge(index_allocation, i, Double.POSITIVE_INFINITY);
                FlowEdge layer1_to_layer2_2 = new FlowEdge(index_allocation, j, Double.POSITIVE_INFINITY);
                fn.addEdge(source_to_layer1);
                fn.addEdge(layer1_to_layer2_1);
                fn.addEdge(layer1_to_layer2_2);
                index_allocation++;
            }
        }
        for (int i = 0; i < number_of_teams; i++) {
            FlowEdge layer2_to_terminal = new FlowEdge(i, terminal_vertex_index, max_wins - wins[i]);
            fn.addEdge(layer2_to_terminal);
        }
        // compute the max flow
        FordFulkerson ffk = new FordFulkerson(fn, source_vertex_index, terminal_vertex_index);
        // check
        boolean isEliminated = false;
        for (FlowEdge edge : fn.adj(source_vertex_index)) {
            if (edge.residualCapacityTo(edge.other(source_vertex_index)) > 0) {
                isEliminated = true;
                break;
            }
        }
        // if all edge from source are full
        // then team is not eliminated
        if (!isEliminated) {
            queries.put(team, null);
            return false;
        }

        // else team is eliminated
        // find the min-cut
        for (String t : teams.keys()) {
            int idx = getTeamIndex(t);
            if (ffk.inCut(idx))
                sets.add(t);
        }
        queries.put(team, sets);
        return true;
    }

    public Iterable<String> certificateOfElimination(String team) {
        isEliminated(team);
        return queries.get(team);
    }

    private void check(String team) {
        if (!teams.contains(team))
            throw new java.lang.IllegalArgumentException();
    }

    private void printInfo() {
        System.out.println("number_of_teams:" + number_of_teams);
        for (int i = 0; i < number_of_teams; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(wins[i] + "\t");
            sb.append(losses[i] + "\t");
            sb.append(remainings[i] + "\t");
            for (int j = 0; j < number_of_teams; j++) {
                sb.append(against[i][j] + "\t");
            }
            System.out.println(sb.toString());
        }
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
