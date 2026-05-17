package assign7.inter ;

import assign7.visitor.* ;
import assign7.lexer.* ;
import assign7.ast.*;


public class LabelNode extends IdentifierNode 
{
    
    static int label=0;

    public LabelNode (Word w,Type type) 
    {
        super(w,type);
    }
    public static LabelNode newLabel()
    {
        label++;
        return new LabelNode(new Word("L"+label,Tag.ID),null);
    }
    public void accept(ASTVisitor v) {

        v.visit(this);
    }
}

