package solis.pl.repository;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import solis.pl.domain.loginTest.loginTestUserDetails;

@Repository
public interface LoginTestMapper {

    @Select("select * from `login_test` where ID = #{ID}")
    loginTestUserDetails getUserById(@Param("ID") String ID);

}
