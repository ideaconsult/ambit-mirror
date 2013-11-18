package ambit2.sln;

import org.openscience.cdk.interfaces.IAtom;

/**
 * This matcher any Atom.
*
*/
public class AnyAtom extends SLNAtom {
   
   private static final long serialVersionUID = -7654321755106011847L;

   /**
    * Creates a new instance
    */
   public AnyAtom() 
   {
   }

   public boolean matches(IAtom atom)
   {
       if (atom.getSymbol().equals("H"))
       {
           Integer massNumber = atom.getMassNumber();
           return massNumber != null;
       }
       return true;
   }

   public String toString()
   {
		return "AnyAtom()";
   }
}

