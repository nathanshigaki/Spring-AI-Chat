package dio.budgeting.domain.model;

public enum Category {
    // Receitas
    SALARIO(TransactionType.INCOME, "Salário"),
    INVESTIMENTOS(TransactionType.INCOME, "Investimentos"),
    FREELANCE(TransactionType.INCOME, "Freelance"),
    OUTRAS_RECEITAS(TransactionType.INCOME, "Outras Receitas"),

    // Despesas
    ALIMENTACAO(TransactionType.EXPENSE, "Alimentação"),
    MORADIA(TransactionType.EXPENSE, "Moradia"),
    TRANSPORTE(TransactionType.EXPENSE, "Transporte"),
    SAUDE(TransactionType.EXPENSE, "Saúde"),
    EDUCACAO(TransactionType.EXPENSE, "Educação"),
    LAZER(TransactionType.EXPENSE, "Lazer"),
    OUTRAS_DESPESAS(TransactionType.EXPENSE, "Outras Despesas");

    private final TransactionType applicableType;
    private final String displayName;

    Category(TransactionType applicableType, String displayName) {
        this.applicableType = applicableType;
        this.displayName = displayName;
    }

    public TransactionType getApplicableType() {
        return applicableType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean matchesType(TransactionType type) {
        return this.applicableType == type;
    }
}
