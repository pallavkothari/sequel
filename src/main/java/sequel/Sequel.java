package sequel;

import collections.AliasWithLocation;
import collections.Ctx;
import collections.ProjectionWithLocation;
import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import visitors.SequelVisitor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

/**
 * Created by pkothari on 7/16/16.
 */
public class Sequel {

    public static final Multimap<String, ProjectionWithLocation> ALIAS_NAME_TO_PROJECTION = HashMultimap.create();
    public static final Multimap<String, AliasWithLocation> PROJECTION_TO_ALIAS = HashMultimap.create();

    public static Select getSelect(String file) throws IOException, JSQLParserException {
        System.out.println("\n\nProcessing... " + file + "\n");
        Path path = Paths.get(file);
        Preconditions.checkState(path.toFile().exists());
        String content = new String(Files.readAllBytes(path));

        Statement stmt = null;  // TODO what are the actual bind variables we use?
        try {
            stmt = CCJSqlParserUtil.parse(content.replaceAll("#", ":"));
        } catch (JSQLParserException e) {
            System.out.println("Parse exception while processing the following file : " + file);
        }
        if (stmt != null) {
            Preconditions.checkArgument(stmt instanceof Select);
        }
        System.out.println("SQL is...\n\t " + stmt);
        return (Select) stmt;
    }

    public void indexSingleFile(String sqlFilename) throws IOException, JSQLParserException {
        String file = getClass().getClassLoader().getResource(sqlFilename).getFile();
        Select select = getSelect(file);
        if (select == null) return;
        // remember to set up your context
        Ctx.get().setFile(sqlFilename);
        Multimap<String, Alias> projectionToAlias = new SequelVisitor()
                .process(select.getWithItemsList())
                .process(select.getSelectBody())
                .getProjectionToAliasMap();

//        System.out.println(" +++ projectionToAlias: ");
//        Sequel.addPTAMapToFinalMaps(projectionToAlias, sqlFilename);

//        printAliasProjectionMaps();
        Ctx.shiva();
    }


    public void indexAllSqls() throws Exception {
        String anyOldFile = getClass().getClassLoader().getResource("Go").getFile();
        File[] sqls = Paths.get(anyOldFile).getParent().toFile().listFiles((dir, name) -> name.endsWith("sql") || name.endsWith("hql"));
        Preconditions.checkNotNull(sqls);

        Multiset<String> allAliases = HashMultiset.create();
        for (File sql : sqls) {
            try {
                Ctx.get().setFile(sql.getName());

                Select select = getSelect(sql.getPath());
                if (select == null) continue;
                Multimap<String, Alias> temp = new SequelVisitor()
                        .process(select.getWithItemsList())
                        .process(select.getSelectBody())
                        .getProjectionToAliasMap();
                for (Alias alias : temp.values()) {
                    allAliases.add(alias.getName());
                }
                addPTAMapToFinalMaps(temp, sql.getName());
            } finally {
                Ctx.shiva();
            }
        }

//        System.out.println("\n\n***allAliases = " + Multisets.copyHighestCountFirst(allAliases));

//        printAliasProjectionMaps();
    }

    private static void addPTAMapToFinalMaps(Multimap<String, Alias> m, String sqlFilename) {
        for (String projection : m.keySet()) {
            for (Alias alias : m.get(projection)) {
                ALIAS_NAME_TO_PROJECTION.put(alias.getName(), ProjectionWithLocation.create(projection, sqlFilename));
                PROJECTION_TO_ALIAS.put(projection, AliasWithLocation.create(alias, sqlFilename));
            }
//            System.out.println(String.format("%1$s -> %2$s", projection, projectionToAlias.getProjectionToAliasMap(projection)));
        }
    }

    private void printAliasProjectionMaps() {
        System.out.println("\n\n\n +++ Alias name -> projections (& locations):");
        String last = null;
        for (String aliasName : Multisets.copyHighestCountFirst(Sequel.ALIAS_NAME_TO_PROJECTION.keys())) {
            if (aliasName.equals(last)) {
                continue;
            }
            last = aliasName;

            Collection<ProjectionWithLocation> projectionsWithLocations = Sequel.ALIAS_NAME_TO_PROJECTION.get(aliasName);
            if (projectionsWithLocations.size() > 1) {
                System.out.print(String.format(" *** %1$d ***: ", projectionsWithLocations.size()));
            }
            System.out.println(String.format("%s -> %s", aliasName, projectionsWithLocations));
        }

        System.out.println("\n\n\n +++ Projection -> alias names (& locations):");
        last = null;
        for (String projection : Multisets.copyHighestCountFirst(Sequel.PROJECTION_TO_ALIAS.keys())) {
            if (projection.equals(last)) {
                continue;
            }
            last = projection;
            Collection<AliasWithLocation> aliasesWithLocations = Sequel.PROJECTION_TO_ALIAS.get(projection);
            if (aliasesWithLocations.size() > 1) {
                System.out.print(String.format(" *** %1$d ***: ", aliasesWithLocations.size()));
            }
            System.out.println(String.format("%s -> %s", projection, aliasesWithLocations));
        }
    }

}
