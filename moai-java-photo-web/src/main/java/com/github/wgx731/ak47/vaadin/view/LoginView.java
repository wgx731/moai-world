package com.github.wgx731.ak47.vaadin.view;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Tag("sa-login-view")
@Route(value = LoginView.ROUTE)
@PageTitle("Login")
public class LoginView extends VerticalLayout {

    private static final long serialVersionUID = 3057526912837310736L;

    public static final String ROUTE = "login";

    private LoginOverlay login = new LoginOverlay();

    public LoginView() {
        login.setAction("login");
        login.setOpened(true);
        login.setForgotPasswordButtonVisible(false);
        login.setTitle("Spring Vaadin Photo App");
        login.setDescription("Login with your LDAP account");
        getElement().appendChild(login.getElement());
    }

}
