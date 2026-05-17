package assign7.ast ;

import assign7.visitor.* ;
import assign7.inter.*;
import java.util.*;
public class WhileStatementNode extends StatementNode 
{

    public ParenthesesNode  cond; 
    public StatementNode    stmt; 
    public LabelNode trueLabel;
    public LabelNode falseLabel;
    
    public List<AssignmentNode>assigns=new ArrayList<AssignmentNode>();

    public WhileStatementNode() 
    {

    }

    public WhileStatementNode (ParenthesesNode cond, StatementNode stmt){

        this.cond = cond;
        this.stmt = stmt;
    }

    public void accept (ASTVisitor v) {

        v.visit(this);
    }

}