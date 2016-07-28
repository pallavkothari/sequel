package visitors;

import collections.Ctx;
import collections.SearchableDocument;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

/**
 * Created by pkothari on 7/15/16.
 */
public class SequelExpressionVisitor implements ExpressionVisitor {

    @Override
    public void visit(NullValue nullValue) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(Function function) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(SignedExpression signedExpression) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(JdbcParameter jdbcParameter) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(JdbcNamedParameter jdbcNamedParameter) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(DoubleValue doubleValue) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(LongValue longValue) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(HexValue hexValue) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(DateValue dateValue) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(TimeValue timeValue) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(TimestampValue timestampValue) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(Parenthesis parenthesis) {
        Ctx.get().log(parenthesis.toString());
    }

    @Override
    public void visit(StringValue stringValue) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(Addition addition) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(Division division) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(Multiplication multiplication) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(Subtraction subtraction) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(AndExpression andExpression) {
        Ctx.get().log(andExpression.toString());
        SearchableDocument.createAndIndexFromFilter(andExpression.toString());
    }

    @Override
    public void visit(OrExpression orExpression) {
        Ctx.get().log(orExpression.toString());
    }

    @Override
    public void visit(Between between) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(EqualsTo equalsTo) {
        Ctx.get().log(equalsTo.toString());
    }

    @Override
    public void visit(GreaterThan greaterThan) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(InExpression inExpression) {
        Ctx.get().log(inExpression.toString());
    }

    @Override
    public void visit(IsNullExpression isNullExpression) {
        Ctx.get().log(isNullExpression.toString());
    }

    @Override
    public void visit(LikeExpression likeExpression) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(MinorThan minorThan) {
        Ctx.get().log(minorThan.toString());
    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        Ctx.get().log(minorThanEquals.toString());
    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(Column tableColumn) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(SubSelect subSelect) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(CaseExpression caseExpression) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(WhenClause whenClause) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(ExistsExpression existsExpression) {
        Ctx.get().log(existsExpression.toString());
    }

    @Override
    public void visit(AllComparisonExpression allComparisonExpression) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(AnyComparisonExpression anyComparisonExpression) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(Concat concat) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(Matches matches) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(BitwiseAnd bitwiseAnd) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(BitwiseOr bitwiseOr) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(BitwiseXor bitwiseXor) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(CastExpression cast) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(Modulo modulo) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(AnalyticExpression aexpr) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(WithinGroupExpression wgexpr) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(ExtractExpression eexpr) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(IntervalExpression iexpr) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(OracleHierarchicalExpression oexpr) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(RegExpMatchOperator rexpr) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(JsonExpression jsonExpr) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(RegExpMySQLOperator regExpMySQLOperator) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(UserVariable var) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(NumericBind bind) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(KeepExpression aexpr) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(MySQLGroupConcat groupConcat) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(RowConstructor rowConstructor) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }

    @Override
    public void visit(OracleHint hint) {
        throw new UnsupportedOperationException(); // TODO (7/17/16) implement me!
    }


}
