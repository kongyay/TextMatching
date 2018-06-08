package textmatching;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Homework2 {

    public static void main(String[] args) {
        int argumentSize = 10;
        int maxProductLength = 0;
        ArrayList<String> allProducts = new ArrayList<>();

        // Argument
        if (args.length == 1) {
            argumentSize = Integer.parseInt(args[0]);
            System.out.println(".. Output Size set to " + args[0]);
        }

        // File vars
        String path = "src/textmatching/product.txt";
        File file = new File(path);

        // Open file
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                allProducts.add(line);
                if(line.length()>maxProductLength)
                    maxProductLength = line.length();
            }
            br.close();
        } catch (IOException e) {
            System.out.println("can't open file: " + e);
        }
        System.out.println(".. Product Size: " + allProducts.size());
        
        // Loop Input
        while(true) {
            // Scan input -> keywords
            Scanner input = new Scanner(System.in);
            System.out.println("Product Search - Input your keyword (s): ");
            String keywordStr = input.nextLine();
            // exit program
            if(keywordStr.length()==0)
                break;
            String[] allKeywords = keywordStr.split(" ");
            System.out.println("Keyword Size: "+ allKeywords.length);

            // Matching
            int id = 0;
            ArrayList<MatchProduct> matchProducts = new ArrayList<>();
            for(String product : allProducts) {
                MatchProduct newMatch = null;
                for(String keyword : allKeywords) {

                    ArrayList<Integer> match_i = findString(product.toLowerCase().toCharArray(),keyword.toLowerCase().toCharArray());
                    if(match_i.size() > 0) {
                        if(newMatch == null)
                            newMatch = new MatchProduct(id,product);
                        newMatch.addKeywordMap(match_i);
                    }

                }
                if(newMatch != null)
                    matchProducts.add(newMatch);
                id++;
            }

            // Sorting
            // Combining 3 conditions into 1 integer by multiplying to its weight
            if(matchProducts.size()>0) {
                // Multiplier size
                int exp = (int) Math.pow(10,Math.ceil(Math.log10(maxProductLength))); // in product.txt max size is 38 -> exp should be 100
                int[] arr = new int[matchProducts.size()]; // to store weight
                int[] index_arr = new int[matchProducts.size()]; // to store index
                for(int i=0;i< matchProducts.size();i++) {
                    int combined = 0;
                    combined += (maxProductLength-matchProducts.get(i).getMatchCount()) * exp * exp; // biggest weight (but in inverse variation)
                    combined += matchProducts.get(i).getFirstDist() * exp; // second weight
                    combined += Math.min(matchProducts.get(i).getMinDist(),maxProductLength); // lowest weight , check min to prevent Integer.MAX
                    arr[i] = combined;
                    index_arr[i] = i;
                }
                radixSort(arr,index_arr);
                ArrayList<MatchProduct> new_matchProducts = new ArrayList<>();
                for(int i=0;i<index_arr.length;i++) {
                    new_matchProducts.add(matchProducts.get(index_arr[i]));
                }
                matchProducts = new_matchProducts;
            }


            // Output
            printList(matchProducts,argumentSize);
        }
        
    }
    
            
    private static void printList(ArrayList<MatchProduct> matchProducts,int argumentSize) {
        System.out.println("Search Result is:");
        for(int i=0;i<argumentSize;i++) {
            if(matchProducts.size()<=i)
                break;
            MatchProduct mp = matchProducts.get(i);
            System.out.println("- " + mp.getName());
        }
        System.out.println(matchProducts.size() + " product(s) matched");
    }

    private static ArrayList<Integer> findString(char[] prod, char[] key) {
        int prod_len = prod.length;
        int key_len = key.length;
        ArrayList<Integer> match_index = new ArrayList<>();
        Map<Character, Integer> lastCharMatch = new HashMap<>(); // Last index of prod that match character in key
        // Set hashmap of char in prodword = -1 (Default/Not Used)
        for (int i = 0; i < prod_len; i++) {
            lastCharMatch.put(prod[i], -1);
        }
        // Set hashmap of char in key = last occurance index
        for (int i = 0; i < key_len; i++) {
            lastCharMatch.put(key[i], i); 
        }
        
        int i = key_len - 1; // product runner
        int j = key_len - 1; // keyword runner
        int last_i = -1;
        while (i < prod_len) {
            // Match
            if (prod[i] == key[j]) {
                // save last i
                if (last_i == -1) { 
                    last_i = i;
                }
                // Math all
                if (j == 0) { 
                    match_index.add(i);
                    i = last_i + key_len;
                    j = key_len - 1;
                    last_i = -1;
                } 
                // Next
                else {
                    i--; // go left until match all / not match
                    j--;
                }
            } 
            // Not Match
            else {
                i += key_len - Math.min(j, 1 + lastCharMatch.get(prod[i])); // find jump step
                j = key_len - 1; // reset
            }
        }
        return match_index; // not found

    }
    
    private static void radixSort(int[] arr, int[] index_arr) {
        List<Integer>[] buckets = new ArrayList[10];
        List<Integer>[] index_buckets = new ArrayList[10];
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new ArrayList<>();
            index_buckets[i] = new ArrayList<>();
        }

        // sort
        boolean flag = false;
        int tmp = -1, divisor = 1;
        while (!flag) {
            flag = true;
            // split input between lists
            for(int i=0;i<arr.length;i++) {
                tmp = arr[i] / divisor;
                buckets[tmp % 10].add(arr[i]);
                index_buckets[tmp % 10].add(index_arr[i]);
                if (flag && tmp > 0) {
                    flag = false;
                }
            }
            // empty lists into input array
            int a = 0;
            for (int b = 0; b < 10; b++) {
                for(int i=0;i<buckets[b].size();i++) {
                    arr[a] = buckets[b].get(i);
                    index_arr[a] = index_buckets[b].get(i);
                    a++;
                }
                buckets[b].clear();
                index_buckets[b].clear();
            }
            // move to next digit
            divisor *= 10;
        }
    }
}

