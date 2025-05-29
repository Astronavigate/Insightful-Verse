package tech.ravon.dao.iviep;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface HaloDao {

    List<List<String>> doSql(String sql);

}
