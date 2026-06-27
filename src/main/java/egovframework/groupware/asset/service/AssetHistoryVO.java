package egovframework.groupware.asset.service;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AssetHistoryVO {
    private Long historyId;
    private Long assetId;
    private String assetNo;
    /** ASSIGN | RETURN | REPAIR | DISPOSE | RELOCATE */
    private String actionCd;
    private Long actorId;
    private String actorName;
    private Long targetUserId;
    private String targetUserName;
    private LocalDateTime actionDt;
    private String beforeStatus;
    private String afterStatus;
    private String memo;
}
