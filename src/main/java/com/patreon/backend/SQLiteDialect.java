package com.patreon.backend;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.type.StandardBasicTypes;

import java.sql.Types;

public class SQLiteDialect extends Dialect {

    public SQLiteDialect() {
        super();
        registerColumnType(Types.INTEGER, "integer");
        registerColumnType(Types.VARCHAR, "text");
        registerColumnType(Types.BOOLEAN, "boolean");
        registerColumnType(Types.DOUBLE, "double");
        registerColumnType(Types.FLOAT, "float");
        registerColumnType(Types.BIGINT, "bigint");
        registerColumnType(Types.CHAR, "char");

        registerFunction("lower", new StandardSQLFunction("lower", StandardBasicTypes.STRING));
        registerFunction("upper", new StandardSQLFunction("upper", StandardBasicTypes.STRING));
        registerFunction("concat", new VarArgsSQLFunction(StandardBasicTypes.STRING, "", "||", ""));
        registerFunction("mod", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "?1 % ?2"));
    }

    @Override
    public boolean supportsLimit() {
        return true;
    }

    @Override
    public String getLimitString(String query, boolean hasOffset) {
        return query + (hasOffset ? " limit ? offset ?" : " limit ?");
    }


    public boolean supportsIdentityColumns() {
        return true;
    }


    public String getIdentityColumnString() {
        return "integer";
    }


    public String getIdentitySelectString() {
        return "select last_insert_rowid()";
    }

    @Override
    public String getAddColumnString() {
        return "add column";
    }

    // 🔥 Add this override
    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new SQLiteIdentityColumnSupport();
    }
}
