package thinkonweb.ml.society.repository.conference;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Repository;
import thinkonweb.ml.society.domain.conference.SoConfTempMember;

import java.util.Date;
import java.util.List;

@Repository
public interface SoConfTempMemberMapper {
    @Insert("INSERT INTO `sc_conf_temp_member` (`conf_id`,`email`,`name`,`phone`,`password`, `department`) VALUES (#{confId},#{email},#{name},#{phone},#{password},#{department})")
    @Options(flushCache = true)
    void insert(SoConfTempMember member);

    @Select("SELECT * FROM `sc_conf_temp_member` WHERE conf_id = #{confId}")
    List<SoConfTempMember> findByConfId(@Param("confId") int confId);

    @Select("SELECT * FROM `sc_conf_temp_member` WHERE conf_id = #{confId} and email = #{email} limit 1")
    SoConfTempMember findByConfIdAndEmail(@Param("confId") int confId, @Param("email") String email);

    @Select("SELECT * FROM `sc_conf_temp_member` WHERE id = #{id}")
    SoConfTempMember findById(@Param("id") int id);

    @Update("UPDATE `sc_conf_temp_member`" +
            " SET `phone` = #{phone}, `email` = #{email}, `password` = #{password}, `phone` = #{phone}, `department` = #{department} WHERE `id` = #{id}")
    @Options(flushCache = true)
    void update(SoConfTempMember soConfTempMember);

    @Update("UPDATE `sc_conf_temp_member` SET `phone` = '', `password` = '' WHERE `id` = #{id}")
    @Options(flushCache = true)
    void erase(SoConfTempMember soConfTempMember);

    @Select("SELECT * FROM `sc_conf_temp_member` WHERE conf_id = #{confId} and prize_exclude=0 and already_prize=0")
    List<SoConfTempMember> findByConfIdAndExcludePrizeExcludeAndAlreadyPrize(@Param("confId") int confId);

    @Select("SELECT * FROM `sc_conf_temp_member` WHERE conf_id = #{confId} and already_prize=1")
    List<SoConfTempMember> findByConfIdAndAlreadyPrize(@Param("confId") int confId);

    @Update("UPDATE `sc_conf_temp_member`" +
            " SET `already_prize` = 1 WHERE `id` = #{id}")
    @Options(flushCache = true)
    void updateAlreadyPrize(@Param("id") String id);

    @Update("UPDATE `sc_conf_temp_member`" +
            " SET `prize_exclude` = if(`prize_exclude` = 1,0,1) WHERE `id` = #{id}")
    @Options(flushCache = true)
    void updatePrizeExclude(@Param("id") String id);

    @Update("UPDATE `sc_conf_temp_member`" +
            " SET `already_prize` = 0 WHERE `conf_id` = #{confId}")
    @Options(flushCache = true)
    void initAlreadyPrize(@Param("confId") int confId);
}