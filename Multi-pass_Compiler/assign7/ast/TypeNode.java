package assign7.ast ;

import assign7.visitor.* ;
import assign7.lexer.* ;


public class TypeNode extends Node
{
    
    public Type basic ;                  
    public ArrayTypeNode array = null;  

    public TypeNode() 
    {

    }

    public TypeNode(Type basic, ArrayTypeNode array) 
    {

        this.basic = basic ;
        this.array = array ; 
    }

    public void accept(ASTVisitor v) 
    {

        v.visit(this) ;
    }

}
