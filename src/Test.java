import java.util.Scanner;

public class Test {

    public static void main(String[] args) {
        System.out.println(sq.search(" cat \\  dog  & you "));
        SearchQuery sq = new SearchQuery("docs\\");
        System.out.println(sq.search("you"));
        System.out.println(sq.search("cat"));
        System.out.println("Use ( , ) , | , \\, & with spaces between");
        Scanner sc = new Scanner(System.in);
        System.out.println(sq.search(sc.nextLine()));
    }
}
