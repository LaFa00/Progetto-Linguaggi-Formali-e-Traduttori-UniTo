import java.util.Scanner;
public class Es1_6 {
public static boolean scan(String s) {
	int i = 0;
	int state = 0;
	while(state >= 0 && i < s.length()) {
		final char ch = s.charAt(i++);
		switch(state) {
			case 0:
			if(ch == 'b')
				state = 0;
			else if(ch == 'a' && i <= 3)
				state = 1;
			else
				state = -1;
			break;
			case 1:
			if(ch == 'a')
				state = 1;
			else if(ch == 'b')
				state = 1;
			else
				state = -1;
			break;

		}	
	}
	return state == 1;
}

public static void main(String[] args){
	System.out.println(scan(args[0]) ? "OK" : "NOPE");
}

}