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

package tech.ravon.dao.iviep.impl;

import org.springframework.stereotype.Component;
import tech.ravon.dao.iviep.HaloDao;

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

            // 从配置文件中读取数据库连接信息
            String url = props.getProperty("url");
            String user = props.getProperty("user");
            String password = props.getProperty("password");

            // 连接数据库
            conn = DriverManager.getConnection(url, user, password);

            ArrayList<String> sqlStatements = parseSQL(sql);
            System.out.println(sqlStatements);

            // 创建Statement对象，执行SQL语句
            stmt = conn.createStatement();

            for (String statement : sqlStatements) {
                resultList.clear();
                // 判断SQL语句类型
                String firstWord = getFirstWord(statement);
                if (firstWord.equalsIgnoreCase("SELECT")) {
                    // 创建PreparedStatement对象
                    PreparedStatement pstmt = conn.prepareStatement(statement);
                    // 对于SELECT语句，执行查询并处理结果集
                    rs = pstmt.executeQuery();
                    // 获取结果集元数据，提取列名
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    List<String> columnNames = new ArrayList<>();
                    for (int i = 1; i <= columnCount; i++) {
                        columnNames.add(metaData.getColumnName(i));
                    }
                    resultList.add(columnNames);
                    // 处理结果集，存入二维列表
                    while (rs.next()) {
                        List<String> row = new ArrayList<>();
                        for (int i = 1; i <= columnCount; i++) {
                            row.add(rs.getString(i)); // 使用getString获取结果集中的字符串值
                        }
                        resultList.add(row);
                    }
                    rs.close();
                    pstmt.close();
                } else {
                    // 对于INSERT, UPDATE, DELETE语句，执行更新操作
                    int rowsAffected = stmt.executeUpdate(statement);
                    System.out.println("Rows affected: " + rowsAffected);
                    List<String> row = new ArrayList<>();
                    row.add("Row affected: " + rowsAffected);
                    resultList.add(row);
                }
                // 处理 resultList，例如输出或者进一步处理
                for (List<String> resultRow : resultList) {
                    System.out.println(resultRow);
                }
            }
            stmt.close();
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
            // 关闭连接
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

        // 使用正则表达式分隔SQL语句，忽略在分号后面的空白字符，直到下一个有效SQL关键字开头的情况（不区分大小写）
        Pattern pattern = Pattern.compile(";\\s*(?=\\b(SELECT|INSERT|UPDATE|DELETE)\\b)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        int lastMatchEnd = 0;

        while (matcher.find()) {
            String statement = sql.substring(lastMatchEnd, matcher.end()).trim();
            sqlStatements.add(statement);
            lastMatchEnd = matcher.end();
        }

        // 添加最后一条语句（没有分号结尾的情况）
        if (lastMatchEnd < sql.length()) {
            sqlStatements.add(sql.substring(lastMatchEnd).trim());
        }

        return sqlStatements;
    }

    private static String getFirstWord(String text) {
        String[] words = text.trim().split("\\s+");
        if (words.length > 0) {
            return words[0].toUpperCase();
        }
        return "";
    }
}
