package solis.pl.repository;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import solis.pl.domain.loginTest.loginTestUserDetails;
import solis.pl.domain.user.UserFile;

@Repository
public interface LoginTestMapper {

    @Insert("insert into `login_test` ( " +
            "ID, " +
            "PW, " +
            "name, " +
            "AUTHORITY " +
            ") values ( " +
            "#{ID}, " +
            "#{PW}, " +
            "#{NAME}, " +
            "#{AUTHORITY} " +
            ")")
    @Options(flushCache = true)
    void insert(loginTestUserDetails userDetails);

    @Select("select * from `login_test` where ID = #{ID}")
    loginTestUserDetails getUserById(@Param("ID") String ID);

}
