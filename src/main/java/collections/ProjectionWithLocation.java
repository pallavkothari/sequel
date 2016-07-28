package collections;

import static util.TextUtil.simplifyString;

public class ProjectionWithLocation {
    private final String projection;
    private final String sqlFilename;
    private final String simpliedProjection;

    public static ProjectionWithLocation create(String projection, String sqlFilename) {
        String simpliedProjection = simplifyString(projection);
        return new ProjectionWithLocation(projection, sqlFilename, simpliedProjection);
    }

    private ProjectionWithLocation(String projection, String sqlFilename, String simpliedProjection) {
        this.projection = projection;
        this.sqlFilename = sqlFilename;
        this.simpliedProjection = simpliedProjection;
    }

    // e.g., "LOAN_ID" matches "loanid" (see simplifiedProjection initialization)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectionWithLocation)) return false;
        ProjectionWithLocation that = (ProjectionWithLocation) o;

        return this.simpliedProjection.equals(that.simpliedProjection);
    }

    @Override
    public int hashCode() {
        return this.simpliedProjection.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%1$s (%2$s)", this.projection, this.sqlFilename);
    }
}
