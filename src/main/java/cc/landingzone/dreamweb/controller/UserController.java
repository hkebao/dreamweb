package cc.landingzone.dreamweb.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.landingzone.dreamweb.model.Page;
import cc.landingzone.dreamweb.model.User;
import cc.landingzone.dreamweb.model.UserRole;
import cc.landingzone.dreamweb.model.WebResult;
import cc.landingzone.dreamweb.service.UserRoleService;
import cc.landingzone.dreamweb.service.UserService;
import cc.landingzone.dreamweb.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * charles
 */
@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @RequestMapping("/getUserInfo.do")
    public void getUserInfo(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByLoginName(username);
            Map<String, String> map = new HashMap<String, String>();
            map.put("userInfo", user.getLoginName() + "  (" + user.getName() + ")");
            List<UserRole> list = userRoleService.getRoleListByUserId(user.getId());
            Collections.sort(list);
            StringBuilder roleListStr = new StringBuilder();
            roleListStr.append("<table>");
            roleListStr.append("<tr><th>Login</th><th>Type</th><th>Name</th><th>Value</th></tr>");
            for (UserRole role : list) {
                roleListStr.append("<tr><td><a href='../../sso/login.do?sp=" + role.getRoleType() + "&userRoleId=" + role.getId() + "' target='_blank'>登录控制台</a></td><td>" + role.getRoleType() + "</td><td>" + role.getRoleName() + "</td><td>"
                        + role.getRoleValue() + "</td></tr>");
            }
            roleListStr.append("</table>");
            map.put("roleList", roleListStr.toString());
            result.setData(map);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/searchUser.do")
    public void searchUser(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String simpleSearch = request.getParameter("simpleSearch");
            Integer start = Integer.valueOf(request.getParameter("start"));
            Integer limit = Integer.valueOf(request.getParameter("limit"));
            Page page = new Page(start, limit);
            List<User> list = userService.searchUser(simpleSearch, page);
            result.setTotal(page.getTotal());
            result.setData(list);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/addUser.do")
    public void addUser(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String formString = request.getParameter("formString");
            User systemUser = JsonUtils.parseObject(formString, User.class);
            userService.addUser(systemUser);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/updateUser.do")
    public void updateUser(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String formString = request.getParameter("formString");
            User updateUser = JsonUtils.parseObject(formString, User.class);
            User dbUser = userService.getUserById(updateUser.getId());
            dbUser.setName(updateUser.getName());
            dbUser.setPhone(updateUser.getPhone());
            dbUser.setComment(updateUser.getComment());
            userService.updateUser(dbUser);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/deleteUser.do")
    public void deleteUser(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            Integer id = Integer.valueOf(request.getParameter("id"));
            userService.deleteUser(id);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    /**
     * 分配管理员
     *
     * @param request
     * @param response
     */
    @RequestMapping("/assignRoleAdmin.do")
    public void assignRoleAdmin(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String loginName = request.getParameter("loginName");
            userService.assignRole(loginName, UserService.User_Role_Admin);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    /**
     * 取消管理员
     *
     * @param request
     * @param response
     */
    @RequestMapping("/cancelRoleAdmin.do")
    public void cancelRoleAdmin(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String loginName = request.getParameter("loginName");
            userService.assignRole(loginName, UserService.User_Role_Guest);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

}
