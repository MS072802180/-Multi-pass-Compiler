package assign7.ast ;

import assign7.lexer.* ;
import assign7.visitor.* ;

public class IdentifierNode extends ExprNode {

    public String id ; 
    public Word w ; 
    public Type n ; 
    public IdentifierNode temp;

    public IdentifierNode () 
    {

    }

    public IdentifierNode (Word w, Type n) 
    {    

        this.id = w.lexeme ;
        this.w = w ;
        this.n = n ;
    }

    public void accept (ASTVisitor v) {

        v.visit(this) ;
    }
   
    public void printNode () {    

        System.out.println("IdentifierNode: " + id) ;
    }
}

