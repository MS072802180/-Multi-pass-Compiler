package assign7.ast ;

import assign7.inter.*;
import assign7.visitor.* ;

public class AssignmentNode extends StatementNode 
{
                    
    public ExprNode   left ;  
    public Node    right ;  
    public TempNode temp;
   
    
    public AssignmentNode () {

    }

    // public AssignmentNode (IdentifierNode id, BinExprNode right) {
    public AssignmentNode (IdentifierNode id, Node right) 
    {

        this.left   = id ; 
        this.right  = right ; 
    }

    
    public void accept(ASTVisitor v) {

        v.visit(this) ;
    }
}
