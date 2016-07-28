import com.google.common.collect.Lists;
import org.junit.Test;
import util.FileUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by pkothari on 7/15/16.
 */
public class FileTest {
    @Test
    public void testSimpleFileWriting() throws IOException {
        String msg = "hi there";
        FileUtil.write("foo", msg);
        checkOutput(msg, "foo.sql2");
    }

    @Test
    public void testWritingMultipleLines() throws Exception {
        FileUtil.write("bar", Lists.newArrayList("first", "second"));
        checkOutput("first\nsecond\n", "bar.sql2");
    }

    private void checkOutput(String msg, String outputFileName) throws IOException {
        Path output = Paths.get("output");
        assertTrue(Files.isDirectory(output));
        Path file = output.resolve(outputFileName);
        assertTrue(Files.exists(file));
        assertThat(new String(Files.readAllBytes(file)), is(equalTo(msg)));
    }
}

