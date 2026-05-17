package assign7.ast ;

import assign7.visitor.* ;
import assign7.ast.*;

public class ParenthesesNode extends ExprNode
{

    public ExprNode expr; 
    public IdentifierNode temp;
    
    public ParenthesesNode () {

    }
    
    public void accept (ASTVisitor v) {

        v.visit(this);
    }
}