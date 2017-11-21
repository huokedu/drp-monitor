package me.sfeer.mapper;

import org.apache.ibatis.jdbc.SQL;

public class TopologySqlProvider {

    public String selectTopology(final String name) {
        return new SQL() {
            {
                SELECT("id,name,`group`,ctime");
                FROM("drp_rm_topology");
                if (name != null)
                    WHERE("name like #{name}");
            }
        }.toString();
    }
}
