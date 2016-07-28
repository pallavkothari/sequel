import collections.Ctx;
import org.junit.Test;
import sequel.Sequel;

/**
 * Just a quick way to analyze and index sql -- no real tests here.
 */
public class SimpleTest {

    @Test
    public void indexSingleFile() throws Exception {
        new Sequel().indexSingleFile("foo.sql");
    }

    @Test
    public void indexAllSqls() throws Exception {
        new Sequel().indexAllSqls();

        printFooter();
    }

    private void printFooter() {
        System.out.println("===========================================================");
        System.out.println("Summary");
        System.out.println("===========================================================");

        System.out.println(Ctx.allTables.size() + " tables in total : ");
        for (String s : Ctx.allTables) {
            System.out.println("\t" + s);
        }
    }

}
