package egovframework.groupware.room.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.room.service.RoomReservationVO;
import egovframework.groupware.room.service.RoomService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
public class RoomController {

    private final RoomService service;

    public RoomController(RoomService service) { this.service = service; }

    @GetMapping("/room/list.do")
    public String list(@RequestParam(required = false) Long roomId,
                       @RequestParam(required = false) String day,
                       @AuthenticationPrincipal CustomUserDetails me,
                       Model model) {
        LocalDate d = (day == null || day.isBlank()) ? LocalDate.now() : LocalDate.parse(day);
        model.addAttribute("rooms", service.findAllRooms());
        model.addAttribute("roomId", roomId);
        model.addAttribute("day", d);
        if (roomId != null) {
            model.addAttribute("room", service.findRoom(roomId));
            model.addAttribute("reservations", service.findByRoomAndDay(roomId, d));
        }
        model.addAttribute("myReservations", service.findByUser(me.getUserId()));
        return "room/list";
    }

    @PostMapping("/room/reserve.do")
    public String reserve(@AuthenticationPrincipal CustomUserDetails me,
                          @RequestParam Long roomId,
                          @RequestParam String startDt,
                          @RequestParam String endDt,
                          @RequestParam(required = false) String purpose) {
        RoomReservationVO vo = new RoomReservationVO();
        vo.setRoomId(roomId);
        vo.setStartDt(LocalDateTime.parse(startDt));
        vo.setEndDt(LocalDateTime.parse(endDt));
        vo.setPurpose(purpose);
        service.reserve(vo, me.getUserId());
        return "redirect:/room/list.do?roomId=" + roomId
                + "&day=" + vo.getStartDt().toLocalDate();
    }

    @PostMapping("/room/cancel.do")
    public String cancel(@AuthenticationPrincipal CustomUserDetails me,
                         @RequestParam Long resId,
                         @RequestParam(required = false) Long roomId) {
        service.cancel(resId, me.getUserId(), me.getRoleCd());
        return "redirect:/room/list.do" + (roomId == null ? "" : "?roomId=" + roomId);
    }
}
