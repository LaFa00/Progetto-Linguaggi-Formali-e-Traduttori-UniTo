import java.io.*;

public class Parser2 {
    private Lexer3 lex;
    private BufferedReader pbr;
    private Token look;

    public Parser2(Lexer3 l, BufferedReader br) {
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
                statlist();
                match(Tag.EOF);
                break;
            default:
                error("Errore in prog");


        }
    }

    private void statlist() {
        switch(look.tag) {
            case '=':
            case Tag.PRINT:
            case Tag.READ:
            case Tag.COND:
            case Tag.WHILE:
            case '{':
                stat();
                statlistp();
                break; 
            default:
                error("Errore in statlist");

        }
    }

    private void statlistp() {
        switch(look.tag) {
            case ';':
                match(';');
                stat();
                statlistp();
                break;
            case '}':
            case Tag.EOF:
                break;
            
            default:
                error("errore in statlistp");
        }
    }

    private void stat() {
        switch(look.tag) {
            case '=':
                match('=');
                match(Tag.ID);
                expr();
                break;
            case Tag.PRINT:
                match(Tag.PRINT);
                match('(');
                exprlist();
                match(')');
                break;
            case Tag.READ:
                match(Tag.READ);
                match('(');
                match(Tag.ID);
                match(')');
                break;
            case Tag.COND:
                match(Tag.COND);
                whenlist();
                match(Tag.ELSE);
                stat();
                break;
            case Tag.WHILE:
                match(Tag.WHILE);
                match('(');
                bexpr();
                match(')');
                stat();
                break;
            case '{':
                match('{');
                statlist();
                match('}');
                break;
            default:
                error("Errore in stat");
                break;
        }
    }


    private void whenlist() {
        switch(look.tag) {
        case Tag.WHEN:
            whenitem();
            whenlistp();
            break;
        default:
            error("Errore in whenlist");
            break;
        }
    }

    private void whenlistp() {
        switch(look.tag) {
            case Tag.WHEN:
                whenitem();
                whenlistp();
                break;
            case Tag.ELSE:
                break;
            default:
                error("Errore in whenlistp");
                break;
        }
    }

    private void whenitem() {
        switch(look.tag) {
            case Tag.WHEN:
                match(Tag.WHEN);
                match('(');
                bexpr();
                match(')');
                match(Tag.DO);
                stat();
                break;
            default:
                error("Errore in whenitem");
        }
    }

    private void bexpr() {
        switch(look.tag) {
            case Tag.RELOP:
                match(Tag.RELOP);
                expr();
                expr();
                break;
            default:
                error("Errore in bexpr");
        }
    }

    private void expr() {
        switch(look.tag) {
            case '+':
                match('+');
                match('(');
                exprlist();
                match(')');
                break;
            case '-':
                match('-');
                expr();
                expr();
                break;
            case '*':
                match('*');
                match('(');
                exprlist();
                match(')');
                break;
            case '/':
                match('/');
                expr();
                expr();
                break;
            case Tag.NUM:
                match(Tag.NUM);
                break;
            case Tag.ID:
                match(Tag.ID);
                break;
            default:
                error("Errore in expr");
                break;
        }
    }

    private void exprlist() {
        switch(look.tag) {
            case '+':
            case '-':
            case '*':
            case '/':
            case Tag.NUM:
            case Tag.ID:
                expr();
                exprlistp();
                break;
            
            default:
                error("Errore in exprlist");
                break;
        }
    }

    private void exprlistp() {
        switch(look.tag) {
            case '+':
            case '-':
            case '*':
            case '/':
            case Tag.NUM:
            case Tag.ID:
                expr();
                exprlistp();
                break;
            case ')':
                break;
            default:
                error("Errore in exprlistp");

        }
    }
    
        
    public static void main(String[] args) {
        Lexer3 lex = new Lexer3 ();
        String path = "testparser2_2.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser2 parser = new Parser2(lex, br);
            parser.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}