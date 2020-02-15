import java.util.Scanner;

public class Test {

    public static void main(String[] args) {
        SearchQuery sq = new SearchQuery("docs\\");
        System.out.println(sq.search("dog"));
        System.out.println(sq.search("you"));
        System.out.println("Use ( , ) , | , \\, & with spaces between");
        System.out.println(sq.search("cat"));
        System.out.println(sq.search(" cat \\  dog  & you "));
        Scanner sc = new Scanner(System.in);
        System.out.println(sq.search(sc.nextLine()));
    }
}
