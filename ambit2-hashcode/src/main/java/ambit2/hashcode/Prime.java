package ambit2.hashcode ;

import java.util.Arrays;
import java.util.Hashtable;
public class Prime {	
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

public static Hashtable<String,Integer> createPrimeNumberHashtable()  {
    
	Hashtable<String,Integer> PrimeNumberHashtable = new Hashtable<String,Integer>();
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
	//System number of element
	number = 113;	
	sht=0;
	for(int k=N;k<N+number;k++)
	{
		     
	        if(numColl[k]==0)
	            continue;
	        else
	        {
	        	//System number of element
	            PrimeNumberHashtable.put("SysNum"+sht, new Integer(numColl[k]));
	            numColl[k] = 0;	          
	            sht++;
	        }

	}
	N = N + number;
	//Molecule size
	number = 257;	
	sht=1;
	for(int k=N;k<N+number;k++)
	{
		     
	        if(numColl[k]==0)
	            continue;
	        else
	        {
	        	//Molecule size
	            PrimeNumberHashtable.put("MolSize"+sht, new Integer(numColl[k]));
	            numColl[k] = 0;	          
	            sht++;
	        }

	}
	
	N = N + number;
	//stereo center
	number = 3;	
	sht=0;
	int[] stereo_bonds={-1,0,1};
	for(int k=N;k<N+number;k++)
	{
		     
	        if(numColl[k]==0)
	            continue;
	        else
	        {
	        	///stereo center
	            PrimeNumberHashtable.put("StereoCenter"+stereo_bonds[sht], new Integer(numColl[k]));
	            numColl[k] = 0;	          
	            sht++;
	        }

	}
	N = N + number;
	//type of bond
	number = 4;	
	sht=1;
	
	for(int k=N;k<N+number;k++)
	{
		     
	        if(numColl[k]==0)
	            continue;
	        else
	        {
	        	//type of bond
	            PrimeNumberHashtable.put("TypeOfBond"+sht, new Integer(numColl[k]));
	            numColl[k] = 0;	          
	            sht++;
	        }

	}
	N = N + number;
	//formal charge
	number = 7;	
	sht=1;
	
	for(int k=N;k<N+number;k++)
	{
		     
	        if(numColl[k]==0)
	            continue;
	        else
	        {
	        	//formal charge
	            PrimeNumberHashtable.put("FormalCharge"+sht, new Integer(numColl[k]));
	            numColl[k] = 0;	          
	            sht++;
	        }

	}
	
	N = N + number;
	//stereo parity
	number = 3;	
	sht=0;
	int[] stereo_parity={-1,0,1};
	for(int k=N;k<N+number;k++)
	{
		     
	        if(numColl[k]==0)
	            continue;
	        else
	        {
	        	///stereo parity
	            PrimeNumberHashtable.put("StereoParity"+stereo_parity[sht], new Integer(numColl[k]));	            
	            numColl[k] = 0;	          
	            sht++;
	        }

	}
	
	N = N + number;
	//cis/trans isomers
	number = 2;	
	sht=0;
	
	for(int k=N;k<N+number;k++)
	{
		     
	        if(numColl[k]==0)
	            continue;
	        else
	        {
	        	//cis/trans isomers
	            PrimeNumberHashtable.put("CisTrans"+sht, new Integer(numColl[k]));
	            numColl[k] = 0;	          
	            sht++;
	        }

	}
	
	
	/*Enumeration<String> e = PrimeNumberHashtable.keys();
    while (e.hasMoreElements()) {
    	String obj = e.nextElement();
        System.out.println("Key "+ obj +" : "+PrimeNumberHashtable.get(obj));
    }*/
    
	
    return PrimeNumberHashtable;

}

}
