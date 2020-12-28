package solis.pl.repository;

import javafx.util.Pair;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import solis.pl.domain.user.User;

import java.util.List;

@Repository
public interface ZoomRoomMapper {

    @Insert("insert into `zoom_room` ( room_number, password ) values ( #{roomNumber}, #{password})")
    void insert(@Param("roomNumber") String roomNumber,@Param("password") String password);

    @Select("select room_number from `zoom_room`")
    List<String> findAll();

    @Select("select ifnull(password,'') from `zoom_room` where room_number = #{roomNumber} Limit 1")
    String findPassword(@Param("roomNumber") String roomNumber);

    @Delete("delete from `zoom_room` where room_number = #{roomNumber}")
    void delete(@Param("roomNumber") String roomNumber);
}
