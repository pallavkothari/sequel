package visitors;

import collections.Ctx;
import collections.SearchableDocument;
import com.google.common.base.MoreObjects;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;

import java.util.List;

/**
 * Created by pkothari on 7/14/16.
 */
public class SequelVisitor implements SelectVisitor, SelectItemVisitor, FromItemVisitor {
    private final Multimap<String, Alias> projectionToAlias = HashMultimap.create();
    private final SequelExpressionVisitor expressionVisitor = new SequelExpressionVisitor();

    @Override
    public void visit(AllColumns allColumns) {
        //System.out.println("allColumns = " + allColumns);
    }

    @Override
    public void visit(AllTableColumns allTableColumns) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(SelectExpressionItem selectExpressionItem) {
        SearchableDocument.createAndIndex(
                selectExpressionItem.getExpression(),
                selectExpressionItem.getAlias(),
                SearchableDocument.SearchableDocumentType.SELECT_EXPRESSION_ITEM);
        Alias alias = MoreObjects.firstNonNull(selectExpressionItem.getAlias(),
                new Alias(selectExpressionItem.getExpression().toString(), false));
        this.projectionToAlias.put(selectExpressionItem.getExpression().toString(), alias);
        Ctx.get().selectExpressionItem(selectExpressionItem, alias);
    }

    @Override
    public void visit(PlainSelect plainSelect) {
        Ctx.get().log("plainSelect = " + plainSelect);
        Ctx.get().visiting();
        selects(plainSelect);
        from(plainSelect);
        joins(plainSelect);
        where(plainSelect);
        Ctx.get().leaving();
    }

    private void selects(PlainSelect plainSelect) {
        for (SelectItem selectItem : plainSelect.getSelectItems()) {
            selectItem.accept(this);
        }
    }

    private void from(PlainSelect plainSelect) {
        if (plainSelect.getFromItem() != null) {
            plainSelect.getFromItem().accept(this);
        }
    }

    private void joins(PlainSelect plainSelect) {
        if (plainSelect.getJoins() != null) {
            Ctx.get().visiting();
            for (Join join : plainSelect.getJoins()) {
                processJoin(join);
            }
            Ctx.get().leaving();
        }
    }

    private void where(PlainSelect plainSelect) {
        if (plainSelect.getWhere() != null) {
            Ctx.get().log("WHERE");
            Ctx.get().visiting();
            plainSelect.getWhere().accept(expressionVisitor);
            Ctx.get().leaving();
        }
    }

    private void processJoin(Join join) {
        Ctx.get().log(join.toString());
        SearchableDocument.createAndIndexFromJoin(join);
        FromItem rightItem = join.getRightItem();
        Ctx.get().visiting();
        rightItem.accept(this);
        if (join.getOnExpression() != null) {
            Ctx.get().log("ON");
            join.getOnExpression().accept(expressionVisitor);
        }
        Ctx.get().leaving();
    }

    @Override
    public void visit(SetOperationList setOpList) {
        Ctx.get().log("Found " + setOpList.getSelects().size() + " sets .. " + setOpList.getOperations());
        for (SelectBody selectBody : setOpList.getSelects()) {
            Ctx.get().visiting();
            selectBody.accept(this);
            Ctx.get().leaving();
        }
    }

    @Override
    public void visit(WithItem withItem) {
        Ctx.get().log("withItem " + withItem);
        Ctx.get().visiting();
        withItem.getSelectBody().accept(this);
        Ctx.get().leaving();
    }

    public SequelVisitor process(SelectBody selectBody) {
        selectBody.accept(this);
        return this;
    }

    public SequelVisitor process(List<WithItem> withItemsList) {
        if (withItemsList == null) return this;
        Ctx.get().log("Found " + withItemsList.size() + " withItems");
        for (WithItem withItem : withItemsList) {
            withItem.accept(this);
        }
        return this;
    }

    public Multimap<String, Alias> getProjectionToAliasMap() {
        return this.projectionToAlias;
    }

    @Override
    public void visit(Table tableName) {
        Ctx.get().log("FROM table " + tableName);
        SearchableDocument.createAndIndexFromClause(tableName);
        Ctx.get().addFromTable(tableName);
    }

    @Override
    public void visit(SubSelect subSelect) {
        Ctx.get().log("FROM SubSelect " + subSelect);
        Ctx.get().visiting();
        subSelect.getSelectBody().accept(this);
        Ctx.get().leaving();
    }

    @Override
    public void visit(SubJoin subjoin) {
        Ctx.get().log("FROM SubJoin " + subjoin);
    }

    @Override
    public void visit(LateralSubSelect lateralSubSelect) {
        Ctx.get().log("FROM LateralSubSelect" + lateralSubSelect);
    }

    @Override
    public void visit(ValuesList valuesList) {
        Ctx.get().log("FROM valuesList " + valuesList);
    }

    @Override
    public void visit(TableFunction tableFunction) {
        Ctx.get().log("FROM tableFunction " + tableFunction);
    }
}
