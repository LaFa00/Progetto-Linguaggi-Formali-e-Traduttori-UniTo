import java.io.*;

public class Translator {
    private Lexer3 lex;
    private BufferedReader pbr;
    private Token look;
    
    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count=0;

    public Translator(Lexer3 l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

     void move() {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) {
    throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
    if (look.tag == t) {
        if (look.tag != Tag.EOF) move();
    } else error("syntax error");
    }

    public void prog() {        
     switch(look.tag) {
        case '=':
        case Tag.PRINT:
        case Tag.READ:
        case Tag.COND:
        case Tag.WHILE:
        case '{':
            int lnext_prog = code.newLabel();
            statlist(lnext_prog);
            code.emitLabel(lnext_prog);
            match(Tag.EOF);
            break;
        default:
            error("Errore in prog");
    }
        try {
          code.toJasmin();
        }
        catch(java.io.IOException e) {
          System.out.println("IO error\n");
        };
    }

    private void statlist(int lnext) {
        switch(look.tag) {
            case '=':
            case Tag.PRINT:
            case Tag.READ:
            case Tag.COND:
            case Tag.WHILE:
            case '{':
                int lnext_stat = code.newLabel();
                stat(lnext_stat);
                code.emitLabel(lnext_stat);
                statlistp(lnext);
                break;
            default:
                error("Errore in statlist");
        }
    }

    private void statlistp(int lnext) {
        switch(look.tag) {
            case ';':
                match(';');
                int lnext_stat = code.newLabel();
                stat(lnext_stat);
                code.emitLabel(lnext_stat);
                statlistp(lnext);
                break;
            case '}':
            case Tag.EOF:
                break;
            default:
                error("Errore in statlistp");
                break;
        }
    }

    public void stat(int lnext) {
        int id_addr = 0;
        switch(look.tag) {
          case '=':
                match('=');
                id_addr = st.lookupAddress(((Word)look).lexeme);
                if(id_addr == -1) { //Controlla se l'ID è già assegnato a un indirizzo
                    id_addr = count; // asseggna all'indirizzo puntato count
                    st.insert(((Word)look).lexeme,count++); // Inserisce nella Symbol Tab
                }
                match(Tag.ID);
                expr();
                code.emit(OpCode.istore,id_addr);
                break;

            case Tag.PRINT:
                match(Tag.PRINT);
                match('(');
                exprlist("print");
                match(')');
                break;
            case Tag.READ:
                match(Tag.READ);
                match('(');
                if (look.tag==Tag.ID) {
                    id_addr = st.lookupAddress(((Word)look).lexeme);
                    if (id_addr==-1) {
                        id_addr = count;
                        st.insert(((Word)look).lexeme,count++);
                    }                    
                    match(Tag.ID);
                    match(')');
                    code.emit(OpCode.invokestatic,0);
                    code.emit(OpCode.istore,id_addr);  
                } else {
                    error("Error in grammar (stat) after read( with " + look);
                }
                break;
            case Tag.COND:
                match(Tag.COND);
                whenlist(lnext);
                match(Tag.ELSE);
                stat(lnext);
                break;
            case Tag.WHILE:
                match(Tag.WHILE);
                int ltrue = code.newLabel();
                int while_start = code.newLabel();
                code.emitLabel(while_start);
                match('(');
                bexpr(ltrue, lnext);
                match(')');
                code.emitLabel(ltrue);
                stat(lnext);
                code.emit(OpCode.GOto, while_start);
                break;
            case '{':
                match('{');
                statlist(lnext);
                match('}');
                break;
            default:
                error("Errore in stat");
            }
     }

     private void whenlist(int lnext) {
        switch(look.tag) {
            case Tag.WHEN:
                int when_next = code.newLabel();
                whenitem(lnext,when_next);
                code.emitLabel(when_next);
                whenlistp(lnext);
                break;
            default:
                error("Errore in whenlist");
                break;
        }
        
     }

     private void whenlistp(int lnext) {
        switch(look.tag) {
            case Tag.WHEN:
                int when_next = code.newLabel();
                whenitem(lnext,when_next);
                code.emitLabel(when_next);
                whenlistp(lnext);
                break;
            case Tag.ELSE:
                break;
            default:
                error("Errore in whenlistp");
        }
     }

     private void whenitem(int lnext, int when_next){
        switch(look.tag) {
            case Tag.WHEN:
                int ltrue = code.newLabel();
                match(Tag.WHEN);
                match('(');
                bexpr(ltrue,when_next);
                match(')');
                match(Tag.DO);
                code.emitLabel(ltrue);
                stat(lnext);
                code.emit(OpCode.GOto,lnext);
                break;
            default:
                error("Errore in whenitem");
                break;
        }
     }

     private void bexpr(int ltrue, int lfalse) {
         if (look.tag == Tag.RELOP) {
            String bexpr = (((Word) look).lexeme);
            match(Tag.RELOP);
            switch (bexpr) {
            case ">":
                expr();
                expr();
                code.emit(OpCode.if_icmpgt, ltrue);
                code.emit(OpCode.GOto,lfalse);
                break;
            case ">=":
                expr();
                expr();
                code.emit(OpCode.if_icmpge, ltrue);
                code.emit(OpCode.GOto,lfalse);
                break;
            case "<":
                expr();
                expr();
                code.emit(OpCode.if_icmplt, ltrue);
                code.emit(OpCode.GOto,lfalse);
                break;
            case "<=":
                expr();
                expr();
                code.emit(OpCode.if_icmple, ltrue);
                code.emit(OpCode.GOto,lfalse);
                break;
            case "==":
                expr();
                expr();
                code.emit(OpCode.if_icmpeq, ltrue);
                code.emit(OpCode.GOto,lfalse);
                break;
            case "<>":
                expr();
                expr();
                code.emit(OpCode.if_icmpne, ltrue);
                code.emit(OpCode.GOto,lfalse);
                break;
            default:
                error("Invalid relational operator");
                break;
            }
        } else {
            error("Errore in bexpr");
        }
     }

    private void expr() {
        String optype = "";
        switch(look.tag) {
          case '+':
                match('+');
                match('(');
                optype = "iadd";
                exprlist(optype);
                match(')');
                break;
            case '-':
                match('-');
                expr();
                expr();
                code.emit(OpCode.isub);
                break;
          case '*':
                match('*');
                match('(');
                optype = "imul";
                exprlist(optype);
                match(')');
                break;
            case '/':
                match('/');
                expr();
                expr();
                code.emit(OpCode.idiv);
                break;
            case Tag.NUM:
                code.emit(OpCode.ldc,((NumberTok)look).value);
                match(Tag.NUM);
                break;
            case Tag.ID:
                int id_addr = st.lookupAddress(((Word)look).lexeme);
                if(id_addr == -1) {
                    error("Variable" + Tag.ID + "not defined");
                }
                code.emit(OpCode.iload,id_addr);
                match(Tag.ID);
                break;
            default:
                error("Errore in expr");

        }
    }

    private void exprlist(String optype) {
        switch(look.tag) {
            case '+':
            case '-':
            case '*':
            case '/':
            case Tag.NUM:
            case Tag.ID:
                expr();
                if(optype == "print")
                    code.emit(OpCode.invokestatic,1); //Stampa il primo elemento, non ci fosse non verrebbe stampato lasciandolo sullo stack
                exprlistp(optype);
                break;
            default:
                error("Errore in exprlist");
                break;

        }
    }

    private void exprlistp(String optype) {
        switch(look.tag) {
            case '+':
            case '-':
            case '*':
            case '/':
            case Tag.NUM:
            case Tag.ID:
                expr();
                if(optype == "print")
                    code.emit(OpCode.invokestatic,1);
                if(optype == "iadd")
                    code.emit(OpCode.iadd);
                if(optype == "imul")
                    code.emit(OpCode.imul);
                exprlistp(optype);
                break;
            case ')':
                break;
            default:
                error("Errore in exprlistp");
        }
    }

     public static void main(String[] args) {
        Lexer3 lex = new Lexer3();
        String path = "Traduttore.lft"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Translator translator = new Translator(lex, br);
            translator.prog();
            System.out.println("Input OK, Generation of code succeded!");
            br.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
}



