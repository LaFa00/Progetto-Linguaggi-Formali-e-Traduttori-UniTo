import java.io.*;

public class Parser {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser(Lexer l, BufferedReader br) {
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

    public void start() {
	   switch(look.tag) {
         case '(':
         case Tag.NUM:
            expr();
            match(Tag.EOF);
            break;
        case Tag.EOF:
        default:
            error("Errore in start");
        }       
    }

    private void expr() {
	   switch(look.tag) {
        case '(':
        case Tag.NUM:
            term();
            exprp();
            break;
        case ')':
        case Tag.EOF:
        default:
            error("Errore trovato nel metodo expr");
       }
    }

    private void exprp() {
	switch (look.tag) {
	case '+':
        match(Token.plus.tag);
        term();
        exprp();
        break;
    case '-':
        match(Token.minus.tag);
        term();
        exprp();
        break;
    case ')':
    case Tag.EOF:
        break;
    default:
        error("Errore trovato nel metodo exprp");
	
	}
    }

    private void term() {
        switch(look.tag) {
            case '(':
            case Tag.NUM:
                fact();
                termp();
                break;
            case '+':
            case '-':
            case ')':
            default:
            error("Errore trovato nel metodo term");

        }
    }

    private void termp() {
        switch(look.tag){
            case '*':
                match(Token.mult.tag);
                fact();
                termp();
                break;
            case '/':
                match(Token.div.tag);
                fact();
                termp();
                break;
            case '+':
            case '-':
            case ')':
            case Tag.EOF:
                break;
            default:
            error("Errore trovato nel metodo termp");
        }
    }

    private void fact() {
        switch(look.tag) {
            case '(':
                match(Token.lpt.tag);
                expr();
                match(Token.rpt.tag);
                break;
            case Tag.NUM:
                match(Tag.NUM);
                break;
            case '*':
            case '/':
            case ')':
            default:
                error("Errore trovato nel metodo fact");

        }
    }
		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "testparser.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}