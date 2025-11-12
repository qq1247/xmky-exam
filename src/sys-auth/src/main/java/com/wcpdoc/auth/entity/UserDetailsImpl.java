package com.wcpdoc.auth.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.wcpdoc.base.entity.User;
import com.wcpdoc.core.entity.LoginUser;

/**
 * 授权认证用户实体
 * 
 * v1.0 zhanghc 2025年11月4日下午8:42:38
 */
public class UserDetailsImpl implements UserDetails {
	private static final long serialVersionUID = 1L;
	private String username;
	private Collection<? extends GrantedAuthority> authorities;
	private boolean enabled;
	private LoginUser loginUser;

	public UserDetailsImpl(User user) {
		this.username = user.getLoginName();
		this.authorities = List.of(new SimpleGrantedAuthority(user.getRole()));
		this.enabled = user.getState() == 1;

		loginUser = new LoginUser() {
			@Override
			public Integer getId() {
				return user.getId();
			}

			@Override
			public String getLoginName() {
				return user.getLoginName();
			}

			@Override
			public String getRole() {
				return user.getRole();
			}
		};
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return "";
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public LoginUser getLoginUser() {
		return loginUser;
	}
}
