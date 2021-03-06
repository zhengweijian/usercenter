package org.dimhat.usercenter.controller;

import org.dimhat.usercenter.Constants;
import org.dimhat.usercenter.controller.form.LoginForm;
import org.dimhat.usercenter.exception.user.PasswordErrorException;
import org.dimhat.usercenter.exception.user.UserFreezeException;
import org.dimhat.usercenter.exception.user.UserNotFindException;
import org.dimhat.usercenter.service.CompanyService;
import org.dimhat.usercenter.service.dto.CompanyDTO;
import org.dimhat.usercenter.web.model.UserInfo;
import org.dimhat.vericode.CaptchaServiceSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author : zwj
 * @data : 2017/3/3
 */
@Controller
@RequestMapping("/")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private CompanyService companyService;

    @RequestMapping(value = "login",method = RequestMethod.GET)
    public String login(Model model, LoginForm form){
        model.addAttribute("form",form);
        return "/login";
    }

    @RequestMapping(value="login",method = RequestMethod.POST)
    public String doLogin(Model model, @Validated LoginForm form, Errors errors,
                          HttpServletRequest request, HttpServletResponse response,HttpSession session){
        if(errors.hasErrors()){
            model.addAttribute(Constants.MSG,errors.getFieldError().getDefaultMessage());
            return login(model,form);
        }
        String captchaId = request.getSession().getId();

        CaptchaServiceSingleton.getInstance().validateResponseForID(captchaId,response);

        try {
            UserInfo userInfo = doLogin(form.getUsername(), form.getPassword());
            session.setAttribute("userInfo",userInfo);
            return "redirect:/index";
        } catch (UserNotFindException e) {
            model.addAttribute(Constants.CODE,-1);
            model.addAttribute(Constants.MSG,"账户名不存在");
        } catch (PasswordErrorException e) {
            model.addAttribute(Constants.CODE,-2);
            model.addAttribute(Constants.MSG,"密码错误");
        } catch (UserFreezeException e) {
            model.addAttribute(Constants.CODE,-3);
            model.addAttribute(Constants.MSG,"账户已被冻结");
        }
        return login(model,form);
    }

    private UserInfo doLogin(String username,String password) throws UserNotFindException, PasswordErrorException, UserFreezeException {
        if(username.indexOf(Constants.USERNAME_SPLIT)==-1){//company login
            CompanyDTO companyDTO = companyService.login(username, password);
            UserInfo userInfo = buildUserInfo(companyDTO);
            return userInfo;
        }else{//employee login
            throw new RuntimeException("unsupport employee login");
        }
    }

    private UserInfo buildUserInfo(CompanyDTO company){
        UserInfo userInfo = new UserInfo();
        userInfo.setCompanyId(company.getId());
        userInfo.setCompanyName(company.getName());
        userInfo.setCompanyUsername(company.getUsername());
        userInfo.setCompanyType(company.getType());

        return userInfo;
    }
}
