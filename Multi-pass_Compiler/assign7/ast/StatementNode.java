package assign7.ast ;

import assign7.visitor.* ;

public class StatementNode 
{

    public StatementNode () 
    {
        
    }
    
    public void accept(ASTVisitor v){
        v.visit(this);
    }
}