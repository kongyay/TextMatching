package textmatching;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Homework2 {

    public static void main(String[] args) {
        int showNum = 10;
        ArrayList<String> products = new ArrayList<>();

        if (args.length > 0)
            showNum = Integer.parseInt(args[0]);

        File file = new File("src/textmatching/product.txt");

        try {
            BufferedReader buf = new BufferedReader(new FileReader(file));
            String line;
            while ((line = buf.readLine()) != null) 
                products.add(line);    
            buf.close();
        } catch (IOException e) {
            System.out.println("Error - " + e);
        }
        
        // Input
        while(true) {
            Scanner inputScan = new Scanner(System.in);
            System.out.println("Product Search - Input your keyword (s): ");
            String input = inputScan.nextLine();
            
            if(input.length()==0) // quit
                break;
            
            String[] keywords = input.split(" ");

            ArrayList<MatchProduct> matchProducts = new ArrayList<>();
            for(String product : products) {
                MatchProduct newMatch = null;
                for(String keyword : keywords) {
                    ArrayList<Integer> match = matching(product.toLowerCase().toCharArray(),keyword.toLowerCase().toCharArray());
                    if(match.size() > 0) {
                        if(newMatch == null)
                            newMatch = new MatchProduct(product);
                        newMatch.addMatch(match);
                    }
                }
                if(newMatch != null)
                    matchProducts.add(newMatch);
            }

            // Sorting
            Collections.sort(matchProducts);
            // Show Output
            System.out.println("Search Result is:");
            for(int i=0;i<showNum;i++) {
                if(matchProducts.size()<=i)
                    break;
                MatchProduct mp = matchProducts.get(i);
                System.out.println("- " + mp.getName());
            }
            System.out.println(matchProducts.size() + " product(s) matched");
        }
        
    }

    private static ArrayList<Integer> matching(char[] product, char[] keyword) {
        int n = product.length;
        int m = keyword.length;
        ArrayList<Integer> match_index = new ArrayList<>();
        Map<Character, Integer> lastCharMatch = new HashMap<>(); 

        for (int i = 0; i < n; i++) {
            lastCharMatch.put(product[i], -1);
        }
        for (int i = 0; i < m; i++) {
            lastCharMatch.put(keyword[i], i); 
        }
        
        int i = m - 1; 
        int k = m - 1; 
        int last_i = -1;
        while (i < n) {
            // Match
            if (product[i] == keyword[k]) {
                if (last_i == -1) { 
                    last_i = i;
                }

                if (k == 0) { 
                    match_index.add(i);
                    i = last_i + m;
                    k = m - 1;
                    last_i = -1;
                } 
                else {
                    i--;
                    k--;
                }
            } 
            else {
                i += m - Math.min(k, 1 + lastCharMatch.get(product[i])); 
                k = m - 1;
            }
        }
        return match_index; 

    }
    
    
}

class MatchProduct implements Comparable<MatchProduct>{
    private final String name;
    private final ArrayList<ArrayList<Integer>> keywordMatchs;

    public MatchProduct(String name) {
        keywordMatchs = new ArrayList<>();
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    void addMatch(ArrayList<Integer> match) {
        keywordMatchs.add(match);
    }

    public int count() {
        return keywordMatchs.size();
    }
    
    public int first() {
        Integer min = Integer.MAX_VALUE;
        for(ArrayList<Integer> index_list : keywordMatchs) {
            if(index_list.get(0)<min) {
                min = index_list.get(0);
            }
        }
        return min;
    }

    public int min() {
        Integer min = Integer.MAX_VALUE;
        for (int k = 0; k < keywordMatchs.size() - 1; k++) {
            for (int i = 0; i < keywordMatchs.get(k).size(); i++) {
                for (int j = 0; j < keywordMatchs.get(k+1).size(); j++) {
                    min = Math.min(min,Math.abs(keywordMatchs.get(k).get(i)-keywordMatchs.get(k+1).get(j)));
                }
            }
        }
        return min;
    }

    @Override
    public int compareTo(MatchProduct o) {
        int cmp1 = this.count();
        int cmp2 = o.count();
        if(cmp1>cmp2)
            return -1;
        else if(cmp1==cmp2) {
            cmp1 = this.first();
            cmp2 = o.first();
            if(cmp1<cmp2)
                return -1;
            else if(cmp1==cmp2) {
                cmp1 = this.min();
                cmp2 = o.min();
                if(cmp1<cmp2)
                    return -1;
                else if(cmp1==cmp2)
                    return 0;
                else 
                    return 1;
            }
            else 
                return 1;
        }
        else 
            return 1; 
    }
}