class MatchProduct {
    private final int id;
    private final String name;
    private final ArrayList<ArrayList<Integer>> keywordMatchs;
    private Integer firstDist;
    private Integer minDist;

    public MatchProduct(int id,String name) {
        keywordMatchs = new ArrayList<>();
        firstDist = null;
        minDist = null;
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    

    void addKeywordMap(ArrayList<Integer> match_i) {
        keywordMatchs.add(match_i);
    }

    public int getMatchCount() {
        return keywordMatchs.size();
    }
    
    public int getFirstDist() {
        // Already done it before
        if(firstDist!=null)
            return firstDist;
        // Find min of "first found" from every keyword matched
        Integer min = Integer.MAX_VALUE;
        for(ArrayList<Integer> index_list : keywordMatchs) {
            if(index_list.get(0)<min) {
                min = index_list.get(0);
            }
        }
        if(firstDist==null)
            firstDist = min;
        return min;
    }

    public int getMinDist() {
        // Already done it before
        if(minDist!=null)
            return minDist;
        // Find min from every keyword "pair"
        Integer min = Integer.MAX_VALUE;
        for (int k = 0; k < keywordMatchs.size() - 1; k++) {
            ArrayList<Integer> a_list = keywordMatchs.get(k);
            ArrayList<Integer> b_list = keywordMatchs.get(k+1);
            for (int i = 0; i < a_list.size(); i++) {
                for (int j = 0; j < b_list.size(); j++) {
                    int index1 = a_list.get(i);
                    int index2 = b_list.get(j);
                    int dist = Math.abs(index1-index2);
                    if (dist < min) {
                        min = dist;
                    }
                }
            }
        }
        if(minDist==null)
            minDist = min;
        return min;
    }
}