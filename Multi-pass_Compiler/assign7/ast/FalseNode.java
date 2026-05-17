package assign7.ast ;

import assign7.visitor.* ;
import assign7.lexer.*;

public class FalseNode extends ExprNode
{

    public FalseNode( ) 
    {
        this.type=Type.Bool;
    }

    public void accept(ASTVisitor v ) 
    {

        v.visit(this) ;
    }

}
