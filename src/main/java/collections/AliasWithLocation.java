package collections;

import net.sf.jsqlparser.expression.Alias;
import util.TextUtil;

public class AliasWithLocation {
    private final Alias alias;
    private final String sqlFilename;
    private final String simplifiedAlias;

    public static AliasWithLocation create(Alias alias, String sqlFilename) {
        String simplifiedAlias = TextUtil.simplifyString(alias.getName());
        return new AliasWithLocation(alias, sqlFilename, simplifiedAlias);
    }

    private AliasWithLocation(Alias alias, String sqlFilename, String simplifiedAlias) {
        this.alias = alias;
        this.sqlFilename = sqlFilename;
        this.simplifiedAlias = simplifiedAlias;
    }

    // e.g., "LOAN_ID" matches "loanid" (see simplifiedAlias initialization)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AliasWithLocation)) return false;
        AliasWithLocation that = (AliasWithLocation) o;

        return this.simplifiedAlias.equals(that.simplifiedAlias);
    }

    @Override
    public int hashCode() {
        return this.simplifiedAlias.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%1$s (%2$s)", this.alias, this.sqlFilename);
    }
}
