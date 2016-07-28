import collections.Ctx;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Created by pkothari on 7/15/16.
 */
public class CtxTest {

    @Rule public ExpectedException npe = ExpectedException.none();

    @Test
    public void test1() {
        Ctx.MyContext myContext = Ctx.get();
        assertNotNull(myContext);
        myContext.setFile("foo");
        assertThat(myContext.getFile(), is("foo"));
        Ctx.shiva();
        assertThat(Ctx.get(), is(notNullValue()));
    }

    @Test
    public void testNpe() {
        npe.expect(NullPointerException.class);
        Ctx.shiva();
        Ctx.get().getFile();
    }
}
