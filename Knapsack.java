/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mytwocents.ads.knapsackads;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;

/**
 *
 * @author Sangeetha Ramadurai
 */
public class Knapsack {
  int[] itemW;
  int[] itemV;

  Knapsack() {
    itemW = null;
    itemV = null;
  }

  void ksZeroOne(int targetWeight, int nItems, int[] iWeights, int[] iValues) {
    //Algo: Meet in the middle approach
    //Step1: make two lists "s1", "s2" of item ID's
    //Step2: Get a list of all subsets of each set "s1", "s2" and
      //maintain one cache each for the two subset lists - "m1", "m2", s.t. only those subsets based on dominance condition
      //(subsets of equal weight however more value) gets into the cache
      //"m1" -- Weight indexed map "m1" of subsets from list "s1". Holds subsets of equal or more value for the given weight and
      //"m2" -- Weight indexed map "m2" of subsets from list "s2". Holds subsets of equal or more value for the given weight
    //Step3: For each subset in map1 find corresponding subset in map2 that satisfies
      //the weight criteria however has a highest value so far.
      //Upon finding the items pack it in the "knapsackContents"


      itemW = iWeights;
      itemV = iValues;
      //Items list with maximum value
      ArrayList<ArrayList<Integer>> knapsackContents = new ArrayList<ArrayList<Integer>>();
      int m = nItems/2;
      int remainingWt = 0;

      //Step1:
      ArrayList<Integer> s1 = new ArrayList<Integer>(); //First half of items IDs
      ArrayList<Integer> s2 = new ArrayList<Integer>(); //Second half of item IDs
      Map<Integer, ArrayList<ArrayList<Integer>>> m1 = new HashMap<Integer, ArrayList<ArrayList<Integer>>>();
      Map<Integer, ArrayList<ArrayList<Integer>>> m2 = new HashMap<Integer, ArrayList<ArrayList<Integer>>>();

      for(int i = 0; i < m; i++) {
        s1.add(i);
      }
      for(int i = m; i <= nItems-1; i++) {
        s2.add(i);
      }

      //Step2:
      ArrayList<ArrayList<Integer>> ld1 = getAllSubsetsByDominance(s1, 0, 0, m1);
      ArrayList<ArrayList<Integer>> ld2 = getAllSubsetsByDominance(s2, 0, m, m2);

      //Step3:
      Set<Map.Entry<Integer, ArrayList<ArrayList<Integer>>>> kvSet1 = m1.entrySet();
      Set<Integer> keySet2 = m2.keySet();
      ArrayList<Integer> mylst = null;
      ArrayList<Integer> blist = null;
      int val1 = 0, val2 = 0, maxval = 0;

      for(Map.Entry<Integer, ArrayList<ArrayList<Integer>>> elem : kvSet1) {
        val1 = val2 = maxval = 0;
        Integer elemK = elem.getKey();
        ArrayList<Integer> elemVlist = (elem.getValue()).get(0);
        remainingWt = targetWeight-(elem.getKey());
        for(Integer ix : elemVlist) {
          val1 += itemV[ix.intValue()];
        }
        if(!knapsackContents.isEmpty()) {
          for(Integer ix : knapsackContents.get(0)) {
            maxval += itemV[ix.intValue()];
          }
        }

        for(Integer k : keySet2) {
          if(k.intValue() <= remainingWt) {
            //If combined value is higher than what is already chosen to be in knapsack choose this set
            ArrayList<Integer> ls = (m2.get(k)).get(0);
            for(Integer ix : ls) {
              val2 += itemV[ix.intValue()];
            }
            if((val1+val2) > maxval) {
              knapsackContents.clear();
              //Get all possible combinations that makes your knapsack rich !!
              for(ArrayList<Integer> l1 : elem.getValue()) {
                mylst = new ArrayList<Integer>();
                mylst.addAll(l1);
                for(ArrayList<Integer> l2 : m2.get(k) ) {
                  blist = new ArrayList<Integer>();
                  blist.addAll(mylst);
                  blist.addAll(l2);
                  knapsackContents.add(blist);
                }
              }
            } // List of Maximum value
          } //List with matching wt constraint found in the other half
        }
      }

      //Printout knapsack contents
      System.out.println("Pack the following items in knapsack that is of highest " +
                        "total value and within weight constraint of " + targetWeight);
      int tW = 0, tV = 0;
      //Looking at all possible options in the ZERO-ONE knapsack
      for(ArrayList<Integer> l : knapsackContents) {
        System.out.println("Choose items");
        for(Integer e : l) {
         tW += itemW[e];
         tV += itemV[e];
         System.out.println("Weight: " + itemW[e] + ", Value: " + itemV[e]);
        }
        System.out.println("Total weight: " + tW + " Total value: " + tV);
      }
  } //ksZeroOne

