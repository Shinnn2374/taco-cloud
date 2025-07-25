package sia.tacoCloud.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import sia.tacoCloud.dao.OrderRepository;
import sia.tacoCloud.data.taco.TacoOrder;
import sia.tacoCloud.security.User;


@Slf4j
@Controller
@RequestMapping("/orders")
@SessionAttributes("tacoOrder")
public class OrderController {

    private final OrderRepository orderRepo;

    public OrderController(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @GetMapping("/current")
    public String orderForm(@ModelAttribute("tacoOrder") TacoOrder tacoOrder) {
        log.info("Current order: {}", tacoOrder);
        return "orderForm";
    }

    @PostMapping
    public String processOrder(@ModelAttribute("tacoOrder") @Valid TacoOrder tacoOrder,
                               SessionStatus sessionStatus, Errors errors, @AuthenticationPrincipal User user ) {
        if (errors.hasErrors()) {
            return "orderForm";
        }

        tacoOrder.setUser(user);

        sessionStatus.setComplete();
        orderRepo.save(tacoOrder);
        return "redirect:/";
    }
}