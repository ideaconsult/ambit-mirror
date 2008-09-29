package ambit2.hashcode ;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.*;
public class Prime {
	public static void main(String[] args) {
	    if(args.length==0)
	    {
	        System.out.print("Please ennter a Number.");
	        System.exit(0);
	    }	    
	    int number= Integer.parseInt(args[0]);
	    //createArrayWithPrimes(10);
	    //createArrayWithSpecificNumberOfPrimes(17);
	    createPrimeNumberHashtable();
	    

	}
	
	/**
	 * generate all primes (0 - number)
	 * @param number
	 * @return
	 */
public static int [] createArrayWithPrimes (int number) {
	    
	    int count=0;
	    
	    int[] numColl= new int[number];
	    for (int i=1;i<number;i++)
	    {
	        if(i==1)
	        {
	            continue;
	        }
	        else if(i==2||i==3||i==5||i==7)
	        {
	            count++;
	            numColl[i]= i;
	            continue;
	        }
	        else
	        {
	            if(i%2==0||i%3==0||i%5==0||i%7==0)
	                continue;
	            else 
	            {
	                numColl[i]= i;
	                count++;
	                continue;
	            }
	        }
	    }
	    Arrays.sort(numColl);

	    System.out.println("Prime numbers between 1 and "+number+" are "+ count);
	    int N=1;
	    for(int i=0;i<numColl.length;i++)
	    {
	        if(numColl[i]==0)
	            continue;
	        else
	        {
	            //System.out.println("Prime number " +N+ " is: "+numColl[i]); 
	            N++;
	        }

	    }
	    return numColl;

	}
/**
 * generates given number of primes
 * @param number
 * @return
 */
public static int [] createArrayWithSpecificNumberOfPrimes (int number) {

 	int count=0;
    
    int[] numColl= new int[100000];
    int[] numColl_primes = new int[number];
    int i=1;
    while (count < number)
    {
    	
        if(i==1)
        {
        	i++;
            continue;
        }
        else if(i==2||i==3||i==5||i==7)
        {
            count++;
            numColl[i]= i;
            i++;
            continue;
        }
        else
        {
            if(i%2==0||i%3==0||i%5==0||i%7==0){
            	i++;
                continue;
            }
            else 
            {
                numColl[i]= i;
                count++;
                i++;
                continue;
            }
        }
       
    }
    Arrays.sort(numColl);

    //System.out.println("Prime numbers between 1 and "+number+" are "+ count);
    int N=0;
    for(int j=0;j<numColl.length;j++)
    {
        if(numColl[j]==0)
            continue;
        else
        {
            //System.out.println("Prime number " +N+ " is: "+numColl[j]); 
            
            numColl_primes[N] = numColl[j];
            N++;
        }

    }
    
    return numColl_primes;
	}

/**
 * 1 step of the algorithm
 * @return
 */
public static Hashtable createPrimeNumberHashtable()  {
    
	Hashtable PrimeNumberHashtable = new Hashtable();
	//number of bonded neighbor atoms
	int number = 17;
	int[] numColl= new int[1000];
	numColl = createArrayWithSpecificNumberOfPrimes (1000);
	int N=0;
	int r=0;
	for(int j=0;j<number;j++)
	{
	        if(numColl[j]==0)
	            continue;
	        else
	        {
	            //System.out.println("Prime number " +N+ " is: "+numColl[j]);
	            //number of bonded neighbor atoms
	            PrimeNumberHashtable.put("NAtoms"+r, new Integer(numColl[j]));
	            numColl[j] = 0;	
	            r++;
	        }

	}
	N = N + number;
	number = 11;
	//Number of bonded hydrogen neighbor atoms	
	int sht=0;
	
	for(int k=N;k<N+number;k++)
	{
		  
	        if(numColl[k]==0)
	            continue;
	        else
	        {
	        	//Number of bonded hydrogen neighbor atoms
	            PrimeNumberHashtable.put("NHAtoms"+sht, new Integer(numColl[k]));
	            numColl[k] = 0;	            
	            sht++;
	        }

	}
	N = N + number;
	number = 3;
	//Acyclic,Monocyclic,More than one ring
	
	int index_cycle=0;
	int[] indexes_cycle= {-1,0,1};
	for(int k=N;k<N+number;k++)
	{
		     
	        if(numColl[k]==0)
	            continue;
	        else
	        {
	        	//Number of bonded hydrogen neighbor atoms
	            PrimeNumberHashtable.put("RingIndex"+indexes_cycle[index_cycle], new Integer(numColl[k]));
	            numColl[k] = 0;	          
	            index_cycle++;
	        }

	}
	Enumeration e = PrimeNumberHashtable.keys();
    while (e.hasMoreElements()) {
    	Object obj = e.nextElement();
        //System.out.println("Key "+ obj +" : "+PrimeNumberHashtable.get(obj));
    }
	
    return PrimeNumberHashtable;

}

}