  //offset to indicate which half we are looking at: 0 - first half, m - second half.
  ArrayList<ArrayList<Integer>> getAllSubsetsByDominance(ArrayList<Integer> s, int index, int offset,
                                    Map<Integer, ArrayList<ArrayList<Integer>>> cache) {
    ArrayList<ArrayList<Integer>> allss;
    if(s.size() == index) {
      allss = new ArrayList<ArrayList<Integer>>();
      ArrayList<Integer> emptysubset = new ArrayList<Integer>();
      allss.add(emptysubset);
    }
    else {
      allss = getAllSubsetsByDominance(s, index+1, offset, cache);
      ArrayList<ArrayList<Integer>> myallss = new ArrayList<ArrayList<Integer>>();

      int itemId = -1;
      for(ArrayList<Integer> lst : allss) {
        ArrayList<Integer> mysubset = new ArrayList<Integer>(); //itemIDWt
        mysubset.addAll(lst);
        itemId = offset+index;
        mysubset.add(0, itemId);
        
        //Run dominance procedure starts
        int wt = 0, newval = 0, val = 0;
        if(!mysubset.isEmpty()) { //If not an empty list
          for(Integer i : mysubset) {
            wt += itemW[i];
            newval += itemV[i];
          }
          ArrayList<ArrayList<Integer>> alist;
          if(cache.containsKey(wt)) {
            //OK to get first list as it only holds list of equal value when there is more than one
            //of them of given weight
            alist = cache.get(wt);
            for(Integer i : (alist).get(0)) {
              val += itemV[i];    
            }
            if(newval > val) {
              alist.clear();
            }
            if(newval >= val) {
              alist.add(mysubset);
              cache.put(wt, alist);
            }
          }
          else { //Add it if not in the cache
            alist = new ArrayList<ArrayList<Integer>>();
            alist.add(mysubset);
            cache.put(wt, alist);
          }
        }

        //Run dominance procedure ends
        
        myallss.add(mysubset);
      }
      allss.addAll(myallss);
    }

    return allss;
  }

  //Dynamic Programming
  void ksUnbounded(int targetWeight, int nItems, int[] iWeights, int[] iValues) {
    //Algo: Dynamic Programming/Memoizing

    //Step1: Maitain a two-dimensional array, Array[0...nItems][0...targetWeight]
      //Note: Weight increses upto targetWeight
    //Step2: Also maintain a ksTrackArray[0...nItems-1][0...targetWeight] to track which items are
      //part of the solution satisfying the weight value constraint
    //Step3: At any point in dynamic programming find: maximum combined weight of any subsets of items, o...currentItem
      //of weight atmost iw (the ith weight in the 2-dimensional array)

    int[][] ksItemCapacity = new int[nItems+1][targetWeight+1];
    int[][] ksTrack = new int[nItems+1][targetWeight+1];

    for(int w = 0; w <= targetWeight; w++) {
      ksItemCapacity[0][w] = 0;
    }

    for(int item = 1; item < nItems; item++) {
      for(int w = 0; w <= targetWeight; w++) {
        //last known Maximum value of KS contents s.t. their weight totals to atmost w-iWeights[item]
        int eItemValue = (iWeights[item] <= w)? ksItemCapacity[item-1][w-iWeights[item]] : 0;
        if( (iWeights[item] <= w) &&
            (iValues[item]+eItemValue) > ksItemCapacity[item-1][w] ) {
          ksItemCapacity[item][w] = eItemValue+iValues[item]; //current item included
          ksTrack[item][w] = 1;
        }
        else {
          ksItemCapacity[item][w] = ksItemCapacity[item-1][w]; //current item not included
          ksTrack[item][w] = 0;
        }
      }
    }

    //Print KS contents
    ArrayList<Integer> ksContents = new ArrayList<Integer>();
    int tW = targetWeight;
    for(int item = nItems; item >= 0; item--) {
      if(ksTrack[item][tW] == 1) {
        tW -= iWeights[item];
        ksContents.add(item);
      }
    }

    System.out.println("Items choosen are:");
    int W = 0, V = 0;
    for(Integer e : ksContents) {
      W += iWeights[e];
      V += iValues[e];
      System.out.println("Weight: " + iWeights[e] + ", Value: " + iValues[e]);
    }
    System.out.println("Total weight: " + W + " Total value: " + V);

  }

   public static void main(String[] args) {

    Knapsack ks = new Knapsack();

    //Zero One Knapsack
    int targetWeight = 15;
    int nItems = 5;
    int[] iWeights = new int[nItems];
    int[] iValues = new int[nItems];
    //Weights and Values
    iWeights[0] = 1;
    iValues[0]  = 2;
    iWeights[1] = 12;
    iValues[1]  = 4;
    iWeights[2] = 2;
    iValues[2]  = 2;
    iWeights[3] = 1;
    iValues[3]  = 1;
    iWeights[4] = 4;
    iValues[4]  = 10;
    ks.ksZeroOne(targetWeight, nItems, iWeights, iValues);

    //Unbounded knapsack (any number of items may be present and hence can be choosen)
 /*   nItems = 9; //28;
    iWeights = new int[nItems];
    iValues = new int[nItems];
    //Weights and Values
    iWeights[0] = 1;
    iValues[0]  = 2;
    iWeights[1] = 1;
    iValues[1]  = 2;
    iWeights[2] = 1;
    iValues[2]  = 2;
    iWeights[3] = 12;
    iValues[3]  = 4;
    iWeights[4] = 2;
    iValues[4]  = 2;
    iWeights[5] = 1;
    iValues[5]  = 1;
    iWeights[6] = 4;
    iValues[6]  = 10;
    iWeights[7] = 4;
    iValues[7]  = 10;
    iWeights[4] = 4;
    iValues[4]  = 10;
  * 
  */
    iWeights = new int[nItems+1];
    iValues = new int[nItems+1];
    iWeights[0] = 0;
    iValues[0]  = 0;
    iWeights[1] = 1;
    iValues[1]  = 2;
    iWeights[2] = 12;
    iValues[2]  = 4;
    iWeights[3] = 2;
    iValues[3]  = 2;
    iWeights[4] = 1;
    iValues[4]  = 1;
    iWeights[5] = 4;
    iValues[5]  = 10;
    ks.ksUnbounded(targetWeight, nItems+1, iWeights, iValues);


  }

}
