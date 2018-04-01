package com.example.demo;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

public class Graph {
    public class Edge {
        public int v, w, next;

        public Edge(int v, int w, int next) {
            this.v = v;
            this.w = w;
            this.next = next;
        }
    }

    public class disjoint_set {
        private int[] father;

        public disjoint_set() {

        }

        public disjoint_set(int n) {
            init(n);
        }

        private void init(int n) {// initialize
            father = new int[n + 1];
            for (int i = 0; i <= n; i++) father[i] = i;
        }

        public int find(int x) {// find x's disjoint
            if (father[x] == x) {
                return x;
            } else {
                father[x] = find(father[x]);
                return father[x];
            }
        }

        public void union(int x, int y) {// union x and y
            father[find(x)] = find(y);
        }
    }

    private Vector<String> word_list_sub = new Vector<>();// save the same length words
    private Vector<Edge> edge = new Vector<>();
    private int[] head;
    private HashMap<String, Integer> dictionary = new HashMap<>();// the same length words searching implement
    private Vector<String> word_list = new Vector<>();// save all the words in the Dictionary
    private disjoint_set union_find = new disjoint_set();

    public Graph(String name) throws IOException {
        DataInputStream in = new DataInputStream(new FileInputStream(name));
        BufferedReader d = new BufferedReader(new InputStreamReader(in));
        String s;
        while ((s = d.readLine()) != null) {
            word_list.addElement(s);
        }
    }

    public Graph() throws IOException {
        this("smalldict1.txt");
    }

    public boolean access(String s_begin, String s_end) {// pend access or not
        int pos_begin = dictionary.get(s_begin), pos_end = dictionary.get(s_end);
        return union_find.find(pos_begin) == union_find.find(pos_end);
    }

    public int getLadder(String s_begin, String s_end, Vector<String> path) {
        Vector<Integer> prev = new Vector<>(0);// save node's previous one
        path.clear();
        int begin = dictionary.get(s_begin), end = dictionary.get(s_end);
        int ans = spfa(word_list_sub.size(), begin, end, prev);// ans represents the shortest ladder length
        path.addElement(s_end);
        for (int now = prev.get(end); now != begin; now = prev.get(now)) {
            path.addElement(word_list_sub.get(now - 1));// dictionary saves the position+1 in word_list
        }
        path.addElement(s_begin);
        return ans;
    }

    public void init(String s_begin, String s_end) {// create the graph and the nodes
        edge.clear();
        dictionary.clear();
        word_list_sub.clear();
        int list_length = word_list.size(), length = s_begin.length();
        union_find.init(list_length);
        // clear all
        head = new int[list_length];
        for (int i = 0; i < list_length; i++) {
            head[i] = -1;
        }
        // head saves first edges,-1 represents null
        for (int i = 0; i < list_length; i++) {
            String now = word_list.get(i);
            if (now.length() != length) continue;// skip words with different length
            word_list_sub.addElement(now);// add to sub
            dictionary.put(now, word_list_sub.size());
        }
        if (dictionary.get(s_end) == null || dictionary.get(s_begin) == null) {
            Controller.error("The two words must be found in the dictionary.");
        }
        list_length = word_list_sub.size();// work in word_list_sub
        for (int i = 0; i < list_length; i++) {
            findEdgeToAdd(word_list_sub.get(i), i + 1);// i+1 presents pos in dictionary
        }
    }

    public void addEdge(int i, int j) {// addEdge from list[i-1] to list[j-1]
        union_find.union(i, j);
        edge.addElement(new Edge(j, 1, head[i]));
        head[i] = edge.size() - 1;
    }

    private int spfa(int tot, int start, int end, Vector<Integer> prev) {// tot represents for list length///calculate the min and get the path
        tot += 2;// indicate enough alloc for vector
        Queue<Integer> queue = new LinkedList<>();// implement queue
        boolean[] vis = new boolean[tot];// visited
        int[] distance = new int[tot];// distance from v0
        for (int i = 0; i < tot; i++) {
            distance[i] = tot;
            vis[i] = false;
            prev.addElement(-1);// -1 represents prev not found
        }
        queue.offer(start);// push v0 into queue
        distance[start] = 0;
        vis[start] = true;
        while (!queue.isEmpty()) {
            int u = queue.poll();
            vis[u] = false;
            for (int i = head[u]; i != -1; i = edge.get(i).next) {
                int v = edge.get(i).v;
                if (distance[v] > distance[u] + edge.get(i).w) {// update
                    distance[v] = distance[u] + edge.get(i).w;
                    prev.set(v, u);// save the path
                    if (!vis[v]) {
                        vis[v] = true;
                        queue.offer(v);
                    }
                }
            }
        }
        return distance[end];
    }

    private void findEdgeToAdd(String s, int u) {// every character in s change from 'a' to 'z' and addEdge if available
        int word_length = s.length();
        for (int j = 0; j < word_length; j++) {
            for (char c = 'a'; c <= 'z'; c++) {
                if (s.charAt(j) == c) continue;
                char[] str = new char[s.length()];
                s.getChars(0, s.length(), str, 0);
                str[j] = c;
                Integer value = dictionary.get(new String(str));//search
                if (value != null) {
                    addEdge(u, value);
                }
            }
        }
    }

}