/**
 * Copyright 2025 Astronavigate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.ravon.dao.inver.impl;

import org.springframework.stereotype.Component;
import tech.ravon.dao.inver.HaloDao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class HaloDaoImpl implements HaloDao {

    @Override
    public List<List<String>> doSql(String sql) {
        List<List<String>> resultList = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // 加载配置文件
            Properties props = new Properties();
            InputStream input = getClass().getClassLoader().getResourceAsStream("jdbc.properties");
            if (input == null) {
                throw new FileNotFoundException("jdbc.properties not found in the classpath");
            }
            props.load(input);

            // 数据库连接信息
            String url = props.getProperty("url");
            String user = props.getProperty("user");
            String password = props.getProperty("password");

            conn = DriverManager.getConnection(url, user, password);

            // 分割多条 SQL
            ArrayList<String> sqlStatements = parseSQL(sql);
            System.out.println(sqlStatements);

            stmt = conn.createStatement();

            for (String statement : sqlStatements) {
                resultList.clear();
                try {
                    boolean hasResultSet = stmt.execute(statement);

                    if (hasResultSet) {
                        // 处理结果集
                        rs = stmt.getResultSet();
                        ResultSetMetaData metaData = rs.getMetaData();
                        int columnCount = metaData.getColumnCount();

                        // 列名
                        List<String> columnNames = new ArrayList<>();
                        for (int i = 1; i <= columnCount; i++) {
                            columnNames.add(metaData.getColumnName(i));
                        }
                        resultList.add(columnNames);

                        // 数据行
                        while (rs.next()) {
                            List<String> row = new ArrayList<>();
                            for (int i = 1; i <= columnCount; i++) {
                                row.add(rs.getString(i));
                            }
                            resultList.add(row);
                        }
                        rs.close();
                    } else {
                        // 非结果集，返回影响行数
                        int rowsAffected = stmt.getUpdateCount();
                        List<String> row = new ArrayList<>();
                        row.add("Rows affected: " + rowsAffected);
                        resultList.add(row);
                    }

                    // 输出结果
                    for (List<String> resultRow : resultList) {
                        System.out.println(resultRow);
                    }

                } catch (SQLException e) {
                    // 捕获单条语句异常，不影响其他语句
                    List<String> exceptionRow = new ArrayList<>();
                    exceptionRow.add("SQL Exception");
                    exceptionRow.add(e.getMessage());
                    resultList.add(exceptionRow);
                    e.printStackTrace();
                }
            }

        } catch (SQLException e) {
            List<String> exceptionRow = new ArrayList<>();
            exceptionRow.add("SQL Exception");
            exceptionRow.add(e.getMessage());
            resultList.add(exceptionRow);
        } catch (FileNotFoundException e) {
            List<String> exceptionRow = new ArrayList<>();
            exceptionRow.add("File Not Found Exception");
            exceptionRow.add(e.getMessage());
            resultList.add(exceptionRow);
        } catch (IOException e) {
            List<String> exceptionRow = new ArrayList<>();
            exceptionRow.add("IO Exception");
            exceptionRow.add(e.getMessage());
            resultList.add(exceptionRow);
        } finally {
            // 关闭资源
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return resultList;
    }

    private static ArrayList<String> parseSQL(String sql) {
        ArrayList<String> sqlStatements = new ArrayList<>();
        Pattern pattern = Pattern.compile(";\\s*(?=\\b(SELECT|INSERT|UPDATE|DELETE|SHOW|DESCRIBE|CALL)\\b)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        int lastMatchEnd = 0;

        while (matcher.find()) {
            String statement = sql.substring(lastMatchEnd, matcher.end()).trim();
            if (!statement.isEmpty()) {
                sqlStatements.add(statement);
            }
            lastMatchEnd = matcher.end();
        }

        // 添加最后一条语句
        if (lastMatchEnd < sql.length()) {
            String statement = sql.substring(lastMatchEnd).trim();
            if (!statement.isEmpty()) {
                sqlStatements.add(statement);
            }
        }

        return sqlStatements;
    }
}
