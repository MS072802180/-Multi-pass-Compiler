package assign7.ast ;

import assign7.inter.*;
import assign7.visitor.* ;
import assign7.lexer.*;
import java.util.*;
public class IfStatementNode extends StatementNode 
{

    public ParenthesesNode cond;    
    public StatementNode stmt;  
    public StatementNode else_stmt;  
    public List<AssignmentNode>assigns=new ArrayList<AssignmentNode>();
    public LabelNode falseLabel;
    public LabelNode trueLabel;
    public LabelNode label;
    

    public IfStatementNode() {

    }

    public IfStatementNode (ParenthesesNode cond, StatementNode stmt, StatementNode else_stmt) {

        this.cond = cond;
        this.stmt = stmt;
        this.else_stmt = else_stmt;
    }

    public void accept(ASTVisitor v) {
        v.visit(this);
    }
}