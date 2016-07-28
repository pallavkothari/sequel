import org.junit.Test;

import static Jest.ElasticSearchTools.searchExpressionsAndAliases;

/**
 * Just a quick entrypoint to run searches for now; no real tests here.
 */
public class SearchTest {

    @Test
    public void search2() {
        searchExpressionsAndAliases("*charge*");
    }

    @Test
    public void search3() {
        searchExpressionsAndAliases("*prncp*");
    }
}
