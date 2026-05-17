# Multi-pass Compiler

A five-stage compiler front-end for a simple imperative language. Reads source code from `input.txt` and emits **three-address intermediate code** to standard output.

---

## Pipeline

```
input.txt  ──►  Lexer  ──►  Parser  ──►  TypeChecker  ──►  InterCode  ──►  Unparser  ──►  stdout
```

| Stage | Class | Responsibility |
|---|---|---|
| 1 | `Lexer` | Scans raw characters and produces a token stream |
| 2 | `Parser` | Consumes tokens, builds an AST, and decomposes compound expressions into temporary variables |
| 3 | `TypeChecker` | Walks the AST and verifies that operand types are consistent throughout |
| 4 | `InterCode` | Labels every loop and branch node with jump targets (`L1`, `L2` …) and wraps conditions in temporaries (`t1`, `t2` …) |
| 5 | `Unparser` | Traverses the decorated AST and prints the final three-address intermediate code |

---

## Requirements

- Java 8 or later — no external libraries required

---

## Compile & Run

```bash
# 1. Unzip so the folder structure is <root>/assign7/...
# 2. From <root>, compile
find assign7 -name "*.java" | xargs javac -d out

# 3. Run (input.txt must be in <root>)
java -cp out assign7.Main
```

---

## How Each Line of `input.txt` Is Processed

Below is every logical construct from the source file with a pipeline trace showing exactly what each stage does with it.

---

### `{` — outer block open

```
Lexer reads '{' as a block-open token
  → Parser creates a BlockStatementNode and pushes a new symbol-table scope
  → TypeChecker enters that scope
  → InterCode enters the block
  → Unparser begins iterating the block's statement list
```

---

### `int i ;` &nbsp;·&nbsp; `int j ;` &nbsp;·&nbsp; `float v ;` &nbsp;·&nbsp; `float x ;`

```
Lexer produces type-keyword + identifier + semicolon
  → Parser builds a DeclarationNode and stores the name with its type in the symbol table
  → TypeChecker confirms the type is legal
  → InterCode records the declaration
  → Unparser emits nothing — declarations are not part of three-address output
```

---

### `float[100] a ;`

```
Lexer reads  float  [  100  ]  a  ;
  → Parser builds a DeclarationNode with an ArrayTypeNode (element: float, size: 100)
    and registers 'a' in the symbol table
  → TypeChecker validates the array type
  → InterCode records it
  → Unparser skips it — no output for declarations
```

---

### `while(false)`

```
Lexer emits  WHILE  (  FALSE  )
  → Parser creates a WhileStatementNode wrapping a FalseNode inside a ParenthesesNode
  → TypeChecker verifies the condition is boolean
  → InterCode assigns the loop entry label L1, wraps 'false' in temp t4, assigns exit label L7
  → Unparser emits:
```
```
L1:
  t4 = false ;
  ifFalse t4 goto L7
  ...body...
  goto L1
L7:
```

---

### `do i = i+1 ; while( v < a[i] ) ;` — first do-while

```
Lexer emits  DO ... WHILE  sequence
  → Parser builds a DoWhileStatementNode; body is AssignmentNode (i = i+1);
    condition 'v < a[i]' causes parseBinExprNode to extract a[i] into
    a condition-temp (t1 = a[i]) stored in the node's condStmts list
  → TypeChecker checks that i is int and v is float-compatible with a[i]
  → InterCode assigns loop label L2, wraps 'v < t1' in temp t5, assigns exit label L3
  → Unparser emits:
```
```
L2:
    i = i + 1 ;
  t1 = a[i] ;
  t5 = v < t1 ;
  ifTrue t5 goto L2
L3:
```

---

### `do i = i+2 ; while( a[i] > v ) ;` — second do-while

```
Same pipeline as the first do-while
  → Parser builds a second DoWhileStatementNode; condition 'a[i] > v' extracts
    a[i] into condStmts as t2
  → TypeChecker checks types
  → InterCode assigns labels L4 / L5 and wraps 't2 > v' in temp t6
  → Unparser emits:
```
```
L4:
    i = i + 2 ;
  t2 = a[i] ;
  t6 = t2 > v ;
  ifTrue t6 goto L4
L5:
```

---

### `if( i >= j ) break ;`

```
Lexer emits  IF  (  ID >= ID  )  BREAK
  → Parser builds an IfStatementNode containing BinExprNode 'i >= j'
    and a BreakStatementNode as its body
  → TypeChecker confirms both operands are int
  → InterCode wraps 'i >= j' in temp t7 and assigns false-exit label L6
  → Unparser resolves 'break' to the enclosing while's exit label L7 and emits:
```
```
  t7 = i >= j ;
  ifFalse t7 goto L6
    goto L7
L6:
```

---

### `x = a[i] ;`

```
Lexer produces  ID  =  ID [ ID ]  ;
  → Parser builds AssignmentNode: left = IdentifierNode 'x',
    right = ArrayAccessNode 'a[i]' — no temporaries needed
  → TypeChecker confirms x is float and a[i] yields float
  → InterCode traverses the node
  → Unparser emits:
```
```
  x = a[i] ;
```

---

### `a[i] = a[j] + 4 ;`

```
Lexer reads an array-access assignment with a binary RHS
  → Parser keeps ArrayAccessNode 'a[i]' as the LHS;
    parseBinExprNode detects 'a[j]' is an array access and extracts it
    into temp t3, leaving the RHS as BinExprNode (t3 + 4)
  → TypeChecker checks float compatibility
  → InterCode traverses
  → Unparser emits:
```
```
  t3 = a[j] ;
  a[i] = t3 + 4 ;
```

---

### `a[j] = x ;`

```
Lexer reads array-access assignment
  → Parser keeps ArrayAccessNode 'a[j]' as the LHS and IdentifierNode 'x'
    as the RHS — no temporaries needed
  → TypeChecker confirms types match
  → InterCode traverses
  → Unparser emits:
```
```
  a[j] = x ;
```

---

### `}` · `}` — close while body and outer block

```
Lexer reads '}'
  → Parser closes the BlockStatementNode and pops the symbol-table scope
  → TypeChecker exits the scope
  → InterCode exits the block
  → Unparser finishes iterating the statement list;
    the while loop's 'goto L1' and final exit label 'L7:' are emitted
    as part of the WhileStatementNode visit
```

---

## Full Output for `input.txt`

```
L1:
  t4 = false ;
  ifFalse t4 goto L7
L2:
    i = i + 1 ;
  t1 = a[i] ;
  t5 = v < t1 ;
  ifTrue t5 goto L2
L3:
L4:
    i = i + 2 ;
  t2 = a[i] ;
  t6 = t2 > v ;
  ifTrue t6 goto L4
L5:
  t7 = i >= j ;
  ifFalse t7 goto L6
    goto L7
L6:
  x = a[i] ;
  t3 = a[j] ;
  a[i] = t3 + 4 ;
  a[j] = x ;
  goto L1
L7:
```
