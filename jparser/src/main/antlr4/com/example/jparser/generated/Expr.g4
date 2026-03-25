grammar Expr;



query : expr EOF #RootQuery;

expr : '(' expr ')' #ParenExpr
     | left=ID op=OP right=BOOL #BoolComparison
     | left=ID op=OP right=SEPNUM #SepNumComparison
     | left=ID op=OP right=NUM #NumComparison
     | left=ID op=OP right=DEC #DecComparison
     | left=ID op=OP right=(STR|ID) #StrComparison
     | left=ID op=OP right=QSTR #QStrComparison
     | left=expr op=OR right=expr #LogicalExpr
     | left=expr op=AND right=expr #LogicalExpr
     | left=ID op=INCL right=anyString #InclComparison
     ;


anyString : STR | QSTR | ID;
AND     : 'AND' | 'and' ;
OR      : 'OR' | 'or' ;
INCL    : 'Include' | 'include' 
        | 'INCLUDE' ;
OP      : '>' | '<' | '=' | '!=' ;
BOOL    : 'true'|'false'|'TRUE'|'FALSE'|'True'|'False';
ID      : [a-zA-Z_][a-zA-Z0-9_]* ;
SEPNUM : [0-9] ([0-9] [0-9]?)? ('.' [0-9] [0-9] [0-9])+ ;
NUM     : [0-9]+; 
DEC     : [0-9]* ('.' | ',') [0-9]+;
QSTR    : '\'' (~'\'')* '\'';
STR     : [a-zA-Z0-9'/:\-]+;
WS      : [ \t\r\n]+ -> skip ;