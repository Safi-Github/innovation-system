package mcit.ddr.innovation.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == 403) {
                return "error/403";  // Redirect to 403.html inside templates/error/
            } else if (statusCode == 404) {
                return "error/404";  // Redirect to 404.html inside templates/error/
            }
        }
        return "error/default"; // A generic error page for other status codes
    }
}
