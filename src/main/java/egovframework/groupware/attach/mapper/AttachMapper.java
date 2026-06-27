package egovframework.groupware.attach.mapper;

import egovframework.groupware.attach.service.AttachVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AttachMapper {

    int insertGroup(@Param("ownerEntity") String ownerEntity,
                    @Param("ownerEntityId") String ownerEntityId);

    /** insertGroup 직후의 groupId를 조회. H2/PG 공용 select. */
    Long lastGroupId();

    int insert(AttachVO vo);

    AttachVO findById(@Param("attachId") Long attachId);

    /** 첨부가 속한 그룹의 owner_entity (BOARD / DOC / ...). 첨부 다운로드 모듈 검증용. */
    String findOwnerEntity(@Param("attachId") Long attachId);

    List<AttachVO> findByGroup(@Param("groupId") Long groupId);

    int delete(@Param("attachId") Long attachId);
}
