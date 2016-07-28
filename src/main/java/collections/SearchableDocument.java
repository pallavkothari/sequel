package collections;

import Jest.ElasticSearchTools;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.gson.Gson;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;

import java.util.EnumSet;

/**
 * Created by jhorwitz on 7/14/16.
 */
// TODO: add sorting (make sort first on filename); then have collections of SearchableDocument objects actually use the sorting (where appropriate)
public class SearchableDocument implements Comparable<SearchableDocument> {
    static private final Gson GSON = new Gson();

    private final String filename;
    private final String expressionString;
    private final String aliasName;
    private final SearchableDocumentType type;

    // TODO: unify createAndIndex, createAndIndexFromClause, etc.
    // TODO: stop abusing SearchableDocument.alias
    public static SearchableDocument createAndIndex(Expression expression, Alias alias, SearchableDocumentType type) {
        String aliasName = (alias == null) ? null : alias.getName();
        SearchableDocument doc = new SearchableDocument(Ctx.get().getFile(), expression.toString(), aliasName, type);
        doc.sendToElasticSearch();
        return doc;
    }


    public static void createAndIndexFromClause(Table tableName) {
        SearchableDocument doc = new SearchableDocument(Ctx.get().getFile(),
                tableName.toString(),
                tableName.getAlias() == null ? null : tableName.getAlias().toString(),
                SearchableDocumentType.FROM_CLAUSE);
        doc.sendToElasticSearch();
    }

    public static void createAndIndexFromFilter(String filter) {
        SearchableDocument doc = new SearchableDocument(Ctx.get().getFile(),
                filter,
                null,
                SearchableDocumentType.FILTER_CLAUSE);
        doc.sendToElasticSearch();
    }

    public static void createAndIndexFromJoin(Join join) {
        SearchableDocument doc = new SearchableDocument(Ctx.get().getFile(),
                join.toString(),
                join.getRightItem().getAlias() == null ? null : join.getRightItem().getAlias().toString(),
                SearchableDocumentType.JOIN_CLAUSE);
        doc.sendToElasticSearch();
    }

    private SearchableDocument(String filename, String expressionString, String aliasName, SearchableDocumentType type) {
        this.filename = filename;
        this.expressionString = expressionString;   // TODO SDH this is currently a bastardization, but meh (and you know nothing Jon Snow!)
        this.aliasName = aliasName;
        this.type = type;
    }

    private void sendToElasticSearch() {
        ElasticSearchTools.indexThisJson(ElasticSearchTools.INDEX_NAME, this.type, GSON.toJson(this));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("filename", this.filename)
                .add("aliasName", this.aliasName)
                .add("expressionString", this.expressionString)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchableDocument that = (SearchableDocument) o;
        return Objects.equal(this.filename, that.filename) &&
                Objects.equal(this.expressionString, that.expressionString) && // TODO?: compare after applying TextUtil.simplifyString (so its easier to be equal)? {sim. for args in other Objects.equal calls here)
                Objects.equal(this.aliasName, that.aliasName) &&
                this.type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.filename, this.expressionString, this.aliasName, this.type);
    }

    public String toPrettyPrintString(EnumSet<Field> fields) {
        MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper(this).add("filename", this.filename);
        if (fields.contains(Field.expressionString)) helper.add("expressionString", this.expressionString);
        if (fields.contains(Field.aliasName)) helper.add("aliasName", this.aliasName);
        helper.add("type", this.type);
        return helper.toString();
    }

    public boolean isType(SearchableDocumentType sdt) {
        return this.type == sdt;
    }

    @Override
    public int compareTo(SearchableDocument that) {
        return ComparisonChain.start()
                .compare(this.type, that.type)
                .compare(this.filename, that.filename)
                .compare(this.expressionString, that.expressionString)
                .compare(this.aliasName, that.aliasName)
                .result();
    }

    public enum SearchableDocumentType {
        FROM_CLAUSE,
        SELECT_EXPRESSION_ITEM,
        JOIN_CLAUSE,
        FILTER_CLAUSE
    }

    // case-sensitive
    public enum Field {
        expressionString,
        aliasName,
        tweet
    }
}
