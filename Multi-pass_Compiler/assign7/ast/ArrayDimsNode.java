package assign7.ast;

import assign7.visitor.*;

public class ArrayDimsNode extends ExprNode 
{
    //a[i][j][k] 
    public ExprNode         size; 
    public ArrayDimsNode    dim;  
    public IdentifierNode temp;
 
   
    public ArrayDimsNode() {

    }

    public ArrayDimsNode (ExprNode size, ArrayDimsNode dim) {

        this.size   =  size; 
        this.dim    =  dim; 
    }

    public void accept(ASTVisitor v) {

        v.visit(this);
    }
}
