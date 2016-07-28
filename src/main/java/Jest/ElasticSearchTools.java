package Jest;

import collections.Ctx;
import collections.SearchableDocument;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.indices.CreateIndex;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by pkothari on 7/14/16.
 */
public class ElasticSearchTools {
    public static final String INDEX_NAME = "test";  // TODO: change this constant
    private static final JestClient JEST = makeClient();
    private static final Gson GSON = new Gson();

    // TODO: return result, handle outside of ElasticSearchTools class(?)
    public static void indexThisJson(String indexName, SearchableDocument.SearchableDocumentType type, String json) {
        String typeName = type.name().toLowerCase();
        Index index = new Index.Builder(json)
                .index(indexName.toLowerCase())
                .type(typeName)
                .id(calculateId())
                .build();
        try {
            DocumentResult result = JEST.execute(index);
            if (!result.isSucceeded()) {
                System.out.println("result = " + result.getJsonString());
            }
        } catch (IOException ioe) {
            rethrow(ioe);
        }
    }

    private static Multiset<String> paths = LinkedHashMultiset.create();
    private static Set<String> hashes = Sets.newHashSet();  // no pun intended

    // seems to work ok for now, but there's a potential problem with this approach:
    // it does not take into account siblings higher in the stack.
    // e.g. 2 "with" items both with similar subtrees might result in a collision,
    // s.t. the original would get overwritten.
    private static String calculateId() {
        String id = Ctx.get().getFile();
        StackTraceElement[] stackTrace = new Exception().getStackTrace();
        for (int i = stackTrace.length - 1; i >= 0; i--) {
            StackTraceElement stackTraceElement = stackTrace[i];
            if (stackTraceElement.getClassName().contains("net.sf.jsqlparser")) {
                id += "/" + stackTraceElement.getClassName();
            }
        }
        paths.add(id);
        String toHash = id + paths.count(id);
        String hash = String.valueOf(toHash.hashCode());
        Preconditions.checkState(!hashes.contains(hash), "id hash collision");
        hashes.add(hash);
        return hash;
    }

    /**
     * @return the _source fields for searchExpressionsAndAliases hits. Use Gson to convert these back to POJOs.
     */
    public static List<JsonObject> searchExpressionsAndAliases(String indexName, String query, SearchableDocument.Field field) {
        SearchSourceBuilder ssb = new SearchSourceBuilder();
        ssb.query(QueryBuilders.wildcardQuery(field.name(), query));
        Search search = new Search.Builder(ssb.toString()).addIndex(indexName).build();

        SearchResult searchResult = null;
        try {
            searchResult = JEST.execute(search);
        } catch (IOException ioe) {
            rethrow(ioe);
        }

        System.out.println("searchResult = " + searchResult.getJsonString());
        return getHitsSources(searchResult);
    }

    private static List<JsonObject> getHitsSources(SearchResult searchResult) {
        JsonObject hits = searchResult.getJsonObject().getAsJsonObject("hits");
        List<JsonObject> hitSources = Lists.newArrayList();
        for (JsonElement jsonElement : hits.getAsJsonArray("hits")) {
            JsonObject source = jsonElement.getAsJsonObject().getAsJsonObject("_source");
            hitSources.add(source);
        }
        return hitSources;
    }

    private static JestClient makeClient() {
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig.Builder("http://localhost:9200").multiThreaded(true).build());
        return factory.getObject();
    }

    public static void createIndex(String indexName) {
        try {
            JestResult result = JEST.execute(new CreateIndex.Builder(indexName.toLowerCase()).build());
            if (!result.isSucceeded()) {
                System.out.println("result = " + result.getErrorMessage());
            }
        } catch (IOException e) {
            rethrow(e);
        }
    }

    private static void rethrow(Exception e) {
        throw new RuntimeException(e);
    }

    public static void searchExpressionsAndAliases(String query) {
        Set<SearchableDocument> expressionHits = searchField(query, SearchableDocument.Field.expressionString);
        Set<SearchableDocument> aliasHits = searchField(query, SearchableDocument.Field.aliasName);
        Sets.SetView<SearchableDocument> intersection = Sets.intersection(expressionHits, aliasHits);
        Sets.SetView<SearchableDocument> expressionHitsOnly = Sets.difference(expressionHits, intersection);
        Sets.SetView<SearchableDocument> aliasHitsOnly = Sets.difference(aliasHits, intersection);

        for (SearchableDocument.SearchableDocumentType sdt : SearchableDocument.SearchableDocumentType.values()) {
            System.out.println(String.format("\n\n  Type: %1$s\n", sdt));
            System.out.println("Expression hits:\n\t\t" + getPrettyPrintString(sdt, expressionHitsOnly, EnumSet.of(SearchableDocument.Field.expressionString)));
            System.out.println("Alias hits:\n\t\t" + getPrettyPrintString(sdt, aliasHitsOnly, EnumSet.of(SearchableDocument.Field.aliasName)));
            System.out.println("Expression + Alias hits:\n\t\t" + getPrettyPrintString(sdt, intersection, EnumSet.allOf(SearchableDocument.Field.class)));
        }
    }

    // TODO: don't loop over docs once for each SDT--partition
    private static String getPrettyPrintString(SearchableDocument.SearchableDocumentType sdt, Sets.SetView<SearchableDocument> docs, EnumSet<SearchableDocument.Field> fields) {
        Joiner joiner = Joiner.on("\n\t\t");
        List<String> spResult = Lists.newArrayList();
        TreeSet<SearchableDocument> sortedDocs = Sets.newTreeSet(docs); // TODO: sort earlier in life cycle
        for (SearchableDocument doc : sortedDocs) {
            if (doc.isType(sdt)) {
                spResult.add(doc.toPrettyPrintString(fields));
            }
        }
        return joiner.join(spResult);
    }

    private static Set<SearchableDocument> searchField(String query, SearchableDocument.Field field) {
        List<JsonObject> results = ElasticSearchTools.searchExpressionsAndAliases(INDEX_NAME, query, field);
        Set<SearchableDocument> docs = Sets.newLinkedHashSet();
        for (JsonObject result : results) {
            docs.add(GSON.fromJson(result, SearchableDocument.class));
        }
        System.out.println("docs = " + docs);
        return docs;
    }
}
