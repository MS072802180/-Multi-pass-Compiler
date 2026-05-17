package assign7.parser ;

import assign7.lexer.* ;
import assign7.ast.*;
import java.util.* ;

public class Env  {

    public Hashtable table; 
    public Env prev; 

    public Env (Env n) 
    {
        
        table = new Hashtable();
        prev = n;
    }

    public void put (Token w, IdentifierNode i) 
    {
       
        table.put(w,i);
    }

    public IdentifierNode get (Token w) 
    {
        
        for (Env e = this ; e != null; e = e.prev){  
            
            IdentifierNode found = (IdentifierNode)(e.table.get(w));   
                        
            if (found != null)  
                return found; 
        }
       
        return null; 
    }
}