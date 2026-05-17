package assign7.ast ;

import assign7.visitor.* ;
import assign7.lexer.* ;


public class ExprNode extends Node 
{
    
    public Type type = null ; 
    IdentifierNode temp=null;

    public ExprNode () 
    {

    }
    
    public void accept(ASTVisitor v) {

        v.visit(this);
    }
}

