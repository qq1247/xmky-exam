package com.wcpdoc.core.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import com.wcpdoc.core.util.StringUtil;

/**
 * 类型转换器
 * 
 * v1.0 zhanghc 2024年1月16日下午1:36:08
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes({ List.class })
public class IntTypeHandler extends BaseTypeHandler<List<Integer>> {

	@Override
	public void setNonNullParameter(PreparedStatement preparedStatement, int i, List<Integer> list, JdbcType jdbcType)
			throws SQLException {
		preparedStatement.setString(i, StringUtil.toStr(list));
	}

	@Override
	public List<Integer> getNullableResult(ResultSet resultSet, String s) throws SQLException {
		String value = resultSet.getString(s);
		return StringUtil.toIntList(value);
	}

	@Override
	public List<Integer> getNullableResult(ResultSet resultSet, int i) throws SQLException {
		String value = resultSet.getString(i);
		return StringUtil.toIntList(value);
	}

	@Override
	public List<Integer> getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
		String value = callableStatement.getString(i);
		return StringUtil.toIntList(value);
	}
}
