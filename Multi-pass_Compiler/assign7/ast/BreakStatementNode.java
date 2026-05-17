package assign7.ast ;

import assign7.inter.*;
import assign7.visitor.* ;


public class BreakStatementNode extends StatementNode{

    public LabelNode label;
    public BreakStatementNode( ){

    }
    
    public void accept(ASTVisitor v ){

        v.visit(this);
    }

}
