package collections;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import util.FileUtil;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;


/**
 * Created by pkothari on 7/15/16.
 */
public class Ctx {

    private static final ThreadLocal<MyContext> ctxHolder = new ThreadLocal<MyContext>() {
        @Override
        protected MyContext initialValue() {
            return new MyContext();
        }
    };

    public static TreeSet<String> allTables = Sets.newTreeSet();

    public static class MyContext {

        private String file;
        private int visitingDepth = 0;
        List<String> messages = Lists.newArrayList();
        TreeSet<String> froms = Sets.newTreeSet();
        TreeSet<String> selects = Sets.newTreeSet();

        public void setFile(String file) {
            this.file = file;
        }

        public String getFile() {
            Preconditions.checkNotNull(file);
            return file;
        }

        public void visiting() {
            visitingDepth++;
        }

        public void leaving() {
            visitingDepth--;
        }

        public void selectExpressionItem(SelectExpressionItem selectExpressionItem, Alias alias) {
            String prefix = tabs() + "select: ";
            String aliasedSelect = aliasCol(alias) + selectExpressionItem.getExpression().toString();
            String msg = prefix + aliasedSelect;
            messages.add(msg);
            selects.add(aliasedSelect);
        }

        private String aliasCol(Alias alias) {
            return alias.getName() + new String(new char[30 - alias.getName().length()]).replace("\0", " ");
        }

        private String tabs() {
            return new String(new char[visitingDepth]).replace("\0", "\t");
        }

        public void flush() {
            FileUtil.write(file, messages);
            echo("Found "+ selects.size() + " selects: ", selects);
            echo("Found " + froms.size() + " tables: ", froms);
        }

        private void echo(String msg, Set<String> set) {
            System.out.println(msg);
            for (String s : set) {
                System.out.println("\t" + s);
            }
            System.out.println();
        }

        public void log(String s) {
            messages.add(tabs() + s);
        }

        public void addFromTable(Table tableName) {
            froms.add(tableName.toString());
            allTables.add(tableName.getFullyQualifiedName().toUpperCase());
        }
    }

    public static MyContext get() {
        return ctxHolder.get();
    }

    /**
     * the destroyer
     */
    public static void shiva() {
        ctxHolder.get().flush();
        ctxHolder.remove();
    }
}
