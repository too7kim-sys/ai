package egovframework.groupware.vehicle.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.vehicle.service.VehicleReservationVO;
import egovframework.groupware.vehicle.service.VehicleService;
import egovframework.groupware.vehicle.service.VehicleVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
public class VehicleController {

    private final VehicleService service;

    public VehicleController(VehicleService service) { this.service = service; }

    @GetMapping("/vehicle/list.do")
    public String list(@AuthenticationPrincipal CustomUserDetails me,
                       @RequestParam(required = false) Long vehicleId,
                       @RequestParam(required = false) String day,
                       Model model) {
        LocalDate d = (day == null || day.isBlank()) ? LocalDate.now() : LocalDate.parse(day);
        model.addAttribute("vehicles", service.findAll());
        model.addAttribute("day", d);
        if (vehicleId != null) {
            model.addAttribute("vehicle", service.findById(vehicleId));
            model.addAttribute("reservations", service.findByVehicleAndDay(vehicleId, d));
        }
        model.addAttribute("vehicleId", vehicleId);
        model.addAttribute("myReservations", service.findByUser(me.getUserId()));
        model.addAttribute("upcoming", service.findUpcoming(10));
        return "vehicle/list";
    }

    @PostMapping("/vehicle/reserve.do")
    public String reserve(@AuthenticationPrincipal CustomUserDetails me,
                          @RequestParam Long vehicleId,
                          @RequestParam String startDt,
                          @RequestParam String endDt,
                          @RequestParam(required = false) String purpose,
                          @RequestParam(required = false) String destination,
                          @RequestParam(required = false, defaultValue = "1") Integer passengerCnt) {
        VehicleReservationVO vo = new VehicleReservationVO();
        vo.setVehicleId(vehicleId);
        vo.setStartDt(LocalDateTime.parse(startDt));
        vo.setEndDt(LocalDateTime.parse(endDt));
        vo.setPurpose(purpose);
        vo.setDestination(destination);
        vo.setPassengerCnt(passengerCnt);
        service.reserve(vo, me.getUserId());
        return "redirect:/vehicle/list.do?vehicleId=" + vehicleId + "&day=" + vo.getStartDt().toLocalDate();
    }

    @PostMapping("/vehicle/cancel.do")
    public String cancel(@AuthenticationPrincipal CustomUserDetails me,
                         @RequestParam Long reservationId,
                         @RequestParam(required = false) Long vehicleId) {
        service.cancel(reservationId, me.getUserId(), me.getRoleCd());
        return "redirect:/vehicle/list.do" + (vehicleId == null ? "" : "?vehicleId=" + vehicleId);
    }

    @PostMapping("/vehicle/start.do")
    public String startUse(@RequestParam Long reservationId, @RequestParam Long vehicleId) {
        service.startUse(reservationId);
        return "redirect:/vehicle/list.do?vehicleId=" + vehicleId;
    }

    @PostMapping("/vehicle/complete.do")
    public String complete(@RequestParam Long reservationId,
                           @RequestParam Long vehicleId,
                           @RequestParam(required = false) Integer mileageEnd) {
        service.complete(reservationId, mileageEnd);
        return "redirect:/vehicle/list.do?vehicleId=" + vehicleId;
    }

    @GetMapping("/vehicle/edit.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String editForm(@RequestParam(required = false) Long vehicleId, Model model) {
        VehicleVO v = vehicleId == null ? new VehicleVO() : service.findById(vehicleId);
        if (v == null) v = new VehicleVO();
        model.addAttribute("v", v);
        return "vehicle/edit";
    }

    @PostMapping("/vehicle/edit.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String save(@ModelAttribute("v") VehicleVO vo) {
        if (vo.getVehicleId() == null) {
            Long id = service.create(vo);
            return "redirect:/vehicle/list.do?vehicleId=" + id;
        }
        service.update(vo);
        return "redirect:/vehicle/list.do?vehicleId=" + vo.getVehicleId();
    }
}
