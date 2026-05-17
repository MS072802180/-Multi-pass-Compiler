package assign7.ast;

import assign7.visitor.*;
// a[i] = k + j ;

public class ArrayAccessNode extends ExprNode {

    public IdentifierNode id; 
    public ExprNode index;    
    public IdentifierNode temp;

    public ArrayAccessNode() {

    }

    public ArrayAccessNode (IdentifierNode id, ExprNode index) {

        this.id = id; 
        this.index = index;
    }

    public void accept (ASTVisitor v) {

        v.visit(this);
    }
}